public class Ordinacija {
    private int id;
    private String ime;
    private String naslov;
    private String telefon;
    private String email;
    private int stZaposlenih;
    private int lastnikId;
    private String lastnikIme;
    private String lastnikPriimek;

    public Ordinacija(int id, String ime, String naslov, String telefon, String email, int stZaposlenih, int lastnikId, String lastnikIme, String lastnikPriimek) {
        this.id = id;
        this.ime = ime;
        this.naslov = naslov;
        this.telefon = telefon;
        this.email = email;
        this.stZaposlenih = stZaposlenih;
        this.lastnikId = lastnikId;
        this.lastnikIme = lastnikIme;
        this.lastnikPriimek = lastnikPriimek;
    }

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public String getNaslov() {
        return naslov;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getEmail() {
        return email;
    }

    public int getStZaposlenih() {
        return stZaposlenih;
    }

    public int getLastnikId() {
        return lastnikId;
    }

    public String getLastnikIme() {
        return lastnikIme;
    }

    public String getLastnikPriimek() {
        return lastnikPriimek;
    }
}
