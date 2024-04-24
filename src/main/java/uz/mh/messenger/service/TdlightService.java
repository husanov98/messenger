package uz.mh.messenger.service;

import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import org.springframework.stereotype.Service;
import uz.mh.messenger.config.ExampleApp;
import uz.mh.messenger.config.TdLibConfig;
import uz.mh.messenger.db.Idlar;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.dto.TgmData;
import uz.mh.messenger.model.ApiResponse;
import uz.mh.messenger.model.Manager;
import uz.mh.messenger.repository.ManagerRepository;
import uz.mh.messenger.repository.StatementRepository;
import uz.mh.messenger.response.MessageResponse;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TdlightService implements Runnable{

    private final TdLibConfig config;
    private final ManagerRepository managerRepository;
    private final StatementDto statementDto;
    private final IntegrateWithEgs egs;
    private final StatementRepository statementRepository;


    public TdlightService(TdLibConfig config, ManagerRepository managerRepository, StatementDto statementDto, IntegrateWithEgs egs, StatementRepository statementRepository){
        this.config = config;
        this.managerRepository = managerRepository;
        this.statementDto = statementDto;
        this.egs = egs;

        this.statementRepository = statementRepository;
    }


    public ApiResponse getChats(String phoneNumber) throws Exception{
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            TDLibSettings settings = config.getTDLibSettings(phoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);
            try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {

                try {

                    TdApi.GetChats getChats = new TdApi.GetChats(null,200);
//                    Thread.sleep(500);
                    TdApi.Chats chats = app.getClient().send(getChats).join();
                    for (long chatId : chats.chatIds) {
                        TdApi.GetChat getChat = new TdApi.GetChat(chatId);
                        CompletableFuture<TdApi.Chat> send = app.getClient().send(getChat);
                        TdApi.Chat chat = send.join();
                        System.out.println(chat.title + ", " + chat.id + "\n");
                        if (chat.id == -1001269019477L) {
                            break;
                        }
                    }

                    apiResponse.setCode(200);
                }catch (Exception e){
                    TelegramError error = (TelegramError) e.getCause();

                    int errorCode = error.getErrorCode();
                    apiResponse.setError(e.getMessage());
                    apiResponse.setSuccess(false);
                    apiResponse.setCode(errorCode);
                    return apiResponse;
                }
            }
        }
        return apiResponse;
    }

    public ApiResponse deleteMessage(String messageId, String chatId, String phoneNumber, String db) throws Exception{
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            TDLibSettings settings = config.getTDLibSettings(phoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);
            try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {

                try {

                    TdApi.DeleteMessages deleteMessages = new TdApi.DeleteMessages();
                    deleteMessages.chatId = Long.parseLong(chatId);
                    deleteMessages.messageIds = new long[]{Long.parseLong(messageId)};
                    deleteMessages.revoke = true;

                    Thread.sleep(500);
                    TdApi.Ok send = app.getClient().send(deleteMessages).join();

                    apiResponse.setCode(200);
                }catch (Exception e){
                    TelegramError error = (TelegramError) e.getCause();

                    int errorCode = error.getErrorCode();
                    apiResponse.setError(e.getMessage());
                    apiResponse.setSuccess(false);
                    apiResponse.setCode(errorCode);
                    return apiResponse;
                }
            }
        }

        return apiResponse;
    }


    public ApiResponse updateMessage(String newText, String messageId, String chatId, String phoneNumber,String db)throws Exception{

        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();
        MessageResponse messageResponse = new MessageResponse();
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {


            TDLibSettings settings = config.getTDLibSettings(phoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);

            try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {
                try {
                    TdApi.ParseTextEntities textEntities = config.parseModeHtml(newText);
                    TdApi.EditMessageText editableText = new TdApi.EditMessageText();
                    editableText.chatId = Long.parseLong(chatId);
                    editableText.messageId = Long.parseLong(messageId);
                    TdApi.InputMessageText messageText = new TdApi.InputMessageText();
                    messageText.text = app.getClient().send(textEntities).join();
                    editableText.inputMessageContent = messageText;
                    TdApi.Message join = app.getClient().send(editableText).join();
                    messageResponse.setMessageId(messageId);
                    apiResponse.setCode(200);
                    apiResponse.setContent(messageResponse);
                }catch (Exception e){
                    TelegramError error = (TelegramError) e.getCause();
                    int errorCode = error.getErrorCode();
                    apiResponse.setError(e.getMessage());
                    apiResponse.setSuccess(false);
                    apiResponse.setCode(errorCode);
                    return apiResponse;
                }
            }
        }
        return apiResponse;
    }


    public int sendMessageToGroup(StatementDto statementDto) throws Exception {

        Init.init();

        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();
        MessageResponse messageResponse = new MessageResponse();
        long begin = System.currentTimeMillis();
        int count = 0;
        int s = 0;

            s++;
            System.out.println(s);
            try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
                TDLibSettings settings = config.getTDLibSettings(statementDto.getPhoneNumber());
                SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
                AuthenticationSupplier user = config.getCurrentUser(settings, statementDto.getPhoneNumber(), clientFactory);
                try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {
                    for (Long id : Idlar.superGroupIds) {
                        s++;
                        System.out.println(Thread.currentThread().getName());
                        System.out.println(s);
                        try {
                            TdApi.SendMessage messageToGroup = new TdApi.SendMessage();
                            TdApi.ParseTextEntities textEntities = config.parseModeHtml(statementDto.getStatementId() + statementDto.getText());
                            TdApi.Message sentMessage;
                            messageToGroup.chatId = id;
                            TdApi.InputMessageText text = new TdApi.InputMessageText();
                            text.text = app.getClient().send(textEntities).join();
                            messageToGroup.inputMessageContent = text;
                            try {
                                sentMessage = app.getClient().sendMessage(messageToGroup, true).join();
                                System.out.println("ketti");
                                count++;
                            } catch (Exception e) {
                                System.out.println("birinchisida jo'nata olmadi");
                                Thread.sleep(500);
                                sentMessage = app.getClient().sendMessage(messageToGroup, true).join();
                            }
                            messageResponse.setMessageId(String.valueOf(sentMessage.id));
                            apiResponse.setContent(messageResponse);
                            apiResponse.setCode(200);
                        } catch (Exception e) {

                            TelegramError error = (TelegramError) e.getCause();
                            int errorCode = error.getErrorCode();
                            apiResponse.setError(e.getMessage());
                            apiResponse.setSuccess(false);
                            apiResponse.setCode(errorCode);
                            System.out.println(e.getMessage());
                        }

                    }
                    statementRepository.updateStatementUpdatedAt(statementDto.getStatementId());
                }catch (Exception e){
                    e.printStackTrace();
                }


            System.out.println("------------------------------");
        }
        long end = System.currentTimeMillis();
        long duration = (end - begin) / 1000;
        System.out.println(duration + " seconds, " + (duration % 1000) + " milliseconds");
        sendToEgs(statementDto,count);
        return count;
    }


    public ApiResponse enterAuthenticationCode(String phoneNumber, String parol) throws Exception {

        Init.init();

        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        ApiResponse apiResponse = new ApiResponse();
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            TDLibSettings settings = config.getTDLibSettings(phoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);


            try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {

                try {
                    Thread.sleep(2000);
                    Thread.sleep(1000);
                    TdApi.CheckAuthenticationCode authenticationCode = new TdApi.CheckAuthenticationCode();
                    authenticationCode.code = parol;

                    Thread.sleep(2000);
                    CompletableFuture<TdApi.Ok> request = app.getClient().send(authenticationCode);
                    TdApi.Ok result = request.get();
                    apiResponse.setCode(200);
                    Manager manager = new Manager(phoneNumber);
                    Optional<Manager> managerOptional = managerRepository.findByPhoneNumber(phoneNumber);
                    if (managerOptional.isEmpty()) {
                        managerRepository.save(manager);
                    }
                }catch (Exception e){
                    TelegramError error = (TelegramError) e.getCause();
                    int errorCode = error.getErrorCode();
                    apiResponse.setError(e.getMessage());
                    apiResponse.setSuccess(false);
                    apiResponse.setCode(errorCode);
                    return apiResponse;
                }
            }
        }
        return apiResponse;
    }


    public ApiResponse registerwithPhoneNumber(String phoneNumber) throws Exception {

        Init.init();

        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();

        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {

            TDLibSettings settings = config.getTDLibSettings(phoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);
            try(ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {
                try {
                    TdApi.PhoneNumberAuthenticationSettings phoneNumberAuthenticationSettings = new TdApi.PhoneNumberAuthenticationSettings();
                    TdApi.SetAuthenticationPhoneNumber phoneNumber1 = new TdApi.SetAuthenticationPhoneNumber(phoneNumber, phoneNumberAuthenticationSettings);
                    Thread.sleep(6000);

                    CompletableFuture<TdApi.Ok> send = app.getClient().send(phoneNumber1);
                    TdApi.Ok ok = send.get();
                    apiResponse.setCode(200);
                }catch (Exception e){
                    TelegramError error = (TelegramError) e.getCause();
                    int errorCode = error.getErrorCode();
                    apiResponse.setError(e.getMessage());
                    apiResponse.setSuccess(false);
                    apiResponse.setCode(errorCode);
                    return apiResponse;
                }
            }
        }
        return apiResponse;
    }


    @Override
    public void run() {
        try {
            sendMessageToGroup(statementDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendToEgs(StatementDto statementDto,int count){
        Integer groupCount = statementDto.getGroupCount();
        if (groupCount < count){
            statementDto.setGroupCount(count);
        }
        statementDto.setSentCount(statementDto.getSentCount() + count);
        TgmData data = new TgmData(statementDto.getStatementId().substring(1),statementDto.getGroupCount(),statementDto.getSentCount());
        List<TgmData> dataList = List.of(data);
        egs.sendToEgs(dataList,"egs");
    }

}







