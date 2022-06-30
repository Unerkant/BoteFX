package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.event.ActionEvent;
import org.slf4j.Logger;

public class TelefonSuccessController {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    public void telefonMessenger(ActionEvent event){
        GlobalViewSwitcher.switchTo(GlobalView.MESSAGE);
        logger.info("Telefon Success Controller" + event);
    }
}
