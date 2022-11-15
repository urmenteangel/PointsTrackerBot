package com.SoleraBootcamp4.PointsTrackerBot.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.stream.JsonReader;
import com.SoleraBootcamp4.PointsTrackerBot.model.MessageChat;
import com.SoleraBootcamp4.PointsTrackerBot.model.MessageSender;
import com.SoleraBootcamp4.PointsTrackerBot.model.TelegramMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@Service
public class PointsTrackerService {

    private final String LOCAL_TEAM_DATA_LOCATION = System.getenv("LOCAL_TEAM_DATA_LOCATION");
    private final String REMOTE_TEAM_DATA_LOCATION = System.getenv("REMOTE_TEAM_DATA_LOCATION");
    private final String MAIN_REF = System.getenv("MAIN_REF");
    private final String TEAM_DATA_URL = System.getenv("TEAM_DATA_URL");

    @Autowired
    PointsTrackerBot bot;

    @PostConstruct
    public void init() {
        bot.setService(this);
    }

    Gson gson = new Gson();

    public void pullTeamData(String payload) {
        if (isTeamDataModified(payload)) {
            try {
                FileUtils.copyURLToFile(new URL(TEAM_DATA_URL), new File(LOCAL_TEAM_DATA_LOCATION));
                JsonArray jsonTeams = readJson(LOCAL_TEAM_DATA_LOCATION);
                ArrayList<SimpleEntry<Integer, String>> scoreboard = getScoreboard(jsonTeams);
                sendDataUpdatedMessage(scoreboard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isTeamDataModified(String payload) {
        JsonObject json = gson.fromJson(payload,
                JsonObject.class);
        String ref = json.get("ref").getAsString();
        if (ref.equals(MAIN_REF)) {
            JsonObject headCommit = json.getAsJsonObject("head_commit");
            // If the value is an Array[], it is parsed as ArrayList
            JsonArray modifiedFiles = headCommit.getAsJsonArray("modified");
            for (JsonElement fileName : modifiedFiles) {
                if (fileName.getAsString().equals(REMOTE_TEAM_DATA_LOCATION)) {
                    return true;
                }
            }
        }
        return false;
    }

    private JsonArray readJson(String path) {

        JsonArray jsonTeams = new JsonArray();
        try {
            JsonObject jsonObject = gson.fromJson(new JsonReader(Files.newBufferedReader(Paths.get(path))),
                    JsonObject.class);
            jsonTeams = jsonObject.getAsJsonArray("teamdata");
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonTeams;

    }

    private ArrayList<SimpleEntry<Integer, String>> getScoreboard(JsonArray jsonT) {
        ArrayList<SimpleEntry<Integer, String>> orderedTeams = new ArrayList<>();

        for (JsonElement team : jsonT) {
            JsonArray activities = team.getAsJsonObject().getAsJsonArray("actividades");
            String teamName = team.getAsJsonObject().get("name").getAsString();
            int teamPoints = 0;
            for (JsonElement activity : activities) {
                teamPoints += activity.getAsJsonObject().get("puntos").getAsInt();
            }
            orderedTeams.add(new SimpleEntry<>(Integer.valueOf(teamPoints), teamName));
        }
        orderedTeams.sort((o1, o2) -> o2.getKey().compareTo(o1.getKey()));
        return orderedTeams;
    }

    private void sendDataUpdatedMessage(ArrayList<SimpleEntry<Integer, String>> teams) {

        String message = "Se ha modificado la clasificación.\n\n";

        message += getWinnerMessage();
        message += "\n\nPara consultar la nueva clasificación completa, usa \"/scoreboard\".";

        bot.sendWinnerMessage(message);
    }

    private String getWinnerMessage() {

        JsonArray jsonTeams = readJson(LOCAL_TEAM_DATA_LOCATION);
        ArrayList<SimpleEntry<Integer, String>> teams = getScoreboard(jsonTeams);

        int maxPoints = teams.get(0).getKey();
        ArrayList<String> winningTeams = getWinningTeams(teams);
        String message = "";

        if (winningTeams.size() > 1) {
            message += "¡Hay empate a " + maxPoints + " puntos entre los equipos ";
            for (int i = 0; i < winningTeams.size(); i++) {
                if (i == winningTeams.size() - 1) {
                    message += "y \"" + formatTeamName(winningTeams.get(i)) + "\"!";
                } else if (i == winningTeams.size() - 2) {
                    message += "\"" + formatTeamName(winningTeams.get(i)) + "\" ";
                } else {
                    message += "\"" + formatTeamName(winningTeams.get(i)) + "\", ";
                }
            }
        } else {
            message += "Va ganando el equipo \"" + formatTeamName(winningTeams.get(0)) + "\" con " + maxPoints
                    + " puntos.";
        }

        return message;
    }

    private ArrayList<String> getWinningTeams(ArrayList<SimpleEntry<Integer, String>> teams) {

        ArrayList<String> winningTeams = new ArrayList<>();

        int maxPoints = teams.get(0).getKey().intValue();

        for (SimpleEntry<Integer, String> team : teams) {
            if (team.getKey() == maxPoints) {
                winningTeams.add(team.getValue());
            } else {
                break;
            }
        }

        return winningTeams;
    }

    private String formatTeamName(String teamName) {
        String formatedTeamName = teamName.toLowerCase();
        for (int i = 0; i < formatedTeamName.length(); i++) {
            if (i == 0) {
                formatedTeamName = formatedTeamName.substring(0, 1).toUpperCase() + formatedTeamName.substring(1);
            } else if (formatedTeamName.charAt(i) == ' ') {
                formatedTeamName = formatedTeamName.substring(0, i + 1)
                        + formatedTeamName.substring(i + 1, i + 2).toUpperCase()
                        + formatedTeamName.substring(i + 2);
            }
        }
        return formatedTeamName;
    }

    public String getScoreboardMessage() {

        JsonArray jsonTeams = readJson(LOCAL_TEAM_DATA_LOCATION);
        ArrayList<SimpleEntry<Integer, String>> teams = getScoreboard(jsonTeams);

        String message = "Esta es la clasificación actual: \n";

        for (SimpleEntry<Integer, String> team : teams) {

            String teamName = formatTeamName(team.getValue());
            message += "\n" + (teams.indexOf(team) + 1) + "º: " + teamName + ": " + team.getKey().intValue()
                    + " puntos.";
        }

        return message;
    }

    public void payloadToTelegramMessage(String payload) {

        System.out.println(payload);

        JsonObject messageJson = gson.fromJson(payload, JsonObject.class).get("message").getAsJsonObject();
        JsonObject senderJson = messageJson.get("from").getAsJsonObject();
        JsonObject chatJson = messageJson.get("chat").getAsJsonObject();

        String senderId = senderJson.get("id").getAsString();
        boolean isBot = senderJson.get("is_bot").getAsBoolean();
        String firstName = senderJson.get("first_name").getAsString();

        JsonElement lastNameElement = senderJson.get("last_name");
        String lastName = lastNameElement != null ? lastNameElement.getAsString() : "";

        JsonElement usernameElement = senderJson.get("username");
        String username = usernameElement != null ? usernameElement.getAsString() : "";

        String chatId = chatJson.get("id").getAsString();
        String title = chatJson.get("title").getAsString();

        String typeElement = chatJson.get("type").getAsString();
        boolean type = (typeElement.equals("supergroup") || typeElement.equals("group")) ? true : false;

        MessageSender sender = new MessageSender(senderId, isBot, firstName, lastName, username);
        MessageChat chat = new MessageChat(chatId, title, type);

        String text = messageJson.get("text").getAsString();

        TelegramMessage message = new TelegramMessage(sender, chat, text);
        bot.sendScoreboardMessage(message);
    }

}
