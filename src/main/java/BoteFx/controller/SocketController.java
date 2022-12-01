package BoteFx.controller;

import BoteFx.service.ConfigService;
import BoteFx.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  Denn 5.5.2022
 */

@Component
public class SocketController {
    @FXML private Label       textAusgabe;
    @FXML private Label       fehlerAusgabe;
    @FXML private TextField   messageTokenField;
    @FXML private TextField   messageTextField;
    @FXML private TextField   meinTokenField;

    private StompSession session;

    @FXML
    public void initialize() {
        //System.out.println("init");
    }

    // Socket Verbindung
    public void socketConnect() {

        // Message Token safari+edge: 07052022140854
        // safari Token: 07052022140725
        String value = messageTokenField.getText();
        String messageToken = messageTokenField.getText();

        List<Transport> transports = new ArrayList<>(2);

        StandardWebSocketClient defaultWebSocketClient = new StandardWebSocketClient();
        transports.add(new WebSocketTransport(defaultWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new AbstractMessageConverter() {
            private final ObjectMapper mapper = new ObjectMapper();

            @Override
            protected boolean supports(Class<?> aClass) { return aClass == Message.class; }
            @Override
            protected Object convertFromInternal(org.springframework.messaging.Message<?> message, Class<?> targetClass, Object conversionHint) {
                String messageText = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
                Message messageObject = null;
                try {
                    messageObject = mapper.readValue(messageText, Message.class);
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
                    System.out.println("connection successfully established");
                    updateOutput("Verbindung aufgebaut!");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    exception.printStackTrace();
                    System.out.println("connection not successfully established");
                    fehlerAusgabe.setText("connection not successfully established");
                    //updateOutput("Fehler!");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    exception.printStackTrace();
                }
            }).get(10, TimeUnit.SECONDS);

            session.subscribe("/messages/receive/" + messageToken, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
                    System.out.println("connection successfully established");
                    updateOutput("Verbindung aufgebaut!");
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
                    System.out.println("Incoming text " + message.getText());

                    updateOutput("Nachricht erhalten: " + message.getText());
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(String.format("Connection id: %s", value));

    } // Ende socketConnect


    // Message Senden
    public void sendMessage() {
        if (session == null) {
            fehlerAusgabe.setText("Es besteht keine Verbindung!");  //outputLabel
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

        Message message = new Message();
        message.setMessagetoken(messageTokenField.getText());
        message.setMeintoken(meinTokenField.getText());
        message.setText(messageTextField.getText());
        message.setDatum(format.format(new Date()));
        message.setRole("default");
        message.setName("Client");
        message.setVorname("Java");
        message.setPseudonym("Java Client");

        session.send("/app/messages", message);

        textAusgabe.setText("Gesendet: " + messageTextField.getText());
        messageTextField.clear(); // messageField
    }

    // Texten Ausgeben
    private void updateOutput(String text) {
        Platform.runLater(() -> {
            textAusgabe.setText(text); // outputLabel
        });
    }

}