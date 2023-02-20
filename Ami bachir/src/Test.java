import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;

public class Test extends Application {
    public static void main(String[] args) {
        Test app = new Test();
        app.selectWithNotes("couleur");
        //app.selectWithNotes(12);
        ObservableList<Suplier> lst= app.getSupliers();
        launch(args);
    }


    /**
     * Connect to the test.db database
     * @return the Connection object
     */
    public Connection connect() {
        //-- SQLite connection string D://Ammi_Bachir/Data
        String url = "jdbc:sqlite:D://Ammi_Bachir/Data/Database.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url, "Admin", "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * select all rows in the warehouses table
     */
    public void selectAll(){
        String sql = "SELECT * from params";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("name") +  "\t" +
                        rs.getString("value"));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Get the warehouse whose capacity greater than a specified notes
     * @param type minimal notes to be selected
     */
    public void selectWithNotes(String type){
        String sql = "SELECT value FROM params WHERE name = ?";

        try (Connection conn = this.connect();
             PreparedStatement pStmt  = conn.prepareStatement(sql)){

            // set the value
            pStmt.setString(1, type);
            ResultSet rs  = pStmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1) );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public ObservableList<Suplier> getSupliers(){
        String sql = "SELECT * from supliers";
        ObservableList<Suplier> list = FXCollections.observableArrayList();
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                list.add(new Suplier(rs.getInt(1),
                                     rs.getString(2),
                                     rs.getString(3),
                                     rs.getString(4)
                        ));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }



    //*****************************************************************
    TableView<Suplier> table;
    TableColumn<Suplier, Integer> idColumn;
    TableColumn<Suplier, String> nameColumn;
    TableColumn<Suplier, String> addressColumn;
    TableColumn<Suplier, String> phonesColumn;


    @Override
    public void start(Stage primaryStage) {
        idColumn = new TableColumn<>("id");
        nameColumn = new TableColumn<>("name");
        addressColumn = new TableColumn<>("address");
        phonesColumn = new TableColumn<>("phones");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phonesColumn.setCellValueFactory(new PropertyValueFactory<>("phones"));

        table = new TableView<>();
        table.setItems(getSupliers());
        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(addressColumn);
        table.getColumns().add(phonesColumn);

        ColorPicker pk = new ColorPicker(Color.RED);
        pk.setOnAction(event -> System.out.println(pk.getPromptText()));
        //***************
        GridPane grid = new GridPane();
        Label titre = new Label("Product");
        titre.setAlignment(Pos.CENTER);

        Label codeLabel = new Label("CODE:");

        TextField codeInput = new TextField();
        codeInput.setPromptText("code");


        //NOM
        Label nameLabel = new Label("NOM:");

        TextField nameInput = new TextField();
        nameInput.setPromptText("NOM");


        //Description
        Label desLabel = new Label("DESCRIPTION:");

        TextField desInput = new TextField();
        nameInput.setPromptText("ABOUT THE PRODUCT");
        //COLOR CHOISEBOX TO CHANGE
        ChoiceBox<String> colorFiltre= new ChoiceBox<>();
        colorFiltre.setItems(FXCollections.observableArrayList("Rouge", "Bleu", "Vert"));

        //UTILITY CHOICE BOX TO CHANGE
        ChoiceBox<String> utilityFiltre = new ChoiceBox<>();
        utilityFiltre.setItems(FXCollections.observableArrayList("Metallique", "Bois", "Murale"));

        //QUANTITY
        Label quantityLabel = new Label("QUANTITY:");
        TextField quantityInput = new TextField();
        quantityInput.setPromptText("+1000");


        //WHEIGHT
        Label weightLabel = new Label("WEIGHT:");

        TextField weightInput = new TextField();
        weightInput.setPromptText("+1000");

        //SEUIL
        Label seuilLabel = new Label("SEUIL:");

        TextField seuilInput = new TextField();
        seuilInput.setPromptText("+1000");

        Button btnSave= new Button("SAVE");

        Button btnCancel= new Button("CANCEL");
        //btnCancel.setOnAction(event -> stage.close());

        HBox btnBox = new HBox(btnCancel,btnSave);
        btnBox.setSpacing(20);
        btnBox.setAlignment(Pos.CENTER);


        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    //*****************************************************************






    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        DBTest app = new DBTest();
        app.selectAll();
        //app.selectWithNotes(12);
    }*/
}
