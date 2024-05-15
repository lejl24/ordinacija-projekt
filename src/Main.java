import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        columnIme.setPrefWidth(150); // Set preferred width for "Ime" column

        TableColumn<Ordinacija, String> columnLastnik = new TableColumn<>("Lastnik");
        columnLastnik.setCellValueFactory(data -> data.getValue().lastnikProperty());

        TableColumn<Ordinacija, String> columnNaslov = new TableColumn<>("Naslov");
        columnNaslov.setCellValueFactory(data -> data.getValue().naslovProperty());
        columnNaslov.setPrefWidth(200); // Set preferred width for "Naslov" column

        TableColumn<Ordinacija, String> columnTelefon = new TableColumn<>("Telefon");
        columnTelefon.setCellValueFactory(data -> data.getValue().telefonProperty());

        TableColumn<Ordinacija, String> columnEmail = new TableColumn<>("Email");
        columnEmail.setCellValueFactory(data -> data.getValue().emailProperty());

        TableColumn<Ordinacija, String> columnKraj = new TableColumn<>("Kraj");
        columnKraj.setCellValueFactory(data -> data.getValue().krajProperty());

        TableColumn<Ordinacija, Void> columnUredi = new TableColumn<>("Uredi");
        columnUredi.setCellFactory(param -> {
            TableCell<Ordinacija, Void> cell = new TableCell<>() {
                private final Button button = new Button("Uredi");

                {
                    button.setOnAction(event -> {
                        Ordinacija ordinacija = getTableView().getItems().get(getIndex());
                        // Tukaj dodajte kodo za preusmeritev na okno za urejanje ordinacije
                        // Na primer: UrediOrdinacijo okno = new UrediOrdinacijo(ordinacija);
                        // okno.prikazi();
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(button);
                    }
                }
            };
            return cell;
        });

        table.getColumns().addAll(columnIme, columnLastnik, columnNaslov, columnTelefon, columnEmail, columnKraj, columnUredi);

        VBox root = new VBox(table);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Povezava s podatkovno bazo
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                ObservableList<Ordinacija> data = FXCollections.observableArrayList();
                String sql = "SELECT o.ime AS ime_ordinacije, CONCAT(l.ime, ' ', l.priimek) AS lastnik, o.naslov AS naslov_ordinacije, o.telefon, o.email, k.ime AS kraj " +
                        "FROM ordinacije o " +
                        "JOIN lastniki l ON o.lastnik_id = l.id " +
                        "JOIN kraji k ON o.kraj_id = k.id";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String ime = rs.getString("ime_ordinacije");
                        String lastnik = rs.getString("lastnik");
                        String naslov = rs.getString("naslov_ordinacije");
                        String telefon = rs.getString("telefon");
                        String email = rs.getString("email");
                        String kraj = rs.getString("kraj");
                        data.add(new Ordinacija(ime, lastnik, naslov, telefon, email, kraj));
                    }
                    Platform.runLater(() -> table.setItems(data));
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }).start();

        // Gumb za dodajanje nove ordinacije
        Button dodajOrdinacijoBtn = new Button("Dodaj ordinacijo");
        DodajanjeOrdinacije dodajanjeOrdinacije = new DodajanjeOrdinacije(null); // Initialize with a null connection
        dodajOrdinacijoBtn.setOnAction(event -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                dodajanjeOrdinacije.setConnection(conn);
                dodajanjeOrdinacije.prikaziOknoDodajanjaOrdinacije();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        root.getChildren().add(dodajOrdinacijoBtn);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
