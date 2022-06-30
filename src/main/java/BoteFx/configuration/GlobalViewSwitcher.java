package BoteFx.configuration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paul Richter(info@bote.de)
 * denn 15.05.2022
 */

public class GlobalViewSwitcher {

    private static Map<GlobalView, Parent> cache = new HashMap<>();

    /**
     * Scene von BoteApp laden
     */
    private static Scene scene;
    public static void setScene(Scene scene1){
        GlobalViewSwitcher.scene = scene1;
    }

    /**
     * Laden von FXML templates
     * @param view
     */
    public static void switchTo(GlobalView view){
        if (scene == null){
            System.out.println("No Scene, GlobalViewSwitcher: Zeile 27");
            return;
        }
        try {
                Parent root;
                if (cache.containsKey(view)){
                    root = cache.get(view);
                    System.out.println("Load from Cache");
                }else{
                    root = FXMLLoader.load(GlobalViewSwitcher.class.getResource(view.getFxmlName()));
                    cache.put(view, root);
                    System.out.println("Load from FXML");
                }
                /* fxml templates Laden */
                scene.setRoot(root);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
