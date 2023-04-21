package BoteFx.utilities;

import BoteFx.model.Message;
import org.springframework.messaging.simp.stomp.StompSession;

public interface SocketResponseHandler {

    void handleError(Throwable throwable);
    void handleTransportError(Throwable throwable);
    void handleNewMessage(Message message);
    void afterConnectionEstablished(StompSession session);

}
