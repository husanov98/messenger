package uz.mh.messenger.config;

import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.jni.TdApi;

public class ExampleApp implements AutoCloseable{

    private final SimpleTelegramClient client;



    private final long adminId;

    public ExampleApp(SimpleTelegramClientBuilder clientBuilder,
                      AuthenticationSupplier<?> authenticationData,
                      long adminId){

        this.adminId = adminId;

        clientBuilder.addUpdateHandler(it.tdlight.jni.TdApi.UpdateAuthorizationState.class, this::onUpdateAuthorizationState);


        clientBuilder.addCommandHandler("stop", this::onStopCommand);


        this.client = clientBuilder.build(authenticationData);


    }


    @Override
    public void close() throws Exception {

        client.close();
    }

    public SimpleTelegramClient getClient() {
        return client;
    }

    private void onUpdateAuthorizationState(it.tdlight.jni.TdApi.UpdateAuthorizationState update){


        it.tdlight.jni.TdApi.AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof it.tdlight.jni.TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof it.tdlight.jni.TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        } else if (authorizationState instanceof it.tdlight.jni.TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateWaitCode) {
            System.out.println("enter code");
        } else if (authorizationState instanceof TdApi.AuthorizationStateWaitOtherDeviceConfirmation) {
            System.out.println("Sent code to other device");
        }else if (authorizationState instanceof TdApi.AuthorizationStateWaitPhoneNumber){
            System.out.println("tel nomer orqali kiryapman");
        }else if (authorizationState instanceof TdApi.AuthorizationStateWaitTdlibParameters){
            System.out.println("tdlib parameters");
        }

    }

    private void onStopCommand(TdApi.Chat chat, TdApi.MessageSender commandSender, String arguments) {
        if (isAdmin(commandSender)) {
            System.out.println("Received stop command. closing...");
            client.sendClose();
        }
    }

    public boolean isAdmin(TdApi.MessageSender sender) {
        if (sender instanceof TdApi.MessageSenderUser messageSenderUser) {
            return messageSenderUser.userId == adminId;
        } else {
            return false;
        }
    }

}
