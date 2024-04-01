import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {
    // Podatki za povezavo na strežnik PostgreSQL
    static final String PGURL = "jdbc:postgresql://ep-empty-bonus-a26htj4v.eu-central-1.aws.neon.tech/databaza?sslmode=require";
    static final String PGUSER = "databaza_owner";
    static final String PGPASSWORD = "p9aAuiRvMYE5";

    @Override
    public void start(Stage primaryStage) {
        TableView<Ordinacija> table = new TableView<>();
        TableColumn<Ordinacija, String> columnIme = new TableColumn<>("Ime");
        columnIme.setCellValueFactory(data -> data.getValue().imeProperty());
        TableColumn<Ordinacija, String> columnNaslov = new TableColumn<>("Naslov");
        columnNaslov.setCellValueFactory(data -> data.getValue().naslovProperty());
        TableColumn<Ordinacija, String> columnTelefon = new TableColumn<>("Telefon");
        columnTelefon.setCellValueFactory(data -> data.getValue().telefonProperty());
        TableColumn<Ordinacija, String> columnEmail = new TableColumn<>("Email");
        columnEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        table.getColumns().addAll(columnIme, columnNaslov, columnTelefon, columnEmail);

        VBox root = new VBox(table);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        ObservableList<Ordinacija> data = FXCollections.observableArrayList();

        // Execute database query in a background thread
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM ordinacije")) {

                // Add retrieved data to ObservableList
                while (rs.next()) {
                    String ime = rs.getString("ime");
                    String naslov = rs.getString("naslov");
                    String telefon = rs.getString("telefon");
                    String email = rs.getString("email");
                    data.add(new Ordinacija(ime, naslov, telefon, email));
                }

                // Update TableView on the JavaFX Application Thread
                Platform.runLater(() -> table.setItems(data));

            } catch (SQLException se) {
                se.printStackTrace(); // Print stack trace to diagnose the issue
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace if an exception occurs during JavaFX application startup
        }
    }
}
