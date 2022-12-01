package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.setting.*;
import BoteFx.service.LayoutService;
import BoteFx.service.TokenService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @FXML private Label settingVersion;

    // IDs von setting Positionen
    @FXML private GridPane profil;
    @FXML private GridPane allgemein;
    @FXML private GridPane darstellung;
    @FXML private GridPane sicherheit;
    @FXML private GridPane toene;
    @FXML private GridPane sprache;
    @FXML private GridPane sticker;
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

        AnchorPane.setTopAnchor(settingAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(settingAnchorPane, 0.0);
        AnchorPane.setRightAnchor(settingAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(settingAnchorPane, 0.0);
        settingVBox.prefWidthProperty().bind(settingScroll.widthProperty());
    }

    /**
     * Getter & Setter für den #rightPane von chatbox.fxml Zeile: 65
     * zugesendet von ChatBoxController Methode: setting()
     */
    private AnchorPane RightPane;
    public AnchorPane getRightPane() { return RightPane; }
    public void setRightPane(AnchorPane rightPane) {
        RightPane = rightPane;
        // bei Setting start wird automatisch Allgemein-Position angezeigt
        MouseEvent mouseEvent = null;
        settingBearbeiten(mouseEvent);
    }


    /**
     *  Setting: Laden von Positionen
     *
     *  1. Senden das Wort "openmessage" an ChatBoxController/changedPane(String actionId),
     *     für Einblenden der RightPane Zeile: ca. 300
     *     FUNKTIONIERT: nur wenn Messenger unter 650px width ist...
     *     siehe ChatBoxController Zeile: 315
     *  2. id: wird ermittelt eine ID von angeklickte Position, ansonsten bei
     *     ersten start wird die allgemeine angezeigt
     *  3. token: weiter an den Controller Senden
     *  4. hover-effekt von Allen GridPane Löschen, before neuer gesetzt wird (for schleife)
     *  5. RightPane: #rightPane weiter an layoutService Senden(wo soll geladen sein)
     *     switch: Daten an den Controller Senden & hover-effekt an der Position Setzen
     *
     *  @param event
     */
    public void settingBearbeiten(MouseEvent event) {

    /* 1 */
        chatBoxController.changedPane("openmessage");

    /* 2 */
        String id = "allgemein";
        if (event != null){
            id = ((Node) event.getSource()).getId();
        }

    /* 3 */
        String token    = tokenService.tokenHolen();

    /* 4 */
        ArrayList<GridPane> settingArray = new ArrayList<>();
        settingArray.add(profil);
        settingArray.add(allgemein);
        settingArray.add(darstellung);
        settingArray.add(sicherheit);
        settingArray.add(toene);
        settingArray.add(sprache);
        settingArray.add(sticker);
        settingArray.add(faq);
        settingArray.add(support);
        settingArray.add(sitzung);
        for (GridPane position: settingArray){
            position.getStyleClass().remove("settingAktiv");
        }

    /* 5 */
        layoutService.setausgabeLayout(RightPane);

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
            case "sticker":         StickerController stickerController = (StickerController) layoutService.switchLayout(GlobalView.STICKER);
                                    stickerController.setStickerToken(token);
                                    stickerController.setStickerHover(sticker);
                                    sticker.getStyleClass().add("settingAktiv");
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

    }

}
