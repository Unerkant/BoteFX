package BoteFx.service;

import BoteFx.Enums.GlobalView;
import BoteFx.configuration.GlobalConfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public class ViewService {

    @Autowired
    private TokenService tokenService;
    private ConfigurableApplicationContext springContext;

    private Scene scene;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setSpringContext(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public Object switchTo(GlobalView globalView) {

        /* prüfen ob token vorhanden ist wenn Leer: HOME Starten */
        File datei = new File(GlobalConfig.FILE_URL+"src/main/resources/static/json/token.json");
        if (datei.length() == 0){
            switch (globalView){
                case MAILSUCCESS:           globalView = GlobalView.MAILSUCCESS; break;
                case TELEFONSUCCESS:        globalView = GlobalView.TELEFONSUCCESS; break;
                case MAILREGISTER:          globalView = GlobalView.MAILREGISTER; break;
                case TELEFONREGISTER:       globalView = GlobalView.TELEFONREGISTER; break;
                case MAILLOGIN:             globalView = GlobalView.MAILLOGIN; break;
                case TELEFONLOGIN:          globalView = GlobalView.TELEFONLOGIN; break;
                default:                    globalView = GlobalView.HOME;
            }
        }

        ViewWithControllerPair viewWithControllerPair = createViewWithController(globalView);
        scene.setRoot(viewWithControllerPair.getView());

        return viewWithControllerPair.getController();
    }

    private ViewWithControllerPair createViewWithController(GlobalView globalView) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(globalView.getFxmlName()));
        fxmlLoader.setControllerFactory(springContext::getBean);

        try {
            Parent root = fxmlLoader.load();
            Object controller = fxmlLoader.getController();

            return new ViewWithControllerPair(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class ViewWithControllerPair {
        private Parent view;
        private Object controller;

        public ViewWithControllerPair(Parent view, Object controller) {
            this.view = view;
            this.controller = controller;
        }

        public Parent getView() {
            return view;
        }

        public void setView(Parent view) {
            this.view = view;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }
    }
}
