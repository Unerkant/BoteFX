package BoteFx.controller;

import BoteFx.configuration.GlobalConfig;
import BoteFx.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * den 18.06.2022
 */
public class ChatController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    /* chat.fxml */
    @FXML private AnchorPane chatAnchorpane;
    @FXML private BorderPane chatBorderPane;

    /* Header */
    @FXML private GridPane headGridPane;
    @FXML private ColumnConstraints columnSchliessen;
    @FXML private Label chatSchliessen;
    @FXML private Label chatFreundName;
    @FXML private Label messageFehlerAusgabe;
    @FXML private ImageView chatFreundBild;

    /* body */
    @FXML private ScrollPane bodyScrollpane;
    @FXML private VBox bodyVbox;
    @FXML private AnchorPane bodyAltMessage;
    @FXML private AnchorPane bodyLiveMessage;

    /* Footer */
    @FXML private GridPane footerGridpane;
    @FXML private ImageView footerOther;
    @FXML private TextArea messageText;
    @FXML private ImageView footerSmile;

    /* Allgemein */
    private StompSession session;
    private String meineToken;
    private String messageToken;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**
         * Init
         */
       // logger.info("INIT:" + url);
    }

    // Socket Verbindung
    public void socketConnect() {

        /**
         * Das Functioniert
         * bin als Chrome (registriert)
         * Token: 25052022181551
         * Message Token: 25052022181609 ( mit Safari befreundet/ gleiche M-token)
         */
        //String value = messageTokenField.getText();
        //String messageToken = messageTokenField.getText();
        meineToken      = "25052022181551";
        messageToken    = "25052022181609";

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

                    infoOutput("OK", "connection successfully established...");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    exception.printStackTrace();

                    infoOutput("FEHLER","connection not successfully established...");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    exception.printStackTrace();
                }
            }).get(10, TimeUnit.SECONDS);

            session.subscribe("/messages/receive/" + messageToken, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {

                    infoOutput("OK", "Verbindung aufgebaut...");
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
                     * Nachricht Erhalten
                     */
                    liveMessageAusgabe(message);
                    //logger.info("Incoming text: " + message.getMeintoken());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //logger.info(String.format("Connection id: %s", messageToken));
    } // Ende socketConnect

    /* Socket Starten */
    public ChatController(){
        socketConnect();
        if (session == null) {
            infoOutput("FEHLER", "Es besteht keine Verbindung!");
            logger.info(" Socket Connect " + session);
        }
    }

    /**
     *      Message Wrap
     *
     * textarea (#messageText) AutoRow (for schleife)
     * leider funktioniert nicht Richtig
     *
     * ENTER-Drücken: message Sende und textarea leeren
     * ESC-Drücken: textare leeren & footerGridPane auf 45px Height setzen
     * variable minHeight: footer GridPane(#footerGridPane) Height 45px festlegen
     */
    final private KeyCombination ENTER      = new KeyCodeCombination(KeyCode.ENTER);
    final private KeyCombination ESC        = new KeyCodeCombination(KeyCode.ESCAPE);
    final private KeyCombination BACKSPACE  = new KeyCodeCombination(KeyCode.BACK_SPACE);
    final private KeyCombination SPACE      = new KeyCodeCombination(KeyCode.SPACE);
    private KeyCode keycode;
    private double wrapHeight;
    private double minHeight = 45; /* footer min-height */
    @FXML
    public void messageWrap(KeyEvent event) {
        keycode     = event.getCode();
        wrapHeight  = messageText.getScrollTop();

        if (ENTER.match(event)){
            messageSend();
            //messageText.clear();
            return;
            //System.out.println("ENTER: " + ENTER);
        } else if (BACKSPACE.match(event)) {
            //System.out.println("ZURÜCK TASTE: " + BACKSPACE);
        } else if (SPACE.match(event)) {
            //System.out.println("LEER TASTE: " + SPACE);
        } else if (ESC.match(event)) {
            messageText.clear();
            footerGridpane.setPrefHeight(minHeight);
            return;
            //System.out.println("ESC: " + ESC);
        } else{
            //System.out.println("ELSE: ");
        }

        // textarea scroll
        for (int y = 0; y <= wrapHeight; y++){
            //footerGridpane.setMinHeight(45);
            footerGridpane.setPrefHeight(minHeight + wrapHeight);
        }
        //System.out.println("Wrap Height: " + wrapHeight);

    }

    /**
     * Message Senden
     * //@param event
     */
    public void messageSend() {

        if (session == null) {
            infoOutput("FEHLER", "Es besteht keine Verbindung!");
            //messageFehlerAusgabe.setText("Es besteht keine Verbindung!");
            logger.info("keine Verbindung " );
            return;
        }
        // Text Field auf Leer Prüfen
        String getText =  messageText.getText();
        if (getText.isEmpty()) {
            logger.info("messageText, ist Leer ");
            return;
        }

        // Aktuelle Date
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

        Message message = new Message();
        message.setMessagetoken(messageToken);
        message.setMeintoken(meineToken);
        message.setText(messageText.getText());
        message.setDatum(format.format(new Date()));
        message.setRole("default");
        message.setName("Client");
        message.setVorname("Java");
        message.setPseudonym("Java Client");

        session.send("/app/messages", message);
        messageText.clear();

        //textAusgabe.setText("Gesendet: " + messageText.getText());
        logger.info("Message Senden: " + messageText.getText());
    }


    /**
     * Message Ausgeben von Zeile 198
     * @param text
     */
    @FXML private AnchorPane livePane;
    @FXML private AnchorPane liveBox;
    @FXML private ImageView liveBild;
    @FXML private Label livePseudonym;
    @FXML private ImageView liveHacken;
    @FXML private Label liveDatum;
    @FXML private ImageView liveDeleteButton;
    @FXML private TextArea liveAusgabe;
    @FXML private VBox liveDeleteBox;
    @FXML private RadioButton liveRadioButton;
    @FXML private ImageView liveCloseRadio;
    @FXML private AnchorPane altFxml;
    @FXML private AnchorPane liveFxml;
    @FXML private ChatController altController;
    @FXML private ChatController liveController;

    @FXML private double textHoch;
    private void liveMessageAusgabe(Message text ) {

        try {
            FXMLLoader liveLoader = new FXMLLoader(getClass().getResource("/templates/fragments/livemessage.fxml"));
            liveFxml = liveLoader.load();
            liveController = liveLoader.getController();

            FXMLLoader altLoader = new FXMLLoader(getClass().getResource("/templates/fragments/livemessage.fxml"));
            altFxml = altLoader.load();
            altController = altLoader.getController();

            //liveFxml = FXMLLoader.load(getClass().getResource("/templates/fragments/livemessage.fxml"));
           // bodyLiveMessage.getChildren().add(liveFxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AnchorPane.setRightAnchor(altFxml, 0.0);
        AnchorPane.setLeftAnchor(altFxml, 0.0);
        AnchorPane.setLeftAnchor(liveFxml, 0.0);
        AnchorPane.setRightAnchor(liveFxml, 0.0);

        // Live Message Anzeigen
        Platform.runLater(()->{
            liveController.livePseudonym.setText(text.getPseudonym());
            liveController.liveDatum.setText(text.getDatum());
            liveController.liveAusgabe.setText(text.getText());

            if (bodyLiveMessage != null) {
                bodyLiveMessage.getChildren().add(liveFxml);
                /* Ausgabe-Message auf 100% width */
                bodyVbox.prefWidthProperty().bind(bodyScrollpane.widthProperty());

                liveController.liveAusgabe.applyCss();
                liveController.liveAusgabe.layout();
                Text t = (Text) liveController.liveAusgabe.lookup(".text");

                //System.out.println("LayoutX "+t.getLayoutX());
                //System.out.println("LayoutY "+t.getLayoutY());
                System.out.println("Test: " + t.getCaretPosition());
                System.out.println("Width: "+t.getBoundsInLocal().getWidth());
                System.out.println("Height: "+t.getBoundsInLocal().getHeight());
                //liveController.liveBox.setPrefHeight(t.getBoundsInLocal().getHeight());
            }
        });

        Platform.runLater(()->{
            altController.livePseudonym.setText(text.getPseudonym());
            altController.liveDatum.setText(text.getDatum());
            altController.liveAusgabe.setText(text.getText());

            if (bodyAltMessage != null) {
                bodyAltMessage.getChildren().add(altFxml);

            }
        });

        logger.info("liveMessageAusgabe(): " + textHoch);
    }


    /**
     * Fehler/Anzeige Einblenden & Ausblenden (ca. 3 sek.)
     * Message-Text Eingabe Feld in Focus setzen
     * body-scroll ausblenden
     *
     * @param ok
     * @param texten
     */
    private void infoOutput(String ok, String texten){
        Platform.runLater(() ->{

            if (messageFehlerAusgabe != null) {
                messageFehlerAusgabe.setText(texten);
                if ("OK".equals(ok)) {
                    messageFehlerAusgabe.setTextFill(Color.GREEN);
                    /* Anzeige-Text Ausblenden */
                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(e -> messageFehlerAusgabe.setText(null));
                    pause.play();
                } else {
                    messageFehlerAusgabe.setTextFill(Color.RED);
                }
            }

            /**
             *  1. TextField-> in Focus setzen
             *  2. ScrollPane Horizontale scroll ausblenden
             */
            if (messageText != null) {

                messageText.requestFocus();
                //bodyScrollpane.getOnScrollFinished();
                //logger.info("Message Focus");
            }

            if (chatFreundName != null){
                chatFreundName.setText(meineToken);
            }
        });
    }


    /**
     * Message schliessen( Rechte Teil)
     * Alle inhalte & selber AnchorPane Löschen
     * @param event
     */
    public void chatClose(MouseEvent event) {

        chatAnchorpane.getChildren().clear();
        ((Pane) chatAnchorpane.getParent()).getChildren().remove(chatAnchorpane);
        logger.info("Message Schliessen: rechte Teil" + chatAnchorpane);
    }

    /**
     * Anzeige von Photo, Kamera & andere
     * @param event
     */
    public void otherZeigen(MouseEvent event) {
        logger.info(" Photo Anzeigen ");
    }

    /**
     * Anzeige den radio-button für die message Löschen
     * @param event
     */
    public void liveDeleteZeigen(MouseEvent event) {
        AnchorPane.setRightAnchor(liveAusgabe, 25.0);
        AnchorPane.setRightAnchor(liveDeleteBox, 0.0);
        AnchorPane.setRightAnchor(liveDeleteButton, -100.0);

        liveDeleteBox.setOnMouseClicked(evt -> {
            AnchorPane.setRightAnchor(liveAusgabe, 5.0);
            AnchorPane.setRightAnchor(liveDeleteBox, -100.0);
            AnchorPane.setRightAnchor(liveDeleteButton, 5.0);
            logger.info(" Meuse click ");
        });
        logger.info(" Message Delete Box Anzeigen ");
    }

    /**
     * message Löschen
     * @param event
     */
    public void liveMessageDelete(ActionEvent event) {
        logger.info(" Message Löschen ");
    }

}
