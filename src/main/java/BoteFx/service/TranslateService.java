package BoteFx.service;

import BoteFx.configuration.BoteConfig;
import BoteFx.controller.MessageController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.CENTER;

/**
 * Den 6.3.2023
 */

@Service
public class TranslateService implements Initializable {

    /**
     * Globale variable, nicht Löschen
     */
    private int hauptWidth;
    private int aktuelleWidth;
    private int childsCount;

    /**
     * Aktuelle MessageController, zugesendet von FreundeCellController(click auf freund)
     *
     * ACHTUNG: dient nur für einen zweck, ob eine message geladen ist oder nicht...
     * wenn message geladen ist dann bei einer verkleinerung unter 650px wird sofort message
     * angezeigt, wenn keine message geladen ist dann freunde anzeigen
     */
    private MessageController messagesController;
    public void setMessagesController(MessageController messagesController) {
        this.messagesController = messagesController;
    }


    /**
     * Sticker Properties holen (true/false)
     */
    @Autowired
    private BoteConfig boteConfig;
    private String smilesBoolean;
    public String getSmileProperties(){
        return boteConfig.getProperties("smileAnzeigen");
    }


    /**
     * Init
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    /**
     * Setter & Getter
     * zugesendet von ChatBoxController
     */
    private StackPane hauptsPane;
    public StackPane getHauptsPane() {
        return hauptsPane;
    }
    public void setHauptsPane(StackPane hauptspane) {
        this.hauptsPane = hauptspane;
    }

    private StackPane linksPane;
    public StackPane getLinksPane() {
        return linksPane;
    }
    public void setLinksPane(StackPane linkspane) {
        this.linksPane = linkspane;
    }

    private StackPane rechtsPane;
    public StackPane getRechtsPane() {
        return rechtsPane;
    }
    public void setRechtsPane(StackPane rechtspane) {
        this.rechtsPane = rechtspane;
    }

    private StackPane smilesPane;
    public StackPane getSmilesPane() { return smilesPane; }
    public void setSmilesPane(StackPane smilesPane) { this.smilesPane = smilesPane; }



    /* ******************************** Auto Resize ************************************ */

    /**
     * Auto resize, (voll automatisch)
     * start in ChatBoxController/initialize/translate.layoutResize();
     *
     * ACHTUNG: bei verkleinerung die App unter 650px und wenn die Rechte Pane Leer ist,
     * wird dann die Linke Seite angezeigt, bei Anzeige eine message in der rechte Pane wird dann
     * automatisch dir rechte Pane angezeigt....
     *
     * Sticker anzeige nur über 1030px + message Controller aktiv, feste breite 350px
     * bei anzeige pane aus dem 2 Reihe wird der Sticker StackPane verdunkelt oder ausgeblendet
     *
     * min. verkleinerung ist width/height 380/550px
     */
    @FXML
    public void layoutResize(){

        smilesBoolean = getSmileProperties();
        hauptsPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number altVal, Number newVal) {

                hauptWidth = (int) hauptsPane.getWidth();
                aktuelleWidth = newVal.intValue();
                childsCount = rechtsPane.getChildren().size();

                if (aktuelleWidth < 651){
                    if (childsCount > 1){
                        // wenn rightStackPane mit message gefühlt ist
                        rightStackPaneGross();
                    } else {
                        // wenn rightPane Leer ist
                        leftPaneGross();
                    }
                } else if (aktuelleWidth > 1029) {

                    if (messagesController != null && smilesBoolean.trim().equals("true")) {
                        smileShow();
                    }

                } else {
                    // hauptStackPane über 650px
                    paneStandard();
                    smileHide();
                }

            }


        });
    }


    /**
     * Linke Pane voll anzeigen (max width: 650px)
     * funktioniere nur unter 650px width hauptPane
     */
    private void leftPaneGross(){
        linksPane.setPrefWidth(aktuelleWidth);
        linksPane.setMinWidth(aktuelleWidth);
        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(aktuelleWidth));
    }


    /**
     * Rechte Pane voll anzeigen (max width: 650px)
     * funktioniere nur unter 650px width hauptPane
     */
    private void rightStackPaneGross(){
        linksPane.setPrefWidth(aktuelleWidth);
        linksPane.setMinWidth(aktuelleWidth);
        AnchorPane.setLeftAnchor(rechtsPane, 0.0);
    }


    /**
     * Linke Pane width: 300px
     * Rechte Pane 380px
     * funktioniere nur, wenn hauptPane über 650px width ist
     */
    private void paneStandard(){
        linksPane.setPrefWidth(300);
        linksPane.setMinWidth(300);
        AnchorPane.setLeftAnchor(rechtsPane, 300.0);
    }

    /* :::::::::::::::::::::::::::: SMILE METHODEN :::::::::::::::::::::::::::::::::: */

    /**
     * Smile Teil, wird angezeigt über 1030px width
     */
    private void smileShow(){
        smilesPane.setVisible(true);
        smilesPane.setMinWidth(350.0);
        smilesPane.setPrefWidth(350.0);
        AnchorPane.setRightAnchor(rechtsPane, 350.0);
        //System.out.println("smile anzeigen: " + aktuelleWidth);
    }


    /**
     * smile Teil, wird geschlossen unter 1030px
     */
    private void smileHide(){
        smilesPane.setMinWidth(0.0);
        smilesPane.setPrefWidth(0.0);
        AnchorPane.setRightAnchor(rechtsPane, 0.0);
        smilesPane.setVisible(false);
        //System.out.println("smile schliessen: " + aktuelleWidth);
    }


    /**
     * wenn wird eine Pane aus Zweite(info) Reihe angezeigt dann wird den Sticker Box ausgeblendet,
     * bei schliessen wieder angezeigt...
     * Alternativ können sie die Sticker-Box schliessen, bei aufrufs anderer Chat werden
     * Sticker wieder angezeigt
     */
    Pane smileAusblender = new Pane();
    private void smilePaneAusblenden(){
        Text text = new Text("Wenn du einen Chat geöffnet hast, erscheinen hier deine Sticker wieder.");
        text.setWrappingWidth(330);
        text.setFill(Color.color(0.5, 0.5, 0.5));
        text.setFont(Font.font("Arial", FontWeight.NORMAL, 15));

        Hyperlink hyperlink = new Hyperlink("Panel verstecken");
        hyperlink.setStyle("-fx-font-size: 1.1em; -fx-text-fill: #1608D3; -fx-padding: 20;");
        hyperlink.setOnAction(e -> {
            smileHide();} );

        VBox vBox = new VBox(text, hyperlink);
        vBox.setAlignment(CENTER);
        vBox.setStyle(" -fx-pref-width: 350; ");
        vBox.layoutYProperty().bind(smileAusblender.heightProperty().subtract(vBox.heightProperty()).divide(2));


        smileAusblender.getChildren().addAll(vBox);
        smileAusblender.setStyle("-fx-background-color: white; -fx-opacity: 0.9;");
        smilesPane.getChildren().add(smileAusblender);
    }


    /**
     * Sticker Ausblenden + Pane Löschen
     * @return
     */
    private EventHandler<ActionEvent> smilePaneEinblenden(){

        smilesPane.getChildren().remove(smileAusblender);
        //stickerHide();
        messagesController = null;
        return null;
    }


    /* :::::::::::::::::::::::::::::::::::: Translate ::::::::::::::::::::::::::::::::::::::::::::: */

    /**
     * Slide funktion für die geladene fxml + rechte StackPane
     *
     * ACHTUNG: Alle fxml-Dateien werden immer in rechtePane mit fxmlLoader geladen,
     * die Haupt Pane(message, einstellungen) wenn sie sind über 650px breit werden sofort in
     * rechtePane anzeigen, unter 650px mit slide funktion
     * die fxml-Dateien aus dem 2 Reihe werden immer mit slide function anzeigen, da muss
     * eine Id zugesendet sein
     *
     *
     *
     * <code>
     *
     *     @Autowired
     *     private LayoutTranslate translate;
     *
     *      // Haupt Pane, keine ID notwendig
     *     translate.showHauptPane();
     *     translate.hideHauptPane();
     *
     *     // 2 reihe, braucht id von geladene Pane
     *     translateService.showNebenPane(freundeInfoHauptPane);
     *     translateService.hideNebenPane(freundeInfoHauptPane);
     *
     * </code>
     *
     * BEISPIELE:
     *      Start haupt fxml, Start in FreundeCellController Zeile: 350
     *      @FXML
     *      private void chatLaden() {
     *          layoutService.setausgabeLayout(rechtePane);
     *          MessageController messageController = (MessageController) layoutService.switchLayout(GlobalView.MESSAGE);
     *
     *          messageController.setChatFreundeDaten(friend);
     *          messageController.setMessageToken(messagesToken);
     *          messageController.setCelleArchorPane(cellAnchorPane);
     *
     *             // Controller an chatBox + translate senden
     *          chatBoxController.setMessageController(messageController);
     *          translate.setMessagesController(messageController);
     *
     *           // Translate Starten (funktioniert nur unter 650px)
     *         translate.showHauptPane();
     *     }
     *
     *
     *     Schliessen haupt fxml, Start in MessageController Zeile: 1500
     *     @FXML
     *     public void messageSchliessen() {
     *         translate.hideHauptPane();
     *         celleArchorPane.getStyleClass().remove("freundeAktiv");
     *     }
     *
     *
     *     Start + Schliessen fxml von 2 Reihe wird immer von geladenen Datei (FreundeInfoController)
     *     ausgeführt(Getter & Setter)
     *      @FXML
     *      public void setInfoFreundData(Freunde infofreunddata) {
     *         this.infoFreundData = infofreunddata;
     *
     *         // Slide function Starten in translateService
     *         translateService.showNebenPane(freundeInfoHauptPane);
     *     }
     *
     *     Schliessen fxml von der 2 reihe, ID von schließende Pane zusenden
     *     @FXML
     *     public void freundeInfoZuruck() {
     *
     *         translateService.hideNebenPane(freundeInfoHauptPane);
     *
     *     }
     */


    /* :::::::::::::::::::::::::: Slide function, Haupt Pane(rechtesPane) ::::::::::::::::::::::::: */


    /**
     * slide funktion nur für die Rechte StackPane(rechtsPane), funktioniere nur unter 650px
     * ansonsten sofort anzeigen
     *
     * ACHTUNG: die ObservableList 'rechtesPane.getChildren' bei jedem Klick wird immer länger
     * rechtesPane.getChildren: [AnchorPane[id=tippPane], AnchorPane[id=messageAnchorPane], AnchorPane[id=messageAnchorPane],
     * AnchorPane[id=messageAnchorPane], AnchorPane[id=messageAnchorPane]]
     *
     * FAZIT: eine Methode zum Löschen von doppelten einträge wird eingebaut, siehe unten
     * removeDuplicate() :  start, Zeile: 396
     *                      Methode, Zeile: 476
     *
     */
    public void showHauptPane(){

        if (hauptWidth < 651){
            Thread tr = new Thread() {
                @Override
                public void run() {
                    for (int z = aktuelleWidth; z >= 0; z--) {

                        try {
                            Thread.sleep(0, 300000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        linksPane.setPrefWidth(aktuelleWidth);
                        linksPane.setMinWidth(aktuelleWidth);
                        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(z));
                    }
                }
            }; tr.start();
        } else if (hauptWidth >1029) {
            if (messagesController != null && smilesBoolean.trim().equals("true")) {
                smileShow();
            }
        } else {
            smileHide();
            // wenn hauptStackPane breiter als 650px ist, rechtePane sofort öffnen
        }

        // observableList von Doppelten einträgen(Object) bereinigen
        removeDuplicate(rechtsPane.getChildren());

    }


    /**
     * nur für schliessen die Rechte StackPane(rechtsPane)
     * funktioniere nur unter width 650px der hauptsPane, ansonsten wir die rechtePane
     * sofort geschlossen und alle inhalte gelöscht
     */
    public void hideHauptPane(){

        if (hauptWidth < 651){
            Thread tr = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i <= aktuelleWidth; i++) {

                        try {
                            Thread.sleep(0, 300000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        /**
                         * leftPane.setPrefWidth(i);
                         * ACHTUNG: nicht prefWidth setzen, drückt die LinkePane zusammen(bis 300px)
                         */
                        linksPane.setMinWidth(i);
                        //AnchorPane.setLeftAnchor(rightStackPane, Double.valueOf(i));
                        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(i));
                    }
                    // bei schliessen, rechtesPane bereinigen außer TippPane
                    Platform.runLater(() -> deleteAllPane(rechtsPane.getChildren()));
                }
            }; tr.start();
        } else {
            // über 650px, click auf zurück: rechtsPane leeren
            deleteAllPane(rechtsPane.getChildren());

        }
        smileHide();
        messagesController = null;
    }


    /* :::::::::::::::::::::::::: Slide Funktionen für die 2 Reihe :::::::::::::::::::::::::::::::: */

    /**
     * Zeigen Pane von 2 Reihe, z.b.s info, einstellungen
     * @param pane
     */
    public void showNebenPane(Pane pane){

        // Slide von rechts nach Links
        pane.translateXProperty().set(rechtsPane.getWidth());
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(pane.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(actionEvent -> {

            // Sticker verstecken
            smilePaneAusblenden();

        });
        timeline.play();
    }


    /**
     * Schliessen Pane von dem 2 Reihe, z.b.s info, einstellungen
     *
     * @param pane
     */
    public void hideNebenPane(Pane pane){

        //Slide von links nach rechts
        pane.translateXProperty().set(0);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(pane.translateXProperty(), rechtsPane.getWidth(), Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event -> {

            // geschlossene Pane Löschen
            deletePane(pane);

            // Sticker freischalten
            smilePaneEinblenden();

        });
        timeline.play();
    }


    /* :::::::::::::::::::::::: Schliessen + delete Methoden ::::::::::::::::::::::::::::::::::::::*/


    /**
     * Löscht genau die geschlossene Pane aus den 2 Reihe
     * ausgelöst bei schliessenPane... Zeile: 251
     * @param pane
     */
    private void deletePane(Pane pane){
        rechtsPane.getChildren().remove(pane);
        //System.out.println("Schliessen: " + rechtsPane.getChildren());
    }


    /**
     * alle children in den Rechte StackPane werden gelöscht, außer die tippPane..
     * wenn message wird geschlossen, die children aus dem 2 Reihe werden hier mitgelöscht
     *
     * ObservableList: [AnchorPane[id=tippPane], AnchorPane[id=messageAnchorPane], AnchorPane[id=freundeInfoHauptPane]]
     *
     * @param auserTippPane
     */
    public void deleteAllPane(ObservableList<Node> auserTippPane){
        auserTippPane.remove(1, auserTippPane.size());
        //System.out.println("Delete All, außer tipp Pane: " + rechtsPane.getChildren());
    }



    /**
     * DOPPELTE OBJECTE ENTFERNEN VON ObservableList (rechtesPane.getChindren())
     *
     * Behält nur je ein Exemplar mehrfach vertretener Objekte in der übergebenen Liste.
     *
     * Alt: Start: [AnchorPane[id=tippPane], AnchorPane[id=messageAnchorPane], AnchorPane[id=messageAnchorPane],
     *              AnchorPane[id=messageAnchorPane], AnchorPane[id=freundeInfoHauptPane], AnchorPane[id=messageAnchorPane]]
     * neue: Start: [AnchorPane[id=tippPane], AnchorPane[id=freundeInfoHauptPane], AnchorPane[id=messageAnchorPane]]
     *
     * @param list eine Liste mit doppelten/mehrfachen Objekt-Exemplaren
     */
    public static void removeDuplicate(List list) {

        int size = list.size();
        for(int i = 0; i < size; i++) {

            Object o1 = list.get(i).toString().trim();

            for(int j = i + 1; j < size; j++) {

                Object o2 = list.get(j).toString().trim();

                if(o1.equals(o2)) {

                    // Löschen was zuerst war geladen
                    list.remove(i);
                    // minus gelöschte Object
                    size--;
                }
            }
        }
    }

}

