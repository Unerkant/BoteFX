package BoteFx.controller;

import BoteFx.model.Freunde;
import BoteFx.model.Message;
import BoteFx.service.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Den 24.11.2022
 */

@Controller
public class MessageController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MethodenService methodenService;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private SocketService socketService;

    /* **** 3 StackPane von ChatBoxController(Setter&Getter), Pop-up-Fenster anzeigen/ausblenden */
    @FXML private StackPane hauptStage;
 /*   @FXML private StackPane rightStage;
    @FXML private StackPane leftStage;*/


    // Haupt Pane
    @FXML private AnchorPane messageAnchorPane;
    @FXML private BorderPane messageBorderPane;

    // Top / header
    @FXML private Label messageProfilBild;
    @FXML private Label headFreundName;
    @FXML private Label headFreundInfo;
    @FXML private Label freundOnlineZeit;
    @FXML private Label headBearbeiten;

    // Center / Body
    @FXML private ScrollPane messageScrollPane;
    @FXML private VBox messageVBox;

    //Bottom / footer
    @FXML private StackPane messageBottomStackPane;
    final AnchorPane messageLoschenPane = new AnchorPane(); //Bottom, für die löschen function Zeile: ab 900
    @FXML private ImageView messageOther;

    @FXML private VBox textareaVBox;
    @FXML private TextArea textareaText;
    @FXML
    private ImageView messageSmile;

    // Allgemein
    private String myColor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /**
         * 3 Setter & Getter, für die Pop-up-Fenster anzeige
         *
         *   Benutzt in: messageBearbeiten(750), loschenInfo(1145)
         */
        hauptStage  = chatBoxController.getHauptStackPane();
    /*    rightStage  = chatBoxController.getRightStackPane();
        leftStage   = chatBoxController.getRightStackPane();*/

        // message.fxml width + height auf 100% ziehen
        AnchorPane.setTopAnchor(messageAnchorPane, 0.0);
        AnchorPane.setRightAnchor(messageAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(messageAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(messageAnchorPane, 0.0);

        // Center: Ausgabe VBox width auf 100% ziehen
        messageVBox.prefWidthProperty().bind(messageAnchorPane.widthProperty());

        // Bottom: TextArea in Focus setzen
        Platform.runLater(() -> textareaText.requestFocus());

        // Methode: altMessage, rundes bild-hintergrund, Zeile: ab 415
        myColor = methodenService.zufallColor();

    }


    /**
     * Getter & Setter
     * zugesenden von FreuneCellController Zeile: 315
     * die messageChat() wird hier automatisch gestartet
     * die headerFreundeDaten wird hier automatisch gestartet,
     * anzeige in Header das Bild und Name
     */
    private StackPane rechtsPane;
    public StackPane getRechtsPane() {
        return rechtsPane;
    }
    public void setRechtsPane(StackPane rechtsPane) { this.rechtsPane = rechtsPane; }

    private AnchorPane celleArchorPane;
    public AnchorPane getCelleArchorPane() {
        return celleArchorPane;
    }
    public void setCelleArchorPane(AnchorPane celleArchorPane) {
        this.celleArchorPane = celleArchorPane;
    }

    private String freundColor;
    public String getFreundColor() {
        return freundColor;
    }
    public void setFreundColor(String freundColor) {
        this.freundColor = freundColor;
    }


    /**
     * zugesendet von FreundeCellController Zeile: 322
     * mit zugesendeten messageToken alle messages aus dem Datenbank(mySql, Bote)
     * holen und als Alte Messages anzeigen
     */
    private String messageToken;
    private ArrayList<Message> allMessages;
    public String getMessageToken() {
        return messageToken;
    }
    public void setMessageToken(String mesagetoken) {
        this.messageToken = mesagetoken;

        //Socket Starten
        //socketService.socketConnect(mesagetoken, (message) -> liveMessage(message), () -> Platform.runLater(() -> fehlerInfo("FEHLER", "sessionNo")));
        socketService.setRechtensPane(rechtsPane);
        socketService.setMessageVBOX(messageVBox);
        socketService.setMessageScrollPanes(messageScrollPane);


        // Alte Message von MySql(Tabelle: messages) Holen
        // Request an Bote & response
        String messUrl  = configService.FILE_HTTP+"messageApi";
        String messJson = "{\"messagesToken\":\""+messageToken+"\"}";
        HttpResponse<String> response = apiService.requestAPI(messUrl, messJson);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        allMessages  = gson.fromJson(response.body(), listType);

        altMessage(allMessages);
    }


    private String chatFreundeDaten;
    private Image headPunkte;
    private ImageView blauPunkte;
    private String freundToken;
    public String getChatFreundeDaten() {
        return chatFreundeDaten;
    }
    public void setChatFreundeDaten(Freunde chatFreundeDaten) {
        this.chatFreundeDaten = String.valueOf(chatFreundeDaten);

        headPunkte = new Image(getClass().getResourceAsStream("/static/img/punkteblau.png"));
        blauPunkte = new ImageView(headPunkte);
        freundToken = chatFreundeDaten.getFreundetoken();
        // methode unten Zeile: 220
        headerFreundeDaten(chatFreundeDaten);
    }



    /**
     * Anzeige in Header nur das Bild + Name + Letzte Online Zeit
     *
     * @param freundData
     */
    private void headerFreundeDaten(Freunde freundData){
        String freundBild = freundData.getFreundebild();
        String freundPseudonym = freundData.getFreundepseudonym();

        // Profil Bild von Bote holen
        Image headFreundImg = new Image(ConfigService.FILE_HTTP+"profilbild/"+freundBild+".png");
        if (headFreundImg.isError()){
            messageProfilBild.setText(freundPseudonym);
            messageProfilBild.setAlignment(Pos.CENTER);
            messageProfilBild.setStyle("-fx-background-radius: 25;"+"-fx-background-color:"+ freundColor +";" +
                    "-fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700; -fx-text-fill: white;");
        }else {
            ImageView imageView = new ImageView(headFreundImg);
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            messageProfilBild.setGraphic(imageView);
        }

        // Name Anzeigen
        headFreundName.setText(freundData.getFreundename().isBlank() ? freundData.getFreundepseudonym() :
                                        freundData.getFreundevorname() +" "+ freundData.getFreundename());
        headFreundName.getStyleClass().add("headFreundsName");

        // Letzte Online Zeit(style direct in message.fxml)
       // freundOnlineZeit.setText("???...MessageControll..Zeile: 358");

        // Message Bearbeiten (Label) onMouseClicked(messageBearbeiten)
        blauPunkte.setFitWidth(30);
        blauPunkte.setFitHeight(30);
        headBearbeiten.setGraphic(blauPunkte);
    }


    /* ****************** Alte Message + live message Ausgabe ********************** */

    /**
     * ACHTUNG: nur alte message aus den Datenbank(keine Live-Message)
     *
     * Alle messages aus dem Datenbank in 'messageVBox' anzeigen(for schleife)
     * ACHTUNG: Der '#messageVBox' ist in eine 'ScrollPane' integriert(message.fxml/center)
     *
     *  ACHTUNG: die 3 variablen
     *  1.  checkGroup: da sind nur selected checkBox zusammen gespeichert
     *      für die Methode: messageLoschenAktiv(checkGroup, paneArray); Zeile: ca. 900
     *      ....
     *  2.  checkArray: sind alle in schleife angezeigte checkBox gespeichert
     *      für die Methode: altChekcBoxShow() + altCheckBoxHide()...
     *      da werden alle ChechBox angezeigt oder versteckt
     *  3.  paneArray: sind alle selectierte messages was zum Löschen sind
     *      mit dem #messageBox(StackPane)...mit der gleichen ID wie die checkBox
     *      für die Methode: messageLoschenAktiv(checkGroup, paneArray); Zeile: ca. 900
     *
     *   KURZE BESCHREIBUNG VON MESSAGE BOX:
     *   alle messages sind in einem Array gespeichert: 'messageData', zugesendet von
     *   setMessageToken(), Zeile: 160, da werden sie von Datenbank geholt
     *
     *   Aufbau selbe message Box:
     *   Haupt-Message Pane ist eine StackPane mit dem ID mesageBox,
     *   in der StackPane ist eine BorderPane geladen,
     *   BorderPane:
     *      a. left: ist einen Bild in Label und Label eine VBox integriert
     *      b. Center: ausgabe von Namen, Hacken, Zeit ist in einen GridPane
     *                 ausgabe von Bilder in Label(wenn wird gepostet)
     *                 ausgabe von Nachrichten(message) zuerst in einem Text(Layout)
     *                 dann der Text in eine TextFlow integriert, dann die alle 3 Elemenzet in
     *                 einem VBox, ...new VBox(altGridPane, altMessageBilder, textFlow);
     *      c. Right:  da sind alle CheckBox in einem VBox untergebracht,
     *                 bei anClicken den checkBox wird eine selectedProperty ausgelöst
     *                 da werden alle angclickte checkBox in einem Array 'checkGroup' gespeichert,
     *                 und schliesslich an die Methode: messageLoschenAktiv(checkGroup, paneArray);
     *                 weitergeleitet für das Löschen oder Weiterleiten
     *    die alle Labels werden in schleife mit Daten gefühlt und schliesslich die stackPane
     *    wird in messageVBox integriert: messageVBox.getChildren().add(mesageBox);
     *    die selbe messageVBox befindet sich in eine ScrollPane,
     *    quelle: message.fxml / Center
     *
     *    ACHTUNG:     StackPane(messageBox) ist ein Message-Block nur von einer einzelnen Message,
     *      *          bei Löschen die Message wird die StackPane nur gelehrt aber selbe Pane erhalten,
     *      *          bis zu nächsten Aktualisierung...
     *      *          kein Style auf StackPane wird aufgelegt
     *      *          Lösch-Methode hier unten: geloschteMessageHide(.., Zeile ab 1050
     *
     * @param messageData
     */
    final List<Long> checkGroup = new ArrayList<>(); // nur selected
    final List<CheckBox> checkArray = new ArrayList<>();        // alle checkBox
    final List<StackPane> paneArray = new ArrayList<>();        // nur hover/zum Löschen StackPane

    private void altMessage(ArrayList<Message> messageData){

        String meinToken = tokenService.tokenHolen();
        //System.out.println("MessageController: Freund Token: " + freundToken +" Mein Token: "+ meinToken);
        for (Message mess :messageData){

            // message Box Pane(complete message Block(nur eine einzelne message))
            StackPane mesageBox = new StackPane();
            mesageBox.setId(String.valueOf(mess.getId()));

        /* 1. Left Pane, inhalt(Freund Bild) */
            Label altBildLabel = new Label(mess.getPseudonym());
            altBildLabel.getStyleClass().add("altBildsLabel");
            // Bild von Bote holen
            Image altImg = new Image(ConfigService.FILE_HTTP+"profilbild/"+mess.getMeintoken()+".png", false);
            if (altImg.isError()){
                altBildLabel.setText(mess.getPseudonym());
                altBildLabel.setStyle( "-fx-background-color:" + (freundToken.equals(mess.getMeintoken()) ? freundColor : myColor) + "; " +
                        "-fx-text-fill: white; -fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700;");
            }else {
                ImageView altImageView = new ImageView(altImg);
                altImageView.setFitHeight(30);
                altImageView.setFitWidth(30);
                altBildLabel.setGraphic(altImageView);
            }
            // Label(bild) ins VBox integrieren
            VBox altLeftBox = new VBox(altBildLabel);
            altLeftBox.getStyleClass().add("altLinksVBox");

        /* 2. GridPane: User Name/Pseudonym + Hacken(grau+grün) + Zeit(von diese message), BorderPane/ center */
            // a. Username
            Label altUserName = new Label(mess.getName().isBlank() ? mess.getPseudonym() : mess.getVorname() +" "+ mess.getName());
            altUserName.getStyleClass().add("altFreundsName");

            // b. Hacken Grau + Blau
            Image imgGrau = new Image(getClass().getResourceAsStream("/static/img/done.png"), 15, 15, false, false);
            Image imgGrun = new Image(getClass().getResourceAsStream("/static/img/donegreen.png"), 15, 15, false, false);
            ImageView imgHakenGrau = new ImageView(imgGrau);
            ImageView imgHakenGrun = new ImageView(imgGrun);
            //imgHakenGrun.setVisible(false);

            // c. Message Zeit
            String zeit = mess.getDatum().substring(8,14);
            Label altMessageZeit = new Label(zeit);
            altMessageZeit.getStyleClass().add("altMessagesZeit");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            // Aktuelle Datum
            DateTimeFormatter aktuell = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            DateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
            String mySqlDatum = mess.getDatum();
            Date date;
            try {
                date = format.parse(mySqlDatum);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // d. GridPane + add column
            GridPane altGridPane = new GridPane();
            // Top Pos. von hacken + Zeit
            GridPane.setValignment(imgHakenGrau, VPos.TOP);
            GridPane.setValignment(imgHakenGrun, VPos.TOP);
            GridPane.setValignment(altMessageZeit, VPos.TOP);
            // new column + width
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setMinWidth(20);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setMinWidth(40);
            // add column
            altGridPane.getColumnConstraints().addAll(column0, column1, column2);
            altGridPane.addColumn(0, altUserName);
            if (Objects.equals(aktuell, date)) {
                altGridPane.addColumn(1, imgHakenGrau);
            } else {
                altGridPane.addColumn(1, imgHakenGrun);
            }
            altGridPane.addColumn(2, altMessageZeit);

            /* Label(Bilder anzeige), Label(message anzeigen) */
            // nach GridPane, den message-text in Label anzeigen
            Label altMessageBilder = new Label("---");
            altMessageBilder.getStyleClass().add("altMessagesBilder");
            String str = mess.getText();
            Text text = new Text(str);
            TextFlow textFlow = new TextFlow(text);

            // add VBox (GridPane + Label + Label)
            VBox altCenterVBox = new VBox(altGridPane, altMessageBilder, textFlow);
            altCenterVBox.getStyleClass().add("altMittesVBox");

        /* 3.  Right Pane, inhalt */
            CheckBox altCheckBox = new CheckBox();
            altCheckBox.getStyleClass().add("checksBox");
            altCheckBox.setStyle("-fx-font-size: 0.1px;");
            altCheckBox.setId(String.valueOf(mess.getId()));
            VBox altRightBox = new VBox(altCheckBox);

            // Group checkBox, für die Methode altCheckBoxGroup()...Zeile: 680
            checkArray.add(altCheckBox);
            altCheckBox.selectedProperty().addListener((observable, oldValue, selectedNow) -> {
                if (selectedNow) {
                    checkGroup.add(Long.valueOf(altCheckBox.getId()));
                    paneArray.add(mesageBox);
                    mesageBox.getStyleClass().add("altMessageHover");
                } else {
                    checkGroup.remove(Long.valueOf(altCheckBox.getId()));
                    paneArray.remove(mesageBox);
                    mesageBox.getStyleClass().remove("altMessageHover");
                }
                // Zeile: ab 1000
                if (checkGroup.size() == 0 && paneArray.size() == 0){
                    // keine checkBox ausgewellt, alles schliessen
                    altCheckBoxHide();

                } else {
                    // selected checkBox an Methode senden
                    messageLoschenAktiv(checkGroup, paneArray);
                }

            });

        /* 4.  Border Pane integration(complete message Box) */

            BorderPane altBorderPane = new BorderPane();
            altBorderPane.getStyleClass().add("altBordersPane");
            altBorderPane.setLeft(altLeftBox);
            altBorderPane.setCenter(altCenterVBox);
            altBorderPane.setRight(altRightBox);
            mesageBox.getChildren().add(altBorderPane);


        /* 5.  message-box anzeigen */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageVBox.getChildren().add(mesageBox);
                    //1.0 means 100% at the bottom
                    messageScrollPane.setVvalue(1.0);
                }
            });
        } // Ende for schleife

    } // Ende altMessage()...



    /* ******************* Message Senden ************************ */

    /**
     * TEXTAREA MAX. LÄNGE 500 ZEICHEN
     *
     * Max Länge von Messenger-Text sind auf 500 Zeichen begrenzt
     * die Methode wird von messageSende gesteuert
     * änderung in ConfigService vornehmen
     */
    private void messageLenge() {
        int maxMesageLenge    = configService.MESSAGE_LENGTH;
        int mesageLenge       = textareaText.getText().length();
        String mesageText     = textareaText.getText();

        if (mesageLenge > maxMesageLenge){
            textareaText.setText(mesageText.substring(0, maxMesageLenge));
            textareaText.positionCaret(mesageText.length());
        } else {
            // Fehler Ausgabe
        }
    }



    /**
     * TEXTAREA AUTO ROW
     *
     * Funktioniert nicht richtig, aufgegeben....
     *
     * Textarea max. height: 350px
     * benutzt von der Methode: messageSenden (hier oben)
     *
     * @param mesagetext
     */
    private void textareaAutoRow(String mesagetext){
        int textareaMaxHoch = configService.TEXTAREA_HEIGHT;
        Text text = new Text();
        text.setWrappingWidth(textareaText.getWidth() - 40);
        text.setText(mesagetext);
        double height = text.getLayoutBounds().getHeight() + 5;
        double pading = 20;
        if (height > textareaMaxHoch) {
            textareaText.setMinHeight(textareaMaxHoch);
        } else {
            textareaText.setMinHeight(height + pading);
        }
        textareaText.setScrollTop(height + 20);

        // für die Methode messageBearbeiten
        messageHeightMerken = height;
    }



    /**
     * MESSAGE SENDEN
     *
     * die Methode reagiert auf Tasten druck, onKeyReleased,
     * gesendet nut per ENTER
     *
     * @param keyEvent
     */
    private  Double messageHeightMerken = 20.0;
    @FXML
    private void messageSenden(KeyEvent keyEvent) {

        String messagesText = textareaText.getText();

        // Text Field auf Leer Prüfen
        if (messagesText.isBlank()) {
            textareaMinHoch();
            messageHeightMerken = 20.0;
            textareaText.clear();
            textareaText.positionCaret(messagesText.length());
            return;
        }

        // Message Senden mit dem ENTER
        if (keyEvent.getCode() == KeyCode.ENTER){
            //ausgabe.setText(messagesText);
            // Aktuelle Date
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");

            Message message = new Message();

            message.setDatum(format.format(new Date()));
            message.setFreundetoken(freundToken);
            message.setMeintoken(tokenService.tokenHolen());
            message.setMessagetoken(messageToken);
            message.setPseudonym("JC");
            message.setVorname("Java");
            message.setName("Client");
            message.setText(messagesText);
            message.setRole("default");

            try {
                socketService.senden(message);
                textareaText.clear();
                //messageHeightMerken = 20.0;
                textareaMinHoch();
            } catch (Exception e){
                fehlerInfo("FEHLER", "sessionNo");
                textareaText.clear();
            }

        } else {
            textareaAutoRow(messagesText);
        }
        // Text auf Länge prüfen + Textarea Auto height
        messageLenge();
    }



    /* ************ Message Bearbeiten (die blaue 3 Punkten [...]) + Hilfe-Methoden ************* */


    /**
     * Message Bearbeiten (click auf die drei Punkte oder 'fertig')
     * aufbaue nur dem Pop-up-Fenster
     *              +
     *   alle click in der Pop-up-Fenster
     *              +
     *   Pop-up-Fenster schliessen
     *
     * message.fxml /
     * Label: # headBearbeiten  onMouseClicked (drei Punkten(Bild) oder fertig(Text))
     *
     * auf der ganzen Stage wir eine ArchorPane draufgesetzt mit der
     * click function Zeile 647 (bearPane.setOnMouseClicked) um der
     * Pop-up-Fenster zu schliessen(message bearbeiten, oben mit der 3 punkten'...')
     *
     *  1. hauptStage:  #hauptPane von ChatBoxController(Setter & Getter)
     *  2. bearPane:    die ArchorPane mit dem Pop-Up-Fenster(positioniert über
     *                  ganze Stage(#hauptPane)), transparent
     *  3. vBoxBearbeiten:  der selber Pop-up-Fenster(), positionier oben recht
     *                      in der bearPane.
     *  4.
     */
    @FXML
    private void messageBearbeiten() {

        //System.out.println("Message Bearbeiten");

        double textareaHoch = textareaText.getHeight();
        if (textareaHoch > 40.0){
            // Message Height Merken
            messageHeightMerken = textareaHoch;
        }

        String fertig = headBearbeiten.getText();
        if ("fertig".equals(fertig)){
            altCheckBoxHide();
            return;
        }

        // aufbaue eine pane über die ganze Stage, und ober Rechts das Pop-up-Fenster
        AnchorPane bearPane = new AnchorPane();
        bearPane.setStyle("-fx-background-color: transparent; -fx-border-color: red;");
        bearPane.prefWidthProperty().bind(hauptStage.widthProperty());
        bearPane.prefHeightProperty().bind(hauptStage.heightProperty());

        VBox vBoxBearbeiten = new VBox();
        vBoxBearbeiten.getStyleClass().add("messagesBearbeiten");
        vBoxBearbeiten.setEffect(new DropShadow(5, Color.GRAY));
        AnchorPane.setTopAnchor(vBoxBearbeiten, 25.0);
        AnchorPane.setRightAnchor(vBoxBearbeiten, 20.0);

        // der Pop-up-Fenster in Schleife erstellen
        final String[] imageFiles = new String[]{
                "/static/img/bearbeiten.png",
                "/static/img/info.png",
                "/static/img/close.png"
        };
        final String[] captions = new String[]{
                "Bearbeiten",
                "Info",
                "Verlauf leeren"
        };
        final String[] urls = new String[]{
                "bearbeiten",
                "userinfo",
                "verlaufleeren"
        };
        final Hyperlink[] hpls  = new Hyperlink[captions.length];
        final Image[] images    = new Image[imageFiles.length];

        for (int i = 0; i < captions.length; i++){

            // Bilder Laden
            final Image image = images[i] = new Image(getClass().getResourceAsStream(imageFiles[i]));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(15);
            imageView.setFitWidth(15);

            // image ins hyperlink integrieren
            final Hyperlink hpl = hpls[i] = new Hyperlink(captions[i], imageView);
            hpl.getStyleClass().add("hypersLink");
            hpl.prefWidthProperty().bind(vBoxBearbeiten.widthProperty());

            // Methoden abruffen
            final String url = urls[i];
            hpl.setOnAction((event) -> {
                switch (url){
                    case "bearbeiten":              altCheckBoxGroup(); break;
                    case "userinfo":                userInfo();         break;
                    case "verlaufleeren":           verlaufLeeren();    break;
                    default: break;
                }
                //Pop-Up-Fester ausblenden
                popUpFensterClose(bearPane);
            });
        }

        // VBox mit hyperlink oben Rechts in Haupt-AnchorPane anzeigen
        vBoxBearbeiten.getChildren().addAll(hpls);
        bearPane.getChildren().add(vBoxBearbeiten);
        hauptStage.getChildren().add(bearPane);

        //Pop-Up-Fester ausblenden
        bearPane.setOnMouseClicked(mouseEvent -> {
                popUpFensterClose(bearPane);
        });

    }



    /**
     * Bottom, textarea height auf 20px schrumpfen, für die anzeige
     * von bearbeiten menu(Löschen, Löschen-Count + weiterleiten)
     */
    private void textareaMinHoch(){
        textareaText.setMinHeight(20.0);
    }



    /**
     * TEXTAREA WIEDERHERSTELLEN
     *
     * Bottom, textarea wiederherstellen nach dem Klick auf 'fertig'
     * ursprüngliches Text wieder anzeigen, wenn werde welches verfasst...
     *
     */
    private void textareaWiederHerstellen(){
        textareaText.setMinHeight(messageHeightMerken);
        textareaText.requestFocus();
        textareaText.positionCaret(textareaText.getText().length());
    }



    /**
     * checkBox verwalten(click auf bearbeiten in pop-up-Fenster)
     *
     * die checkBox sind erstellt in Methode: altMessage() und
     * in einen Group gesammelt, die checkBox werden nur per css
     * angezeigt, die font-size werden von 0.1px auf 16px geändert...
     * der VBox(von checkBoxen) wird automatisch geschlossen, weil leer ist
     *
     *
     */
    private void altCheckBoxGroup(){
        // anzeige von checkBoxen + bottom löschen-Menü
        altCheckBoxShow();
        textareaMinHoch();
        messageLoschenDisabled();
    }


    /**
     * Alle CheckBox schliessen
     *
     * bei
     */
    private void altCheckBoxHide(){

        // Header: text löschen, Bild einfügen
        headBearbeiten.setText("");
        blauPunkte.setFitHeight(30);
        blauPunkte.setFitWidth(30);
        headBearbeiten.setGraphic(blauPunkte);

        // checkBox verstecken
        for (CheckBox hide : checkArray){
            hide.setStyle("-fx-font-size: 0.1px;");
            if (hide.isSelected()){
                hide.setSelected(false);
            }
        }

        // Bottom Löschen-Box ausblenden
        messageLoschenHide();
        textareaWiederHerstellen();
    }


    /**
     * CheckBox anzeigen (Alle)
     */
    private void altCheckBoxShow(){

        // Header Bild löschen, Text einfügen
        headBearbeiten.setGraphic(null);
        headBearbeiten.setText("fertig");

        // checkBox anzeigen
        for (CheckBox zeige : checkArray){
            zeige.setStyle("-fx-font-size: 1.0em;");
        }
    }



    /**
     * Disabled Message-Löschen Pane(nur Information anzeige)
     *
     *
     *
     * disable Information Fenster in der bottom bereich
     * wird zusammen nur mit dem 'unchecked checkBox' angezeigt...
     *
     * ausgelöst durch, click auf 'bearbeiten' in Pop-up-Fenster...
     */
    private void messageLoschenDisabled() {

        // messageLoschenAktiv ausblenden
        messageLoschenHide();

        // messageLoschenDisabled Laden
        Parent loschenDisable;
        try {
            loschenDisable = FXMLLoader.load(getClass().getResource("/templates/fragments/messageLoschenDisabled.fxml"));
        } catch (IOException e) {
            // Fehler anzeige functionier nicht, wird java.lang.NullPointerException ausgeführt
            fehlerInfo("NO", "fxmlDisable");
            throw new RuntimeException(e);
        }

        messageLoschenPane.getChildren().add(loschenDisable);
        AnchorPane.setLeftAnchor(loschenDisable, 0.0);
        AnchorPane.setRightAnchor(loschenDisable, 0.0);
        messageBottomStackPane.getChildren().add(messageLoschenPane);
    }



    /**
     * Aktive Message-Löschen, mit Methoden
     *
     * Achtung: zugesendete parameter checkGroup: in diesem Array sind
     * nur selected checkBox gespeichert mit der ID(von dem message)
     * List<Long>: [205, 207, 202, 200]
     * StackPane: [StackPane[id=261, styleClass=altMessageHover],
     *            StackPane[id=273, styleClass=altMessageHover],
     *            StackPane[id=274, styleClass=altMessageHover]]
     *
     */
    private void messageLoschenAktiv(List<Long> checkGruppe, List<StackPane> paneGruppe){

        // messageLoschenDisabled ausblenden
        messageLoschenHide();

        // fxml Laden + alle Node holen
        Parent loschenAktiv ;
        Map<String, Object> idsObject;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/templates/fragments/messageLoschenAktiv.fxml"));
            loschenAktiv = loader.load();
            // Node in einem Object speichern
            idsObject = loader.getNamespace();
        } catch (IOException e) {
            // Fehler anzeige functionier nicht, wird java.lang.NullPointerException ausgeführt
            fehlerInfo("NO", "fxmlAktiv");
            throw new RuntimeException(e);
        }
        // fxml ausgeben/anzeigen + auf 100% length ziehen
        messageLoschenPane.getChildren().add(loschenAktiv);
        AnchorPane.setLeftAnchor(loschenAktiv, 0.0);
        AnchorPane.setRightAnchor(loschenAktiv, 0.0);
        messageBottomStackPane.getChildren().add(messageLoschenPane);

        // Zahl ausgewählte Message zum Löschen anzeigen
        Label loschenCount = (Label)idsObject.get("messageLoschenCount");
        int checkCount = checkGruppe.size();
        loschenCount.setText(String.valueOf(checkCount));

        //Click auf den 'Löschen', unten in angeblendeten box
        HBox clickLoschen = (HBox)idsObject.get("loschenHBox");
        clickLoschen.setOnMouseClicked(mouseEvent -> {
            // alle checkBox & StackPane IDs weiter geben, zum Löschen
            geloschteMessageHide(paneGruppe);
            messageLoschen(checkGruppe);

        });

        // Click auf dem 'Weiterleiten'
        HBox clickWeiterleiten = (HBox)idsObject.get("weiterHBox");
        clickWeiterleiten.setOnMouseClicked(mouseEvent -> {
            //System.out.println("Mesage Weiterleiten:" + checkGroup);
            //messageWeiterleiten(checkGruppe);
        });
    }



    /**
     * Zum Löschen Messages mit Request an Bote senden
     *
     * alle selected checkBox(gewellt zum Löschen) werden in einem List-Array zusammen
     * gesammelt und mit Request an Bote/ApiMessage/messageLoschenApi als List<Long>
     * zum Löschen gesendet...
     *  so sehen gesendete List -Array aus:
     *      [261]                       = eine Message Löschen
     *      [261, 275, 287,356, 360]    = mehrere Message Löschen
     *
     * @param mesageIds
     */
    private void messageLoschen(List<Long> mesageIds){

        String loschenUrl  = configService.FILE_HTTP+"messageLoschenApi";
        HttpResponse<String> response = apiService.requestAPI(loschenUrl, String.valueOf(mesageIds));

        // alle checkBox ausblenden, selected:false
        altCheckBoxHide();

        // Information von gelöschte Elementen anzeigen, nach 5 sek. ausblenden
        int messageCount = Integer.parseInt(response.body());
        if (messageCount > 0){
            loschenInfo(messageCount);
            System.out.println("Gelöschte Message Conut aunzeigen: " + messageCount);
        } else {
            fehlerInfo("NO", "loschenNO");
        }

        System.out.println("response: " + response.body() );


    }


    /**
     * Gelöschte message Ausblenden
     *
     * zugesendete StackPane IDs von gelöschten Message, sind in einem List-Array gesammelt
     * und nach dem Löschen werden aus dem Message-Verlauf gelöscht,
     *
     *  zugesendete List-Array sieht so aus:
     *      [StackPane[id=261, styleClass=altMessageHover]]     = eine message zu Löschen
     *      [StackPane[id=261, styleClass=altMessageHover], StackPane[id=275, styleClass=altMessageHover]] = mehreren
     *
     *  Fazit: StackPane(paneIds) ist ein Message-Block nur von einer einzelnen Message,
     *          bei Löschen die Message wird die StackPane nur gelehrt aber selbe Pane erhalten,
     *          bis zu nächsten Aktualisierung...
     *          kein Style auf StackPane wird aufgelegt
     *
     * @param paneIds
     */
    private void geloschteMessageHide(List<StackPane> paneIds){

        for (StackPane mesagePane : paneIds){
            mesagePane.getChildren().clear();
        }
    }


    /**
     * Alle Message Löschen
     */
    private void verlaufLeeren(){
        System.out.println("Verlauf Leern");
    }



    /**
     * Message Weiterleiten
     *
     * @param nachrichtWeiterleiten
     */
    private void messageWeiterleiten(List<String> nachrichtWeiterleiten){

        System.out.println("Nachricht Weiterleiten: " + nachrichtWeiterleiten );
    }



    /**
     * Löschen Anzeige(bottom) ausblenden
     *
     * der unten in Textarea eingeblendeten Löschen-Info ausblenden
     */
    private void messageLoschenHide(){
        messageLoschenPane.getChildren().clear();
        messageBottomStackPane.getChildren().remove(messageLoschenPane);
    }

    /**
     * der Bearbeiten-Pop-up-Fenster(rechts oben mit 3 punkten '...' ) schliessen
     * Quelle: Methode: messageBearbeiten Zeile: 755
     */
    private void popUpFensterClose(AnchorPane pane){
        pane.getChildren().clear();
        ((Pane)pane.getParent()).getChildren().remove(pane);
    }


    /* ************************* Weiter Methoden ************************** */

    private void userInfo(){
        System.out.println("User Information");
    }


    /**
     * Message schliessen + Reset Hover
     */
    public void messageSchliessen() {
        translate.closeStackPane();
        celleArchorPane.getStyleClass().remove("freundeAktiv");
    }


    /**
     *  Telefonate, Zurzeit funktioniert noch nicht
     */
    public void messageTelefon() {
        System.out.println("Telefonate: functioniert noch nicht");
    }


    /**
     * Foto, Datei, Kamera Zeigen
     */
    public void otherZeigen() {
        System.out.println("Bild + Datei + Kamera");
    }


    /**
     * Anzeige von Smile Icon
     */
    public void smileZeigen() {
        System.out.println("Smile zeigen");
    }



    /* ************************** Fehler Anzeigen **************** */

    /**
     * Kurze anzeige für gelöschte Messages
     *
     * @param count
     */
    final AnchorPane loschPane = new AnchorPane();
    private void loschenInfo(int count){
        System.out.println("loschen Info vor: " +count);
        // Loschen Pane Leeren
        messageLoschenHide();

        System.out.println("loschen Info danach: " +count);
        Label loschInfo = new Label(count + " Messages werden gelöscht");
        loschInfo.getStyleClass().add("loschInfo");
        //loschInfo.setMaxWidth(Double.MIN_NORMAL);
        AnchorPane.setLeftAnchor(loschInfo, Region.USE_COMPUTED_SIZE);
        //AnchorPane.setTopAnchor(loschInfo, 5d);
        AnchorPane.setRightAnchor(loschInfo, Region.USE_COMPUTED_SIZE);
        //AnchorPane.setBottomAnchor(loschInfo, 5d);
        loschInfo.setAlignment(Pos.CENTER);
        loschPane.getChildren().add(loschInfo);
        hauptStage.getChildren().add(loschPane);

        // Anzeige Ausblenden
        pauseZeit("loschPane", 10);

    }



    /**
     * Fehler Anzeigen
     *
     * automatische ausblenden von Fehler-Anzeige,  ca. 5 Sek....
     *
     * ACHTUNG: zurzeit sid alle Fehlermeldungen ausgeblendet
     *      1. sockedConnect: Zeile 243, 250, 263
     *      2. messageSenden: Zeile 427, 436
     *      3. Aktive Fehler:
     *          a. messageLoschen Zeile: 1000
     *          b. messageLoschenAktiv(.. Zeile: 900 (functionier auch nicht)
     *
     *      ***für die fehler ausgabe gibst separate Label(#headFreundInfo)
     *
     * @param ok
     * @param fehlerText
     */
    private void fehlerInfo(String ok, String fehlerText){

        headFreundInfo.setTextFill(  ok == "OK" ? Color.GREEN : Color.RED );

        switch (fehlerText){
            case "connectOk":           headFreundInfo.setText("connection successfully established...");
                                        break;
            case "connectNo":           headFreundInfo.setText("connection not successfully established...");
                                        break;
            case "sessionOk":           headFreundInfo.setText("Session Verbindung aufgebaut...");
                                        break;
            case "sessionNo":           headFreundInfo.setText("Es besteht keine Verbindung!");
                                        break;
            case "textLeer":            headFreundInfo.setText("Feld darf nicht leer sein");
                                        break;
            case "fxmlDisable":         headFreundInfo.setText("125: xml kann nicht geladen werden");
                                        break;
            case "fxmlAktiv":           headFreundInfo.setText("126: xml kann nicht geladen werden");
                                        break;
            case "loschenNO":           headFreundInfo.setText("127: Message Löschen fehlgeschlagen");
                                        break;
            default: break;

        }

        pauseZeit("infoLabel", 10);
    }


    /**
     * Automatische ausblenden von Pop-up-Fenster nach der Zeit
     *
     *  1. Label: headFreundInfo, Fehler anzeige in Header bereich
     *  2. AnchorPane 'loschPane', Haup-Box von Pop-up-Fenster für
     *     die Anzeigen gelöschte messages,
     *     Methode: loschenInfo(... Zeile: 1150
     *
     * @param pane
     * @param sek
     */
    private void pauseZeit(String pane, int sek){
        PauseTransition pause = new PauseTransition(Duration.seconds(sek));
        pause.setOnFinished(e -> {
            switch (pane) {
                case "infoLabel":           headFreundInfo.setText(null);
                                            break;
                case "loschPane":           popUpFensterClose(loschPane);
                                            break;
                default: break;
            }
        });
        pause.play();
    }

}
