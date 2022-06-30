package BoteFx.controller;

import BoteFx.configuration.GlobalConfig;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MessageController implements Initializable {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    /**
     * die 3 AnchorPane hauptPane, leftPane & rightPane nicht
     * verändern oder Löschen, werden in Initialize & für weitere
     * Methoden benutzt
     * 1. initialize Zeile: 43 + 89
     * 2. GlobalConfig Zeile: 86-91(setPane + getPane)
     */
    @FXML private AnchorPane hauptPane;
    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;

    @FXML private BorderPane freundePane;
    @FXML private Label headerLabel;
    @FXML private Label footerLabel;

    /**
     * AnchorPane ID für weitere nutzung
     * 1. GlobalConfig (gesendet von initialize)
     * @return
     */
    public AnchorPane getHauptPane() {
        return hauptPane;
    }

    /**
     * Chat Methode Starten( klick auf den Freund/Linke Teil)
     */
    @FXML
    private AnchorPane chatmessage;
    public void chatStarten(ActionEvent event) {
        try {
            chatmessage = FXMLLoader.load(getClass().getResource("/templates/chat.fxml"));
            rightPane.getChildren().add(chatmessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AnchorPane.setLeftAnchor(chatmessage, 0.0);
        AnchorPane.setTopAnchor(chatmessage, 0.0);
        AnchorPane.setRightAnchor(chatmessage, 0.0);
        AnchorPane.setBottomAnchor(chatmessage, 0.0);
        logger.info("Chat Starten Methode: " + chatmessage);
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
         * ID hauptPane an GlobalConfig übergeben
         */
        var pane = getHauptPane();
        GlobalConfig.setPane(pane);

        // für die LeftPane -- später Löschen
        headerLabel.setMaxWidth(650);
        footerLabel.setMaxWidth(650);
        logger.info("Message Controller: initialize");

        /**
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
                            logger.info(" 1 ");

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
                            logger.info(" 2 ");

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
                        logger.info(" 3 ");
                    } else {
                        // Leer Lauf: newVal.intValue()
                        //logger.info("Leer Lauf 3: " + newVal.intValue());
                    }

                } /* Ende IF-ELSE */
            } /* Ende public void changed */
        }); /* Ende hauptPane */
    }  /* Ende initialize */


    /**
     * Change Pane(wenn App bleibt unter 650px verkleinert)
     *
     * ergänzung zu function chanched(oben) als Mobile version
     * die function funktioniert bei eine Große unter 650px
     * gemessen wird an AnchorPane
     *
     * anbindung möglichkeit: ID NICHT VERÄNDERN
     * ACHTUNG: Genau diese ID + onAction angeben.
     * 1. Hyper link fx:id="openfreunde" onAction="#chanchedPane" text="Zurück"
     * 2. Hyper link fx:id="openmessage" onAction="#chanchedPane" text="Message"
     *
     * @param event
     */

    private String actionId;
    public void chanchedPane(ActionEvent event) {
        actionId = ((Node) event.getSource()).getId();
        /* app auf verkleinerung prüfen */
        haupWidth = (int) hauptPane.getWidth();

    if(haupWidth < 650) {
        Thread tr = new Thread() {
            @Override
            public void run() {
                if ("openfreunde".equals(actionId)) {

                    for (int i = 0; i <= aktuelleWidth; i++) {
                        try {
                            Thread.sleep(1);
                            AnchorPane.setLeftAnchor(rightPane, Double.valueOf(i));
                            leftPane.setVisible(true);
                            leftPane.setMinWidth(i);
                            //logger.info("oben 650: " + i );
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else if ("openmessage".equals(actionId)) {
                    
                    for (int z = aktuelleWidth; z >= 0; z--) {
                        try {
                            Thread.sleep(1);
                            leftPane.setMinWidth(z);
                            rightPane.setVisible(true);
                            AnchorPane.setLeftAnchor(rightPane, Double.valueOf(z));
                            //logger.info("unten null: " + z);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else {
                    logger.info("Unerwartete Fehler bei MessengerController: Zeile 155 ");
                    //Leer
                }
            }
        }; tr.start();

    } else {
        //rightPane.getChildren().clear();
        logger.info("Das Hauptfenster ist mehr als 650px Groß" + hauptPane.getChildren());
    }/* Ende if hauptWidth */
    }/* Ende chanchedPane */

}
