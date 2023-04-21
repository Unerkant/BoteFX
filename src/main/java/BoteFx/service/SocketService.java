package BoteFx.service;


import BoteFx.model.Message;
import BoteFx.utilities.SocketResponseHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Den 24.03.2023
 */

@Service
public class SocketService {

    private WebSocketStompClient stompClient;
    private StompSession session;

    /**
     * connect wird in ChatBoxController Zeile: 94 (initialize) gestartet
     *
     * neue Nachricht als responseHandler, Zeile: 134 an den MessageController Zeile: ~455
     * über den ChatBoxController Zeile: 255  weiterleiten...
     *
     * message senden, Zeile: 205
     */
    public void connect(String token, SocketResponseHandler responseHandler){

        if (session != null){
            System.out.println("Session schon gesetzt");
            return;
        }

        /**
         * ...
         */
        if (stompClient == null){
            stompClient = createNewStompClient();
        }

        try {
            session =  stompClient.connect("http://localhost:8080/register", new StompSessionHandler() {

                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    System.out.println("Verbindung zum Server besteht ");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.out.println("Error:" + exception.getMessage());
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.out.println("Transport Error: " + exception.getMessage());
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return null;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    // Leer
                }
            }).get(10000, TimeUnit.MILLISECONDS);
            session.subscribe("/messages/receive/" + token, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    responseHandler.afterConnectionEstablished(session);
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    responseHandler.handleError(exception);
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    responseHandler.handleTransportError(exception);
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Message message = (Message) payload;

                    /**
                     * Neue Message anzeigen
                     * bei eingehend die neue Nachricht wird den message-array als responseHandler an den
                     * ChatBoxController, Zeile: 94 zurückgesendet als this: socketService.connect(meinToken, this);
                     * und schliesslich weiter an den MessageController gesendet, Zeile: 244
                     * public void handleNewMessage(Message message)....
                     *
                     */

                    responseHandler.handleNewMessage(message);
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler: " + e);
        }

    } // Ende Connect


    /**
     *
     * @return
     */
    public WebSocketStompClient createNewStompClient() {
        List<Transport> transports = new ArrayList<>();

        StandardWebSocketClient defaultWebSocketClient = new StandardWebSocketClient();
        transports.add(new WebSocketTransport(defaultWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        registerMessageConverter(stompClient);

        return stompClient;
    }

    /**
     * ???
     *
     * @param stompClient
     */
    private void registerMessageConverter(WebSocketStompClient stompClient) {
        stompClient.setMessageConverter(new AbstractMessageConverter() {
            private final ObjectMapper mapper = new ObjectMapper();

            @Override
            protected boolean supports(Class<?> aClass) { return aClass == Message.class; }
            @Override
            protected Object convertFromInternal(org.springframework.messaging.Message<?> message, Class<?> targetClass, Object conversionHint) {
                String jsonMessage = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Message messageObject = null;
                try {
                    messageObject = mapper.readValue(jsonMessage, Message.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return messageObject;
            }

            @Override
            protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
                try {
                    return mapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                return super.convertToInternal(payload, headers, conversionHint);
            }
        });
    }



    /**
     * Message Senden,
     *
     *
     * start in MessageController Zeile:  ca. 500
     *
     * @param message
     */
    public void senden(Message message){

        session.send("/app/messages", message);
    }

}
