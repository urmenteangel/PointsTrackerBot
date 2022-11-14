package com.SoleraBootcamp4.PointsTrackerBot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class PointTrackerBot extends TelegramLongPollingBot {

    private final String groupId = "-1001722891281";
    //private final String soleraGroupId = "-1001561970415";

    @Override
    public void onUpdateReceived(Update update) {

        Message receivedMessage = update.getMessage();
        String chatId = receivedMessage.getChatId().toString();

        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if (chatId.equals(groupId)) {
                if (receivedMessage.getText().equals("/scoreboard")
                        || receivedMessage.getText().equals("/scoreboard@" + getBotUsername())) {
                    // Generate scoreboard
                    message.setText("Esta funcionalidad no está disponible.");
                    execute(message);

                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendWinnerMessage(String message) {
        SendMessage sendMessage = new SendMessage(groupId, message);
        try {
            execute(sendMessage);
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
