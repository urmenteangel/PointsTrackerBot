package com.SoleraBootcamp4.PointsTrackerBot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class PointTrackerBot extends TelegramLongPollingBot {

    private PointsTrackerService pointsTrackerService;

    private final String groupId = "-1001722891281";
    // private final String soleraGroupId = "-1001561970415";

    public void setService(PointsTrackerService pointsTrackerService) {
        this.pointsTrackerService = pointsTrackerService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message receivedMessage = update.getMessage();
        String chatId = receivedMessage.getChatId().toString();

        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if(receivedMessage.isGroupMessage()){
                if (chatId.equals(groupId)) {
                    if (receivedMessage.getText().equals("/scoreboard")
                            || receivedMessage.getText().equals("/scoreboard@" + getBotUsername())) {
                        message.setText(pointsTrackerService.getScoreboardMessage());
                    }
                } else {
                    message.setText("Sorry, this bot only works in certain groups.");
                }
            } else {
                message.setText("Sorry " + receivedMessage.getFrom().getUserName() +", this bot only works in groups.");
            }
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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

}
