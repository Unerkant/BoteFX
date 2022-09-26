package BoteFx.Enums;

/**
 * Den 15.05.2022
 */
public enum GlobalView {
    HOME("/templates/login/home.fxml"),
    MAILLOGIN("/templates/login/maillogin.fxml"),
    MAILREGISTER("/templates/login/mailregister.fxml"),
    MAILSUCCESS("/templates/login/mailsuccess.fxml"),
    TELEFONLOGIN("/templates/login/telefonlogin.fxml"),
    TELEFONREGISTER("/templates/login/telefonregister.fxml"),
    TELEFONSUCCESS("/templates/login/telefonsuccess.fxml"),
    CHATBOX("/templates/chatbox.fxml"),
    CHATMESSAGE("/templates/chatmessage.fxml"),
    CHATFREUNDE("/templates/chatfreunde.fxml");

    private String fxmlName;
    GlobalView(String fxmlName){
        this.fxmlName = fxmlName;
    }

    public String getFxmlName(){
        return fxmlName;
    }

}
