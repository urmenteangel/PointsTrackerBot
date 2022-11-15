package com.SoleraBootcamp4.PointsTrackerBot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class PointTrackerBot extends TelegramWebhookBot {

    private PointsTrackerService pointsTrackerService;

    private final String groupId = "-1001722891281";
    // private final String soleraGroupId = "-1001561970415";

    public void setService(PointsTrackerService pointsTrackerService) {
        this.pointsTrackerService = pointsTrackerService;
    }

    public void sendWinnerMessage(String winnerMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(groupId);
        message.setText(winnerMessage);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public String getBotUsername() {
        return "PointsTrackerBot";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Message receivedMessage = update.getMessage();
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

        return message;
    }

    @Override
    public String getBotPath() {
        String webhookUrl = "";
        try {
            webhookUrl =  getWebhookInfo().getUrl();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return webhookUrl;
    }

}
