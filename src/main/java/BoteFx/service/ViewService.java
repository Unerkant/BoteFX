package BoteFx.service;

import BoteFx.Enums.GlobalView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Datum Unbekannt
 */

@Service
public class ViewService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private LanguageService languageService;


    private Scene scene;
    public Scene getScene() {
        return scene;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }


    /**
     * QUELLE: BoteApp.java Zeile: 38
     * @param springContext
     */
    private ConfigurableApplicationContext springContext;
    public void setSpringContext(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }


    /**
     * wenn kein Token vorhanden ist, register modus starten
     * @param globalView
     * @return
     */
    public Object switchTo(GlobalView globalView) {

        String myToken = tokenService.meinToken();

        if (myToken == null){
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
        fxmlLoader.setResources(languageService.getCurrentUsedResourceBundle());
        fxmlLoader.setControllerFactory(springContext::getBean);

        try {
            //Parent root = fxmlLoader.load();
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
