package BoteFx.service;

import BoteFx.Enums.GlobalView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Den 27.09.2022
 */

@Service
public class LayoutService {

    @Autowired
    private LanguageService languageService;



    /**
     * Getter & Setter von AnchorPane
     * zugesendete AnchorPane ID:
     */
    private Pane ausgabeLayout;
    public Pane getausgabeLayout() {
        return ausgabeLayout;
    }
    public void setausgabeLayout(Pane ausgabeLayout1) {
        this.ausgabeLayout = ausgabeLayout1;
    }



    /**
     * QEULLE: BoteApp.java Zeile: 40
     * @param layoutContext1
     */
    private ConfigurableApplicationContext layoutContext;
    public void setLayoutContext(ConfigurableApplicationContext layoutContext1){
        this.layoutContext = layoutContext1;
    }



    public Object switchLayout(GlobalView globalView){

        // vor dem Laden, zuerst in Element/Pane alles Löschen
        //ausgabeLayout.getChildren().clear();
        LayoutControllerPair layoutControllerPair = createLayoutController(globalView);
        ausgabeLayout.getChildren().add(layoutControllerPair.getView());
        //scene.setRoot(layoutControllerPair.getView());

        return layoutControllerPair.getController();
    }

    /**
     * Den 17.11.2022
     * geändert von private auf public...beschreibung unten Zeile: 84
     *
     * @param globalView
     * @return
     */
    public LayoutControllerPair createLayoutController(GlobalView globalView){

        FXMLLoader loader = new FXMLLoader(getClass().getResource(globalView.getFxmlName()));
        loader.setResources(languageService.getCurrentUsedResourceBundle());
        loader.setControllerFactory(layoutContext::getBean);

        try {
            Parent parent = loader.load();
            Object controller = loader.getController();
            return new LayoutControllerPair(parent, controller);

        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }

        return null;
    }

    /**
     *   Den 17.11.2022
     *   geändert von private auf public, weil...
     *   brauchte nur Parent von freundecell.fxml für weitere ausgabe in schleife
     *   "freundeVBox.getChildren().add(pair.getView())"
     *   Quelle: FreundeController Zeile: 130
     */
    public class LayoutControllerPair{

        private Parent view;
        private Object controller;
        public LayoutControllerPair(Parent view, Object controller){
            this.view       = view;
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
