package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.setting.*;
import BoteFx.service.LayoutService;
import BoteFx.service.TokenService;

import BoteFx.service.TranslateService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Den 5.10.2022
 */

@Controller
public class SettingController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private TokenService tokenService;

    @FXML private AnchorPane settingAnchorPane;
    @FXML private ScrollPane settingScroll;
    @FXML private VBox settingVBox;
    @FXML private ImageView settingBild;
    @FXML private Label settingSprache;
    @FXML private Label settingAktiveSitzung;
    @Value("${app.version}")
    private String appVersion;
    @FXML private Label settingVersion;

    // IDs von setting Positionen
    @FXML private GridPane profil;
    @FXML private GridPane allgemein;
    @FXML private GridPane darstellung;
    @FXML private GridPane sicherheit;
    @FXML private GridPane toene;
    @FXML private GridPane sprache;
    @FXML private GridPane smile;
    @FXML private GridPane speicher;
    @FXML private GridPane ordner;
    @FXML private GridPane faq;
    @FXML private GridPane support;
    @FXML private GridPane sitzung;

    /**
     *  AnchorPane + VBox (setting Box) auf 100% width Ziehen
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Init
        settingVersion.setText(appVersion);
    }

    /**
     * Getter & Setter für den #rightPane von chatbox.fxml Zeile: 65
     * zugesendet von ChatBoxController Methode: setting()
     */
    private StackPane RightPane;
    public StackPane getRightPane() { return RightPane; }
    public void setRightPane(StackPane rightPane) {
        RightPane = rightPane;

        /**
         * bei neuem Setting Start wird automatisch Position 'allgemein' angezeigt
         * 
         * Kurze Beschreibung: bei settingHandle auf position 2 wird die ID auf
         * allgemein gesetzt
         */
        MouseEvent mouseEvent = null;
        settingHandle(mouseEvent);
    }


    /**
     *  Setting: Laden von Positionen
     *
     *
     *  @param event
     */
    public void settingHandle(MouseEvent event) {

    /* 1 */
        //Löschen vorherige anzeige
        translate.deleteAllPane(RightPane.getChildren());

    /* 2 */
        // bei erstem Start, allgemein anzeigen
        String id = "sprache";
        if (event != null){
            id = ((Node) event.getSource()).getId();
            translate.showHauptPane();
        }

    /* 3 */
        String token    = tokenService.meinToken();

    /* 4 */
        ArrayList<GridPane> settingArray = new ArrayList<>();
        settingArray.add(profil);
        settingArray.add(allgemein);
        settingArray.add(darstellung);
        settingArray.add(sicherheit);
        settingArray.add(toene);
        settingArray.add(sprache);
        settingArray.add(smile);
        settingArray.add(speicher);
        settingArray.add(ordner);
        settingArray.add(faq);
        settingArray.add(support);
        settingArray.add(sitzung);
        for (GridPane position: settingArray){
            position.getStyleClass().remove("settingAktiv");
        }

    /* 5 */
        layoutService.setausgabeLayout(RightPane);
        //System.out.println("Rechte Pane: " + RightPane.getChildren());

        /**
         * ACHTUNG: keine slide function, sofort in RightPane anzeigen und schliessen
         * methode sind in jeder Controller unter die Methode ....Zuruck angelegt
         * z.b.s bei profil = profilZuruck, faq = faqZuruck
         */

        switch (id){
            case "profil":          ProfilController profilController = (ProfilController) layoutService.switchLayout(GlobalView.PROFIL);
                                    profilController.setProfilToken(token);
                                    profilController.setProfilHover(profil);      // GridPane ID weiter an ProfilController Senden
                                    profil.getStyleClass().add("settingAktiv");
                                    break;

            case "allgemein":       AllgemeinController allgemeinController = (AllgemeinController) layoutService.switchLayout(GlobalView.ALLGEMEIN);
                                    allgemeinController.setAllgemeinToken(token);
                                    allgemeinController.setAllgemeinHover(allgemein);
                                    allgemein.getStyleClass().add("settingAktiv");
                                    break;

            case "darstellung":     DarstellungController darstellungController = (DarstellungController) layoutService.switchLayout(GlobalView.DARSTELLUNG);
                                    darstellungController.setDarstellungToken(token);
                                    darstellungController.setDarstellungHover(darstellung);
                                    darstellung.getStyleClass().add("settingAktiv");
                                    break;

            case "sicherheit":      SicherheitController sicherheitController = (SicherheitController) layoutService.switchLayout(GlobalView.SICHERHEIT);
                                    sicherheitController.setSicherheitToken(token);
                                    sicherheitController.setSicherheitHover(sicherheit);
                                    sicherheit.getStyleClass().add("settingAktiv");
                                    break;

            case "toene":           ToeneController toeneController = (ToeneController) layoutService.switchLayout(GlobalView.TOENE);
                                    toeneController.setToeneToken(token);
                                    toeneController.setToeneHover(toene);
                                    toene.getStyleClass().add("settingAktiv");
                                    break;

            case "sprache":         SpracheController spracheController = (SpracheController) layoutService.switchLayout(GlobalView.SPRACHE);
                                    spracheController.setSpracheToken(token);
                                    spracheController.setSpracheHover(sprache);
                                    sprache.getStyleClass().add("settingAktiv");
                                    break;

            case "smile":           SmileController smileController = (SmileController) layoutService.switchLayout(GlobalView.SMILE);
                                    smileController.setSmileToken(token);
                                    smileController.setSmileHover(smile);
                                    smile.getStyleClass().add("settingAktiv");
                                    break;

            case "speicher":        SpeicherController speicherController = (SpeicherController) layoutService.switchLayout(GlobalView.SPEICHER);
                                    speicherController.setSpeicherToken(token);
                                    speicherController.setSpeicherHover(speicher);
                                    speicher.getStyleClass().add("settingAktiv");
                                    break;

            case "ordner":          OrdnerController ordnerController = (OrdnerController) layoutService.switchLayout(GlobalView.ORDNER);
                                    ordnerController.setOrdnerToken(token);
                                    ordnerController.setOrdnerHover(ordner);
                                    ordner.getStyleClass().add("settingAktiv");
                                    break;

            case "faq":             FaqController faqController = (FaqController) layoutService.switchLayout(GlobalView.FAQ);
                                    faqController.setFaqToken(token);
                                    faqController.setFaqHover(faq);
                                    faq.getStyleClass().add("settingAktiv");
                                    break;

            case "support":         SupportController supportController = (SupportController) layoutService.switchLayout(GlobalView.SUPPORT);
                                    supportController.setSupportToken(token);
                                    supportController.setSupportHover(support);
                                    support.getStyleClass().add("settingAktiv");
                                    break;

            case "sitzung":         SitzungController sitzungController = (SitzungController) layoutService.switchLayout(GlobalView.SITZUNG);
                                    sitzungController.setSitzungToken(token);
                                    sitzungController.setSitzungHover(sitzung);
                                    sitzung.getStyleClass().add("settingAktiv");
                                    break;

            default:                AllgemeinController defaultController = (AllgemeinController) layoutService.switchLayout(GlobalView.ALLGEMEIN);
                                    defaultController.setAllgemeinToken(token);
                                    defaultController.setAllgemeinHover(allgemein);
                                    allgemein.getStyleClass().add("settingAktiv");
                                    break;
        }

        /**
         * wenn unter 650px ist, slide function wird aktiviert, siehe translateService Zeile: ~400
         */
        translate.showHauptPane();
    }

    public void closeSettingSearch(MouseEvent event) {
    }

    public void kontoHinzufugen(MouseEvent event) {
    }
}
