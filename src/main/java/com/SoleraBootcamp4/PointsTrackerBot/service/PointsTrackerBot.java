package com.SoleraBootcamp4.PointsTrackerBot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import com.SoleraBootcamp4.PointsTrackerBot.model.MessageChat;
import com.SoleraBootcamp4.PointsTrackerBot.model.TelegramMessage;

@Service
public class PointsTrackerBot {

    private PointsTrackerService pointsTrackerService;

    private final String baseUrl = "https://api.telegram.org/bot" + getBotToken() + "/";
    private final String GROUP_ID = System.getenv("GROUP_ID");

    public void setService(PointsTrackerService pointsTrackerService) {
        this.pointsTrackerService = pointsTrackerService;
    }

    public void sendWinnerMessage(String winnerMessage) {
        sendMessage(winnerMessage, GROUP_ID);
    }

    public void sendCommandResponse(TelegramMessage receivedMessage) {

        String message = "";

        MessageChat chat = receivedMessage.getChat();
        String chatId = chat.getId();
        String text = receivedMessage.getText();

        if (chat.isGroupOrSuperGroup()) {
            if (chatId.equals(GROUP_ID)) {
                if (text.equals("/scoreboard") || text.equals("/scoreboard@" + getBotUsername())) {
                    message = pointsTrackerService.getScoreboardMessage();
                } else if (text.equals("/help") || text.equals("/help@" + getBotUsername()) || text.equals("/ayuda")
                        || text.equals("/ayuda@" + getBotUsername())) {
                    message = pointsTrackerService.getHelpMessage();
                }
            } else {
                message = "Sorry @" + receivedMessage.getSender().getUsername() + ", this bot only works in certain groups.";
            }
        } else {
            message = "Sorry @" + receivedMessage.getSender().getUsername() + ", this bot only works in groups.";
        }

        if (!message.equals("")) {
            sendMessage(message, chatId);
        }

    }

    private void sendMessage(String message, String chatId) {
        try {
            String urlString = baseUrl + "sendmessage";
            HttpPost httpPost = new HttpPost(urlString);
            httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.addHeader("charset", "UTF-8");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            
            nameValuePairs.add(new BasicNameValuePair("chat_id", chatId + ""));
            nameValuePairs.add(new BasicNameValuePair("text", message));
            nameValuePairs.add(new BasicNameValuePair("parse_mode", "html"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

            CloseableHttpClient client = HttpClients.createDefault();
            client.execute(httpPost);

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
