package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


/**
    *   den 20.05.2022
    */
@Controller
public class MailLoginController {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    /**
     * Login E-Mail Validate
     * 1. Mail toLowerCase
     * 2. Mail auf 50 Zeichen Begrenzen
     *
     * @param event
     */
    @FXML private Label mailloginfehler;
    @FXML private TextField maillogininput;
    @FXML private Button mailloginbutton;
    private int maxLimit = 50;
    private String mail;
    private boolean valid;

    private String token = "25052022181457";

    /* Zum Testen */
    public void allSuchen(ActionEvent event) {

        mailloginfehler.setText("Fehler von Mail Login Controller");
        logger.info("Methode allSuchen: " + event);
    }


/* 1. validate */
    public void mailLowerCase(KeyEvent keyEvent) {
        maillogininput.textProperty().addListener((ov, oldValue, newValue )->{
            maillogininput.setText(newValue.toLowerCase());
            //logger.info(" toLowerCase ");
        });
    }

/* 2. validate */
    public void mailLength(KeyEvent keyEvent) {
        maillogininput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obValue, String oldValue, String newValue) {
                if (maillogininput.getText().length() > maxLimit){
                    String s = maillogininput.getText().substring(0, maxLimit);
                    maillogininput.setText(s);
                    mailloginfehler.setText("E-Mail ist zu Groß, erlaubt sind nur 50 Zeichen");
                }
            }
        });
    }

/* 3. validate */
    @FXML
    public void mailLoginValidate(ActionEvent event){
        mail = maillogininput.getText();
        valid = GlobalConfig.mailValid(mail);
        try {
                if (valid){
                    mailloginfehler.setText(mail);
                    logger.info("Gultige E-Mail" );
                }else{
                    mailloginfehler.setText("Keine gültige E-Mail");
                }
        }catch (Exception e){
            mailloginfehler.setText(String.valueOf(e));
        }
        //GlobalViewSwitcher.switchTo(GlobalView.MAILREGISTER);
        //logger.info("Mail Login Validate");
    }


    /**
     * Wechseln zu telefonlogin.fxml
     * @param event
     */
    @FXML
    public void telefonLoginLinks(ActionEvent event){
        GlobalViewSwitcher.switchTo(GlobalView.LOGINTELEFON);
        logger.info("Telefon Login");
    }

    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    @FXML
    public void mailloginDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
    }
    @FXML
    public void mailloginPressed(MouseEvent event) {
        GlobalConfig.pressed(event);
    }


    /**
     * Stage Fenster schliessen...
     */
    @FXML
    public void mailloginClose(ActionEvent event){

        GlobalConfig.stageClose(event);
        logger.info("Mail Login Close");
    }



}
