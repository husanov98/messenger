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

    public TdlightService(String phoneNumber, String text, String db,TdLibConfig config){
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.db = db;
        this.config = config;
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
        List<Group> groups = new ArrayList<>();
        {
            groups.add(new Group("Logistika.uz\uD83C\uDDFA\uD83C\uDDFFРЕФ",-1001487366957L));
            groups.add(new Group("МИР ГРУЗОПЕРЕВОЗОК",-1001297162006L));
            groups.add(new Group("ГРУЗОПЕРЕВОЗКИ \uD83D\uDCE6 ПОИСК ГРУЗА\uD83D\uDD0EПОИСК ТРАНСПОРТА \uD83D\uDE9A ЛОГИСТИКА",-1001185877958L));
            groups.add(new Group("Yuk UZB",-1001584609714L));
            groups.add(new Group(" ,,РОССИЯ УЗБЕКИСТАН УЗБЕКИСТАН РОССИЯ''",-1001138127394L));
            groups.add(new Group("YUK MARKAZI",-1001210236379L));
            groups.add(new Group("Грузоперевозки КАЗАХСТАН",-1001731181261L));
            groups.add(new Group("BarakaYuklari",-1001351394674L));
            groups.add(new Group(" ЛАГиТ - перевозки \uD83D\uDE9B \uD83D\uDE9A \uD83D\uDEA2 ✈️\uD83D\uDE9C",-1001307336546L));
            groups.add(new Group("Yuk\uD83C\uDDFA\uD83C\uDDFF Petak\uD83C\uDDFA\uD83C\uDDFF\uD83D\uDE9A\uD83D\uDE9B\uD83D\uDEA6",-1001427357476L));
            groups.add(new Group("\uD83C\uDF0EТРАНСПОРТ ГРУЗЫ ЛОГИСТИКА\uD83C\uDF0D",-1001120006387L));
            groups.add(new Group("Yuk markazi |\uD83C\uDDFA\uD83C\uDDFF| Yuk bor",-1001394512469L));
            groups.add(new Group("Перевозки OCAS.PL Евразия",-1001350790526L));
            groups.add(new Group("DEGER PROJECT LOGISTICS",-1001323699723L));
            groups.add(new Group("Yuk tashish xizmati\uD83D\uDE9B\uD83D\uDE9A",-1001384066217L));
            groups.add(new Group("\uD83C\uDDFA\uD83C\uDDFF Logistika super UZB \uD83C\uDDFA\uD83C\uDDFF",-1001158469042L));
            groups.add(new Group("TRUCKERS",-1001183422845L));
            groups.add(new Group("ЛОГИСТИКА мира Грузоперевозки",-1001429263731L));
            groups.add(new Group("Yuk markazi \uD83C\uDF10 Gruppa",-1001667902377L));
            groups.add(new Group("VseGruzy Грузоперевозки Груза Логистика Грузоперевозки по миру",-1001484194817L));
            groups.add(new Group("",-1001328104399L));
            groups.add(new Group("",-1001348663230L));
            groups.add(new Group("",-1001234214428L));
            groups.add(new Group("",-1001397241850L));
            groups.add(new Group("",-1001478142288L));
            groups.add(new Group("",-1001467738475L));
            groups.add(new Group("",-1001266224966L));
            groups.add(new Group("",-1001457886378L));
            groups.add(new Group("",-1001134933755L));
            groups.add(new Group("",-1001309087139L));
            groups.add(new Group("",-1001421793061L));
            groups.add(new Group("",-1001293476197L));
            groups.add(new Group("",-1001428317095L));
            groups.add(new Group("",-1001059051836L));
            groups.add(new Group("",-1001500268528L));
            groups.add(new Group("",-1001384382030L));
            groups.add(new Group("",-1001409525633L));
            groups.add(new Group("",-1001468135171L));
            groups.add(new Group("",-1001458177815L));
            groups.add(new Group("",-1001883268304L));
            groups.add(new Group("",-1001769852300L));
            groups.add(new Group("",-1001480484920L));
            groups.add(new Group("",-1001423580746L));
            groups.add(new Group("",-1001229767857L));
            groups.add(new Group("",-1001235383010L));
            groups.add(new Group("",-1001495752146L));
            groups.add(new Group("",-1001401190184L));
            groups.add(new Group("",-1001351153112L));
            groups.add(new Group("",-1001654579537L));
            groups.add(new Group("",-1001003905469L));
            groups.add(new Group("",-1001224639865L));
            groups.add(new Group("",-1001448387476L));
            groups.add(new Group("",-1001393204560L));
            groups.add(new Group("",-1001450848210L));
            groups.add(new Group("",-1001347320518L));
            groups.add(new Group("",-1001372087764L));
            groups.add(new Group("",-1001253466056L));
            groups.add(new Group("",-1001458388263L));
            groups.add(new Group("",-1001140851535L));
            groups.add(new Group("",-1001331316708L));
            groups.add(new Group("",-1001185801766L));
            groups.add(new Group("",-1001101869964L));
            groups.add(new Group("",-1001459968768L));
            groups.add(new Group("",-1001095025602L));
            groups.add(new Group("",-1001141647843L));
            groups.add(new Group("",-1001701008155L));
            groups.add(new Group("",-1001207336741L));
            groups.add(new Group("",-1001493062918L));
            groups.add(new Group("",-1001268122187L));
            groups.add(new Group("",-1001680796797L));
            groups.add(new Group("",-1001151684062L));
            groups.add(new Group("",-1001405473886L));
            groups.add(new Group("",-1001200466918L));
            groups.add(new Group("",-1001331954409L));
            groups.add(new Group("",-1001548162092L));
            groups.add(new Group("",-1001637660992L));
            groups.add(new Group("",-1001232773600L));
            groups.add(new Group("",-1001359437585L));
            groups.add(new Group("",-1001304090674L));
            groups.add(new Group("",-1001531247478L));
            groups.add(new Group("",-1001055138795L));
            groups.add(new Group("",-1001244407059L));
        }

            s++;
            System.out.println(s);
            try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {
                TDLibSettings settings = config.getTDLibSettings(phoneNumber);
                SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
                AuthenticationSupplier user = config.getCurrentUser(settings, phoneNumber, clientFactory);
                try (ExampleApp app = new ExampleApp(clientBuilder, user, 0)) {
                    for (Group group : groups) {
                        s++;
                        System.out.println(Thread.currentThread().getName());
                        System.out.println(s);
                        try {
                            TdApi.SendMessage messageToGroup = new TdApi.SendMessage();
                            TdApi.ParseTextEntities textEntities = config.parseModeHtml(htmlText);
                            TdApi.Message sentMessage;
                            messageToGroup.chatId = group.getChatId();
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

                    System.out.println("uxladi");
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







