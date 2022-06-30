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
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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

    /* Haupt + Header */
    @FXML private AnchorPane chatMessage;
    @FXML private BorderPane chatBorderPane;
    @FXML private GridPane chatGridPane;
    @FXML private ColumnConstraints columnSchliessen;
    @FXML private Label chatSchliessen;
    @FXML private Label messageFehlerAusgabe;
    @FXML private Label freundName;
    private String nameFreund;
    @FXML private ImageView chatUserBild;

    /* body */
    @FXML private ScrollPane messageScrollPane;
    @FXML private Label textAusgabe;

    /* Footer */
    @FXML private ImageView otherFooter;
    @FXML private TextField messageText;

    /* Allgemein */
    private StompSession session;
    private String meineToken;
    private String messageToken;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Chat controlller Inint");
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
                    logger.info("connection successfully established");
                    //updateOutput("Verbindung aufgebaut!");
                    okFehlerAnzeige("OK", "connection successfully established");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    exception.printStackTrace();
                    logger.info("connection not successfully established");
                    okFehlerAnzeige("FEHLER","connection not successfully established");
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
                    logger.info("connection successfully established" + messageToken);
                    okFehlerAnzeige("OK", "connection successfully established");
                    //updateOutput("Verbindung aufgebaut!");
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
                    updateOutput(message);
                    logger.info("Incoming text: " + message.getMeintoken());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.info(String.format("Connection id: %s", messageToken));
    } // Ende socketConnect

    /* Socket Starten */
    public ChatController(){
        socketConnect();
    }

    /**
     * Message Senden & Live Anzeigen
     * @param event
     */
    public void sendMessage(ActionEvent event) {
        if (session == null) {
            okFehlerAnzeige("FEHLER", "Es besteht keine Verbindung!");
            //messageFehlerAusgabe.setText("Es besteht keine Verbindung!");
            return;
        }

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
        messageText.clear(); // messageField

        //textAusgabe.setText("Gesendet: " + messageText.getText());
        logger.info("Message Senden: " + messageText.getText());
    }


    /**
     * Message Ausgeben von Zeile 198
     * @param text
     */
    @FXML private AnchorPane liveBox = new AnchorPane();
    private void updateOutput(Message text) {
        String template = "" +
                "<children>\n" +
                "   <VBox alignment=\"TOP_CENTER\" prefWidth=\"50.0\" AnchorPane.bottomAnchor=\"0.0\" AnchorPane.leftAnchor=\"0.0\" AnchorPane.topAnchor=\"0.0\">\n" +
                "       <children>\n" +
                "           <ImageView fx:id=\"livebild\" styleClass=\"livebild\" fitHeight=\"30.0\" fitWidth=\"30.0\" pickOnBounds=\"true\" preserveRatio=\"true\">\n" +
                "               <image>\n" +
                "                   <Image url=\"@/static/img/face.png\" />\n" +
                "               </image>\n" +
                "           </ImageView>\n" +
                "       </children>\n" +
                "       <opaqueInsets>\n" +
                "            <Insets />\n" +
                "       </opaqueInsets>\n" +
                "       <padding>\n" +
                "           <Insets top=\"5.0\" />\n" +
                "       </padding>" +
                "   </VBox>\n" +
                "   <Label fx:id=\"livePseudonym\" text=\"%pseudonym%\" styleClass=\"livepseudonym\" alignment=\"TOP_LEFT\" layoutX=\"121.0\" layoutY=\"6.0\" prefHeight=\"20.0\" prefWidth=\"180.0\" AnchorPane.leftAnchor=\"50.0\" AnchorPane.rightAnchor=\"125.0\" AnchorPane.topAnchor=\"5.0\" />\n" +
                "   <ImageView fx:id=\"liveHacken\" styleClass=\"livehacken\" fitHeight=\"25.0\" fitWidth=\"25.0\" layoutX=\"236.0\" layoutY=\"4.0\" pickOnBounds=\"true\" preserveRatio=\"true\" AnchorPane.rightAnchor=\"95.0\" AnchorPane.topAnchor=\"0.0\">\n" +
                "       <image>\n" +
                "           <Image url=\"@static/img/doppeltgrun.png\" />\n" +
                "       </image>\n" +
                "   </ImageView>\n" +
                "   <Label fx:id=\"liveDatum\" text=\"%datum%\" styleClass=\"livedatum\" alignment=\"CENTER\" contentDisplay=\"CENTER\" layoutX=\"306.0\" layoutY=\"6.0\" prefWidth=\"70.0\" AnchorPane.rightAnchor=\"25.0\" AnchorPane.topAnchor=\"5.0\" />\n" +
                "   <ImageView fx:id=\"liveDeleteButton\" fitHeight=\"15.0\" fitWidth=\"15.0\" onMouseClicked=\"#liveDeleteZeigen\" pickOnBounds=\"true\" preserveRatio=\"true\" AnchorPane.rightAnchor=\"5.0\" AnchorPane.topAnchor=\"5.0\">\n" +
                "       <image>\n" +
                "           <Image url=\"@/static/img/delete.png\" />\n" +
                "       </image>\n" +
                "   </ImageView>" +
                "   <Label fx:id=\"liveText\" text=\"%messageAusgabe%\" styleClass=\"livetext\" wrapText=\"true\" alignment=\"TOP_LEFT\" layoutX=\"138.0\" layoutY=\"20.0\" prefHeight=\"30.0\" prefWidth=\"270.0\" AnchorPane.bottomAnchor=\"0.0\" AnchorPane.leftAnchor=\"50.0\" AnchorPane.rightAnchor=\"5.0\" AnchorPane.topAnchor=\"25.0\" />\n" +
                "   <VBox fx:id=\"liveDeleteBox\" styleClass=\"livedeletebox\" alignment=\"TOP_CENTER\" layoutX=\"288.0\" layoutY=\"-71.0\" prefWidth=\"25.0\" style=\"-fx-border-color: red;\" AnchorPane.bottomAnchor=\"0.0\" AnchorPane.rightAnchor=\"-25.0\" AnchorPane.topAnchor=\"0.0\">\n" +
                "       <children>\n" +
                "           <RadioButton fx:id=\"liveRadioButton\" styleClass=\"liveradiobutton\" onAction=\"#liveMessageDelete\" contentDisplay=\"CENTER\" mnemonicParsing=\"false\" />\n" +
                "       </children>\n" +
                "       <opaqueInsets>\n" +
                "           <Insets />\n" +
                "       </opaqueInsets>\n" +
                "       <padding>\n" +
                "           <Insets top=\"5.0\" />\n" +
                "       </padding>\n" +
                "   </VBox>\n" +
                "</children>";


        StringBuilder sb = new StringBuilder();
            sb.append(
                    template
                            .replaceAll("%messageBild%", text.getName())
                            .replaceAll("%pseudonym%", text.getPseudonym())
                            .replaceAll("%datum%", text.getDatum())
                            .replaceAll("%messageAusgabe%", text.getText()));

        String outer =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "\n" +
                        "<?import javafx.geometry.Insets?>\n" +
                        "<?import javafx.scene.control.Label?>\n" +
                        "<?import javafx.scene.control.RadioButton?>\n" +
                        "<?import javafx.scene.image.Image?>\n" +
                        "<?import javafx.scene.image.ImageView?>\n" +
                        "<?import javafx.scene.layout.AnchorPane?>\n" +
                        "<?import javafx.scene.layout.VBox?>\n" +
                        "\n" +
                        "<AnchorPane    fx:id=\"liveBox\"\n" +
                        "               styleClass=\"livebox\"\n" +
                        "               xmlns=\"http://javafx.com/javafx/18\"\n" +
                        "               xmlns:fx=\"http://javafx.com/fxml/1\"\n" +
                        "               fx:controller=\"BoteFx.controller.ChatController\"\n" +
                        "               prefHeight=\"70.0\" prefWidth=\"350.0\" >\n" +
                        "%content%\n" +
                        "</AnchorPane>";
        String done = outer.replaceAll("%content%", sb.toString());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("myfile.fxml");
            out.write(done.getBytes());
            out.close();
            FXMLLoader loader = new FXMLLoader();
            liveBox = loader.load(new FileInputStream("myfile.fxml"));
            //final Parent parent = (Parent) loader.load(new FileInputStream("myfile.fxml"));
            //messageBox = loader.load(new FileInputStream("myfile.fxml"));
            Platform.runLater(() ->{
                if (messageScrollPane != null) {
                    messageScrollPane.setContent(liveBox);
                    liveBox.prefWidthProperty().bind(messageScrollPane.widthProperty());
                }
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("updateOutput(): " + liveBox);
        //titleToolbar.getChildren().addAll(ellispsis != null ? ellispsis : new HBox());
        // messageScrollPane.setContent(messagePane);
    }


    /**
     * Fehler/Anzeige Einblenden & Ausblenden (ca. 3 sek.)
     * Message-Text Eingabe Feld in Focus setzen
     *
     * @param ok
     * @param text
     */
    private void okFehlerAnzeige(String ok, String text){
        Platform.runLater(() ->{
            logger.info("Text Methode: " + text);
            if (text != null && text.isBlank()) {
                messageFehlerAusgabe.setText(text);
                if (ok.equals("OK")) {
                    messageFehlerAusgabe.setTextFill(Color.GREEN);
                    freundName.setText(meineToken);
                } else {
                    messageFehlerAusgabe.setTextFill(Color.RED);
                }

                /* Anzeige-Text Ausblenden */
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e -> messageFehlerAusgabe.setText(null));
                pause.play();
            }

            /**
             *  1. TextField-> in Focus setzen
             *  2. ScrollPane Horizontale scroll ausblenden
             */
            if (messageText != null) {
                messageText.requestFocus();
                messageScrollPane.setFitToWidth(true);
                //messageScrollPane.getOnScrollFinished();
            }
        });
    }


    /**
     * Message schliessen( Rechte Teil)
     * Alle inhalte & selber AnchorPane Löschen
     * @param event
     */
    public void chatClose(MouseEvent event) {

        chatMessage.getChildren().clear();
        ((Pane) chatMessage.getParent()).getChildren().remove(chatMessage);
        logger.info("Message Schliessen: rechte Teil" + chatMessage);
    }

    /**
     * Anzeige von Photo, Kamera & andere
     * @param event
     */
    public void otherZeigen(MouseEvent event) {
        logger.info(" Photo Anzeigen ");
    }

    /**
     * Message Löschen
     * @param event
     */
    public void liveDeleteZeigen(MouseEvent event) {
        logger.info(" Message Delete Box Anzeigen ");
    }

    public void liveMessageDelete(ActionEvent event) {
        logger.info(" Message Löschen ");
    }
}
