package BoteFx.controller;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.fragments.FreundeCellController;
import BoteFx.controller.fragments.FreundeLeerController;
import BoteFx.model.Freunde;
import BoteFx.service.ApiService;
import BoteFx.service.ConfigService;
import BoteFx.service.LayoutService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Den 1.11.2022
 */
@Controller
public class FreundeController implements Initializable {
    @Autowired
    private ConfigService configService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private ChatBoxController chatBoxController;
    private FreundeCellController cellController;
    private FreundeCellController activeCellController;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML private AnchorPane freundeAnchorPane;
    @FXML private ImageView imgFreundRemove;
    @FXML private ImageView imgCloseRemove;
    @FXML private Label meineName;
    @FXML private ImageView imgBekanntenEinladen;
    @FXML private ScrollPane freundeScroll;
    @FXML private VBox freundeVBox;



    /**
     * zieht die AnchorPane & ScrollPane auf 100%
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(freundeAnchorPane, 0.0);
        AnchorPane.setRightAnchor(freundeAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(freundeAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(freundeAnchorPane, 0.0);

        AnchorPane.setTopAnchor(freundeScroll, 0.0);
        AnchorPane.setRightAnchor(freundeScroll, 0.0);
        AnchorPane.setBottomAnchor(freundeScroll, 0.0);
        AnchorPane.setLeftAnchor(freundeScroll, 0.0);
        freundeVBox.prefWidthProperty().bind(freundeScroll.widthProperty());
        freundeVBox.prefHeightProperty().bind(freundeScroll.heightProperty());
    }

    /**
     *  Setter & Getter
     *  token, freundePane & rightPane sind von ChatBoxController zugesendet. Zeile: 104
     *  Methode: chatten()... (chatten wird Automatisch gestartet von initialize)
     *
     *  a. token: wird von request benutzt, meine alle freunde von den Datenbank(MySql)
     *     holen(Bote/ApiFreundeController), response als json-array mit allem Freunde
     *
     *  b. rechtsPane: benutzt von die Methode freundEinladen() Zeile: 152(hier)
     *
     *  c. freundenPane: benutzt von die Methode freundeLaden()/ else: wenn keine
     *     freunde vorhanden sind, Zeile: 135(hier)
     *
     *  Methode freundeLaden() starten & token als parameter übergeben
     */
    private String meinToken;
    private AnchorPane rechtsPane;
    private AnchorPane freundenPane;
    public AnchorPane getRechtsPane() {
        return rechtsPane;
    }
    public void setRechtsPane(AnchorPane rightZugesendet) {
        this.rechtsPane = rightZugesendet;
    }
    public AnchorPane getFreundenPane() {
        return freundenPane;
    }
    public void setFreundenPane(AnchorPane paneZugesendet) {
        this.freundenPane = paneZugesendet;
    }
    public String getMeineToken() {
        return meinToken;
    }
    public void setMeineToken(String tokenZugesendet) {
        this.meinToken = tokenZugesendet;
        // Freunde Laden
        freundeLaden(tokenZugesendet);
    }

    /* ******************* Freunde Laden/Anzeigen ************************ */

    /**
     *  Automatische Laden, Zeile: 113 (hier oben)
     *
     *  Freunde von Datenbank (Bote/MySql/globalBote/freunde+message) holen/Laden & Anzeigen
     *  zusätzlich: in Bote/ApiFreundeController werde letzte Chat-Message mit dem Datum aus der
     *  Tabelle: message geholt und in der myFreunde-Array integriert...
     *  Weiter Information auf der App: Bote/ApiFreundeController
     *
     *  a. ELSE{}: noch keine Freunde: leeres response-array ausgabe = []
     *     response.body().length() = Ausgabe: 2 (zählt die 2 eckigen klammern [])
     *
     *  b. IF{}: mit der schleife, wird jeder einzelne Freund geladen und in
     *  freundecell.fxml angezeigt mit dem eigenen Controller..
     *  die Freunde-Daten werden mit setProperties (Setter) an den FreundeCellController
     *  weiter geleitet für weiteren verarbeitung
     *  weitere Informationen: FreundeCellController/setProperties
     *
     *  c. rootAndControllerPairs: Array mit allem Freunden, für die Methode:
     *     freundeRemoveClick(), Zeile: 200 (hier unten)
     *     ...anzeige von dem Remove-Button alle Freunde
     *
     * @param myToken
     */
    private final ArrayList<LayoutService.LayoutControllerPair> rootAndControllerPairs = new ArrayList<>();
    public void freundeLaden(String myToken) {

        // Meine Name Anzeigen
        meineName.setText("richterpaul@mail.de");

        // Request & response an/von Bote
        String urlApi = configService.FILE_HTTP+"freundeApi";
        String json = "{ \"sendToken\":\""+ myToken +"\" }";
        HttpResponse<String> response = apiService.requestAPI(urlApi, json);

        // zählt die 2 eckigen klammern [], darum 3
        if (response.body().length() > 3){

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Freunde>>(){}.getType();
            ArrayList<Freunde> friends = gson.fromJson(response.body(), listType);

            for (Freunde fried : friends){

                LayoutService.LayoutControllerPair pair = layoutService.createLayoutController(GlobalView.FREUNDECELL);
                rootAndControllerPairs.add(pair);
                freundeVBox.getChildren().add(pair.getView());

                cellController = (FreundeCellController) pair.getController();
                cellController.setRechtePane(rechtsPane);
                cellController.setProperties(fried);
            }

        } else {
            // Keine Freunde
            layoutService.setausgabeLayout(freundenPane);
            FreundeLeerController fragmentController = (FreundeLeerController) layoutService.switchLayout(GlobalView.FREUNDELEER);
            fragmentController.setRechtePane(rechtsPane);
        }

    }

    /* *************************** Freunde-Anzeige Hover Setzen ****************** */


    /**
     * Click auf die Freunde 'Fläche', gestartet in FreundeCellController/cellClick
     * Zeile: 163
     * den Hover effekt wird in FreundeCellController/setHover gesetzt Zeile: 250
     * Hover Position merken: Zeile 44 (hier oben)
     *
     * @param currentCellController
     */
    public void freundeHover(FreundeCellController currentCellController){

        for (LayoutService.LayoutControllerPair pair : rootAndControllerPairs){
            ((FreundeCellController) pair.getController()).resetHover();
        }

        // Hover effekt setzen
        currentCellController.setHover();
        // Hover Position merken
        activeCellController = currentCellController;
    }


    /* ****************************** Freunde Remove ****************************** */


    /**
     * nur für Image tausch, benutzt in FreundeCellController/cellClick()...
     *
     */
    public void freundeRemoveSchliessen(){
        imgRemoveZeigen(false);
        removePaneZeigen(false);
    }

    /**
     * Click aud den remove Button: Links oben, icon mit minus zeichen
     * Zeige den roten Button: Methode removePaneZeigen(), Zeile:218 (hier unten)
     *
     * Hover effect wird ausgesetzt
     */
    public void freundRemoveClick() {
        removePaneZeigen(true);
        if (activeCellController != null) {
            activeCellController.resetHover();
        }
        imgRemoveZeigen(true);
    }

    /**
     * Click auf dem Close Button: Links oben, runde icon mit dem Kreuz
     * versteckt den roten Button: Methode removePaneZeigen(), Zeile:218 (hier unten)
     *
     * Hover effekt wird an der gleiche position gesetzt
     */
    public void freundRemoveCloseClick() {
        removePaneZeigen(false);
        if (activeCellController != null) {
            activeCellController.setHover();
        }
        imgRemoveZeigen(false);
    }

    /**
     * Methode nur für die Anzeige den roten Button
     * Anzeige von der Remove pane wird in FreundeCellController/freundeRemoveZeigen()
     * Zeile: 223
     * @param zeige
     */
    private void removePaneZeigen(boolean zeige) {
        for (LayoutService.LayoutControllerPair pair : rootAndControllerPairs) {
            ((FreundeCellController) pair.getController()).paneRemoveZeigen(zeige);
        }
    }

    /**
     * Anzeige von icon:
     *  1. den icon mit dem minus zeichen
     *  2. den runde icon mit dem kreuz
     * @param zeige
     */
    private void imgRemoveZeigen(boolean zeige) {
        imgFreundRemove.setVisible(!zeige);
        imgCloseRemove.setVisible(zeige);
    }


    /* *************************** Bekannten zum Chatten Einladen ******************************** */

    /**
     * Bekannten oder Freunde zum Chat Einladen
     * click auf dem icon-männchen mit plus
     *
     * wenn Freunde-Oberfläche hover ist oder roter-löschen-icon
     * angezeigt wird dann zuerst alles schliessen
     */
    public void bekanntenEinladenClick(){

        // Freunden Hover effekt ausschalten
        if (activeCellController != null){
            activeCellController.resetHover();
        }

        // remove-Pane schliessen
        double removeOff = cellController.paneONPrufen();
        if (removeOff == 0.0){
            freundeRemoveSchliessen();
        }

        // einlade.fxml in der rightPane anzeigen
        bekanntenEinladen();

    }

    /**
     * Click auf den rechten icon, mit plus zeichen
     * Freunde Einladen, zeigt die einladen.fxml Seite
     */
    private void bekanntenEinladen() {
        chatBoxController.changedPane("openmessage");
        layoutService.setausgabeLayout(rechtsPane);
        EinladenController einladenController = (EinladenController) layoutService.switchLayout(GlobalView.EINLADEN);
    }

}
