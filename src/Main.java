import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;

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

    private TableView<Ordinacija> table;
    private ObservableList<Ordinacija> data;

    @Override
    public void start(Stage primaryStage) {
        table = new TableView<>();

        TableColumn<Ordinacija, String> columnIme = new TableColumn<>("Ime");
        columnIme.setCellValueFactory(new PropertyValueFactory<>("ime"));
        columnIme.setPrefWidth(150);

        TableColumn<Ordinacija, String> columnLastnik = new TableColumn<>("Lastnik");
        columnLastnik.setCellValueFactory(new PropertyValueFactory<>("lastnikIme"));

        TableColumn<Ordinacija, String> columnNaslov = new TableColumn<>("Naslov");
        columnNaslov.setCellValueFactory(new PropertyValueFactory<>("naslov"));
        columnNaslov.setPrefWidth(200);

        TableColumn<Ordinacija, String> columnTelefon = new TableColumn<>("Telefon");
        columnTelefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));

        TableColumn<Ordinacija, String> columnEmail = new TableColumn<>("Email");
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Ordinacija, Void> columnUredi = new TableColumn<>("Uredi");
        columnUredi.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Uredi");

            {
                editButton.setOnAction(event -> {
                    Ordinacija ordinacija = getTableView().getItems().get(getIndex());
                    try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                        UrejanjeOrdinacij urejanjeOrdinacije = new UrejanjeOrdinacij(ordinacija, conn);
                        urejanjeOrdinacije.prikaziOknoUrejanjaOrdinacije();
                        refreshTable();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        TableColumn<Ordinacija, Void> columnIzbrisi = new TableColumn<>("Izbriši");
        columnIzbrisi.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Izbriši");

            {
                deleteButton.setOnAction(event -> {
                    Ordinacija ordinacija = getTableView().getItems().get(getIndex());
                    try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                        PotrdiIzbris potrdiIzbris = new PotrdiIzbris(ordinacija, conn);
                        potrdiIzbris.prikaziOknoPotrditveIzbrisa();
                        refreshTable();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(columnIme, columnLastnik, columnNaslov, columnTelefon, columnEmail, columnUredi, columnIzbrisi);

        VBox root = new VBox(table);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Povezava s podatkovno bazo
        loadData();

        // Gumb za dodajanje nove ordinacije
        Button dodajOrdinacijoBtn = new Button("Dodaj ordinacijo");
        DodajanjeOrdinacije dodajanjeOrdinacije = new DodajanjeOrdinacije(null); // Initialize with a null connection
        dodajOrdinacijoBtn.setOnAction(event -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                dodajanjeOrdinacije.setConnection(conn);
                dodajanjeOrdinacije.prikaziOknoDodajanjaOrdinacije();
                refreshTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        root.getChildren().add(dodajOrdinacijoBtn);
    }

    private void loadData() {
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                data = FXCollections.observableArrayList();
                String sql = "SELECT o.id, o.ime AS ime_ordinacije, o.naslov AS naslov_ordinacije, o.telefon, o.email, o.st_zaposlenih, o.lastnik_id, " +
                        "l.ime AS lastnik_ime, l.priimek AS lastnik_priimek " +
                        "FROM ordinacije o " +
                        "JOIN lastniki l ON o.lastnik_id = l.id";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String ime = rs.getString("ime_ordinacije");
                        String naslov = rs.getString("naslov_ordinacije");
                        String telefon = rs.getString("telefon");
                        String email = rs.getString("email");
                        int stZaposlenih = rs.getInt("st_zaposlenih");
                        int lastnikId = rs.getInt("lastnik_id");
                        String lastnikIme = rs.getString("lastnik_ime");
                        String lastnikPriimek = rs.getString("lastnik_priimek");

                        data.add(new Ordinacija(id, ime, naslov, telefon, email, stZaposlenih, lastnikId, lastnikIme, lastnikPriimek));
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

    private void refreshTable() {
        loadData();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
