package BoteFx.Enums;

/**
 * Den 15.05.2022
 *
 *    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z, Ä, Ö, Ü, ß
 */
public enum GlobalView {

    ALLGEMEIN("/templates/setting/allgemein.fxml"),
    CHATBOX("/templates/chatbox.fxml"),
    DARSTELLUNG("/templates/setting/darstellung.fxml"),
    EINLADEN("/templates/einladen.fxml"),
    FAQ("/templates/setting/faq.fxml"),
    FREUNDE("/templates/freunde.fxml"),
    FREUNDECELL("/templates/fragments/freundecell.fxml"),
    FREUNDEINFO("/templates/freundeinfo.fxml"),
    HOME("/templates/login/home.fxml"),
    KONTAKTE("/templates/kontakte.fxml"),
    MAILLOGIN("/templates/login/maillogin.fxml"),
    MAILREGISTER("/templates/login/mailregister.fxml"),
    MAILSUCCESS("/templates/login/mailsuccess.fxml"),
    MESSAGE("/templates/message.fxml"),
    MESSAGELEER("/templates/fragments/messageleer.fxml"),
    MESSAGEWEITERLEITEN("/templates/fragments/messageweiterleiten.fxml"),
    ORDNER("/templates/setting/ordner.fxml"),
    PROFIL("/templates/setting/profil.fxml"),
    SETTING("/templates/setting.fxml"),
    SITZUNG("/templates/setting/sitzung.fxml"),
    SICHERHEIT("/templates/setting/sicherheit.fxml"),
    SPEICHER("/templates/setting/speicher.fxml"),
    SPRACHE("/templates/setting/sprache.fxml"),
    SMILE("/templates/setting/smile.fxml"),
    SMILEBOX("/templates/smilebox.fxml"),
    SUPPORT("/templates/setting/support.fxml"),
    TELEFON("/templates/telefon.fxml"),
    TELEFONLOGIN("/templates/login/telefonlogin.fxml"),
    TELEFONREGISTER("/templates/login/telefonregister.fxml"),
    TELEFONSUCCESS("/templates/login/telefonsuccess.fxml"),
    TOENE("/templates/setting/toene.fxml");


    private String fxmlName;
    GlobalView(String fxmlName){
        this.fxmlName = fxmlName;
    }

    public String getFxmlName(){
        return fxmlName;
    }

}
