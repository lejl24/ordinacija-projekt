import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DodajanjeOrdinacije {
    private Connection conn;

    public DodajanjeOrdinacije(Connection conn) {
        this.conn = conn;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void prikaziOknoDodajanjaOrdinacije() {
        Stage stage = new Stage();
        VBox root = new VBox();

        HBox imePriimekBox = new HBox(); // Create an HBox for name and surname fields
        TextField imeField = new TextField();
        imeField.setPromptText("Ime lastnika");
        TextField priimekField = new TextField();
        priimekField.setPromptText("Priimek lastnika");
        imePriimekBox.getChildren().addAll(imeField, priimekField);

        TextField imeOrdinacijeField = new TextField();
        imeOrdinacijeField.setPromptText("Ime ordinacije");
        TextField stZaposlenihField = new TextField();
        stZaposlenihField.setPromptText("Število zaposlenih");

        TextField naslovField = new TextField();
        naslovField.setPromptText("Naslov ordinacije");
        TextField telefonField = new TextField();
        telefonField.setPromptText("Telefon");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        ComboBox<String> krajComboBox = new ComboBox<>();
        krajComboBox.setPromptText("Izberite kraj");
        // Fill the combo box with available cities
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT ime FROM kraji";
            var resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                krajComboBox.getItems().add(resultSet.getString("ime"));
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

            // Check if the connection is not null and both name and surname are provided
            if (conn != null && !imeLastnika.isEmpty() && !priimekLastnika.isEmpty() && !imeOrdinacije.isEmpty() && izbranKraj != null) {
                try {
                    // Insert name and surname into the lastniki table
                    String insertLastnikSql = "INSERT INTO lastniki (ime, priimek) VALUES (?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertLastnikSql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, imeLastnika);
                    pstmt.setString(2, priimekLastnika);
                    pstmt.executeUpdate();

                    // Get the ID of the last inserted row in lastniki table
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    int lastnikId = -1;
                    if (generatedKeys.next()) {
                        lastnikId = generatedKeys.getInt(1);
                    }

                    // Get the ID of the selected city from the database
                    int krajId = -1;
                    String getKrajIdSql = "SELECT id FROM kraji WHERE ime = ?";
                    pstmt = conn.prepareStatement(getKrajIdSql);
                    pstmt.setString(1, izbranKraj);
                    ResultSet resultSet = pstmt.executeQuery();
                    if (resultSet.next()) {
                        krajId = resultSet.getInt("id");
                    }

                    // Insert other data into the ordinacije table
                    String insertOrdinacijaSql = "INSERT INTO ordinacije (ime, naslov, telefon, email, st_zaposlenih, lastnik_id, kraj_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertOrdinacijaSql);
                    pstmt.setString(1, imeOrdinacije);
                    pstmt.setString(2, naslov);
                    pstmt.setString(3, telefon);
                    pstmt.setString(4, email);
                    pstmt.setInt(5, stZaposlenih);
                    pstmt.setInt(6, lastnikId);
                    pstmt.setInt(7, krajId);
                    pstmt.executeUpdate();

                    stage.close(); // Close the window after successful insertion
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        root.getChildren().addAll(imePriimekBox, imeOrdinacijeField, stZaposlenihField, naslovField, telefonField, emailField, krajComboBox, dodajBtn);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
