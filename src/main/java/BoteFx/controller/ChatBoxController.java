package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.service.ConfigService;
import BoteFx.service.LayoutService;
import BoteFx.service.TokenService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Zeit unbekannt
 */

@Controller
public class ChatBoxController implements Initializable {

    @Autowired
    private ConfigService configService;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private TokenService tokenService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * die 3 StackPane nicht verändern
     *
     * sind gedacht für die anzeige globale Pop-up-Fenster
     *
     */
    @FXML private StackPane hauptStackPane;
/*    @FXML private StackPane leftStackPane;
    @FXML private StackPane rightStackPane;*/

    /**
     * die 3 AnchorPane hauptPane, leftPane & rightPane nicht
     *
     * verändern oder Löschen, werden in Initialize & für weitere
     * Methoden benutzt
     * 1. initialize Zeile: 43 + 89
     */
    @FXML private AnchorPane hauptPane;
    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;

    @FXML private AnchorPane freundePane;
    @FXML private ImageView kontakteImg;
    @FXML private ImageView telefonateImg;
    @FXML private ImageView chattenImg;
    @FXML private ImageView settingImg;

    /**
     *  Getter & Setter von 3 StackPane für den globalen Zugriff...
     *
     *  wird benutzt:
     *  1. MessageController/messageBearbeiten() Zeile: 725
     *   kurze beschreibung: über die ganze stage(haupStackPane) wird eine pane(transparent)
     *   gesetzt mit einem Pop-up-Fenster, da sind die hyperlink für die
     *   Methoden zu starten, um das Pop-up-Fenster wieder zu schliessen wird dann
     *   die StackPane als click-Methode benutzt....
     *   (einen click an beliebige stelle in die ganze Stage bereich)
     *
     *   die MessageController ist hier als Beispiel genutzt,
     *   da können anderer Pop-up-Fenster angezeigt werden
     *
     * @return
     */
    public StackPane getHauptStackPane() {
        return hauptStackPane;
    }
    public void setHauptStackPane(StackPane hauptstackpane) {
        this.hauptStackPane = hauptstackpane;
    }
/*
    public StackPane getLeftStackPane() { return leftStackPane; }
    public void setLeftStackPane(StackPane leftStackPane) { this.leftStackPane = leftStackPane;}

    public StackPane getRightStackPane() { return rightStackPane; }
    public void setRightStackPane(StackPane rightStackPane) { this.rightStackPane = rightStackPane; }*/

    /* ***************************** Methoden *********************************  */


    /**
     *  die 4 Methoden werden von chatbox(bottom) gesteuert
     *  1. kontakte
     *  2. telefonate
     *  3. chatten (wird Automatisch gestartet(initialize) Zeile: 213)
     *  4. setting
     *
     * @param event
     */
    public void kontakte(MouseEvent event) {
        rightPane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        KontakteController kontakteController = (KontakteController) layoutService.switchLayout(GlobalView.KONTAKTE);

        // Bild hover setzen
        String id = ((Node) event.getSource()).getId();
        changedImg(id);
    }

    /**
     * Telefonate anzeigen
     * @param event
     */
    public void telefonate(MouseEvent event) {

        rightPane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        TelefonController telefonController = (TelefonController) layoutService.switchLayout(GlobalView.TELEFON);

        // Bild hover setzen
        String id = ((Node) event.getSource()).getId();
        changedImg(id);
    }


    /**
     * Chat Starten
     *
     * Automatische Laden von Chat-Freunde, Bild auf Blau setzen
     * Token aus TokenService holen & an FreundeController weiter senden
     *
     * die Methode wird Automatisch gestartet im public void initialize(){}
     * Zeile: 203
     */
    public void chatten(MouseEvent event) {

        rightPane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        FreundeController freundeController = (FreundeController) layoutService.switchLayout(GlobalView.FREUNDE);
        freundeController.setRechtsPane(rightPane);
        freundeController.setFreundenPane(freundePane);
        freundeController.setMeineToken(tokenService.tokenHolen());

        if (event != null) {
            String id = ((Node) event.getSource()).getId();
            // Bild hover setzen
            changedImg(id);
        }else {
            Image chatImage = new Image(getClass().getResourceAsStream("/static/img/chatblau.png"));
            chattenImg.setImage(chatImage);
        }

    }

    /**
     * Setting Laden
     * @param event
     */
    public void setting(MouseEvent event) {

        rightPane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        SettingController settingController = (SettingController) layoutService.switchLayout(GlobalView.SETTING);
        settingController.setRightPane(rightPane);


        // Bild unter in Bottom-Menu hover setzen
        String id = ((Node) event.getSource()).getId();
        changedImg(id);
    }

    /**
     * Menü Bilder(bottom) bei aktiv Blau setzen
     * @param id
     */
    public void changedImg(String id){
        //System.out.println("changedIMG ID: " + id);
        Image kontakt       = new Image(getClass().getResourceAsStream("/static/img/user.png"));
        Image kontaktBlau   = new Image(getClass().getResourceAsStream("/static/img/userblau.png"));
        Image phone         = new Image(getClass().getResourceAsStream("/static/img/phone.png"));
        Image phoneBlau     = new Image(getClass().getResourceAsStream("/static/img/phoneblau.png"));
        Image chat          = new Image(getClass().getResourceAsStream("/static/img/chat.png"));
        Image chatBlau      = new Image(getClass().getResourceAsStream("/static/img/chatblau.png"));
        Image setting       = new Image(getClass().getResourceAsStream("/static/img/setting.png"));
        Image settingBlau   = new Image(getClass().getResourceAsStream("/static/img/settingblau.png"));

        switch (id){
            case "kontakteImg":                 kontakteImg.setImage(kontaktBlau);
                                                //kontakteImg.setDisable(true);
                                                telefonateImg.setImage(phone);
                                                chattenImg.setImage(chat);
                                                settingImg.setImage(setting);
                                                break;
            case "telefonateImg":               telefonateImg.setImage(phoneBlau);
                                                kontakteImg.setImage(kontakt);
                                                chattenImg.setImage(chat);
                                                settingImg.setImage(setting);
                                                break;
            case "chattenImg":                  chattenImg.setImage(chatBlau);
                                                //kontakteImg.setDisable(false);
                                                kontakteImg.setImage(kontakt);
                                                telefonateImg.setImage(phone);
                                                settingImg.setImage(setting);
                                                break;
            case "settingImg":                  settingImg.setImage(settingBlau);
                                                kontakteImg.setImage(kontakt);
                                                telefonateImg.setImage(phone);
                                                chattenImg.setImage(chat);
                                                break;
            default:                            break;
        }
    }



    /* ******************************* nur für Autoresize(wenn App verkleinert wird) **************************  */

    /**
     *  function chanched + chanchedPane sind für die Verkleinerung/Vergrößerung die App
     *
     *  1. chanched(): wird Aktiv bei App verleinerung unter 650px
     *  2. chanchedPane(): schaltet per function von leftPane auf rightPane wenn die
     *     App unter 650px bleibt
     *
     *  variable nicht verändern oder Löschen
     */
    private List fxmlTag;
    private int aktuelleWidth;
    private int haupWidth;
    private int sperre1  = 1;
    private int sperre2  = 1;
    private int sperre3  = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /**
         * Automatische Laden von Chat-Freunden
         * die Methode wird gestartet in Zeile: 90
         */
        MouseEvent event = null;
        chatten(event);


        /**
         *  die Methode funktioniert bei eine Große unter 650px
         *  gemessen wird an AnchorPane (Hauptpane)
         *
         *  Pane Autoresize(wenn App verkleinert wird)
         *  bei Verkleinerung der Browser wird nur die focusierte Pane
         *  angezeigt, andere wird ausgeblendet.
         *  um ständigen reLoaden zu vermeiden ist eine Sperre eingebaut
         *  mit sperre1...2...3
         */
        hauptPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number altVal, Number newVal) {
                /* Rechte Pane auf Leer prüfen */
                fxmlTag = rightPane.getChildren();

                /* aktuelleWidth-> für die function chanchedPane(Unten) */
                aktuelleWidth = newVal.intValue();

                /* A: wenn die App unter 650px verkleinert wird dann eine Pane Ausblenden */
                if (newVal.intValue() < 650) {

                    /* 1. wenn Message Seite(Rechte Pane) leer ist dann Linke Pane(freunde) anzeigen */
                    if (fxmlTag.size() == 0) {
                        /* nur einmal Ausführen */
                        if (sperre1 == 1) {
                            leftPane.setPrefWidth(aktuelleWidth);
                            leftPane.setMaxWidth(650);
                            leftPane.setMinWidth(300);
                            AnchorPane.setRightAnchor(leftPane, 0.0);

                            rightPane.setVisible(false);

                            sperre1++;
                            sperre2 = 1;
                            sperre3 = 1;
                            //logger.info(" 1 ");

                        } else {
                            // Leer Lauf: newVal.intValue()
                            //logger.info("Leer Lauf 1: " + newVal.intValue());
                        }

                    /* 2. wenn Message Text angezeigt wird dann Rechte Pane anzeigen lassen/Linke Pane(freunde) schliessen */
                    } else {

                        /* nur einmal Ausführen */
                        if (sperre2 == 1) {
                            AnchorPane.setLeftAnchor(rightPane, 0.0);

                            leftPane.setMinWidth(0);
                            leftPane.setVisible(false);

                            sperre2++;
                            sperre1 = 1;
                            sperre3 = 1;
                            //logger.info(" 2 ");

                        } else {
                            // Leer Lauf: newVal.intValue()
                            //logger.info("Leer Lauf 2: " + newVal.intValue());
                        }
                    }

                /* B: wenn die App über 650px vergrössert wird dann beide Pane anzeigen */
                } else {

                    /* 3. über 650px-> Beide Pane anzeigen */
                    if (sperre3 == 1) {
                        AnchorPane.setLeftAnchor(rightPane, 300.0);
                        rightPane.setVisible(true);

                        //AnchorPane.clearConstraints(leftPane);
                        AnchorPane.setRightAnchor(leftPane, (Double) null);
                        leftPane.setPrefWidth(300);
                        leftPane.setVisible(true);

                        sperre3++;
                        sperre1 = 1;
                        sperre2 = 1;
                        //logger.info(" 3 ");
                    } else {
                        // Leer Lauf: newVal.intValue()
                        //logger.info("Leer Lauf 3: " + newVal.intValue());
                    }

                } /* Ende IF-ELSE */
            } /* Ende public void changed */
        }); /* Ende hauptPane */
    }  /* Ende initialize */


    /**
     * die Methode funktioniert nur unter 650px width
     *
     * die Methode wird gestartet nur von anklicken von Freunde(leftPane)
     * oder der Button "Zurück" oben bei Setting Positionen(rightPane)
     *
     * wenn Haupt-Fenster ist mehr als 650px width, wird die rightPane
     * geleert Zeile: 365
     *
     * anbindung möglichkeit: NICHT VERÄNDERN
     * 1. nur 2 Parameter sind erlaubt(1 zugesendet)
     *      a. openfreunde
     *     ODER
     *      b. openmessage
     *
     * 2. @Autowired
     *    ChatBoxController chatBoxController;
     *    chatBoxController.changedPane("openfreunde");
     *    ODER
     *    chatBoxController.changedPane("openmessage");
     *
     * 3. ...
     */
    public void changedPane(String actionText) {
        /* app auf verkleinerung prüfen */
        haupWidth = (int) hauptPane.getWidth();

    if(haupWidth < 650) {
        Thread tr = new Thread() {
            @Override
            public void run() {
                if ("openfreunde".equals(actionText)) {

                    for (int i = 0; i <= aktuelleWidth; i++) {
                        try {
                            Thread.sleep(1);
                            AnchorPane.setLeftAnchor(rightPane, Double.valueOf(i));
                            leftPane.setVisible(true);
                            leftPane.setMinWidth(i);
                            //logger.info("oben: " + aktuelleWidth + " / " + i );
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else if ("openmessage".equals(actionText)) {
                    
                    for (int z = aktuelleWidth; z >= 0; z--) {
                        try {
                            Thread.sleep(1);
                            leftPane.setMinWidth(z);
                            rightPane.setVisible(true);
                            AnchorPane.setLeftAnchor(rightPane, Double.valueOf(z));
                            //logger.info("unten null: " + aktuelleWidth + " / " + z);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else {

                    System.out.println("Unerwartete Fehler bei ChatBoxController: Zeile 307 + 321 + 336 ");
                    //Leer
                }
            }
        }; tr.start();

    } else {
        rightPane.getChildren().clear();
       // System.out.println("ChatBoxController: Das Hauptfenster ist mehr als 650px Groß" + hauptPane.getChildren());
    }/* Ende if hauptWidth */
    }/* Ende chanchedPane */
}
