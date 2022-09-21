package BoteFx;

import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
     *  Aktuelle Seite wird geladen von GlobalViewSwitcher mit function
     *  switchTo, die fxml-name-constance(was soll geladen sein) wird durch function
     *  von GlobelView bestimmt. z.b.s
     *   @FXML
     *     public void zumMailLogin(ActionEvent event) {
     *         GlobalViewSwitcher.switchTo(GlobalView.LOGINMAIL);
     *     }
     */
@SpringBootApplication
public class BoteApp extends Application {

    private ViewService viewService;

    @Override
    public void init() {
        ConfigurableApplicationContext springContext = SpringApplication.run(BoteApp.class);
        viewService = springContext.getBean(ViewService.class);
        viewService.setSpringContext(springContext);
    }

    @Override
    public void start(Stage stage) throws Exception {

        var scene = new Scene(new Pane());

        /* FXML templates laden */
        viewService.setScene(scene);
        viewService.switchTo(GlobalView.CHATBOX);

        /* Stage CSS */
        scene.getStylesheets().add(getClass().getResource(GlobalConfig.FILE_CSS).toExternalForm());
        Screen screen = Screen.getPrimary();
        Rectangle2D rect = screen.getVisualBounds(); /* height auf 100% ziehen */
        stage.setTitle("Bote");
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(655);
             /* Später, rect.getHeight() benutzen*/
        //stage.setHeight(rect.getHeight());
        stage.setMinWidth(GlobalConfig.MIN_WIDTH);
        stage.setMinHeight(GlobalConfig.MIN_HEIGHT);
       // stage.setResizable(false);

        /* Stage Starten */
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception{
        super.stop();
    }

}

