package com.SoleraBootcamp4.PointsTrackerBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component
public class PointsTrackerBot {

    private Gson gson = new Gson();

    private PointsTrackerService pointsTrackerService;

    private final String groupId = "-1001722891281";
    // private final String soleraGroupId = "-1001561970415";

    public void setService(PointsTrackerService pointsTrackerService) {
        this.pointsTrackerService = pointsTrackerService;
    }

    public void sendWinnerMessage(String winnerMessage) {

    }

    private void sendScoreboardMessage(JsonObject update) {

        /* Message receivedMessage = update.getMessage();
        String chatId = receivedMessage.getChatId().toString();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (receivedMessage.isGroupMessage()) {
            if (chatId.equals(groupId)) {
                if (receivedMessage.getText().equals("/scoreboard")
                        || receivedMessage.getText().equals("/scoreboard@" + getBotUsername())) {
                    message.setText(pointsTrackerService.getScoreboardMessage());
                }
            } else {
                message.setText("Sorry, this bot only works in certain groups.");
            }
        } else {
            String lastName = receivedMessage.getFrom().getLastName() == null ? ""
                    : receivedMessage.getFrom().getLastName();
            message.setText("Sorry " + receivedMessage.getFrom().getFirstName()
                    + " " + lastName + ", this bot only works in groups.");
        }
        execute(message); */
    }

    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    public String getBotUsername() {
        return "PointsTrackerBot";
    }

    public void payloadToJson(String payload) {
    }

}
