import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Ordinacija {
    private final StringProperty ime;
    private final StringProperty naslov;
    private final StringProperty telefon;
    private final StringProperty email;

    public Ordinacija(String ime, String naslov, String telefon, String email) {
        this.ime = new SimpleStringProperty(ime);
        this.naslov = new SimpleStringProperty(naslov);
        this.telefon = new SimpleStringProperty(telefon);
        this.email = new SimpleStringProperty(email);
    }

    public String getIme() {
        return ime.get();
    }

    public StringProperty imeProperty() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime.set(ime);
    }

    public String getNaslov() {
        return naslov.get();
    }

    public StringProperty naslovProperty() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov.set(naslov);
    }

    public String getTelefon() {
        return telefon.get();
    }

    public StringProperty telefonProperty() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon.set(telefon);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }
}
