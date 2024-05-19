import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class DodajanjeOrdinacije {

    private static final String PGURL = "jdbc:postgresql://ep-empty-bonus-a26htj4v.eu-central-1.aws.neon.tech/databaza?sslmode=require";
    private static final String PGUSER = "databaza_owner";
    private static final String PGPASSWORD = "p9aAuiRvMYE5";
    private static final String URL = PGURL;

    private ObservableList<String> kraji = FXCollections.observableArrayList();
    private Stage stage;

    public DodajanjeOrdinacije() {
        fetchKrajiFromDatabase();  // Fetch list of kraji when initializing
    }

    public void prikaziOknoDodajanjaOrdinacije() {
        stage = new Stage();
        stage.setTitle("Dodajanje Ordinacije");

        // Creating form elements
        Label imeLabel = new Label("Ime lastnika:");
        TextField imeField = new TextField();

        Label priimekLabel = new Label("Priimek lastnika:");
        TextField priimekField = new TextField();

        Label imeOrdinacijeLabel = new Label("Ime ordinacije:");
        TextField imeOrdinacijeField = new TextField();

        Label stZaposlenihLabel = new Label("Število zaposlenih:");
        TextField stZaposlenihField = new TextField();

        Label naslovLabel = new Label("Naslov:");
        TextField naslovField = new TextField();

        Label telefonLabel = new Label("Telefon:");
        TextField telefonField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label krajLabel = new Label("Kraj:");
        ComboBox<String> krajComboBox = new ComboBox<>();
        krajComboBox.setItems(kraji);

        Button dodajBtn = new Button("Dodaj");
        dodajBtn.setOnAction(event -> {
            String imeLastnika = imeField.getText();
            String priimekLastnika = priimekField.getText();
            String imeOrdinacije = imeOrdinacijeField.getText();
            int stZaposlenih = Integer.parseInt(stZaposlenihField.getText());
            String naslov = naslovField.getText();
            String telefon = telefonField.getText();
            String email = emailField.getText();
            String izbranKraj = krajComboBox.getValue();

            if (izbranKraj != null && !izbranKraj.isEmpty()) {
                String lastnikId = insertLastnik(imeLastnika, priimekLastnika);
                if (lastnikId != null) {
                    String krajId = getKrajId(izbranKraj);
                    if (krajId != null) {
                        insertOrdinacija(Integer.parseInt(lastnikId), imeOrdinacije, stZaposlenih, naslov, telefon, email, Integer.parseInt(krajId));

                        // Clear fields after submission
                        imeField.clear();
                        priimekField.clear();
                        imeOrdinacijeField.clear();
                        stZaposlenihField.clear();
                        naslovField.clear();
                        telefonField.clear();
                        emailField.clear();
                        krajComboBox.getSelectionModel().clearSelection();
                    }
                }
            } else {
                System.out.println("Prosimo izberite kraj.");
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

        GridPane.setConstraints(imeOrdinacijeLabel, 0, 2);
        GridPane.setConstraints(imeOrdinacijeField, 1, 2);

        GridPane.setConstraints(stZaposlenihLabel, 0, 3);
        GridPane.setConstraints(stZaposlenihField, 1, 3);

        GridPane.setConstraints(naslovLabel, 0, 4);
        GridPane.setConstraints(naslovField, 1, 4);

        GridPane.setConstraints(telefonLabel, 0, 5);
        GridPane.setConstraints(telefonField, 1, 5);

        GridPane.setConstraints(emailLabel, 0, 6);
        GridPane.setConstraints(emailField, 1, 6);

        GridPane.setConstraints(krajLabel, 0, 7);
        GridPane.setConstraints(krajComboBox, 1, 7);

        GridPane.setConstraints(dodajBtn, 1, 8);

        grid.getChildren().addAll(
                imeLabel, imeField,
                priimekLabel, priimekField,
                imeOrdinacijeLabel, imeOrdinacijeField,
                stZaposlenihLabel, stZaposlenihField,
                naslovLabel, naslovField,
                telefonLabel, telefonField,
                emailLabel, emailField,
                krajLabel, krajComboBox,
                dodajBtn
        );

        VBox layout = new VBox(10);
        layout.getChildren().addAll(grid);
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void fetchKrajiFromDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ime FROM kraji")) {

            kraji.clear();
            while (resultSet.next()) {
                kraji.add(resultSet.getString("ime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getKrajId(String krajName) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM kraji WHERE ime = ?")) {
            preparedStatement.setString(1, krajName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String insertLastnik(String imeLastnika, String priimekLastnika) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             CallableStatement statement = connection.prepareCall("{ ? = call insert_lastnik(?, ?) }")) {

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2, imeLastnika);
            statement.setString(3, priimekLastnika);
            statement.execute();

            int lastnikId = statement.getInt(1);
            if (lastnikId > 0) {
                System.out.println("Lastnik inserted successfully!");
                return String.valueOf(lastnikId);
            } else {
                System.out.println("Failed to insert lastnik.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertOrdinacija(int lastnikId, String imeOrdinacije, int stZaposlenih, String naslov, String telefon, String email, int krajId) {
        try (Connection connection = DriverManager.getConnection(URL, PGUSER, PGPASSWORD);
             CallableStatement statement = connection.prepareCall("{ ? = call insert_ordinacija(?, ?, ?, ?, ?, ?, ?) }")) {

            statement.registerOutParameter(1, Types.INTEGER);
            statement.setInt(2, lastnikId);
            statement.setString(3, imeOrdinacije);
            statement.setInt(4, stZaposlenih);
            statement.setString(5, naslov);
            statement.setInt(6, Integer.parseInt(telefon)); // Pretvori telefon v integer
            statement.setString(7, email);
            statement.setInt(8, krajId);
            statement.execute();

            int ordinacijaId = statement.getInt(1);
            if (ordinacijaId > 0) {
                System.out.println("Ordinacija inserted successfully!");
            } else {
                System.out.println("Failed to insert ordinacija.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
