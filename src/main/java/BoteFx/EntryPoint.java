package BoteFx;
import com.sun.javafx.application.LauncherImpl;
import org.springframework.boot.SpringApplication;

public class EntryPoint {

    public static void main(String[] args) {
        LauncherImpl.launchApplication(BoteApp.class, null, args);
    }

}
