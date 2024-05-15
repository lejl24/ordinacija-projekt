public class Lastnik {
    private String ime;
    private String priimek;

    public Lastnik(String ime, String priimek) {
        this.ime = ime;
        this.priimek = priimek;
    }

    // Dodaj getterja za ime in priimek
    public String getIme() {
        return ime;
    }

    public String getPriimek() {
        return priimek;
    }

    // Metoda za lepši izpis lastnika
    @Override
    public String toString() {
        return ime + " " + priimek;
    }
}
