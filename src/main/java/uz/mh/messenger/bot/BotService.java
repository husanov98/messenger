package uz.mh.messenger.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.enums.StatementStatus;
import uz.mh.messenger.service.GroupService;
import uz.mh.messenger.service.StatementService;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot {
    private final GroupService groupService;
    private final StatementService statementService;

    public BotService(@Value("${bot.token}") String botToken, GroupService groupService, StatementService statementService){
        super(botToken);
        this.groupService = groupService;
        this.statementService = statementService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long id = update.getMessage().getChat().getId();

            Message message = update.getMessage();
            String text = message.getText();
            String newMessage = createNewMessage(text);
            System.out.println(newMessage);
//            try {
//                System.out.println(newMessage);
//                for (int i = 0; i < 5; i++) {
//                    Thread thread = new Thread(() -> {
//                        try {
//                            groupService.sendMessageToGroup("998946604481",newMessage,"egs");
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                    thread.start();
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    private String createNewMessage(String message){
        int startIndex = 0;
        int endIndex = 0;
        startIndex = message.indexOf("_") + 1;
        endIndex = message.indexOf(" ");
        String id = '#' + message.substring(startIndex,endIndex);
        String newMessage = "";
        int fromIndex = message.indexOf("\n");
        int lastIndex = message.indexOf("@") - 1;
        newMessage = message.substring(fromIndex,lastIndex);
        save(id,newMessage);
        return id + newMessage;
    }

    private void save(String id,String text){

        String phoneNumber = getPhoneNumber(text);

        StatementDto statementDto = new StatementDto();
        statementDto.setStatementId(id);
        statementDto.setStatus(StatementStatus.ACTUAL);
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

    @Override
    public String getBotUsername() {
        return "@news_chat_b7_bot";
    }
}
