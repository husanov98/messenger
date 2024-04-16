package uz.mh.messenger.config;

import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
@Component
public class TdLibConfig {

    public TdApi.ParseTextEntities parseModeHtml(String htmlText){
        TdApi.TextParseModeHTML parseModeHTML = new TdApi.TextParseModeHTML();
        return new TdApi.ParseTextEntities(htmlText,parseModeHTML);
    }

    public AuthenticationSupplier getCurrentUser(TDLibSettings settings, String phoneNumber, SimpleTelegramClientFactory clientFactory){
        SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);
        SimpleAuthenticationSupplier<?> user = AuthenticationSupplier.user(phoneNumber);
        return  user;
    }

    public final SimpleTelegramClientBuilder getBuilder(SimpleTelegramClientFactory clientFactory,TDLibSettings settings){
        SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);
        return clientBuilder;
    }

    public TDLibSettings getTDLibSettings(String phoneNumber){
        APIToken apiToken = new APIToken(23036764, "9e9c556cd961599f5031fdbf0829b8e4");

        TDLibSettings settings = TDLibSettings.create(apiToken);

        Path sessionPath = Paths.get("tdlight-session" + "-" + phoneNumber.substring(1));
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));
        return settings;
    }
}
