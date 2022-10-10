package com.converter.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;

public class TelegramBot extends TelegramLongPollingBot {



    private final String fCsvPath = System.getProperty("user.dir") + "/data_sql.csv";
    private final String fCsvName = "data_sql.csv";

    private final String fJsonPath = System.getProperty("user.dir") + "/Критические_ошибки_по_топику_contingent_from_mes_to_emias.csv";
    private final String fJsonName = "Критические_ошибки_по_топику_contingent_from_mes_to_emias.csv";


    private long enterChatId;
    private DownloadFileFromTelegram downloadFileFromTelegram = new DownloadFileFromTelegram();
    private ConverterFromCSVToExcel converterFromCSVToExcel = new ConverterFromCSVToExcel();
    private ConverterFromJsonToExcel csvJSonToExcelConverter = new ConverterFromJsonToExcel();

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (update.hasMessage()) {

            enterChatId = message.getChatId();

            if (enterChatId == chat && message.getDocument().getFileName().equalsIgnoreCase(fCsvName)) {

                String fileId = message.getDocument().getFileId();

                downloadFileFromTelegram.uploadFile(fCsvPath, fileId, token);

                File converter = converterFromCSVToExcel.converter();

                sendMessageFile(converter);
            }
            if (enterChatId == chat && message.getDocument().getFileName().equalsIgnoreCase(fJsonName)) {

                String fileId = message.getDocument().getFileId();

                downloadFileFromTelegram.uploadFile(fJsonPath, fileId, token);

                File converter = csvJSonToExcelConverter.convertedJson();

                sendMessageFile(converter);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void sendMessageFile(File file) {
        try {
            execute(new SendDocument().setChatId(chat).setDocument(file).setParseMode("HTML"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
