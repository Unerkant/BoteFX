package BoteFx.configuration;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GlobalConfig {

    /**
     * Globalle Constance deffinieren
     */

    public static final String FILE_CSS         = "/static/css/style.css";
    public static final String FILE_URL         = "/Users/paulrichter/ideaProject/BoteFX/";
    public static final String FILE_HTTP        = "http://localhost:8080/";

    public static final double DEFAULT_WIDTH    = 480;
    public static final double DEFAUL_HEIGHT    = 620;
    public static final double MIN_WIDTH        = 380;
    public static final double MIN_HEIGHT       = 720;
    public static final double MAX_WIDTH        = 650;
    public static final double START_WIDTH      = 480;
    public static final double START_HEIGHT     = 720;


    /**
     * Logger Service
     */
    public static Logger getLogger(Class clas){
        return LoggerFactory.getLogger(clas);
    }


    /**
     * Aktuelle Datum
     * 1. deDatum() -> für Datenbank speichern
     * 2. Identifikation Token erstellen(nach aktuelle Datum),
     *    Tag+Monat+Jahr+Stunde+Minute+Sekunde
     */
    public static String deDatum(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date de = new Date();
        return format.format(de);
    }


    /**
     * User Token
     * wird generiert von aktuelle Datum als 14-stellige Zahlen
     * Tag+Monat+Jahr+Stunde+Minute+Sekunde
     *
     * @return
     */
    public static String IdentifikationToken(){
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date token = new Date();
        return format.format(token);
    }


    /**
     * E-Mail Adresse Validieren
     *
     * return true/false
     */
    public static boolean mailValid(String mail){
        //String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,20})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }


    /**
     * Stage Fenster mit Maus frei an Bildschirm Positionieren
     */
    private static double x = 0;
    private static double y = 0;
    @FXML
    public static void dragget(MouseEvent event){
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
        //System.out.println("Global dragged");
    }
    @FXML
    public static void pressed(javafx.scene.input.MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
        //System.out.println("Global pressed");
    }


    /**
     * Stage Fenster Schliessen
     */
    public static void stageClose(ActionEvent event){
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        System.out.println("Global Stage Close");
    }

}