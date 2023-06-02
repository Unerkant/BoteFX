package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.fragments.WeiterleitenController;
import BoteFx.model.Freunde;
import BoteFx.model.Message;
import BoteFx.model.Usern;
import BoteFx.service.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Den 24.11.2022
 */

@Controller
public class MessageController implements Initializable {

    @Autowired
    private SocketService socketService;
    @Autowired
    private TranslateService translate;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private MethodenService methodenService;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private UserdataService userdataService;


    @FXML private AnchorPane messageAnchorPane;
    @FXML private StackPane messageHauptStackPane;
    @FXML private BorderPane messageBorderPane;
    @FXML private StackPane messageTopStackPane;
    @FXML private Label messageProfilBild;
    @FXML private Label headFreundName;
    @FXML private Label headFreundInfo;
    @FXML private Label freundOnlineZeit;
    @FXML public ImageView onlinePhone;
    @FXML public ImageView noPhone;
    @FXML private Label headBearbeiten;
    @FXML private StackPane messageCenterStackPane;
    @FXML private ScrollPane messageScrollPane;
    @FXML private StackPane messageBottomStackPane;
    @FXML private ImageView messageOther;
    @FXML private TextArea textareaText;
    @FXML private ImageView messageSmile;
    @FXML private VBox messageVBox;


    // Allgemein
    /**
     * Haupt Stage von ChatBoxController
     */
    @FXML private StackPane hauptStage;
    @FXML private StackPane rightStage;
    @FXML private String myColor;
    private String meinerToken;
    Usern mineData = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Message VBox auf 100% ziehen*/
        messageVBox.prefWidthProperty().bind(messageAnchorPane.widthProperty());

        /* HauptPane ID holen, ChatBoxController */
        hauptStage = chatBoxController.getHauptStackPane();

        /* rightPane ID holen, ChatBoxController */
        rightStage = chatBoxController.getRightPane();

        // Bottom: TextArea in Focus setzen
        Platform.runLater(() -> textareaText.requestFocus());

        // Methode: altMessage, rundes bild-hintergrund, Zeile: ab 415
        myColor = methodenService.zufallColor();

        // Meiner Token
        meinerToken = tokenService.meinToken();

        // Meine Daten
        mineData = userdataService.meineDaten();
    }


    /* ****************** Setter & Getter von FreundeCellController Zeile: 318 ****************  */



    /**
     * User Bild oder Pseudonym hintergrund Color, zugesendet von
     * FreundeCellController/ Zeile: 323
     *
     * der User Bild-Hintergrund oder Message-Bild oder Freunde-Bild/pseudonym
     * sind alle identisch
     */
    private String freundColor;
    public String getFreundColor() { return freundColor; }
    public void setFreundColor(String freundColor) { this.freundColor = freundColor; }



    /**
     * celleAnchorPane - ist in FreundeCellController den Freunde-Box/ Freunde-Klick-Box
     */
    private AnchorPane celleArchorPane;
    public AnchorPane getCelleArchorPane() { return celleArchorPane; }
    public void setCelleArchorPane(AnchorPane celleArchorPane) { this.celleArchorPane = celleArchorPane; }



    /**
     * Message Token von FreuneCellContorlle zugesendet: Zeile: 322
     * alle Messages aus der Datenbank holen und den Methode altMessage(), ausgeben
     */
    private String messageToken;
    private ArrayList<Message> allMessages;
    public String getMessageToken() { return messageToken;}
    public void setMessageToken(String mesagetoken) {
        this.messageToken = mesagetoken;

        // Alte Message von MySql(Tabelle: messages) Holen
        // Request an Bote & response
        String messUrl  = configService.FILE_HTTP+"messageApi";
        String messJson = "{\"messagesToken\":\""+messageToken+"\"}";
        HttpResponse<String> response = apiService.requestAPI(messUrl, messJson);

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
        allMessages  = gson.fromJson(response.body(), listType);

        //Collections.reverse(allMessages);
        messagesAusgeben(allMessages);

    }


    /**
     * Freunde Daten zugesendet von FreudeCellController Zeile: 320
     *
     * werde in Header der message Name + online-Zeit angezeigt
     */
    private Freunde chatFreundeDaten;
    private Image headPunkte;
    private ImageView blauPunkte;
    private String freundToken;
    public Freunde getChatFreundeDaten() { return chatFreundeDaten; }
    public void setChatFreundeDaten(Freunde chatfreundedaten) {
        this.chatFreundeDaten = chatfreundedaten;

        headPunkte = new Image(getClass().getResourceAsStream("/static/img/punktenblue.png"));
        blauPunkte = new ImageView(headPunkte);
        freundToken = chatFreundeDaten.getFreundetoken();

        // methode unten Zeile: 220
        headerFreundeDaten(chatFreundeDaten);
    }





    /* *********************** Header, Freunde Daten Anzeigen ************************* */

    private void headerFreundeDaten(Freunde freundData){
        String freundBild = freundData.getFreundebild();
        String freundPseudonym = freundData.getFreundepseudonym();

        // Profil Bild von Bote holen
        Image headFreundImg = new Image(configService.FILE_HTTP+"profilbild/"+freundBild+".png", true);
        if (headFreundImg.isError()){
            messageProfilBild.setText(freundPseudonym);
            messageProfilBild.setAlignment(Pos.CENTER);
            messageProfilBild.setStyle("-fx-background-radius: 25;"+"-fx-background-color:"+ freundColor +";" +
                    "-fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700; -fx-text-fill: white;");
        }else {
            ImageView imageView = new ImageView(headFreundImg);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            messageProfilBild.setGraphic(imageView);
        }

        // Name Anzeigen
        headFreundName.setText(freundData.getFreundename().isBlank() ? freundData.getFreundepseudonym() :
                freundData.getFreundevorname() +" "+ freundData.getFreundename());

        // Letzte Online Zeit(style direct in message.fxml)
        // freundOnlineZeit.setText("???...MessageControll..Zeile: 358");

        // Click auf die drei Punkten, onMouseClicked(messageBearbeitenStart)
        blauPunkte.setFitWidth(20);
        blauPunkte.setFitHeight(20);
        headBearbeiten.setGraphic(blauPunkte);
    }


    /* *********************** Alte Message + neue Message Ausgabe ******************* */



    /**
     *  Allgemeine variable für die Methoden messageAusgeben + neueMessage
     */
    final List<Long> checkBoxSelected = new ArrayList<>();              // CheckBox: nur selected
    final List<StackPane> stackPaneSelected = new ArrayList<>();        // StackPane: zum Löschen, hover
    final List<CheckBox> allCheckBoxArray = new ArrayList<>();          // CheckBox: alle, benutzt in cellCheckBoxHide()
    final List<VBox> cellRightVBoxArray = new ArrayList<>();            // VBox: alle, für die show/hide CheckBox
    final List<String> messageWeiterleitenArray = new ArrayList<>();    // alle selected-messages für weiterleiten
    private VBox cellRightVBox;                                         // VBox: CheckBox(show/hide) Zeile:395/ab 700


    /**
     * Neue Message Anzeigen, gestartet von ChatBoxController/ Zeile: 275
     * die einzelne(Letzte/Live) zugesendete neuMessage wird in einem
     * ArrayList<> umgewandelt, weil die Alte Message von der Datenbank
     * sind in einem List-Array gespeichert...
     *
     * die Methode messagesAusgeben akzeptiert als Parameter einen ArrayList
     *
     * @param neuMessage
     */
    public void neueMessage(Message neuMessage) {


        ArrayList<Message> newMessage = new ArrayList<Message>(Collections.singleton(neuMessage));

        messagesAusgeben(newMessage);
        scrollBottom(messageScrollPane);
    }


    /**
     * Alte Message Ausgeben, gestartet Zeile: 160
     * @param allmesage
     */
    private void messagesAusgeben(ArrayList<Message> allmesage){

        String meinToken = tokenService.meinToken();

        for (Message mess : allmesage){

        /**
         *  1. StackPane: Haupt Pane, mit iD: cellHauptStackPane
         *
         *  hier wird nur eine message ausgegeben mit allem Daten,
         *  User Bild, User Name, Gelesen/Ungelesen, message Datum,
         *  Bilder, message ausgabe + CheckBox für bearbeitung
         */
            StackPane cellHauptStackPane = new StackPane();
            cellHauptStackPane.setId(String.valueOf(mess.getId()));

        /**
         *  2. VBox(Label)
         *
         *     VBox(Label(User Bild))
         *     der VBox wird in der Position N.5 in die BorderPane integriert
         */
            Label cellUserBildLabel = new Label(mess.getPseudonym());
            cellUserBildLabel.getStyleClass().add("cellBildsLabel");
            // Bild von Bote holen
            Image cellProfilImg = new Image(configService.FILE_HTTP+"profilbild/"+mess.getMeintoken()+".png", true);
            if (cellProfilImg.isError()){
                cellUserBildLabel.setText(mess.getPseudonym());
                cellUserBildLabel.setStyle( "-fx-background-color:" + (freundToken.equals(mess.getMeintoken()) ? freundColor : myColor) + "; " +
                        "-fx-text-fill: white; -fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700;");
            }else {
                ImageView cellProfilBild = new ImageView(cellProfilImg);
                cellProfilBild.setFitHeight(30);
                cellProfilBild.setFitWidth(30);
                cellUserBildLabel.setGraphic(cellProfilBild);
            }
            // Label(bild) ins VBox integrieren
            VBox cellLeftVBox = new VBox(cellUserBildLabel);
            cellLeftVBox.getStyleClass().add("cellLinksVBox");

        /**
         *  3. VBox(GridPane + Label + TextFlow)
         *
         *      VBox(
         *           GridPane( Label:Name, Label:Korrektur, Image:hacken, Label:Datum)
         *           Label('vorgesehen für Bilder Ausgabe')
         *           TextFlow(Text(Message Ausgabe))
         *          )
         *
         *        der VBox wird in der Position N.5 in die BorderPane mit der
         *        ID: cellBorderPane in den Center integriert
         */
            // a. Username
            Label cellUserName = new Label(mess.getName().isBlank() ? mess.getPseudonym() : mess.getVorname() +" "+ mess.getName());
            cellUserName.getStyleClass().add("cellUsersName");

            // b. Text-Message Korrigieren (text anzeige: bearbeitet)
            Label cellTextKorrektur = new Label("bearbeitet");
            cellTextKorrektur.getStyleClass().add("cellMesagesZeit");

            // b. Hacken Grau + Grün
            StackPane cellDonePane = new StackPane();
            ImageView imgHakenGrau = new ImageView(new Image(getClass().getResourceAsStream("/static/img/hacken.png")));
            ImageView imgHakenGrun = new ImageView(new Image(getClass().getResourceAsStream("/static/img/donegreen.png")));
            imgHakenGrau.setFitWidth(15);
            imgHakenGrau.setFitHeight(15);
            imgHakenGrun.setFitWidth(15);
            imgHakenGrun.setFitHeight(15);

            imgHakenGrau.setVisible(false);
            imgHakenGrun.setVisible(false);
            cellDonePane.getChildren().addAll(imgHakenGrun, imgHakenGrau);

            // c. Message Zeit
            String zeit = mess.getDatum().substring(8,14);
            Label cellMessageZeit = new Label(zeit);
            cellMessageZeit.getStyleClass().add("cellMesagesZeit");

            // d. GridPane + add column(),
            GridPane cellGridPane = new GridPane();
            // Top Pos. von hacken + Zeit
            GridPane.setValignment(cellTextKorrektur, VPos.TOP);
            GridPane.setValignment(imgHakenGrau, VPos.TOP);
            GridPane.setValignment(imgHakenGrun, VPos.TOP);
            GridPane.setValignment(cellMessageZeit, VPos.TOP);
            // new column + width
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setHgrow(Priority.ALWAYS);
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setMinWidth(50);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setMinWidth(20);
            ColumnConstraints column3 = new ColumnConstraints();
            column3.setMinWidth(40);
            // add column
            cellGridPane.getColumnConstraints().addAll(column0, column1, column2, column3);
            cellGridPane.addColumn(0, cellUserName);
            cellGridPane.addColumn(1, cellTextKorrektur);
            cellGridPane.addColumn(2, cellDonePane);
            cellGridPane.addColumn(3, cellMessageZeit);
            

            // Label(Bilder anzeige), Label(message anzeigen)
            Label cellMessageBilder = new Label("hier werden Bilder Angezeigt");
            cellMessageBilder.getStyleClass().add("cellMesagesBilder");

            // Message-Text ausgeben
            String str = mess.getText();
            Text text = new Text(str);
            TextFlow textFlow = new TextFlow(text);
            textFlow.getStyleClass().add("cellTextsFlow");

            // add VBox (GridPane + Label + Label)
            VBox cellCenterVBox = new VBox(cellGridPane, cellMessageBilder, textFlow);
            cellCenterVBox.getStyleClass().add("cellMittesVBox");

            
        /**
         *  4.  AnchorPane( VBox( CheckBox(id) ) )
         */
            CheckBox cellCheckBox = new CheckBox();
            cellCheckBox.getStyleClass().add("cellChecksBox");
            cellCheckBox.setId(String.valueOf(mess.getId()));

            cellRightVBox = new VBox(cellCheckBox);
            cellRightVBox.setId(String.valueOf(mess.getId()));

            // CheckBox verstecken, show/hide ab Zeile: 700
            AnchorPane cellRightAnchorPane = new AnchorPane(cellRightVBox);
            AnchorPane.setRightAnchor(cellRightVBox, -25.0);

            // Click auf die Zeit Anzeige
            cellMessageZeit.setOnMouseClicked((event) ->{
                clickAufZeitAnzeige();
                cellCheckBox.setSelected(true);
            });

            cellRightVBoxArray.add(cellRightVBox);
            allCheckBoxArray.add(cellCheckBox);

            cellCheckBox.selectedProperty().addListener((observable, oldValue, selectedNow) -> {

                /* observable, oldValue, selectedNow: output:~ selected/false/true */
                /* selected message in Array speichern + hover setzen und umgekehrt */
                if (selectedNow) {
                    checkBoxSelected.add(Long.valueOf(cellCheckBox.getId()));
                    stackPaneSelected.add(cellHauptStackPane);
                    cellHauptStackPane.getStyleClass().add("cellMessagesHover");
                    messageWeiterleitenArray.add(mess.getText());
                } else {
                    checkBoxSelected.remove(Long.valueOf(cellCheckBox.getId()));
                    stackPaneSelected.remove(cellHauptStackPane);
                    cellHauptStackPane.getStyleClass().remove("cellMessagesHover");
                    messageWeiterleitenArray.remove(mess.getText());
                }


                // Message Löschen Teil
                if (checkBoxSelected.size() == 0 && stackPaneSelected.size() == 0){

                    // keine checkBox aus gewellt, alles schliessen
                    messageLoschenHide();
                    cellCheckBoxHide();

                    /* Message Löschen, Zeile: 950 */
                    loschenDisabled();

                } else {

                    /* Message Löschen, Zeile: 800 */
                    messageLoschenHandle(checkBoxSelected, stackPaneSelected, messageWeiterleitenArray);

                }

            });

        /**
         *  5. BorderPane(Left:VBox(Bild), Center:VBox(message), Right:AnchorPane(CheckBox))
         */
            BorderPane cellBorderPane = new BorderPane();
            cellBorderPane.getStyleClass().add("cellBordersPane");
            cellBorderPane.setLeft(cellLeftVBox);
            cellBorderPane.setCenter(cellCenterVBox);
            cellBorderPane.setRight(cellRightAnchorPane);
            cellHauptStackPane.getChildren().add(cellBorderPane);


        /**
         *  6.  message-box anzeigen
         */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    messageVBox.getChildren().add(cellHauptStackPane);

                    // Bottom Scroll, immer letzte Message Anzeigen
                    messageScrollPane.setVvalue(1.0);
                    //scrollBottom(messageScrollPane);
                }
            });

        } // Ende for schleife
    }


    /**
     * die scrollPane immer nach oben scrollen
     *
     * Duration.seconds oder millis
     *
     * @param pane
     */
    private void scrollBottom(ScrollPane pane){
        Animation animation = new Timeline(
                new KeyFrame(Duration.millis(600),
                        new KeyValue(pane.vvalueProperty(), 1)));
        animation.play();
    }


    /* ******************* Message Senden ************************ */


    /**
     * MESSAGE SENDEN
     *
     * die Methode reagiert auf Tasten druck, onKeyReleased,
     * gesendet nut per ENTER
     *
     * Message-Array:
     *      Message{id='0', datum='24.04.23 16:08', freundetoken='09042023111429', meintoken='12042023204557',
     *      messagetoken='12042023205845', pseudonym='JC', vorname='Java', name='Client',
     *      text='so sieht einen Array von message', role='default'}
     *
     * @param keyEvent
     */
    private  Double messageHeightMerken = 20.0;
    @FXML
    private void messageSenden(KeyEvent keyEvent) {

        String messagesText = textareaText.getText();

        // Text Field auf Leer Prüfen
        if (messagesText == null || messagesText.trim().length() == 0) {
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
            message.setMeintoken(meinerToken);
            message.setMessagetoken(messageToken);
            message.setPseudonym(mineData.getPseudonym());
            message.setVorname(mineData.getVorname());
            message.setName(mineData.getName());
            message.setText(messagesText);
            message.setRole("default");

            try {
                socketService.senden(message);
                textareaText.clear();
                //messageHeightMerken = 20.0;
                textareaMinHoch();
            } catch (Exception e){

                textareaText.clear();
            }

        } else {
            textareaAutoRow(messagesText);
        }
        // Text auf Länge prüfen + Textarea Auto height
        messageLenge();
    }


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



    /* *************** Methoden von der Pop-up-Fenster(mit drei Punkten) ********************* */


    /**
     * Click auf drei Punkte & 'fertig'
     */
    public void messageBearbeitenStart(){

        textareaHochMerken();

        String fertig = headBearbeiten.getText();
        if ("fertig".equals(fertig)){

            cellCheckBoxHide();
            messageLoschenHide();

        } else {

            messageBearbeiten();
        }
    }

    /**
     * click auf die Datum/uhrzeit
     */
    public void clickAufZeitAnzeige(){

        textareaHochMerken();

        String fertig = headBearbeiten.getText();
        if ("fertig".equals(fertig)){

            cellCheckBoxHide();
            messageLoschenHide();

        } else {

            cellCheckBoxShow();
            messageLoschenShow();
            textareaMinHoch();
        }
    }


    /**
     *  Header: Text 'fertig' gegen Bild(drei Punkte) tauschen
     */
    private void clickDreiPunkteShow(){

        headBearbeiten.setText("");
        blauPunkte.setFitHeight(30);
        blauPunkte.setFitWidth(30);
        headBearbeiten.setGraphic(blauPunkte);
    }


    /**
     *  Header: Bild-drei-Punkte gegen, Text 'fertig' tauschen
     */
    private void clickDreiPunkteHide(){

        headBearbeiten.setGraphic(null);
        headBearbeiten.setText("fertig");
    }


    /**
     * der Pop-up-Fenster mit verschiedene click Methoden
     *
     * aufbaue eine pane über die ganze Stage, und ober Rechts das Pop-up-Fenster,
     * mit click auf die oberfläche in der ganzen Stage wird der Pop-up-Fenster geschlossen
     * popUpFensterClose(spiegelungHaupStage);
     */
    private void messageBearbeiten() {

        AnchorPane spiegelungHaupStage = new AnchorPane();
        spiegelungHaupStage.setStyle("-fx-background-color: transparent;");

        VBox popUpBearbeiten = new VBox();
        popUpBearbeiten.getStyleClass().add("messagesBearbeiten");
        AnchorPane.setTopAnchor(popUpBearbeiten, 25.0);
        AnchorPane.setRightAnchor(popUpBearbeiten, 20.0);

        // der Pop-up-Fenster in Schleife erstellen
        final String[] imageFiles = new String[]{
                "/static/img/icons/bearbeiten-36.png",
                "/static/img/icons/info-36.png",
                "/static/img/icons/benutzergruppe-36.png",
                "",
                "/static/img/icons/stopuhr-36.png",
                "/static/img/icons/close-36.png",
                "/static/img/icons/deletered-36.png"
        };
        final String[] captions = new String[]{
                "Bearbeiten",
                "Info",
                "Gruppe erstellen",
                "",
                "Auto-Löschung von Nachrichten",
                "Verlauf leeren",
                "Chat Löschen"
        };
        final String[] urls = new String[]{
                "bearbeiten",
                "userinfo",
                "gruppeerstellen",
                "",
                "autoleoschen",
                "verlaufleeren",
                "chatleoschen"
        };
        final Hyperlink[] hpls  = new Hyperlink[captions.length];
        final Image[] images    = new Image[imageFiles.length];

        for (int i = 0; i < captions.length; i++){

            // Bilder Laden
            final Image image = images[i] = new Image(getClass().getResourceAsStream(imageFiles[i]));
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);

            // text + image ins hyperlink integrieren
            final Hyperlink hpl = hpls[i] = new Hyperlink(captions[i], imageView);
            hpl.getStyleClass().add("hypersLink");
            hpl.prefWidthProperty().bind(popUpBearbeiten.widthProperty());

            // Trennlinie, aus dem Leeren Hyperlink, nur mit css
            if (i == 3){
                hpl.getStyleClass().add("hypersLinkHR");
                hpl.setDisable(true);
            }

            // Methoden abruffen
            final String url = urls[i];
            hpl.setOnAction((event) -> {
                switch (url){
                    case "bearbeiten":              cellCheckBoxZeigen();   break;
                    case "userinfo":                userInfo();             break;
                    case "gruppeerstellen":         gruppeErstellen();      break;
                    case "autoleoschen":            autoLoschen();          break;
                    case "verlaufleeren":           verlaufLeeren();        break;
                    case "chatleoschen":            chatLeoschen();         break;
                    default: break;
                }
                //Pop-Up-Fester ausblende
                methodenService. popUpFensterClose(spiegelungHaupStage);
            });
        }

        // VBox mit hyperlink oben Rechts in Haupt-AnchorPane anzeigen
        popUpBearbeiten.getChildren().addAll(hpls);
        spiegelungHaupStage.getChildren().add(popUpBearbeiten);
        hauptStage.getChildren().add(spiegelungHaupStage);


        // Leer, keine message vorhanden, link-bearbeiten:disabled
        Hyperlink linkDisabled = hpls[0];
        if (allMessages.size() == 0) {
            linkDisabled.setDisable(true);
        }

        // RED-Links, Letzte eintrag....  chat löschen style-class: hypersLinkRed
        Hyperlink letzteLinks = hpls[hpls.length -1];
        if (letzteLinks != null) {
            letzteLinks.getStyleClass().add("hypersLinkRed");
        }

        //Pop-Up-Fester ausblenden
        spiegelungHaupStage.setOnMouseClicked(mouseEvent -> {
            methodenService.popUpFensterClose(spiegelungHaupStage);
        });
    }


    /**
     * click auf bearbeiten in der Pop-up-Fenster...
     */
    private void cellCheckBoxZeigen(){

        cellCheckBoxShow();
        textareaMinHoch();
        clickDreiPunkteHide();
        // Löschen Teil, ab 800
        messageLoschenShow();
        
    }


    /**
     * Alle checkBox anzeigen
     *
     */
    private void cellCheckBoxShow(){

        clickDreiPunkteHide();

        for (VBox show : cellRightVBoxArray){
            AnchorPane.setRightAnchor(show, 1.0);
        }
    }


    /**
     * Alle CheckBox wieder verstecken + unselected, wenn
     * selected waren
     */
    private void cellCheckBoxHide(){

        clickDreiPunkteShow();
        textareaWiederHerstellen();

        for (VBox hide : cellRightVBoxArray){
            AnchorPane.setRightAnchor(hide, -25.0);
        }

        // checkBox unselected,
        for (CheckBox hide : allCheckBoxArray){
            if (hide.isSelected()){
                hide.setSelected(false);
            }
        }

    }



    private void textareaHochMerken(){
        double textareaHoch = textareaText.getHeight();
        if (textareaHoch > 40.0){
            // Message Height Merken
            messageHeightMerken = textareaHoch;
        }
    }

    
    /**
     * Bottom, textarea height auf 20px schrumpfen, für die anzeige
     * der Löschen-Fenster
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


    /* *************** Message Löschen *********************** */

    /**
     * Message Löschen
     * die Methode messageLoschenHandle wird gestartet in der Methode
     * cellCheckBoxZeigen() / Zeile: 700
     *
     * Achtung: die zugesendete parameter checkBoxSelected + StackPaneSelected:
     * sind selectierte CheckBox + StackPane in einem List-Array gesammelt
     * mit gespeicherten ID von der Message...
     *
     * mit diesem Format werden sie mit requestApi versendet
     * List<Long>: [386, 385, 384]
     * StackPane: [
     *              StackPane[id=386, styleClass=cellMessagesHover],
     *              StackPane[id=385, styleClass=cellMessagesHover],
     *              StackPane[id=384, styleClass=cellMessagesHover]
     *            ]
     */
    int checkCount;
    final AnchorPane messageLoschenPane = new AnchorPane();
    final Label loschLabelDisable = new Label("Löschen");
    final Hyperlink loschHyperlinkAktiv = new Hyperlink("Löschen");
    final Label loschCount = new Label();
    final Label loschAuswehlenLabel = new Label("Nachrichten auswählen");
    final Label weiterLabelDisable = new Label("Weiterleiten");
    final Hyperlink weiterHyperlinkAktiv = new Hyperlink("Weiterleiten");
    /*List<Long> checkGruppe, List<StackPane> paneGruppe*/
    private void messageLoschenHandle(List<Long> checkGruppe, List<StackPane> stackGruppe, List<String> messageGruppe){

        checkCount = checkGruppe.size();


        loschenAktiv();

        /* Message Löschen, Zeile: 980*/
        loschHyperlinkAktiv.setOnAction((event) -> {
            
            messageLoschen(checkGruppe, stackGruppe);
        });
        
        /* Message Weiterleiten Handle, Zeile: 1000*/
        weiterHyperlinkAktiv.setOnAction((event) -> {
            messageWeiterleitenHandle(messageGruppe);
        });

        //System.out.println("Loschen Handle:" + checkCount);
    }



    /**
     * Message Löschen Block wird unten in Textarea Bereich angezeigt
     * gestartet wird in oberer Pop-up-Fenster 'bearbeiten', Zeile: 690
     *
     * Message Löschen Block Beschreibung:
     * Haupt Pane ist eine AnchorPan, mit integrierte GridPane mit 3 column
     *  a. links column: StackPane mit Label(Text + Bild), Disabled + Hyperlink(Text + Bild), Aktiv(Red)
     *  b. Center column: HBox mit Label(Count) + Label(Text)
     *  c. Right column: StackPane mit Label(Text + Bild), Disabled + Hyperlink(Text + Bild), Aktiv(Red)
     * schliesslich wird die Haup-Löschen-Block, AnchorPane in einer StackPane angezeigt( show/hide )
     */
    private void messageLoschenShow(){

        /**
         * StackPane( Label(Image + Text),   Hyperlink(Bild + Text) ), wird ins GridPane/Left integriert
         */
        ImageView loschImg = new ImageView(new Image(getClass().getResourceAsStream("/static/img/delete.png")));
        ImageView loschImgRed = new ImageView(new Image(getClass().getResourceAsStream("/static/img/deletered.png")));
        loschImg.setFitWidth(25);
        loschImg.setFitHeight(25);
        loschImgRed.setFitWidth(25);
        loschImgRed.setFitHeight(25);

        loschLabelDisable.setGraphic(loschImg);
        loschLabelDisable.getStyleClass().add("loschDisabled");

        loschHyperlinkAktiv.setGraphic(loschImgRed);
        loschHyperlinkAktiv.getStyleClass().add("loschAktiv");
        loschHyperlinkAktiv.setVisible(false);

        StackPane loschStackPane = new StackPane(loschLabelDisable, loschHyperlinkAktiv);

        /**
         * HBox( Label(Zahl) + Label(Nachrichten Ausgewählt) ), wird in GridPane/Center integriert
         */
        loschCount.getStyleClass().add("loschAktiv");
        loschAuswehlenLabel.getStyleClass().add("loschDisabled");
        HBox countHBox = new HBox(loschCount, loschAuswehlenLabel);
        countHBox.getStyleClass().add("loschHBox");
        countHBox.setAlignment(Pos.CENTER);

        /**
         * StackPane(Label(Text + Bild), Hyperlink(Text + Bild), wird in GridPane/Right integriert
         */
        ImageView weiterImg = new ImageView(new Image(getClass().getResourceAsStream("/static/img/forwardblack.png")));
        ImageView weiterImgBlau = new ImageView(new Image(getClass().getResourceAsStream("/static/img/forwardblue.png")));
        weiterImg.setFitWidth(30);
        weiterImg.setFitHeight(30);
        weiterImgBlau.setFitWidth(30);
        weiterImgBlau.setFitHeight(30);

        weiterLabelDisable.setGraphic(weiterImg);
        weiterLabelDisable.setContentDisplay(ContentDisplay.RIGHT);
        weiterLabelDisable.getStyleClass().add("loschDisabled");

        weiterHyperlinkAktiv.setGraphic(weiterImgBlau);
        weiterHyperlinkAktiv.setContentDisplay(ContentDisplay.RIGHT);
        weiterHyperlinkAktiv.getStyleClass().add("loschBlau");
        weiterHyperlinkAktiv.setVisible(false);

        StackPane weiterStackPane = new StackPane(weiterLabelDisable, weiterHyperlinkAktiv);

        /**
         * GridPane, Integration von 3 HBox
         */
        GridPane loschGridPane = new GridPane();
        AnchorPane.setLeftAnchor(loschGridPane, 0.0);
        AnchorPane.setTopAnchor(loschGridPane, 0.0);
        AnchorPane.setRightAnchor(loschGridPane, 0.0);
        AnchorPane.setBottomAnchor(loschGridPane, 0.0);

        ColumnConstraints column0 = new ColumnConstraints();
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column0.setMinWidth(80);
        column1.setHgrow(Priority.ALWAYS);
        column2.setMinWidth(80);

        loschGridPane.getColumnConstraints().addAll(column0, column1, column2);
        loschGridPane.addColumn(0, loschStackPane);
        loschGridPane.addColumn(1, countHBox);
        loschGridPane.addColumn(2, weiterStackPane);

        /* Haupt Block, AnchorPane Zeile: 813 */
        messageLoschenPane.getChildren().add(loschGridPane);
        messageLoschenPane.getStyleClass().add("loschAnchorPane");

        /* StackPane, bestand Teil die message.fxml/bottom  */
        messageBottomStackPane.getChildren().add(messageLoschenPane);

    }


    /**
     * Message Löschen Block unten in Textarea: ausblenden
     *
     */
    private void messageLoschenHide(){
        messageLoschenPane.getChildren().clear();
        //loschGridPane.getChildren().clear();
       // messageBottomStackPane.getChildren().remove(loschGridPane);
        messageBottomStackPane.getChildren().remove(messageLoschenPane);
    }


    /**
     * Message Löschen Bloch unten in Textarea: visible: false setzen
     */
    private void loschenDisabled(){

        loschLabelDisable.setVisible(true);
        loschHyperlinkAktiv.setVisible(false);

        loschCount.setText("");
        loschAuswehlenLabel.setStyle("-fx-text-fill: #858585;");

        weiterLabelDisable.setVisible(true);
        weiterHyperlinkAktiv.setVisible(false);

    }


    /**
     * Message Löschen, unten in Textarea: visible: true setzen
     */
    private void loschenAktiv(){

        loschLabelDisable.setVisible(false);
        loschHyperlinkAktiv.setVisible(true);


        loschCount.setText(String.valueOf(checkCount));
        if (checkCount == 1){
            loschAuswehlenLabel.setText("Nachricht ausgewählt");
        } else {
            loschAuswehlenLabel.setText("Nachrichten ausgewählt");
        }
        loschAuswehlenLabel.setStyle("-fx-text-fill: #000000;");

        weiterLabelDisable.setVisible(false);
        weiterHyperlinkAktiv.setVisible(true);

    }


    /**
     * Selectierte message aus der Datenbank löschen + aktuelle-gelöschte message ausblenden
     *
     * @param checkID
     * @param stackID
     */
    private void messageLoschen(List<Long> checkID, List<StackPane> stackID){

        String loschUrl  = configService.FILE_HTTP+"messageLoschenApi";
        HttpResponse<String> response = apiService.requestAPI(loschUrl, String.valueOf(checkID));

        // Gelöschte Message ausblenden
        geloschteMessageHide(stackID);

        // Message Löschen Bloch unten in Textarea: ausblenden
        messageLoschenHide();
        cellCheckBoxHide();

        // Kurze Information Anzeigen
        String text =  response.body()+" Messages werden gelöscht! ";
        messagesPopUpFensterInfo(messageCenterStackPane, text);
    }


    /**
     * gelöschte Message ausblenden, live
     * @param paneIds
     */
    private void geloschteMessageHide(List<StackPane> paneIds){

        for (StackPane mesagePane : paneIds){
            mesagePane.getChildren().clear();
        }
    }


    /* ******************* Message Weiterleiten **************** */

    /**
     * Message Weiterleiten
     */
    private void messageWeiterleitenHandle(List<String> mesageText){


        layoutService.setausgabeLayout(hauptStage);
        // fxml laden mit switchLayout oder switchView
        WeiterleitenController weiterleitenController = (WeiterleitenController) layoutService.switchLayout(GlobalView.MESSAGEWEITERLEITEN);
        weiterleitenController.setWeiterleitenText(mesageText.toString());

        System.out.println("Message Weiterleiten Handle: " + hauptStage );
    }


    /* *************** Weitere Methoden von den Pop-up-Fenster(mit drei Punkten) ********************* */


    /**
     * gestartet in Pop-up-Fenster(mit 3 Punkten)
     */
    private void userInfo(){

        layoutService.setausgabeLayout(rightStage);
        FreundeInfoController freundeInfoController = (FreundeInfoController) layoutService.switchLayout(GlobalView.FREUNDEINFO);
        freundeInfoController.setInfoFreundData(chatFreundeDaten);
        freundeInfoController.setInfoFreundMessage(allMessages);
        translate.slidePane(rightStage.getChildren());


        System.out.println("Message Controller, User Info: " + chatFreundeDaten);

    }

    private void gruppeErstellen(){
        System.out.println("Gruppe Erstellen");
    }


    /**
     * Auto-Löschen von Nachrichten
     * 1 Tag
     * 1 Woche
     * 1 Monat
     */
    private void autoLoschen(){
        System.out.println("Auto Löschen von Nachrichten");
    }


    /**
     * gestartet in Pop-up-Fenster(mit dem 3 Punkten)
     */
    private void verlaufLeeren(){
        System.out.println("Verlauf Leeren");
    }


    /**
     * Chat Löschen, das gleiche wie Freund Löschen
     */
    private void chatLeoschen(){ System.out.println("Chat Löschen"); }


    /* ******************** Weitere Methoden **************************** */

    public void smileZeigen() {
        System.out.println("Smile Zeigen");
    }


    public void otherZeigen() {
        System.out.println("Other Zeigen");
    }


    public void onlinePhone() {
        System.out.println("Telefon Zeigen");
    }



    /* ************* Message Schliessen + Freunde hover effekt ausblenden  ***************** */

    /**
     * Click auf dem Pfeil 'ZURÜCK'(< blau), oben in header Bereich
     *
     * celleAnchorPane = ist eine complete Freunde-Pane mit allen Daten von FreundeCellController...
     *  a. translate:       message schliessen
     *  b. celleAnchorPane: hover effekt ausgeblendet
     */
    public void messageSchliessen() {
        translate.closeStackPane();
        celleArchorPane.getStyleClass().remove("freundeAktiv");
    }

    /* ***************** Pop-up-Fenster für den Info + Close *************************** */


    /**
     * Pop-up-Fenster Information kürzlich anzeigen und nach 3 Sekunden ausblenden(Fade)
     *
     * Parameter StackPane: sind insgesamt 4 StackPane
     *      1. hauptStage: liegt unten komplette BoteFx-App, zugesendet von ChatBoxController
     *      2. messageTopStackPane: liegt in Header bereich
     *      3. messageCenterStackPane: liegt in Center, message Bereich
     *      4. messageBottomStackPane: liegt in Bottom bereich, textarea, senden
     * Parameter: textInfo, als String
     *
     * @param textInfo
     */
    private void messagesPopUpFensterInfo(StackPane pane, String textInfo){

        Label popInfo = new Label(textInfo);
        popInfo.getStyleClass().add("popInfoLabel");
        pane.getChildren().add(popInfo);

        // Label für 3 Sekunden einblenden
        methodenService.fadeIn(popInfo, 3);
        //messageCenterStackPane.getChildren().remove(popInfo);
    }

    /* ********************* Bilder, Drag -and-Drop Feature ***********************  */

    // in Arbeit
}
