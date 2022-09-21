package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TelefonSuccessController {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;

    public void telefonMessenger(ActionEvent event){
        viewService.switchTo(GlobalView.CHATBOX);
        logger.info("Telefon Success Controller" + event);
    }
}