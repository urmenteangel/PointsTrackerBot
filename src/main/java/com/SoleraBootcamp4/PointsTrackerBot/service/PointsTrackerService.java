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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@Service
public class PointsTrackerService {

    private final String TEAM_DATA_LOCATION = "res/teamdata.json";
    private final String MAIN_REF = "refs/heads/main";
    private final String TEAM_DATA_URL = "https://raw.githubusercontent.com/urmenteangel/bootcampsolera/main/src/data/teamdata.json";

    @Autowired
    PointTrackerBot bot;

    @PostConstruct
    public void init() {
        bot.setService(this);
    }

    Gson gson = new Gson();

    public void pullTeamData(String payload) {
        if (isTeamDataModified(payload)) {
            try {
                FileUtils.copyURLToFile(new URL(TEAM_DATA_URL), new File(TEAM_DATA_LOCATION));
                JsonArray jsonTeams = readJson(TEAM_DATA_LOCATION);
                ArrayList<SimpleEntry<Integer, String>> scoreboard = getScoreboard(jsonTeams);
                sendCurrentWinnerMessage(scoreboard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JsonArray readJson(String path) {

        JsonArray jsonTeams = new JsonArray();
        try {
            JsonElement jsonElement = gson.fromJson(new JsonReader(Files.newBufferedReader(Paths.get(path))),
                    JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
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

    private boolean isTeamDataModified(String payload) {
        JsonObject json = gson.fromJson(payload,
                JsonObject.class);
        String ref = json.get("ref").getAsString();
        if (ref.equals(MAIN_REF)) {
            JsonObject headCommit = json.getAsJsonObject("head_commit");
            // If the value is an Array[], it is parsed as ArrayList
            JsonArray modifiedFiles = headCommit.getAsJsonArray("modified");
            for (JsonElement fileName : modifiedFiles) {
                if (fileName.getAsString().equals("src/data/teamdata.json")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendCurrentWinnerMessage(ArrayList<SimpleEntry<Integer, String>> teams) {
        ArrayList<String> winningTeams = new ArrayList<>();

        int maxPoints = teams.get(0).getKey().intValue();

        for (SimpleEntry<Integer, String> team : teams) {
            if (team.getKey() == maxPoints) {
                winningTeams.add(team.getValue());
            } else {
                break;
            }
        }

        String message = "Se ha modificado la clasificación.\n\n";

        if (winningTeams.size() > 1) {
            message += "¡Hay empate a " + maxPoints + " puntos entre los equipos ";
            for (int i = 0; i < winningTeams.size(); i++) {
                if (i == winningTeams.size() - 1) {
                    message += "y \"" + winningTeams.get(i) + "\"!";
                } else if (i == winningTeams.size() - 2) {
                    message += "\"" + winningTeams.get(i) + "\" ";
                } else {
                    message += "\"" + winningTeams.get(i) + "\", ";
                }
            }
        } else {
            message += "Va ganando el equipo \"" + winningTeams.get(0) + "\" con " + maxPoints + " puntos.";
        }

        bot.sendWinnerMessage(message);
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

    public String getScoreboardMessage() {

        JsonArray jsonTeams = readJson(TEAM_DATA_LOCATION);
        ArrayList<SimpleEntry<Integer, String>> teams = getScoreboard(jsonTeams);

        String message = "Esta es la clasificación actual: \n";

        for (SimpleEntry<Integer, String> team : teams) {
            String teamName = team.getValue().toLowerCase();
            teamName = teamName.substring(0, 1).toUpperCase() + teamName.substring(1);
            for (int i = 0; i < teamName.length(); i++) {
                if (teamName.charAt(i) == ' ') {
                    teamName = teamName.substring(0, i + 1) + teamName.substring(i + 1, i + 2).toUpperCase()
                            + teamName.substring(i + 2);
                }
            }
            message += "\n" + (teams.indexOf(team) + 1) + "º: " + teamName + ": " + team.getKey().intValue() + " puntos.";
        }

        return message;
    }

}
