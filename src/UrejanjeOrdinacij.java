import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UrejanjeOrdinacij {
    private Ordinacija ordinacija;
    private Connection connection;

    public UrejanjeOrdinacij(Ordinacija ordinacija, Connection connection) {
        this.ordinacija = ordinacija;
        this.connection = connection;
    }

    public void prikaziOknoUrejanjaOrdinacije() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Urejanje ordinacije");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Dodajanje besedilnih polj za urejanje podatkov
        TextField imeField = new TextField(ordinacija.getIme());
        TextField lastnikImeField = new TextField(ordinacija.getLastnikIme());
        TextField lastnikPriimekField = new TextField(ordinacija.getLastnikPriimek());
        TextField naslovField = new TextField(ordinacija.getNaslov());
        TextField telefonField = new TextField(ordinacija.getTelefon());
        TextField emailField = new TextField(ordinacija.getEmail());

        gridPane.add(new Label("Ime ordinacije:"), 0, 0);
        gridPane.add(imeField, 1, 0);
        gridPane.add(new Label("Ime lastnika:"), 0, 1);
        gridPane.add(lastnikImeField, 1, 1);
        gridPane.add(new Label("Priimek lastnika:"), 0, 2);
        gridPane.add(lastnikPriimekField, 1, 2);
        gridPane.add(new Label("Naslov:"), 0, 3);
        gridPane.add(naslovField, 1, 3);
        gridPane.add(new Label("Telefon:"), 0, 4);
        gridPane.add(telefonField, 1, 4);
        gridPane.add(new Label("Email:"), 0, 5);
        gridPane.add(emailField, 1, 5);

        Button potrdiButton = new Button("Potrdi");
        potrdiButton.setOnAction(event -> {
            String novoIme = imeField.getText();
            String novLastnikIme = lastnikImeField.getText();
            String novLastnikPriimek = lastnikPriimekField.getText();
            String novNaslov = naslovField.getText();
            String novTelefon = telefonField.getText();
            String novEmail = emailField.getText();

            posodobiOrdinacijo(novoIme, novLastnikIme, novLastnikPriimek, novNaslov, novTelefon, novEmail);
            stage.close();
        });

        gridPane.add(potrdiButton, 1, 6);

        Scene scene = new Scene(gridPane, 400, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void posodobiOrdinacijo(String ime, String lastnikIme, String lastnikPriimek, String naslov, String telefon, String email) {
        try {
            // Posodobitev tabele ordinacije
            String updateOrdinacijaSQL = "UPDATE ordinacije SET ime = ?, naslov = ?, telefon = ?, email = ? WHERE id = ?";
            try (PreparedStatement updateOrdinacijaStmt = connection.prepareStatement(updateOrdinacijaSQL)) {
                updateOrdinacijaStmt.setString(1, ime);
                updateOrdinacijaStmt.setString(2, naslov);
                updateOrdinacijaStmt.setString(3, telefon);
                updateOrdinacijaStmt.setString(4, email);
                updateOrdinacijaStmt.setInt(5, ordinacija.getId());
                updateOrdinacijaStmt.executeUpdate();
            }

            // Posodobitev tabele lastniki
            String updateLastnikSQL = "UPDATE lastniki SET ime = ?, priimek = ? WHERE id = ?";
            try (PreparedStatement updateLastnikStmt = connection.prepareStatement(updateLastnikSQL)) {
                updateLastnikStmt.setString(1, lastnikIme);
                updateLastnikStmt.setString(2, lastnikPriimek);
                updateLastnikStmt.setInt(3, ordinacija.getLastnikId());
                updateLastnikStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Dodajte obravnavo napak po potrebi
        }
    }
}
