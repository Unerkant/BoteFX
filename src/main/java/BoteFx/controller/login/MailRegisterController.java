package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.event.ActionEvent;
import org.slf4j.Logger;

public class MailRegisterController {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());
    public void mailRegister(ActionEvent event){

        GlobalViewSwitcher.switchTo(GlobalView.MAILSUCCESS);
        logger.info("Mail Register Controller" +event);
    }

}
