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
    EINLADEN("/templates/einladen.fxml"),
    MESSAGE("/templates/message.fxml"),
    FREUNDE("/templates/freunde.fxml"),
    SETTING("/templates/setting.fxml"),
    KONTAKTE("/templates/kontakte.fxml"),
    TELEFON("/templates/telefon.fxml"),
    PROFIL("/templates/setting/profil.fxml"),
    ALLGEMEIN("/templates/setting/allgemein.fxml"),
    DARSTELLUNG("/templates/setting/darstellung.fxml"),
    SICHERHEIT("/templates/setting/sicherheit.fxml"),
    TOENE("/templates/setting/toene.fxml"),
    SPRACHE("/templates/setting/sprache.fxml"),
    STICKER("/templates/setting/sticker.fxml"),
    FAQ("/templates/setting/faq.fxml"),
    SUPPORT("/templates/setting/support.fxml"),
    SITZUNG("/templates/setting/sitzung.fxml"),

    FREUNDECELL("/templates/fragments/freundecell.fxml"),
    FREUNDELEER("/templates/fragments/freundeleer.fxml"),
    MESSAGELEER("/templates/fragments/messageleer.fxml");

    private String fxmlName;
    GlobalView(String fxmlName){
        this.fxmlName = fxmlName;
    }

    public String getFxmlName(){
        return fxmlName;
    }

}
