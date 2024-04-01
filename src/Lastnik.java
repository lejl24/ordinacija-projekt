import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Lastnik {
    private final StringProperty ime;
    private final StringProperty priimek;

    public Lastnik(String ime, String priimek) {
        this.ime = new SimpleStringProperty(ime);
        this.priimek = new SimpleStringProperty(priimek);
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

    public String getPriimek() {
        return priimek.get();
    }

    public StringProperty priimekProperty() {
        return priimek;
    }

    public void setPriimek(String priimek) {
        this.priimek.set(priimek);
    }
}
