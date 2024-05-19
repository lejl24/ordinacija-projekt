import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

class DodajanjeZaposlenega {

    private static final String PGURL = "jdbc:postgresql://ep-empty-bonus-a26htj4v.eu-central-1.aws.neon.tech/databaza?sslmode=require";
    private static final String PGUSER = "databaza_owner";
    private static final String PGPASSWORD = "p9aAuiRvMYE5";
    private static final String URL = PGURL;

    private ObservableList<String> ordinacije = FXCollections.observableArrayList();
    private Stage stage;

    public DodajanjeZaposlenega() {
        fetchOrdinacijeFromDatabase();  // Fetch list of ordinacije when initializing
    }

    public void prikaziOknoDodajanjaZaposlenega() {
        stage = new Stage();
        stage.setTitle("Dodajanje Zaposlenega");

        // Creating form elements
        Label imeLabel = new Label("Ime:");
        TextField imeField = new TextField();

        Label priimekLabel = new Label("Priimek:");
        TextField priimekField = new TextField();

        Label polozajLabel = new Label("Polozaj:");
        TextField polozajField = new TextField();

        Label ordinacijaLabel = new Label("Ordinacija:");
        ComboBox<String> ordinacijaComboBox = new ComboBox<>();
        ordinacijaComboBox.setItems(ordinacije);

        Button dodajBtn = new Button("Dodaj");
        dodajBtn.setOnAction(event -> {
            String ime = imeField.getText();
            String priimek = priimekField.getText();
            String polozaj = polozajField.getText();
            String izbranaOrdinacija = ordinacijaComboBox.getValue();

            if (izbranaOrdinacija != null && !izbranaOrdinacija.isEmpty()) {
                int ordinacijaId = getOrdinacijaId(izbranaOrdinacija);
                if (ordinacijaId != -1) {
                    insertZaposleni(ime, priimek, polozaj, ordinacijaId);

                    // Clear fields after submission
                    imeField.clear();
                    priimekField.clear();
                    polozajField.clear();
                    ordinacijaComboBox.getSelectionModel().clearSelection();
                }
            } else {
                System.out.println("Prosimo izberite ordinacijo.");
            }
        });

        // Layout setup
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        GridPane.setConstraints(imeLabel, 0, 0);
        GridPane.setConstraints(imeField, 1, 0);

        GridPane.setConstraints(priimekLabel, 0, 1);
        GridPane.setConstraints(priimekField, 1, 1);

        GridPane.setConstraints(polozajLabel, 0, 2);
        GridPane.setConstraints(polozajField, 1, 2);

        GridPane.setConstraints(ordinacijaLabel, 0, 3);
        GridPane.setConstraints(ordinacijaComboBox, 1, 3);

        GridPane.setConstraints(dodajBtn, 1, 4);

        grid.getChildren().addAll(
                imeLabel, imeField,
                priimekLabel, priimekField,
                polozajLabel, polozajField,
                ordinacijaLabel, ordinacijaComboBox,
                dodajBtn
        );

        VBox layout = new VBox(10);
        layout.getChildren().addAll(grid);
        Scene scene = new Scene(layout, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    private void fetchOrdinacijeFromDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ime FROM ordinacije")) {

            ordinacije.clear();
            while (resultSet.next()) {
                ordinacije.add(resultSet.getString("ime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getOrdinacijaId(String imeOrdinacije) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM ordinacije WHERE ime = ?")) {
            preparedStatement.setString(1, imeOrdinacije);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void insertZaposleni(String ime, String priimek, String polozaj, int ordinacijaId) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             CallableStatement statement = connection.prepareCall("{ call insert_zaposleni(?, ?, ?, ?) }")) {
            statement.setString(1, ime);
            statement.setString(2, priimek);
            statement.setString(3, polozaj);
            statement.setInt(4, ordinacijaId);
            statement.execute();
            System.out.println("Zaposleni successfully inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}