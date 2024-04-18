package uz.mh.messenger.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.mh.messenger.dto.StatementDto;
import uz.mh.messenger.enums.StatementStatus;
import uz.mh.messenger.model.Manager;
import uz.mh.messenger.repository.ManagerRepository;
import uz.mh.messenger.service.StatementService;

import java.util.Optional;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot {
    private final ManagerRepository managerRepository;
    private final StatementService statementService;

    public BotService(@Value("${bot.token}") String botToken, ManagerRepository managerRepository, StatementService statementService){
        super(botToken);
        this.managerRepository = managerRepository;
        this.statementService = statementService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long id = update.getMessage().getChat().getId();

            Message message = update.getMessage();
            String text = message.getText();
            createNewMessage(text);
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
        Optional<Manager> optionalManager = managerRepository.findByPhoneNumber(phoneNumber);
        if (optionalManager.isPresent()) {
            StatementDto statementDto = new StatementDto();
            statementDto.setStatementId(id);
            statementDto.setStatus(StatementStatus.ACTUAL);
            statementDto.setSentCount(0);
            statementDto.setGroupCount(0);
            statementDto.setText(text);
            statementDto.setPhoneNumber(phoneNumber);
            statementService.saveStatement(statementDto);
        }else {
            System.out.println("Bu bratan hali ro'yxatdan o'tmagan");
        }
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
