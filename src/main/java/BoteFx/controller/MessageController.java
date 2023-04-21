package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.fragments.MessageCellController;
import BoteFx.model.Freunde;
import BoteFx.model.Message;
import BoteFx.service.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.*;
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
import javafx.scene.input.MouseEvent;
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


    @FXML private AnchorPane messageAnchorPane;
    @FXML private BorderPane messageBorderPane;
    @FXML private Label messageProfilBild;
    @FXML private Label headFreundName;
    @FXML private Label headFreundInfo;
    @FXML private Label freundOnlineZeit;
    @FXML public ImageView onlinePhone;
    @FXML public ImageView noPhone;
    @FXML private Label headBearbeiten;
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
    @FXML private String myColor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Message VBox auf 100% ziehen*/
        messageVBox.prefWidthProperty().bind(messageAnchorPane.widthProperty());

        /* HauptPane ID holen, ChatBoxController */
        hauptStage = chatBoxController.getHauptStackPane();

        // Bottom: TextArea in Focus setzen
        Platform.runLater(() -> textareaText.requestFocus());

        // Methode: altMessage, rundes bild-hintergrund, Zeile: ab 415
        myColor = methodenService.zufallColor();
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

        alteMessages(allMessages);
    }


    /**
     * Freunde Daten zugesendet von FreudeCellController Zeile: 320
     *
     * werde in Header der message Name + online-Zeit angezeigt
     */
    private String chatFreundeDaten;
    private Image headPunkte;
    private ImageView blauPunkte;
    private String freundToken;
    public String getChatFreundeDaten() { return chatFreundeDaten; }
    public void setChatFreundeDaten(Freunde chatFreundeDaten) {
        this.chatFreundeDaten = String.valueOf(chatFreundeDaten);

        headPunkte = new Image(getClass().getResourceAsStream("/static/img/punkteblau.png"));
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


    /* *********************** Alte Message + neue Message Ausgabe ******************* */


    /**
     * Alte Message von den Globalen(Bote, MySql) Datenbank holen und Ausgeben,
     * wird gestartet in Zeile: 158
     */
    private void alteMessages(ArrayList<Message> altMessage){

        messagesAusgeben(altMessage);

    }



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

    }


    /**
     * hier werden in eine schleife die Alte-Message und die Neue-Message Ausgegeben
     * @param allmesage
     */
    final List<Long> checkBoxSelected = new ArrayList<>();              // CheckBox: nur selected
    final List<StackPane> stackPaneSelected = new ArrayList<>();        // StackPane: zum Löschen, hover
    final List<CheckBox> allCheckBoxArray = new ArrayList<>();          // CheckBox: alle, benutzt in cellCheckBoxHide()
    final List<VBox> cellRightVBoxArray = new ArrayList<>();             // VBox: alle, für die show/hide CheckBox
    private VBox cellRightVBox;
    // die 3 final, für den Löschen Teil
    @FXML final Hyperlink linkLoschen = new Hyperlink();
    @FXML final Label labelCount = new Label();
    @FXML final Hyperlink linkWeiterleiten = new Hyperlink();

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
            Image cellProfilImg = new Image(ConfigService.FILE_HTTP+"profilbild/"+mess.getMeintoken()+".png", false);
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
            //Image imgGrau = new Image();
            //Image imgGrun = new Image();
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

            AnchorPane cellRightAnchorPane = new AnchorPane(cellRightVBox);
            AnchorPane.setRightAnchor(cellRightVBox, -25.0);

            cellRightVBoxArray.add(cellRightVBox);
            allCheckBoxArray.add(cellCheckBox);

            cellCheckBox.selectedProperty().addListener((observable, oldValue, selectedNow) -> {
                /* observable, oldValue, selectedNow: output:~ selected/false/true */
                /* selected message in Array speichern + hover setzen und umgekehrt */
                if (selectedNow) {
                    checkBoxSelected.add(Long.valueOf(cellCheckBox.getId()));
                    stackPaneSelected.add(cellHauptStackPane);
                    cellHauptStackPane.getStyleClass().add("cellMessagesHover");
                } else {
                    checkBoxSelected.remove(Long.valueOf(cellCheckBox.getId()));
                    stackPaneSelected.remove(cellHauptStackPane);
                    cellHauptStackPane.getStyleClass().remove("cellMessagesHover");
                }

                System.out.println("selectedProperty: " + observable +"/"+ oldValue +"/"+ selectedNow );
                // Message Löschen Teil
                if (checkBoxSelected.size() == 0 && stackPaneSelected.size() == 0){
                    // keine checkBox aus gewellt, alles schliessen
                    messageLoschenHide();
                    cellCheckBoxHide();

                } else {
                    // checkBox + StackPane selected
                    linkLoschen.setText(String.valueOf(checkBoxSelected));
                    int checkCount = checkBoxSelected.size();
                    labelCount.setText(String.valueOf(checkCount));
                    linkWeiterleiten.setText(String.valueOf(checkBoxSelected));
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
         *  6.  message-box anzeigen */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    messageVBox.getChildren().add(cellHauptStackPane);

                    // Bottom Scroll, immer letzte Message Anzeigen
                    scrollBottom(messageScrollPane);
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
            message.setMeintoken(tokenService.meinToken());
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



    /* *************** Message Bearbeiten ********************** */


    /**
     * Message Bearbeiten, click auf die Drei Blau Punkte...
     * nur für show/hide den Pop-up-Fenster + checkBox(für den Löschen)
     * Löschen ist in separaten Teil 'Message Löschen'
     */
    @FXML
    public void messageBearbeiten() {

        //System.out.println("Message Bearbeiten");

        double textareaHoch = textareaText.getHeight();
        if (textareaHoch > 40.0){
            // Message Height Merken
            messageHeightMerken = textareaHoch;
        }

        String fertig = headBearbeiten.getText();
        if ("fertig".equals(fertig)){

            cellCheckBoxHide();
            messageLoschenHide();
            return;
        }

        // aufbaue eine pane über die ganze Stage, und ober Rechts das Pop-up-Fenster
        AnchorPane spiegelungHaupStage = new AnchorPane();
        spiegelungHaupStage.setStyle("-fx-background-color: transparent; -fx-border-color: red;");
        spiegelungHaupStage.prefWidthProperty().bind(hauptStage.widthProperty());
        spiegelungHaupStage.prefHeightProperty().bind(hauptStage.heightProperty());

        VBox popUpBearbeiten = new VBox();
        popUpBearbeiten.getStyleClass().add("messagesBearbeiten");
        popUpBearbeiten.setEffect(new DropShadow(5, Color.GRAY));
        AnchorPane.setTopAnchor(popUpBearbeiten, 25.0);
        AnchorPane.setRightAnchor(popUpBearbeiten, 20.0);

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
            hpl.prefWidthProperty().bind(popUpBearbeiten.widthProperty());

            // Methoden abruffen
            final String url = urls[i];
            hpl.setOnAction((event) -> {
                switch (url){
                    case "bearbeiten":              cellCheckBoxZeigen();   break;
                    case "userinfo":                userInfo();             break;
                    case "verlaufleeren":           verlaufLeeren();        break;
                    default: break;
                }
                //Pop-Up-Fester ausblenden
                popUpFensterClose(spiegelungHaupStage);
            });
        }

        // VBox mit hyperlink oben Rechts in Haupt-AnchorPane anzeigen
        popUpBearbeiten.getChildren().addAll(hpls);
        spiegelungHaupStage.getChildren().add(popUpBearbeiten);
        hauptStage.getChildren().add(spiegelungHaupStage);

        //Pop-Up-Fester ausblenden
        spiegelungHaupStage.setOnMouseClicked(mouseEvent -> {
            popUpFensterClose(spiegelungHaupStage);
        });
    }


    /**
     * checkBox verwalten(click auf bearbeiten in pop-up-Fenster)
     */
    private void cellCheckBoxZeigen(){

        messageLoschenShow();
        cellCheckBoxShow();
        textareaMinHoch();
        
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
    
    
    /**
     *  Header: Bild-drei-Punkte löschen, Text 'fertig' einfügen
     */
    private void clickDreiPunkteHide(){

        headBearbeiten.setGraphic(null);
        headBearbeiten.setText("fertig");
    }

    
    /**
     *  Header: Text 'fertig' Löschen, Bild(drei Punkte) einfügen
     */
    private void clickDreiPunkteShow(){

        headBearbeiten.setText("");
        blauPunkte.setFitHeight(30);
        blauPunkte.setFitWidth(30);
        headBearbeiten.setGraphic(blauPunkte);
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
     * der Bearbeiten-Pop-up-Fenster(rechts oben mit 3 punkten '...' ) schliessen
     * Quelle: Methode: messageBearbeiten Zeile: 670
     */
    private void popUpFensterClose(Pane pane){
        
        pane.getChildren().clear();
        ((Pane)pane.getParent()).getChildren().remove(pane);
        
    }


    /* *************** Message Löschen *********************** */

    /**
     * Message Löschen
     * die Methode messageLoschenShow wird gestartet in der Methode
     * cellCheckBoxZeigen() / Zeile: 700
     *
     * Achtung: zugesendete parameter checkBoxSelected: in diesem Array sind
     * nur selected checkBox gespeichert mit der ID(von dem message)
     * mit diesem Format werden sie mit requestApi versendet
     * List<Long>: [205, 207, 202, 200]
     * StackPane: [StackPane[id=261, styleClass=altMessageHover],
     *            StackPane[id=273, styleClass=altMessageHover],
     *            StackPane[id=274, styleClass=altMessageHover]]
     *
     */
    @FXML final AnchorPane messageLoschenPane = new AnchorPane();
    final GridPane loschGridPane = new GridPane();
    /*List<Long> checkGruppe, List<StackPane> paneGruppe*/
    private void messageLoschenShow(){

        /**
         * HBox(Image(Bild) + Label(Löschen) ), wird ins GridPane/Left integriert
         */
        //Image imgGrau = new Image(getClass().getResourceAsStream("/static/img/hacken.png"));
        ImageView loschImg = new ImageView(new Image(getClass().getResourceAsStream("/static/img/delete.png")));
        loschImg.setFitWidth(20);
        loschImg.setFitHeight(20);
        Label loschText = new Label("Löschen");
        HBox loschHBox = new HBox(loschImg, loschText);

        /**
         * HBox( Label(5) + Label(Nachrichten Ausgewählt) ), wird in GridPane/Center integriert
         */
        Label loschCount = new Label();
        Label loschWellen = new Label("Nachrichten auswählen");
        HBox countHBox = new HBox(loschCount, loschWellen);

        /**
         * HBox(Label(Weiterleiten) + Image(Bild) ), wird in GridPane/Right integriert
         */
        Label loschweiter = new Label();
        ImageView weiterImg = new ImageView(new Image(getClass().getResourceAsStream("/static/img/forward.png")));
        weiterImg.setFitWidth(20);
        weiterImg.setFitHeight(20);
        HBox weiterHBox = new HBox(loschweiter, weiterImg);


        loschGridPane.add(loschHBox, 0, 0);
        loschGridPane.add(countHBox, 1, 0);
        loschGridPane.add(weiterHBox, 2, 0);
       /* loschGridPane.addColumn(0, loschHBox);
        loschGridPane.addColumn(1, countHBox);
        loschGridPane.addColumn(0, weiterHBox);*/


        //messageLoschenPane.getChildren().addAll(loschHBox, countHBox);
        loschGridPane.getStyleClass().add("mesagesLoschPane");

        messageBottomStackPane.getChildren().add(loschGridPane);
    }


    /**
     * Löschen Anzeige(bottom) ausblenden
     *
     * der unten in Textarea eingeblendeten Löschen-Info ausblenden
     */
    private void messageLoschenHide(){
        //messageLoschenPane.getChildren().clear();
        loschGridPane.getChildren().clear();
        messageBottomStackPane.getChildren().remove(loschGridPane);
       // messageBottomStackPane.getChildren().remove(messageLoschenPane);
    }


    /* ***************  Weitere Methoden ********************* */


    /**
     * gestartet in Pop-up-Fenster(mit 3 Punkten)
     */
    private void userInfo(){
        System.out.println("User Info");
    }


    /**
     * gestartet in Pop-up-Fenster(mit dem 3 Punkten)
     */
    private void verlaufLeeren(){
        System.out.println("Verlauf Leeren");
    }

    public void smileZeigen() {
        System.out.println("Smile Zeigen");
    }


    public void otherZeigen() {
        System.out.println("Other Zeigen");
    }


    public void onlinePhone() {
        System.out.println("Telefon Zeigen");
    }



    /* *********************** Message Schliessen ***************** */

    /**
     * celleAnchorPane = eine complete Freunde-Pane mit allen Daten von FreundeCellController...
     *
     * hier wird bei schliessen die Message-Ausgabe(messageVBox) nur den Hover-Effekt ausgeblendet in
     * den FreundeCellController(Freunde Celle)
     */
    public void messageSchliessen() {
        translate.closeStackPane();
        celleArchorPane.getStyleClass().remove("freundeAktiv");
    }
}
