package BoteFx;

import BoteFx.configuration.GlobalConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BoteApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BoteApp.class.getResource("/templates/bote.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), GlobalConfig.DEFAULT_WIDTH, GlobalConfig.DEFAUL_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/static/css/style.css").toExternalForm());
        stage.setTitle("Bote");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}