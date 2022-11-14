package com.SoleraBootcamp4.PointsTrackerBot.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

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

    Gson gson = new Gson();
    private JsonArray teams;

    public void pullTeamData(String payload) {
        if (isTeamDataModified(payload)) {
            try {
                FileUtils.copyURLToFile(new URL(TEAM_DATA_URL), new File(TEAM_DATA_LOCATION));
                readJson(TEAM_DATA_LOCATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JsonArray readJson(String path) {

        JsonElement jsonElement;
        try {
            jsonElement = gson.fromJson(new JsonReader(Files.newBufferedReader(Paths.get(path))),
                    JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            teams = jsonObject.getAsJsonArray("teamdata");
            getCurrentWinner(teams);
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return teams;

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

    private void getCurrentWinner(JsonArray teams) {
        ArrayList<String> winningTeams = new ArrayList<>();
        
        NavigableMap<Integer, String> orderedTeams = getScoreboard(teams);

        Entry<Integer, String> winningTeam = orderedTeams.pollFirstEntry();
        int maxPoints = winningTeam.getKey();
        winningTeams.add(winningTeam.getValue());

        if(!orderedTeams.isEmpty()){
            Entry<Integer, String> nextTeam = orderedTeams.pollFirstEntry();
            if(nextTeam.getKey().intValue() == maxPoints){
                winningTeams.add(nextTeam.getValue());
            } if (nextTeam.getKey().intValue() < maxPoints){
                orderedTeams.clear();
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

    private NavigableMap<Integer, String> getScoreboard(JsonArray teams){
        TreeMap<Integer, String> unorderedTeams = new TreeMap<>();
        
        for (JsonElement team : teams) {
            JsonArray activities = team.getAsJsonObject().getAsJsonArray("actividades");
            String teamName = team.getAsJsonObject().get("name").getAsString();
            int teamPoints = 0;
            for (JsonElement activity : activities) {
                teamPoints += activity.getAsJsonObject().get("puntos").getAsInt();
            }
            unorderedTeams.put(Integer.valueOf(teamPoints), teamName);
        }
        return unorderedTeams.descendingMap();
    }

}
