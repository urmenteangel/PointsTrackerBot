package com.SoleraBootcamp4.PointsTrackerBot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.SoleraBootcamp4.PointsTrackerBot.model.MessageChat;
import com.SoleraBootcamp4.PointsTrackerBot.model.TelegramMessage;
import com.google.gson.Gson;
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

        try {
            String urlString = baseUrl + "sendmessage";
            HttpPost httpPost = new HttpPost(urlString);
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.addHeader("charset", "UTF-8");

            /// Create list of parameters
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            /// Add chatid to the list
            nameValuePairs.add(new BasicNameValuePair("chat_id", chatId + ""));
            /// Add text to the list
            nameValuePairs.add(new BasicNameValuePair("text", message));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            CloseableHttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(httpPost);
            System.out.println(EntityUtils.toString(response.getEntity()));

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
