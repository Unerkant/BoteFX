package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.event.ActionEvent;
import org.slf4j.Logger;

public class MailSuccessController {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());
    public void mailMessenger(ActionEvent event){

        GlobalViewSwitcher.switchTo(GlobalView.MESSAGE);
        logger.info("Mail Success Controller" + event);
    }
}
