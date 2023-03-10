package BoteFx.service;

import BoteFx.model.Message;
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
import java.util.function.Consumer;

@Service
public class SocketService {
    private StompSession session;

    private String meinsToken;

    private WebSocketStompClient stompClient;

    public void socketConnect(String messageToken, Consumer<Message> liveMessage, Runnable fehlerAnzeige) {
        disconnect();

        List<Transport> transports = new ArrayList<>(2);

        StandardWebSocketClient defaultWebSocketClient = new StandardWebSocketClient();
        transports.add(new WebSocketTransport(defaultWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new AbstractMessageConverter() {
            private final ObjectMapper mapper = new ObjectMapper();

            @Override
            protected boolean supports(Class<?> aClass) { return aClass == Message.class; }
            @Override
            protected Object convertFromInternal(org.springframework.messaging.Message<?> message, Class<?> targetClass, Object conversionHint) {
                String textareaText = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Message messageObject = null;
                try {
                    messageObject = mapper.readValue(textareaText, Message.class);
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
        }); // Ende stompClient


        try {
            session = stompClient.connect("http://localhost:8080/register", new StompSessionHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return null;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {

                }

                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

                    //fehlerInfo("OK", "connectOk");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    exception.printStackTrace();

                    //fehlerInfo("FEHLER","connectNo");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    //exception.printStackTrace();
                    fehlerAnzeige.run();
                }
            }).get(10, TimeUnit.SECONDS);

            session.subscribe("/messages/receive/" + messageToken, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {

                    //fehlerInfo("OK", "sessionOk");
                    //liveMessageAusgabe("Verbindung aufgebaut!");
                }

                @Override
                public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void handleTransportError(StompSession stompSession, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders stompHeaders, Object object) {
                    Message message = (Message) object;

                    /**
                     * Nachricht Erhalten/Ausgeben*/

                    liveMessage.accept(message);
                    //System.out.println("Incoming text: " + message.getText());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void send(Message message) {
        session.send("/app/messages", message);
    }

    private void disconnect() {
        stopSession();
        stopStompClient();
    }

    private void stopStompClient() {
        if (stompClient == null) {
            return;
        }

        stompClient.stop();
        stompClient = null;
    }

    private void stopSession() {
        if (session == null) {
            return;
        }

        session.disconnect();
        session = null;
    }
}
