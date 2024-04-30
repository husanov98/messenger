package uz.mh.messenger.service;

import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.client.SimpleTelegramClientFactory;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi;
import org.springframework.stereotype.Service;
import uz.mh.messenger.config.ExampleApp;
import uz.mh.messenger.config.TdLibConfig;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.enums.StatementStatus;
import uz.mh.messenger.mapper.StatementMapper;
import uz.mh.messenger.model.Manager;
import uz.mh.messenger.repository.ManagerRepository;
import uz.mh.messenger.repository.StatementRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GetNotificationService {
    private final StatementRepository statementRepository;
    private final TdLibConfig config;
    private final StatementService statementService;


    public GetNotificationService(StatementRepository statementRepository, TdLibConfig config,StatementService statementService) {
        this.statementRepository = statementRepository;
        this.config = config;
        this.statementService = statementService;
    }
    public void getNotifications() throws Exception{
        Init.init();
        Log.setLogMessageHandler(1,new Slf4JLogMessageHandler());
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()){
            TDLibSettings settings = config.getTDLibSettings("+998933899112");
            SimpleTelegramClientBuilder clientBuilder = config.getBuilder(clientFactory, settings);
            AuthenticationSupplier user = config.getCurrentUser(settings, "+998933899112", clientFactory);
            try(ExampleApp app = new ExampleApp(clientBuilder,user,0)) {
                try {
                    TdApi.User me = app.getClient().getMeAsync().get(1, TimeUnit.MINUTES);
                    System.out.println(me.firstName);
//                    statementRepository.finByCratedAt();

                    long chatId = -1001269019477L;
                    CompletableFuture<TdApi.Messages> send = app.getClient().send(new TdApi.GetChatHistory(chatId,0,0,10,true));
                    Thread.sleep(2000);
                    TdApi.Messages messages = send.get();

                    System.out.println("message count = " + messages.messages.length);
                    TdApi.MessageText messageText;
                    for (TdApi.Message message : messages.messages) {
                        System.out.println("===========================================");

                        System.out.println(message.id);
                        TdApi.MessageContent content = message.content;
                        messageText = (TdApi.MessageText) content;
//                        System.out.println("------------------------");
                        System.out.println(messageText.text.text);
//                        System.out.println("------------------------");

                        createNewMessage(messageText.text.text,message.id);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void createNewMessage(String text,Long messageId){
        StatementStatus status;
        if (text.contains("Запрос закрыт!")){
            status = StatementStatus.CLOSED;
        }else {
            status = StatementStatus.ACTUAL;
        }
        int startIndex = 0;
        int endIndex = 0;
        startIndex = text.indexOf("_") + 1;
        endIndex = text.indexOf(" ");
        String id = '#' + text.substring(startIndex,endIndex);
        System.out.println(id);
        System.out.println("======================================================");
        String newMessage = "";
        int fromIndex = text.indexOf("\n");
        int headIndex = text.indexOf("Контакты:");
        String substring = text.substring(headIndex);
        int tailIndex = substring.indexOf("\n");
        int lastIndex = headIndex + tailIndex;
        newMessage = text.substring(fromIndex,lastIndex);
        save(id,newMessage,messageId,status);
    }
    private void save(String id,String text,Long messageId,StatementStatus status){
        String phoneNumber = getPhoneNumber(text);
        StatementDto statementDto = new StatementDto();
        statementDto.setMessageId(messageId);
        statementDto.setStatementId(id);
        statementDto.setStatus(status);
        statementDto.setSentCount(0);
        statementDto.setGroupCount(0);
        statementDto.setText(text);
        statementDto.setPhoneNumber(phoneNumber);
        statementService.saveStatement(statementDto);
    }
    private String getPhoneNumber(String text){
        //am
        int length = "Контакты:  ".length();
        int startIndex;
        startIndex = text.indexOf("Контакты:  ");
        return text.substring(startIndex + length);
    }
}
