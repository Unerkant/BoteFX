package BoteFx.service;


import BoteFx.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Den 24.03.2023
 */
@Service
public class SocketService {

    @Autowired
    private LanguageService languageService;
    @Autowired
    private LayoutService layoutService;

    @Value("${messageServer.address.pattern}")
    private String serverBaseAdressPattern;

    @Value("${messageServer.address}")
    private String serverBaseAddress;

    @Value("${messageServer.port}")
    private String serverPort;

    @Value("${messageServer.registerPath}")
    private String registerPath;

    @Value("${messageServer.channelPath}")
    private String channelPath;

    @Value("${messageServer.connectingTimeout:10}")
    private int serverConnectingTimeout;

    @Value("${messageServer.messageSendingEndPoint}")
    private String mesaageSendingEndpoint;

    private WebSocketStompClient stompClient;
    private StompSession session;


    /**
     * Setter&Getter
     * Rechte StackPane: gedacht für die globalen Pop-up-Fenster anzeige,
     * z.b.s Fehler oder Info
     */
    private StackPane rechtensPane;
    public StackPane getRechtensPane() { return rechtensPane; }
    public void setRechtensPane(StackPane rechtenspane) { this.rechtensPane = rechtenspane; }

    /**
     *  messageVBox: für die Live Message Ausgabe
     */
    private VBox messageVBOX;
    public VBox getMessageVBOX() { return messageVBOX; }
    public void setMessageVBOX(VBox messageVBOX) { this.messageVBOX = messageVBOX; }

    /**
     * Automatische scroll nach oben, immer akktuelle Message anzeigen
     */
    private ScrollPane messageScrollPanes;
    public ScrollPane getMessageScrollPanes() { return messageScrollPanes; }
    public void setMessageScrollPanes(ScrollPane messageScrollPanes) { this.messageScrollPanes = messageScrollPanes; }


    /**
     * gestartet in ChatBoxController Zeile: 90 (initialize)
     */
    public void connect(String token){

        if (session != null){
            System.out.println("Session schon gesetzt");
            return;
        }

        /**
         *
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
                    System.out.println("Session ok: " + session.getSessionId());
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    System.out.println("Session Error: " + exception.getMessage());
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.out.println("Session Transpor Error: " + exception.getMessage() );
                }

                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    Message message = (Message) payload;

                    // ****************  Live Message Ausgeben ********************

                    /**
                     *  den message-array möchte ich in MessageController haben...
                     *  die liveAusgabe-Methode in MessageController kannst du als 'liveMessage' anlegen,
                     *  da möchte ich automatisch den array 'message' bekommen
                     *
                     *  Viel Spaß bei Basteln
                     */
                    //liveMessage(message);
                    //System.out.println("Message angekommen an: " + message);
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
     * Message empfangen und weiter leiten
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
     * Start in MessageController Zeile:  ca. 500
     *
     * @param message
     */
    public void senden(Message message){

        session.send("/app/messages", message);
    }



    /**
     * Live-Chat, start hier Zeile: 180
     *
     *  //@param messages
     */
/*    private void liveMessage(Message messages){
        //Label liveAusgabe = new Label();
        //liveAusgabe.setStyle("-fx-border-color: red;");
        Parent liveAusgabe;
        Map<String, Object> idObject;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/fragments/messagecell.fxml"));
            liveAusgabe = loader.load();
            idObject = loader.getNamespace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Node-id aus dem messagecell.fxml holen
        Label liveUserBild = (Label)idObject.get("mesageUserBild");
        Label liveUserName = (Label)idObject.get("mesageUserName");
        Text liveMesageText = (Text)idObject.get("mesageTextPane");
        Label liveBilder = (Label)idObject.get("mesageBilder");

        Image liveImg = new Image(ConfigService.FILE_HTTP+"profilbild/"+messages.getMeintoken()+".png", false);
        ImageView liveImage = new ImageView(liveImg);
        liveImage.setFitWidth(30);
        liveImage.setFitHeight(30);
        liveUserBild.setGraphic(liveImage);
        liveUserName.setText(messages.getName());
        liveBilder.setText(messages.getMeintoken());
        liveMesageText.setText(messages.getText());


        Platform.runLater(() ->{
            //liveAusgabe.setText(messages.getText());
            messageVBOX.getChildren().add(liveAusgabe);

            // Bottom Scroll, immer letzte Message Anzeigen
            Animation animation = new Timeline(
                    new KeyFrame(Duration.seconds(2),
                            new KeyValue(messageScrollPanes.vvalueProperty(), 1)));
            animation.play();
        });
    }*/

}
