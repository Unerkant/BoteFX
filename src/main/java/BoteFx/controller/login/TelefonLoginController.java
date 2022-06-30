package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.event.ActionEvent;
import org.slf4j.Logger;

public class TelefonLoginController {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    public void telefonRegister(ActionEvent event){
        GlobalViewSwitcher.switchTo(GlobalView.TELEFONREGISTER);
        logger.info("Telefon Login Controller" + event);
    }

    public void mailLogin(ActionEvent event) {
        GlobalViewSwitcher.switchTo(GlobalView.LOGINMAIL);
        logger.info("Telefon Login Controller");
    }
}
