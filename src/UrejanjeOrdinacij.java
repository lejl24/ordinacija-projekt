import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
        TextField naslovField = new TextField(ordinacija.getNaslov());
        TextField telefonField = new TextField(String.valueOf(ordinacija.getTelefon()));
        TextField emailField = new TextField(ordinacija.getEmail());
        TextField stZaposlenihField = new TextField(String.valueOf(ordinacija.getStZaposlenih()));

        ComboBox<String> krajComboBox = new ComboBox<>();
        loadKraji(krajComboBox);

        gridPane.add(new Label("Ime ordinacije:"), 0, 0);
        gridPane.add(imeField, 1, 0);
        gridPane.add(new Label("Kraj:"), 0, 1);
        gridPane.add(krajComboBox, 1, 1);
        gridPane.add(new Label("Naslov:"), 0, 2);
        gridPane.add(naslovField, 1, 2);
        gridPane.add(new Label("Telefon:"), 0, 3);
        gridPane.add(telefonField, 1, 3);
        gridPane.add(new Label("Email:"), 0, 4);
        gridPane.add(emailField, 1, 4);
        gridPane.add(new Label("Število zaposlenih:"), 0, 5);
        gridPane.add(stZaposlenihField, 1, 5);

        Button potrdiButton = new Button("Potrdi");
        potrdiButton.setOnAction(event -> {
            String novoIme = imeField.getText();
            String novNaslov = naslovField.getText();
            String novTelefon = telefonField.getText();
            String novEmail = emailField.getText();
            String novKraj = krajComboBox.getValue().split(":")[0];
            int novStZaposlenih = Integer.parseInt(stZaposlenihField.getText());

            try {
                int novTelefonInt = Integer.parseInt(novTelefon);
                posodobiOrdinacijo(novoIme, novNaslov, novTelefonInt, novEmail, Integer.parseInt(novKraj), novStZaposlenih);
                stage.close();
            } catch (NumberFormatException e) {
                // Prikaz sporočila o napaki, če telefon ni veljavno število
                telefonField.setStyle("-fx-border-color: red;");
                telefonField.setPromptText("Vnesite veljavno številko");
            }
        });

        gridPane.add(potrdiButton, 1, 6);

        Scene scene = new Scene(gridPane, 400, 400);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void loadKraji(ComboBox<String> krajComboBox) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, ime FROM kraji");
             ResultSet resultSet = statement.executeQuery()) {
            ObservableList<String> krajiList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ime = resultSet.getString("ime");
                krajiList.add(id + ": " + ime);
            }
            krajComboBox.setItems(krajiList);
            // Set default value based on existing data
            for (String item : krajiList) {
                if (item.startsWith(ordinacija.getKrajId() + ":")) {
                    krajComboBox.setValue(item);
                    break;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error loading kraji: " + ex.getMessage());
        }
    }

    public void posodobiOrdinacijo(String ime, String naslov, int telefon, String email, int krajId, int stZaposlenih) {
        try {
            String updateOrdinacijaSQL = "UPDATE ordinacije SET ime = ?, naslov = ?, telefon = ?, email = ?, kraj_id = ?, st_zaposlenih = ? WHERE id = ?";
            try (PreparedStatement updateOrdinacijaStmt = connection.prepareStatement(updateOrdinacijaSQL)) {
                updateOrdinacijaStmt.setString(1, ime);
                updateOrdinacijaStmt.setString(2, naslov);
                updateOrdinacijaStmt.setInt(3, telefon);
                updateOrdinacijaStmt.setString(4, email);
                updateOrdinacijaStmt.setInt(5, krajId);
                updateOrdinacijaStmt.setInt(6, stZaposlenih);
                updateOrdinacijaStmt.setInt(7, ordinacija.getId());
                updateOrdinacijaStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
