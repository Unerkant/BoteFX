package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.event.ActionEvent;
import org.slf4j.Logger;

public class TelefonRegisterController {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    public void telefonSuccess(ActionEvent event){
        GlobalViewSwitcher.switchTo(GlobalView.TELEFONSUCCESS);
        logger.info("Telefon Register Controller" + event);
    }
}
