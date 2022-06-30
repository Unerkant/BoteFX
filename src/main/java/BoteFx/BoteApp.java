package BoteFx;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    @Override
    public void start(Stage stage) throws Exception {

        var scene = new Scene(new Pane());
        //scene.setFill(Color.TRANSPARENT);
        //stage.initStyle(StageStyle.TRANSPARENT);

        /* FXML templates laden */
        GlobalViewSwitcher.setScene(scene);
        GlobalViewSwitcher.switchTo(GlobalView.MESSAGE);

        /* Stage CSS */
        scene.getStylesheets().add(getClass().getResource(GlobalConfig.FILE_CSS).toExternalForm());
        Screen screen = Screen.getPrimary();
        Rectangle2D rect = screen.getVisualBounds(); /* height auf 100% ziehen */
        stage.setTitle("Bote");
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(GlobalConfig.START_WIDTH);
        stage.setHeight(GlobalConfig.START_HEIGHT); /* Später Löschen, rect.getHeight() benutzen*/
        //stage.setHeight(rect.getHeight());
        stage.setMinWidth(GlobalConfig.MIN_WIDTH);
        stage.setMinHeight(GlobalConfig.MIN_HEIGHT);
       // stage.setResizable(false);

        /* Stage Starten */
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception{
        super.stop();
    }

}

