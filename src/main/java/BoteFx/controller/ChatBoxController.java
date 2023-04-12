package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.service.LayoutService;
import BoteFx.service.SocketService;
import BoteFx.service.TokenService;

import BoteFx.service.TranslateService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Überarbeitet am 10.03.2023
 */

@Controller
public class ChatBoxController implements Initializable {
    @Autowired
    private TranslateService translate;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SocketService socketService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * die 3 StackPane hauptPane, leftPane & rightPane nicht Löschen oder ändern
     */
    @FXML private StackPane hauptPane;
    @FXML private StackPane leftPane;
    @FXML private StackPane rightPane;

    @FXML private AnchorPane freundePane;
    @FXML private ImageView kontakteImg;
    @FXML private ImageView telefonateImg;
    @FXML private ImageView chattenImg;
    @FXML private ImageView settingImg;
    @FXML private AnchorPane tippPane;

    /**
     *  Getter & Setter von StackPane für den globalen Zugriff...
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
        return hauptPane;
    }
    public void setHauptStackPane(StackPane hauptstackpane) {
        this.hauptPane = hauptstackpane;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // StackPane an Translate Übergeben
        translate.setHauptsPane(hauptPane);
        translate.setLinksPane(leftPane);
        translate.setRechtsPane(rightPane);
        // Auto resize Starten
        translate.layoutResize();

        /* ************************************ SOCKET STARTEN ********************* */

        String meinToken = tokenService.tokenHolen();
        socketService.connect(meinToken);

        // Freunde Laden
        chatten();
    }

    /* ***************************** 4 Haupt Methoden *********************************  */

    /**
     *  die 4 Methoden werden von chatbox(bottom) gesteuert
     *  1. kontakte
     *  2. telefonate
     *  3. chatten (wird Automatisch gestartet(initialize) Zeile: 94)
     *  4. setting
     *
     */
    @FXML
    public void kontakte() {

        freundePane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        KontakteController kontakteController = (KontakteController) layoutService.switchLayout(GlobalView.KONTAKTE);

        final String kontaktID = kontakteImg.getId();
        changedImg(kontaktID);
    }

    /**
     * Telefonate anzeigen
     */
    @FXML
    public void telefonate() {
        freundePane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        TelefonController telefonController = (TelefonController) layoutService.switchLayout(GlobalView.TELEFON);

        final String telefonId = telefonateImg.getId();
        changedImg(telefonId);
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
    @FXML
    public void chatten() {
        freundePane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        FreundeController freundeController = (FreundeController) layoutService.switchLayout(GlobalView.FREUNDE);
        freundeController.setRechtsPane(rightPane);
        freundeController.setFreundenPane(freundePane);
        freundeController.setMeineToken(tokenService.tokenHolen());

       /* if (event != null) {
            String id = ((Node) event.getSource()).getId();
            // Bild hover setzen
            changedImg(id);
        }else {
            Image chatImage = new Image(getClass().getResourceAsStream("/static/img/chatblau.png"));
            chattenImg.setImage(chatImage);
        }*/
        final String chattenId = chattenImg.getId();
        changedImg(chattenId);

    }

    /**
     * Setting Laden
     */
    @FXML
    public void setting() {
        freundePane.getChildren().clear();
        layoutService.setausgabeLayout(freundePane);
        SettingController settingController = (SettingController) layoutService.switchLayout(GlobalView.SETTING);
        settingController.setRightPane(rightPane);

        final String settingId = settingImg.getId();
        changedImg(settingId);
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

}
