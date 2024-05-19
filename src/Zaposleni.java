import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Zaposleni extends Application {
    static final String PGURL = "jdbc:postgresql://ep-empty-bonus-a26htj4v.eu-central-1.aws.neon.tech/databaza?sslmode=require";
    static final String PGUSER = "databaza_owner";
    static final String PGPASSWORD = "p9aAuiRvMYE5";

    private TableView<Zaposlen> table;
    private ObservableList<Zaposlen> data;

    @Override
    public void start(Stage primaryStage) {
        table = new TableView<>();
        data = FXCollections.observableArrayList();

        TableColumn<Zaposlen, String> columnIme = new TableColumn<>("Ime");
        columnIme.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));

        TableColumn<Zaposlen, String> columnPriimek = new TableColumn<>("Priimek");
        columnPriimek.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriimek()));

        TableColumn<Zaposlen, String> columnPolozaj = new TableColumn<>("Polozaj");
        columnPolozaj.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPolozaj()));

        TableColumn<Zaposlen, String> columnOrdinacija = new TableColumn<>("Ordinacija");
        columnOrdinacija.setCellValueFactory(cellData -> {
            int ordinacijaId = cellData.getValue().getOrdinacijaId();
            String ordinacijaIme = fetchOrdinacijaName(ordinacijaId);
            return new SimpleStringProperty(ordinacijaIme);
        });

        // Razširitev tretjega in četrtega stolpca
        columnPolozaj.prefWidthProperty().bind(table.widthProperty().multiply(0.20));
        columnOrdinacija.prefWidthProperty().bind(table.widthProperty().multiply(0.25));

        table.getColumns().addAll(columnIme, columnPriimek, columnPolozaj, columnOrdinacija);

        VBox root = new VBox(table);

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Zaposleni");
        primaryStage.show();

        loadData();
    }

    private String fetchOrdinacijaName(int ordinacijaId) {
        try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
            String sql = "SELECT ime FROM ordinacije WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, ordinacijaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ime");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void loadData() {
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                data.clear();
                String sql = "SELECT ime, priimek, polozaj, ordinacija_id FROM zaposleni";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String ime = rs.getString("ime");
                        String priimek = rs.getString("priimek");
                        String polozaj = rs.getString("polozaj");
                        int ordinacijaId = rs.getInt("ordinacija_id");

                        data.add(new Zaposlen(ime, priimek, polozaj, ordinacijaId));
                    }
                    Platform.runLater(() -> table.setItems(data));
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Define the Zaposlen class here
    public static class Zaposlen {
        private final String ime;
        private final String priimek;
        private final String polozaj;
        private final int ordinacijaId;

        public Zaposlen(String ime, String priimek, String polozaj, int ordinacijaId) {
            this.ime = ime;
            this.priimek = priimek;
            this.polozaj = polozaj;
            this.ordinacijaId = ordinacijaId;
        }

        public String getIme() {
            return ime;
        }

        public String getPriimek() {
            return priimek;
        }

        public String getPolozaj() {
            return polozaj;
        }

        public int getOrdinacijaId() {
            return ordinacijaId;
        }
    }
}
