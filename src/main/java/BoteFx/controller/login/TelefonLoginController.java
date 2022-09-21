package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;
import javafx.event.ActionEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TelefonLoginController {

    @Autowired
    private ViewService viewService;

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    public void telefonRegister(ActionEvent event){
        viewService.switchTo(GlobalView.TELEFONREGISTER);
        logger.info("Telefon Login Controller" + event);
    }

    public void mailLogin(ActionEvent event) {
        viewService.switchTo(GlobalView.LOGINMAIL);
        logger.info("Telefon Login Controller");
    }
}
