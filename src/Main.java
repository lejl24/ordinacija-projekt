import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {
    static final String PGURL = "jdbc:postgresql://ep-empty-bonus-a26htj4v.eu-central-1.aws.neon.tech/databaza?sslmode=require";
    static final String PGUSER = "databaza_owner";
    static final String PGPASSWORD = "p9aAuiRvMYE5";

    private TableView<Ordinacija> table;
    private ObservableList<Ordinacija> data;

    @Override
    public void start(Stage primaryStage) {
        table = new TableView<>();
        data = FXCollections.observableArrayList();

        TableColumn<Ordinacija, String> columnIme = new TableColumn<>("Ime");
        columnIme.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));
        columnIme.setPrefWidth(150);

        TableColumn<Ordinacija, String> columnLastnik = new TableColumn<>("Lastnik");
        columnLastnik.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastnikIme()));

        TableColumn<Ordinacija, String> columnNaslov = new TableColumn<>("Naslov");
        columnNaslov.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaslov()));
        columnNaslov.setPrefWidth(200);

        TableColumn<Ordinacija, String> columnTelefon = new TableColumn<>("Telefon");
        columnTelefon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefon()));

        TableColumn<Ordinacija, String> columnEmail = new TableColumn<>("Email");
        columnEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

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

        Button dodajOrdinacijoBtn = new Button("Dodaj ordinacijo");
        DodajanjeOrdinacije dodajanjeOrdinacije = new DodajanjeOrdinacije();
        dodajOrdinacijoBtn.setOnAction(event -> {
            dodajanjeOrdinacije.prikaziOknoDodajanjaOrdinacije();
            refreshTable();
        });

        HBox buttonBox = new HBox(10.0, dodajOrdinacijoBtn);
        buttonBox.setPadding(new Insets(10.0));
        HBox.setHgrow(dodajOrdinacijoBtn, Priority.ALWAYS);

        VBox layout = new VBox(10.0, table, buttonBox);
        layout.setPadding(new Insets(10.0));
        Scene scene = new Scene(layout, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Ordinacije");
        primaryStage.show();

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(PGURL, PGUSER, PGPASSWORD)) {
                data.clear();
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
}
