import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Ordinacija {
    private final StringProperty ime;
    private final StringProperty lastnik;
    private final StringProperty naslov;
    private final StringProperty telefon;
    private final StringProperty email;
    private final StringProperty kraj;

    public Ordinacija(String ime, String lastnik, String naslov, String telefon, String email, String kraj) {
        this.ime = new SimpleStringProperty(ime);
        this.lastnik = new SimpleStringProperty(lastnik);
        this.naslov = new SimpleStringProperty(naslov);
        this.telefon = new SimpleStringProperty(telefon);
        this.email = new SimpleStringProperty(email);
        this.kraj = new SimpleStringProperty(kraj);
    }

    public StringProperty imeProperty() {
        return ime;
    }

    public StringProperty lastnikProperty() {
        return lastnik;
    }

    public StringProperty naslovProperty() {
        return naslov;
    }

    public StringProperty telefonProperty() {
        return telefon;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty krajProperty() {
        return kraj;
    }
}
