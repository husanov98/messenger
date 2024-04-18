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
import uz.mh.messenger.model.ApiResponse;
import uz.mh.messenger.model.Group;
import uz.mh.messenger.model.Manager;
import uz.mh.messenger.repository.ManagerRepository;
import uz.mh.messenger.response.MessageResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TdlightService implements Runnable{
    private String phoneNumber;
    private String text;
    private String db;

    private final TdLibConfig config;
    private final ManagerRepository managerRepository;

    public TdlightService(String phoneNumber, String text, String db,TdLibConfig config,ManagerRepository managerRepository){
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.db = db;
        this.config = config;
        this.managerRepository = managerRepository;
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
                    Thread.sleep(500);
                    TdApi.Chats chats = app.getClient().send(getChats).join();
                    for (long chatId : chats.chatIds) {
                        TdApi.GetChat getChat = new TdApi.GetChat(chatId);
                        CompletableFuture<TdApi.Chat> send = app.getClient().send(getChat);
                        TdApi.Chat chat = send.join();
                        System.out.println(chat.title + ", " + chat.id + "\n");
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


    public int sendMessageToGroup(String phoneNumber, String htmlText,String db) throws Exception {

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
                TDLibSettings settings = config.getTDLibSettings(phoneNumber);
                SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
                AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);
                try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {
                    for (Long id : Idlar.superGroupIds) {
                        s++;
                        System.out.println(Thread.currentThread().getName());
                        System.out.println(s);
                        try {
                            TdApi.SendMessage messageToGroup = new TdApi.SendMessage();
                            TdApi.ParseTextEntities textEntities = config.parseModeHtml(htmlText);
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
                }catch (Exception e){
                    e.printStackTrace();
                }


            System.out.println("------------------------------");
        }
        long end = System.currentTimeMillis();
        long duration = (end - begin) / 1000;
        System.out.println(duration + " seconds, " + (duration % 1000) + " milliseconds");
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

//                    System.out.println("uxladi");
                    Thread.sleep(2000);

//                    setCode(parol);
//                    String code1 = getCode();
                    Thread.sleep(1000);
                    TdApi.CheckAuthenticationCode authenticationCode = new TdApi.CheckAuthenticationCode();
                    authenticationCode.code = parol;

                    Thread.sleep(2000);
                    CompletableFuture<TdApi.Ok> request = app.getClient().send(authenticationCode);
                    TdApi.Ok result = request.get();
                    apiResponse.setCode(200);
                    Manager manager = new Manager(phoneNumber);
                    managerRepository.save(manager);
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
//    asab
    public ApiResponse addCurrentAccountToGroups(String mainPhoneNumber,String virtualPhoneNumber) throws Exception{
        Init.init();
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());
        ApiResponse apiResponse = new ApiResponse();
        int count = 0;
        try(SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            TDLibSettings settings = config.getTDLibSettings(mainPhoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, mainPhoneNumber, clientFactory);
            try (ExampleApp app = new ExampleApp(clientBuilder,user,0)){
                long addingUserId = addingUserId(virtualPhoneNumber);
                for (Long superGroupId : Idlar.superGroupIds) {
                    try {
                        TdApi.AddChatMember addChatMember = new TdApi.AddChatMember(superGroupId,addingUserId,100);
                        CompletableFuture<TdApi.Ok> send = app.getClient().send(addChatMember);
                        TdApi.Ok ok = send.get();
                        count++;
                    }catch (Exception e){
                        TelegramError error = (TelegramError) e.getCause();
                        System.out.println(error.getMessage());
                        System.out.println("could not be added!");
                    }

                }
                apiResponse.setCode(200);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        System.out.println(count);
        return apiResponse;
    }
    private long addingUserId(String virtualPhoneNumber) throws Exception{
        Init.init();
        try(SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
            TDLibSettings settings = config.getTDLibSettings(virtualPhoneNumber);
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, virtualPhoneNumber, clientFactory);
            try (ExampleApp app = new ExampleApp(clientBuilder,user,0)){
                TdApi.User me = app.getClient().getMeAsync().get(1, TimeUnit.MINUTES);
                long id = me.id;
                return id;
            }
        }
    }

    @Override
    public void run() {
        try {
            sendMessageToGroup(phoneNumber,text,db);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}







