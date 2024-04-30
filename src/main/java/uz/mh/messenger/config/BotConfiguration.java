//package uz.mh.messenger.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//import uz.mh.messenger.bot.BotService;
//
//
//@Slf4j
//@Configuration
//public class BotConfiguration {
//    @Bean
//    public TelegramBotsApi telegramBotsApi(BotService bot)throws TelegramApiException{
//        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
//        api.registerBot(bot);
//        return api;
//    }
//    @Bean
//    public String myString(){
//        return "";
//    }
//}
