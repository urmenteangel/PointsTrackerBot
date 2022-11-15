package com.SoleraBootcamp4.PointsTrackerBot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.SoleraBootcamp4.PointsTrackerBot.model.MessageChat;
import com.SoleraBootcamp4.PointsTrackerBot.model.TelegramMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@Service
public class PointsTrackerBot {

    private Gson gson = new Gson();

    private PointsTrackerService pointsTrackerService;

    private final String baseUrl = "https://api.telegram.org/bot" + getBotToken() + "/";
    private final String groupId = "-1001722891281";
    // private final String soleraGroupId = "-1001561970415";

    public void setService(PointsTrackerService pointsTrackerService) {
        this.pointsTrackerService = pointsTrackerService;
    }

    public void sendWinnerMessage(String winnerMessage) {
        String message = "";
    }

    public void sendScoreboardMessage(TelegramMessage receivedMessage) {

        String message = "";

        MessageChat chat = receivedMessage.getChat();
        String chatId = chat.getId();
        String text = receivedMessage.getText();

        if (chat.isGroupOrSuperGroup()) {
            if (chatId.equals(groupId)) {
                if (text.equals("/scoreboard") || text.equals("/scoreboard@" + getBotUsername())) {
                    message = pointsTrackerService.getScoreboardMessage();
                }
            } else {
                message = "Sorry, this bot only works in certain groups.";
            }
        } else {
            String lastName = receivedMessage.getSender().getLastName();
            message = "Sorry " + receivedMessage.getSender().getFirstName() + " " + lastName
                    + ", this bot only works in groups.";
        }

        JsonObject json = new JsonObject();
        json.addProperty("chat_id", chatId);
        json.addProperty("text", message);

        String jsonStringified = json.toString();

        try {
            URL url = new URL(baseUrl + "sendMessage");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonStringified.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    public String getBotUsername() {
        return "PointsTrackerBot";
    }

}
