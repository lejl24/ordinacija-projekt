import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PotrdiIzbris {
    private Ordinacija ordinacija;
    private Connection connection;

    public PotrdiIzbris(Ordinacija ordinacija, Connection connection) {
        this.ordinacija = ordinacija;
        this.connection = connection;
    }

    public void prikaziOknoPotrditveIzbrisa() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Potrditev izbrisa");

        Label label = new Label("Ali ste prepričani, da želite izbrisati to ordinacijo?");

        Button izbrisiButton = new Button("Izbriši");
        izbrisiButton.setOnAction(event -> {
            izbrisiOrdinacijo();
            stage.close();
        });

        Button prekliciButton = new Button("Prekliči");
        prekliciButton.setOnAction(event -> stage.close());

        HBox buttonsBox = new HBox(10, izbrisiButton, prekliciButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, label, buttonsBox);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 150);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void izbrisiOrdinacijo() {
        try {
            String deleteSQL = "DELETE FROM ordinacije WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
                stmt.setInt(1, ordinacija.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
