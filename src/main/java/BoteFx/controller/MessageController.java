package BoteFx.controller;

import BoteFx.controller.fragments.FreundeCellController;
import BoteFx.model.Freunde;
import BoteFx.model.Message;
import BoteFx.service.ApiService;
import BoteFx.service.ConfigService;
import BoteFx.service.TokenService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Den 24.11.2022
 */

@Controller
public class MessageController implements Initializable {
    @Autowired
    private ApiService apiService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private TokenService tokenService;

    @FXML
    private AnchorPane messageAnchorPane;
    @FXML
    private BorderPane messageBorderPane;
    @FXML
    private Label messageFehlerAusgabe;
    @FXML
    private GridPane messageFooter;
    @FXML
    private ImageView messageFreundBild;
    @FXML
    private Label messageFreundName;
    @FXML
    private GridPane messageHead;
    @FXML
    private ImageView messageOther;
    @FXML
    private ColumnConstraints messageSchliessen;
    @FXML
    private ScrollPane messageScrollPane;
    @FXML
    private ImageView messageSmile;
    @FXML
    private TextArea messageText;
    @FXML
    private VBox messageVBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // message.fxml width + height auf 100% ziehen
        AnchorPane.setTopAnchor(messageAnchorPane, 0.0);
        AnchorPane.setRightAnchor(messageAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(messageAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(messageAnchorPane, 0.0);

        // TextArea in Focus setzen
        Platform.runLater(() -> messageText.requestFocus());
    }


    /**
     * Getter & Setter
     * zugesenden von FreuneCellController Zeile: 315
     * die messageChat() wird hier automatisch gestartet
     */
    private String messageToken;
    private AnchorPane rechtsPane;
    private AnchorPane celleArchorPane;

    public String getMessageToken() {
        return messageToken;
    }
    public void setMessageToken(String mesagetoken) {
        this.messageToken = mesagetoken;

        //Socket Starten
        socketConnect();

        // Alte Message von MySql(Tabelle: messages) Holen
        // Request an Bote & response
        String messUrl  = configService.FILE_HTTP+"messageApi";
        String messJson = "{\"messagesToken\":\""+messageToken+"\"}";
        HttpResponse<String> response = apiService.requestAPI(messUrl, messJson);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        ArrayList<Message> allMesech  = gson.fromJson(response.body(), listType);

        altMessage(allMesech);

    }

    public AnchorPane getRechtsPane() {
        return rechtsPane;
    }
    public void setRechtsPane(AnchorPane rechtsPane) {
        this.rechtsPane = rechtsPane;
    }

    public AnchorPane getCelleArchorPane() {
        return celleArchorPane;
    }
    public void setCelleArchorPane(AnchorPane celleArchorPane) {
        this.celleArchorPane = celleArchorPane;
    }


    /* ******************* Socked ******************************************* */

    /**
     * Socket Connect
     *
     */
    private StompSession session;
    private String meinsToken;
    public void socketConnect() {

        meinsToken = tokenService.tokenHolen();
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

                    infoOutput("OK", "connectOk");
                }

                @Override
                public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                    exception.printStackTrace();

                    infoOutput("FEHLER","connectNo");
                }

                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    exception.printStackTrace();
                }
            }).get(10, TimeUnit.SECONDS);

            session.subscribe("/messages/receive/" + messageToken, new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {

                    infoOutput("OK", "sessionOk");
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
                     * Nachricht Erhalten/Ausgeben
                     */
                    liveMessage(message);
                    //System.out.println("Incoming text: " + message.getText());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //System.out.println(String.format("Connection id: %s", messageToken));

    } // Ende socketConnect


    /* ******************* Ende Socked / message Senden + Fehler Ausgabe ************************************** */

    /**
     * Alte message anzeigen
     *
     *
     * @param messageData
     */
    private void altMessage(ArrayList<Message> messageData){

        AnchorPane altArchor = new AnchorPane();

        for (Message mess :messageData){
            Label label = new Label(mess.getText());
            messageVBox.getChildren().add(label);
           // messageVBox.getChildren().add(altArchor);

            //altArchor.getChildren().addAll(altText, altName);
           // messageVBox.getChildren().add(altArchor);

        }
        //System.out.println("Alte Message Ausgabe: ");
    }


    /**
     * Message Senden
     * die Methode reagiert auf Tasten druck, onKeyReleased,
     * gesendet nut per ENTER
     *
     * @param keyEvent
     */
    @FXML
    private void messageSenden(KeyEvent keyEvent) {

        String messText = messageText.getText();

        if (session == null) {
            infoOutput("FEHLER", "sessionNo");
            return;
        }
        // Text Field auf Leer Prüfen
        if (messText.isBlank()) {

            infoOutput("FEHLER", "textLeer");
            messageText.positionCaret(messText.length());
            return;
        }

        if (keyEvent.getCode() == KeyCode.ENTER){
            //ausgabe.setText(messText);

            // Aktuelle Date
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

            Message message = new Message();
            message.setMessagetoken(messageToken);
            message.setMeintoken(meinsToken);
            message.setText(messText);
            message.setDatum(format.format(new Date()));
            message.setRole("default");
            message.setName("Client");
            message.setVorname("Java");
            message.setPseudonym("Java Client");

            session.send("/app/messages", message);
            messageText.clear();

            System.out.println("Senden Message");
        }

        // Text auf Länge prüfen
        messageLenge();
    }
    /**
     * Max Länge von Messenger-Text sind auf 500 Zeichen begrenzt
     * die Methode wird von messageSende gesteuert
     * änderung in ConfigService vornehmen
     *
     */
    private void messageLenge() {
        int maxMessLenge    = configService.MESSAGE_LENGTH;
        int messLenge       = messageText.getText().length();
        String messText     = messageText.getText();

        if (messLenge > maxMessLenge){
            messageText.setText(messText.substring(0, maxMessLenge));
            messageText.positionCaret(messText.length());
        } else {
            // Fehler Ausgabe
        }
    }



    /**
     * Live Chat
     * @param messech
     */
    private void liveMessage(Message messech){
        Label liveAusgabe = new Label();
        liveAusgabe.setStyle("-fx-border-color: red;");
        Platform.runLater(() ->{
            liveAusgabe.setText(messech.getText());
            messageVBox.getChildren().add(liveAusgabe);
        });

    }


    /**
     * Fehler Anzeigen
     *
     * automatische ausblenden von Fehler-Anzeige,  ca. 5 Sek....
     *
     * @param ok
     * @param fehlerText
     */
    private void infoOutput(String ok, String fehlerText){

        messageFehlerAusgabe.setTextFill(  ok == "OK" ? Color.GREEN : Color.RED );

        switch (fehlerText){
            case "connectOk":           messageFehlerAusgabe.setText("connection successfully established...");
                                        break;
            case "connectNo":           messageFehlerAusgabe.setText("connection not successfully established...");
                                        break;
            case "sessionOk":           messageFehlerAusgabe.setText("Session Verbindung aufgebaut...");
                                        break;
            case "sessionNo":           messageFehlerAusgabe.setText("Es besteht keine Verbindung!");
                                        break;
            case "textLeer":            messageFehlerAusgabe.setText("Feld darf nicht leer sein");
                                        break;
            default: break;

        }
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> messageFehlerAusgabe.setText(null));
        pause.play();
    }

    /* ************************* Weiter Methoden ************************** */


    /**
     * Foto, Datei, Kamera Zeigen
     * @param mouseEvent
     */
    public void otherZeigen(MouseEvent mouseEvent) {
        System.out.println("Bild + Datei + Kamera");
    }


    /**
     * Anzeige von Smile Icon
     * @param mouseEvent
     */
    public void smileZeigen(MouseEvent mouseEvent) {
        System.out.println("Smile zeigen");
    }


    /**
     * Message schliessen + Reset Hover
     */
    public void messageSchliessen() {
        rechtsPane.getChildren().clear();
        celleArchorPane.getStyleClass().remove("freundeAktiv");
    }
}
