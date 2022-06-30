package BoteFx.configuration;

public enum GlobalView {
    HOME("/templates/login/home.fxml"),
    LOGINMAIL("/templates/login/maillogin.fxml"),
    MAILREGISTER("/templates/login/mailregister.fxml"),
    MAILSUCCESS("/templates/login/mailsuccess.fxml"),
    LOGINTELEFON("/templates/login/telefonlogin.fxml"),
    TELEFONREGISTER("/templates/login/telefonregister.fxml"),
    TELEFONSUCCESS("/templates/login/telefonsuccess.fxml"),
    MESSAGE("/templates/message.fxml"),
    CHAT("/templates/chat.fxml"),
    BOTE("/templates/bote.fxml");

    private String fxmlName;
    GlobalView(String fxmlName){
        this.fxmlName = fxmlName;
    }

    public String getFxmlName(){
        return fxmlName;
    }

}
