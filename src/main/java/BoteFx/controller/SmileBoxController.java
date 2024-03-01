package BoteFx.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Den 3.01.2024
 */
@Controller
public class SmileBoxController implements Initializable {


    @FXML private ScrollPane smileBoxScroll;
    @FXML private BorderPane emojiBorder;
    @FXML private BorderPane stickerBorder;
    @FXML private BorderPane gifBorder;
    @FXML private FlowPane emojiFlow;
    @FXML private FlowPane stickerFlow;
    @FXML private FlowPane gifFlow;
    @FXML private Hyperlink emojiLink;
    @FXML private Hyperlink stickerLink;
    @FXML private Hyperlink gifLink;

   private String nodeAktiv;
   private boolean handScrollAktiv = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Sticker + smile Laden & anzeigen
        emojisAnzeigen();
        stickersAnzeigen();
        gifsAnzeigen();


        /**
         * bei erstem Start hover effect aus dem emoji Link setzen
         * scrollHande methode: scrollen mit zwei finger
         *
         * ACHTUNG: variable nodeAktiv: hier wird den letzten Klick gespeichert, den scrolling effect
         * zu vermeiden, wenn wird auf dem aktiven Link(bottom menü) gedrückt
         *
         */
        nodeAktiv = "emojiLink";
        actionTrue(emojiLink);
        scrollHandle();

    }


    /* ********************* Emoji + Sticker & GIFs ins FlowPane Laden ******************* */


    /**
     * Emoji Anzeigen, die Bilder werden von Methode smileLaden() geladen
     */
    private void emojisAnzeigen(){
        /* absolute Path: /Users/paulrichter/ideaProject/FXArchiv/src/main/resources/static/smile  */
        String path = "src/main/resources/static/emoji";
        File folder = new File(path);
        File[] listFiles = folder.listFiles();

        emojiFlow.setPadding(new javafx.geometry.Insets(15, 15, 15, 15));
        emojiFlow.setHgap(16);
        emojiFlow.setVgap(16);

        for (final File file : listFiles){
            ImageView imageView;
            imageView = smileLaden(file);
            imageView.setStyle("-fx-padding: 10;");

            //Bilder einbinden
            emojiFlow.getChildren().add(imageView);
        }

    }


    /**
     * Sticker Anzeigen, die Bilder werden von Methode smileLaden() geladen
     */
    private void stickersAnzeigen(){
        /* absolute Path: /Users/paulrichter/ideaProject/FXArchiv/src/main/resources/static/smile  */
        String path = "src/main/resources/static/sticker";
        File folder = new File(path);
        File[] listFiles = folder.listFiles();

        // Tile Pane Einstellung
        //emojiFlowPane.setStyle("-fx-border-color: red;");
        stickerFlow.setPadding(new javafx.geometry.Insets(15, 15, 15, 15));
        stickerFlow.setHgap(16);
        stickerFlow.setVgap(16);

        for (final File file : listFiles){
            ImageView imageView;
            imageView = smileLaden(file);
            imageView.setStyle("-fx-padding: 10;");

            //Bilder einbinden
            stickerFlow.getChildren().add(imageView);
            //gifsFlow.getChildren().add(imageView);
        }

    }


    /**
     * GIFs Anzeigen, die Bilder werden von Methode smileLaden() geladen
     */
    private void gifsAnzeigen(){
        /* absolute Path:  /Users/paulrichter/ideaProject/FXArchiv/src/main/resources/static/smile  */
        String path = "src/main/resources/static/gif";
        File folder = new File(path);
        File[] listFiles = folder.listFiles();

        // Tile Pane Einstellung
        //emojiFlowPane.setStyle("-fx-border-color: red;");
        gifFlow.setPadding(new Insets(15, 15, 15, 15));
        gifFlow.setHgap(16);
        gifFlow.setVgap(16);

        for (final File file : listFiles){
            ImageView imageView;
            imageView = smileLaden(file);
            imageView.setStyle("-fx-padding: 10;");

            //Bilder einbinden
            gifFlow.getChildren().add(imageView);
            //stickerFlow.getChildren().add(imageView);
            //gifsFlow.getChildren().add(imageView);
        }

    }


    /**
     * Bilder von Datei Laden, gild für alle 3 obere Methoden
     *  1. emojisAnzeigen
     *  2. stickersAnzeigen
     *  3. gifsAnzeigen
     * @param imgFile
     * @return
     */
    private ImageView smileLaden(final File imgFile){

        ImageView imageView = null;
        try {

            final javafx.scene.image.Image image = new Image(new FileInputStream(imgFile), 30, 0, true, true);
            imageView = new ImageView(image);
            imageView.setFitWidth(30);
            // Image Click
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("Meuse Click: " + event);
                }
            });

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return imageView;
    }


    /* :::::::::::::::::::::::::::::: Sticker Scroll Methoden  :::::::::::::::::::::::::::::::::::: */


    /**
     *              ALLGEMEINE BESCHREIBUNG VON SCROLLING
     *
     * ACHTUNG: die ScrollPane hatte immer den wert 1.0 egal wie lang ist sie
     * z.b.s: wenn eine scrollPane hatte 3 children(innen 3 Pane) dann
     *  Pane 1 hatte den wert 0.0 an der linke seite
     *  Pane 2 hatte den wert 0.5 an der linke seite
     *  Pane 3 hatte den wert 1.0 an der linke seite
     */


    /* ********************************* Bottom click *********************************** */

    /**
     * click auf dem Hyperlink unten in smile menü
     *
     * @param event
     */
    public void actionHandle(ActionEvent event){
        String hpl = ((Hyperlink)event.getSource()).getId();

        // click auf Aktive link ignorieren
        if (!hpl.equals(nodeAktiv)){

            switch (hpl){

                case "emojiLink":       // hover an emojiLink setzen
                                        emojiHover(hpl);
                                        if (nodeAktiv.equals("stickerLink")){
                                            // wenn stickerLink ist aktiv
                                            scrollNachRechts(0.5, -0.1);
                                        } else {
                                            // wenn gifLink ist aktiv
                                            scrollNachRechts(1.0, -0.1);
                                        }
                                        break;

                case "stickerLink":     // hover an stickerLink setzen
                                        stickerHover(hpl);
                                        if (nodeAktiv.equals("emojiLink")){
                                             // wenn emojiLink ist Aktiv
                                            scrollNachLinks(0.0, 0.51);
                                        } else {
                                            // wenn gifLink ist Aktiv
                                            scrollNachRechts(1.0, 0.49);
                                        }
                                        break;

                case "gifLink":         // hover an gifLink setzen
                                        gifHover(hpl);
                                        if (nodeAktiv.equals("emojiLink")){
                                            // wenn emojiLink ist Aktiv
                                            scrollNachLinks(0.0, 1.01);
                                        } else {
                                            // wenn stickerLink ist Aktiv
                                            scrollNachLinks(0.5, 1.01);
                                        }
                                        break;

                default:                    break;
            }

        } else {
            // click auf den aktiven Link, nichts machen
        }

    }


    /**
     * hover effect auf dem emoji Link setzen, andere ausblenden
     * @param eLink
     */
    private void emojiHover(String eLink){

        nodeAktiv = eLink;
        actionFalse(stickerLink);
        actionFalse(gifLink);
        actionTrue(emojiLink);

    }


    /**
     * hover effect auf dem sticker Link setzen, ander ausblenden
     * @param sLink
     */
    private void stickerHover(String sLink){

        nodeAktiv = sLink;
        actionFalse(emojiLink);
        actionFalse(gifLink);
        actionTrue(stickerLink);

    }


    /**
     * hover effect auf dem gif Link setzen, andere ausblenden
     * @param gLink
     */
    private void gifHover(String gLink){

        nodeAktiv = gLink;
        actionFalse(emojiLink);
        actionFalse(stickerLink);
        actionTrue(gifLink);

    }

    /**
     * css parameter auf den Hyperlink einbinden(hover einblenden)
     * @param link
     */
    private void actionTrue(Hyperlink link){

        link.getStyleClass().add("stickerLink");

    }

    /**
     * css parameter aus den Hyperlink löschen(hover ausblenden)
     * @param link
     */
    private void actionFalse(Hyperlink link){

        link.getStyleClass().remove("stickerLink");

    }



    /* ********************* scrolling mit der Zwei Finger ************************ */

    /**
     * KURZE BESCHREIBUNG: die Methode scrollHandle wird in init automatisch gestartet
     *
     *  1. scrolling mit den Zwei Finger an der Smile oberfläche(nicht mit dem click unten in bottom menü)
     *  2. bei Loslassen die 2 finger werde die postion an scrollNachLinks oder scrollNachRechts gesendet
     *  3. die variable handScrollAktiv wird auf true gesetzt(zeigt das mit finger gescrollt nicht mit dem click)
     *  4. in die Methode scrollNachLinks oder scrollNachRechts nach dem beendigung den scrolling wir geprüft,
     *   womit war gescrollt, mit zwei finger oder mit dem click
     *  5. wen mit click: nicht machen, der hover effect an lick werde bei actinHandle gesetzt
     *  6. wen mit zwei finger: mus ermittel welche smile-fenster wird aktuell angezeigt
     *      a: von dem Methode scrollNachLinks oder scrollNachRechts wir die scrollPane an die methode
     *          visibleHandle gesendet, da werde ermittelt welch borderPane ist aktiv, danach
     *          den Link hover gesetzt
     *      b: rückgabewert von methode: List nodes = getVisibleNodes(pane); ist eine BorderPane(smile ausgabe)
     *  ACHTUNG: die ermitlung von aktive smile-Fenster, wird nur bei hand scrollen, nicht bei click(in bottom),
     *  und dien nur für hover effect..
     *
     */


    /**
     * Mit zwei Finger Scrollen zwischen den emoji, sticker oder gif
     *
     * ACHTUNG: das ist eine zusätzliche method zum click in der menü unten in bottom
     */
    private void scrollHandle(){

        smileBoxScroll.setOnScrollStarted(s -> {
            //double startValue = round( stickerScroll.getHvalue() );
            // keine verwendung
        });

        smileBoxScroll.setOnScrollFinished(f -> {

            double scrollValue = round( smileBoxScroll.getHvalue() );
            if (scrollValue > 0.0 && scrollValue < 0.25){

                scrollNachLinks(scrollValue, 0.51);

            } else if (scrollValue > 0.25 && scrollValue < 0.51) {

                scrollNachRechts(scrollValue, 0.0);

            } else if (scrollValue > 0.5 && scrollValue < 0.75) {

                scrollNachLinks(scrollValue, 1.01);

            } else if (scrollValue > 0.75 && scrollValue < 1.0) {

                scrollNachRechts(scrollValue, 0.49);

            } else {
                // nicht machen
            }

            handScrollAktiv = true;
        });

    }


    /**
     * HOVER EFFECT SETZEN BEI SCROLLEN MIT DEN ZWEI FINGER
     *
     * Start in scrolling Methoden (ganz unten)
     * Aktuelle angezeigte BorderPane ermitteln, ob das emoji oder sticker oder gif ist.
     *
     *
     * ACHTUNG:
     * @param pane
     */
    private void visibleHandle(ScrollPane pane){

        List nodes = getVisibleNodes(pane);

        if (nodes.getFirst().equals(emojiBorder)){

            emojiHover(emojiLink.getId());

        } else if (nodes.getFirst().equals(stickerBorder)) {

            stickerHover(stickerLink.getId());

        } else if (nodes.getFirst().equals(gifBorder)) {

            gifHover(gifLink.getId());

        } else {
            //emojiHover(emojiLink.getId());
        }

        System.out.println("Handle: " + nodes.getFirst());

    }


    /**
     * ermitteln welches smile BorderPane aktuell angezeigt wird, für hover effect in bottom menü
     * angezeigt wird als list-array: erste BorderPane ist aktiv
     *                  [BorderPane[id=stickerBorder], BorderPane[id=gifBorder]]
     *
     * @param pane
     * @return
     */
    private List<Node> getVisibleNodes(ScrollPane pane) {
        List<Node> visibleNodes = new ArrayList<>();
        Bounds paneBounds = pane.localToScene(pane.getBoundsInParent());
        if (pane.getContent() instanceof Parent) {
            for (Node n : ((Parent) pane.getContent()).getChildrenUnmodifiable()) {
                Bounds nodeBounds = n.localToScene(n.getBoundsInLocal());
                if (paneBounds.intersects(nodeBounds)) {
                    visibleNodes.add(n);
                }
            }
        }
        return visibleNodes;
    }



    /**
     * double Wert nach komma auf 2 zahlen reduzieren
     * Original: 0.07714285829237533 oder 0.15000000266092173, höchste bis 1.0
     * neu Zahl: 0.09, 0.12 oder 0.32, höchste Zahl 1.0
     *
     * @param val
     * @return
     */
    private static double round(double val) {

        double s = Math.pow(10, 2);
        return Math.round(val * s) / s;
    }


    /* ::::::::::::::::::::::::::::::: Scroll Methoden :::::::::::::::::::::::::::::::::: */


    /**
     * scrolling läuft nach links, bis linke seite erreicht den wert 0.5 oder 1.0
     * 'nachLinks'(kleine wert: z.b.s 0.1) & 'vonRechts'(großer wert: 0.5)
     * die scrollPane wirt hinter den linke rand verschwinden
     *
     * @param nachLinks
     * @param vonRechts
     */
    private void scrollNachLinks(final double nachLinks, final double vonRechts){

        //System.out.println("nach Links: " + vonLinks +"/"+ nachRechts );

        Thread tread = new Thread(){
            public void run(){
                for (double x = nachLinks; x <= vonRechts; x+=0.01) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    smileBoxScroll.setHvalue(x);
                    //System.out.println("Tread: " + von +"/"+ nach);
                    //pane.setPrefHeight(Double.valueOf(i));
                }

                // angezeigte/active BorderPane ermitteln, für den hover effect in bottom menü
                if (handScrollAktiv){ visibleHandle(smileBoxScroll); }
                handScrollAktiv = false;
            }
        }; tread.start();
    }



    /**
     * die scrolling läuft nach rechts, bis die linke seite erreicht den wert 0.5 oder 0.0
     * 'vonLinks' ist der größere wert & die 'nachRechts' kleinere
     * die scrollPane wird hinter den Rechtes rand verschwinden
     *
     * ACHTUNG:
     *
     * @param vonLinks
     * @param nachRechts
     */
    private void scrollNachRechts(final double vonLinks, final double nachRechts){

        //System.out.println("nach Rechts: " + vonRechts +"/"+ nachLinks );
        Thread tread = new Thread(){
            public void run() {

                for (double y = vonLinks; y >= nachRechts; y-= 0.01) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    smileBoxScroll.setHvalue(y);
                    //System.out.println("For: " + i);
                }

                // angezeigte/active BorderPane ermitteln, für den hover effect in bottom menü
                if (handScrollAktiv) { visibleHandle(smileBoxScroll); }
                handScrollAktiv = false;
            }
        }; tread.start();
    }


}
