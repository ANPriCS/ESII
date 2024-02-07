package org.example;
import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Card;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ShipmentDashboard extends Application {

    private Connection connection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        connection = establishDatabaseConnection(); //connexão à base de dados

        // Layout do dashboard
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setSpacing(10);

        // Filtros por texto e por botão
        TextField customerFilter = new TextField();
        Button filterButton = new Button("Filter");
        filterButton.setOnAction(event -> {
            updateDashboard(customerFilter.getText());
        });

        // Extração de dados inicial, não filtrada
        List<ShipmentData> shipments = fetchDataFromDatabase();
        updateDashboard(null);


        root.getChildren().addAll(customerFilter, filterButton);
        root.getChildren().addAll(getShipmentCards(shipments));


        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Shipment Dashboard");
        primaryStage.show();
    }

    private Connection establishDatabaseConnection() {
        try {
            // Por Ajustar à base de dados, baseado na lógica do esquema
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/encomendas", "admin", "teste123");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ShipmentData> fetchDataFromDatabase(String customerFilter) {
        List<ShipmentData> shipments = new ArrayList<>();
        try {
            String sql = "SELECT * FROM encomendas";
            if (customerFilter != null) {
                sql += " WHERE tribunal = ?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            if (customerFilter != null) {
                statement.setString(1, customerFilter);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ShipmentData shipment = new ShipmentData();
                shipment.setId(resultSet.getInt("IDenc"));
                shipment.setCustomerName(resultSet.getString("tribunal"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                shipment.setDate(dateFormat.format(resultSet.getDate("dataencomenda")));
                shipment.setCost(resultSet.getDouble("custo"));
                shipments.add(shipment);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }

    private List<Node> getShipmentCards(List<ShipmentData> shipments) {
        List<Node> cards = new ArrayList<>();
        for (ShipmentData shipment : shipments) {
            Card card = new Card();
            card.setText(
                    "Shipment ID: " + shipment.getId() + "\n" +
                            "Tribunal: " + shipment.getTribunal() + "\n" +
                            "Data: " + shipment.getDate() + "\n" +
                            "Custo : " + shipment.getCost()
                            "Produto : " + shipment.getProd()
            );
            cards.add(card);
        }
        return cards;
    }

    private void updateDashboard(String customer) {
    }
}