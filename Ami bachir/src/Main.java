import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class Main extends Application {}
    public static void main(String[] args) {launch(args);
    }

    BorderPane rootPane;
    BorderPane mainPane;

    //Buttons ****************
    Button btnAdd = new Button("Ajouter");

    Button btnEdit = new Button("Modifer");

    Button btnDelete = new Button("Supprimer");

    Button btnBuy = new Button("Acheter");

    Button btnSell = new Button("Vendre");



    Button btnProduct = new Button("Produits");
    Button btnSuplier = new Button("Fournisseurs");
    Button button3 = new Button("Transactions");
    Button button4 = new Button("Parametres");


    //Label titre = new Label("Produits");

    Label userId;
    Label level ;
    TableView<Suplier> tableSuplier;
    TableView<Product> tableProduct;
    //*******************************************************

    TextField codeInput, nameInput, descInput, weightInput, quantityInput, seuilInput;
    ChoiceBox<String> colorInput, utilityInput;


    ObservableList<String> colorList , utilityList;

    ObservableList<User> userList;

    String whereProduct = null;
    String whereSupplier = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestion de Stock");

        rootPane = new BorderPane();
        mainPane = new BorderPane();

        //**************** get database values *****************
        colorList = getParamsList("couleur");
        utilityList = getParamsList("utilite");
        userList = getUsers();


        //*************** Product table ***************
        tableProduct = getTableProduct();
        //tableProduct.getSelectionModel().getSelectedIndex();
        tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
                btnSell.setDisable(false);
                btnBuy.setDisable(false);
            }
        });

        //*********  Top box Products ****************
        //VBox topBox = buildProductTopBox();
        //__________________________________

        //************** Status bar *******************
        userId = new Label();
        //userId.setText("Bachir");
        userId.setFont(Font.font("Default",FontWeight.BOLD,18));

        level = new Label();
        level.setFont(Font.font("Default",FontWeight.BOLD,14));
        level.setAlignment(Pos.BOTTOM_LEFT);

        Separator s1 = new Separator(Orientation.VERTICAL);
        s1.setPrefHeight(20);

        HBox statusBox = new HBox();
        statusBox.setSpacing(5);
        statusBox.setPadding(new Insets(5, 5, 0, 5));
        statusBox.getChildren().addAll(userId,s1,level);
        statusBox.setAlignment(Pos.BASELINE_LEFT);
        //__________________________________________

        //*********** Bottom box ******************
        //______________________________________
        btnAdd.setMinSize(80,35);
        btnAdd.setOnAction(event -> {
            if(mainPane.getCenter() == tableProduct) manageProduct(true);
            else manageSupplier(true);
        });
        btnBuy.setMinSize(80,35);
        btnBuy.setDisable(true);
        btnBuy.setOnAction(event -> actionsProduct(true));

        btnSell.setMinSize(80,35);
        btnSell.setDisable(true);
        btnSell.setOnAction(event -> actionsProduct(false));

        btnDelete.setMinSize(80,35);
        btnDelete.setDisable(true);
        btnDelete.setOnAction(event -> {
            if(mainPane.getCenter() == tableProduct)
                deleteProduct();
            else
                deleteSupplier();

            btnDelete.setDisable(true);
        });

        btnEdit.setMinSize(80,35);
        btnEdit.setDisable(true);
        btnEdit.setOnAction(event -> {
            if(mainPane.getCenter() == tableProduct){
                manageProduct(false);
                tableProduct.getSelectionModel().clearSelection();
            }
            else {
                manageSupplier(false);
                tableSuplier.getSelectionModel().clearSelection();
            }
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
        });





        HBox bottomBox = new HBox(btnAdd,btnEdit,btnDelete,btnBuy,btnSell);
        bottomBox.setSpacing(20);
        bottomBox.setPadding(new Insets(5, 20, 5, 5));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setStyle("-fx-background-color : #5C3863");
        //____________________________________



        VBox listOfButtons = new VBox(btnProduct,btnSuplier,button3,button4);
        listOfButtons.setSpacing(5);

        btnProduct.setMinSize(140,40);
        btnProduct.setDisable(true);
        btnProduct.setOnAction(event -> {
            mainPane.setCenter(tableProduct);
            mainPane.setTop(buildProductTopBox());
            btnProduct.setDisable(true);
            btnSuplier.setDisable(false);
            //***************
            btnBuy.setVisible(true);
            btnSell.setVisible(true);
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
            btnBuy.setDisable(true);
            btnSell.setDisable(true);
            tableSuplier.getSelectionModel().clearSelection();
        });

        btnSuplier.setMinSize(140,40);
        btnSuplier.setOnAction(event -> {
            if(tableSuplier == null)  {
                tableSuplier = getTableSuplier();
                tableSuplier.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        btnEdit.setDisable(false);
                        btnDelete.setDisable(false);
                    }
                });
            }

            mainPane.setCenter(tableSuplier);
            mainPane.setTop(buildSupplierTopBox());
            btnProduct.setDisable(false);
            btnSuplier.setDisable(true);
            //***************
            btnBuy.setVisible(false);
            btnSell.setVisible(false);
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
            tableProduct.getSelectionModel().clearSelection();
        });

        ContextMenu test = new ContextMenu();
        MenuItem item = new MenuItem("Clear");
        item.setOnAction(event -> tableProduct.getSelectionModel().clearSelection());
        test.getItems().add(item);
            tableProduct.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
                if(event.getButton() == MouseButton.SECONDARY)
                    test.show(tableProduct, event.getScreenX(),event.getScreenY());
            });

        button3.setMinSize(140,40);
        button4.setMinSize(140,40);

        listOfButtons.setPadding(new Insets(2,4,25,4));

        //********************** Menu Bar ***********************************
        Menu filemenu = new Menu("File");
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> Platform.exit());

        filemenu.getItems().add(new MenuItem("Project"));
        filemenu.getItems().add(new MenuItem("New.."));
        filemenu.getItems().add(exit);

        MenuBar menubar = new MenuBar();
        menubar.getMenus().add(filemenu);
        //*******************************************************************

        //**************** Fermer le programe*********/
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            fermer();
        });

        //****************************************
        rootPane.setTop(menubar);
        rootPane.setLeft(listOfButtons);
        rootPane.setBottom(statusBox);
        listOfButtons.setStyle("-fx-background-color : #000040");
        //#000020
        rootPane.setCenter(mainPane);
        //mainPane.setTop(topBox);
        mainPane.setTop(buildProductTopBox());
        mainPane.setCenter(tableProduct);
        mainPane.setBottom(bottomBox);
        Scene scene = new Scene(rootPane,900,600);
        scene.getStylesheets().add("css.css");
        primaryStage.setScene(scene);
        logIn(primaryStage);
        //primaryStage.show();

    }

    private void fermer(){
        Alert confirmer = new Alert(Alert.AlertType.CONFIRMATION);
        confirmer.setTitle("Confirmation");
        confirmer.setHeaderText(null);
        confirmer.setContentText("voulez vous Vraiment quitter ?");
        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");
        confirmer.getButtonTypes().removeAll(ButtonType.CANCEL,ButtonType.OK);
        confirmer.getButtonTypes().addAll(btnOui,btnNon);
        Optional<ButtonType> res = confirmer.showAndWait();
        if(res.get() == btnOui)
             System.exit(0);
    }
    //__________________________________________________________________________________________________________________

    public ObservableList<Product> getProducts(){
        String sql = "SELECT * from products";
        if(whereProduct != null && whereProduct.length() > 0) sql = sql+" WHERE " + whereProduct;
        ObservableList<Product> list = FXCollections.observableArrayList();
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                list.add(new Product(rs.getString(1),
                                     rs.getString(2),
                                     rs.getString(3),
                                     rs.getString(4),
                                     rs.getString(5),
                                     rs.getInt(6),
                                     rs.getFloat(7),
                                     rs.getInt(8)
                        ));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }


    public ObservableList<Suplier> getSuppliers(){
        String sql = "SELECT * from supliers";
        if(whereSupplier != null && whereSupplier.length() > 0) sql = sql + " WHERE " + whereSupplier;
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
            System.out.println("SQL : " + sql);
        }
        return list;
    }

    public Connection connect() {
        //-- SQLite connection string D://Ammi_Bachir/Data
        String url = "jdbc:sqlite:C://Users//zeydo//Downloads//Database.db";
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url, "Admin", "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public TableView<Product> getTableProduct(){
        TableView<Product> tableProduct = new TableView<>();
        TableColumn<Product, String> codeColumn = new TableColumn<>("code");
        TableColumn<Product, String> nameColumn = new TableColumn<>("nom");
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("description");
        descriptionColumn.setPrefWidth(250);
        TableColumn<Product, String> colorColumn = new TableColumn<>("couleur");
        TableColumn<Product, String> utilityColumn = new TableColumn<>("utilité");
        TableColumn<Product,Integer> quantityColumn = new TableColumn<>("quantité");
        TableColumn<Product, Float> weightColumn = new TableColumn<>("poids");
        TableColumn<Product, Integer> seuilColumn = new TableColumn<>("seuil");

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        utilityColumn.setCellValueFactory(new PropertyValueFactory<>("utility"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        seuilColumn.setCellValueFactory(new PropertyValueFactory<>("seuil"));

        tableProduct.setItems(getProducts());
        tableProduct.getColumns().add(codeColumn);
        tableProduct.getColumns().add(nameColumn);
        tableProduct.getColumns().add(descriptionColumn);
        tableProduct.getColumns().add(colorColumn);
        tableProduct.getColumns().add(utilityColumn);
        tableProduct.getColumns().add(quantityColumn);
        tableProduct.getColumns().add(weightColumn);
        tableProduct.getColumns().add(seuilColumn);
        return tableProduct;
    }

    public TableView<Suplier> getTableSuplier(){
        TableView<Suplier> tableSuplier = new TableView<>();

        TableColumn<Suplier, Integer> idColumn = new TableColumn<>("id");
        idColumn.setPrefWidth(40);

        TableColumn<Suplier, String> nameColumn = new TableColumn<>("nom");
        nameColumn.setPrefWidth(200);

        TableColumn<Suplier, String> addressColumn = new TableColumn<>("adresse");
        addressColumn.setPrefWidth(240);

        TableColumn<Suplier, String> phonesColumn = new TableColumn<>("contact");
        phonesColumn.setPrefWidth(140);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phonesColumn.setCellValueFactory(new PropertyValueFactory<>("phones"));

        tableSuplier.setItems(getSuppliers());
        tableSuplier.getColumns().add(idColumn);
        tableSuplier.getColumns().add(nameColumn);
        tableSuplier.getColumns().add(addressColumn);
        tableSuplier.getColumns().add(phonesColumn);

        return tableSuplier;
    }

    public void manageSupplier(Boolean adding){
        Suplier temp = tableSuplier.getSelectionModel().getSelectedItem();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle((adding ? "Ajouter ": "Modifier ")+"un Fournisseur");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20,20,20,20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        //NOM
        Label namLabel=new Label("NOM:");
        grid.add(namLabel, 0, 0);
        TextField nameInput =new TextField();
        nameInput.setPromptText("Nom");
        grid.add(nameInput, 1, 0);
        //ALERT
        Label alert = new Label();
        alert.setTextFill(Color.RED);
        grid.add(alert,1,1);
        //ADRESSE
        Label adresseLabel=new Label("Adresse:");
        grid.add(adresseLabel, 0, 2);
        TextField adresseInput =new TextField();
        adresseInput.setPromptText("Rue 213");
        grid.add(adresseInput, 1, 2);
        //CONTACT
        Label contactLabel=new Label("Contact:");
        grid.add(contactLabel, 0, 3);
        TextField contactInput =new TextField();
        contactInput.setPromptText("0123456789");
        grid.add(contactInput, 1, 3);
        if(!adding){
            if(temp != null){
                nameInput.setText(temp.getName());
                adresseInput.setText(temp.getAddress());
                contactInput.setText(temp.getPhones());
            }
        }

        //SAVE
        Button btnSave= new Button("Valider");
        btnSave.setOnAction(event -> {
            if(nameInput.getLength() == 0){
                alert.setText("Nom du fournisseur est vide !!");
                return;
            }
            if(adding) insertSupplier(nameInput.getText(),adresseInput.getText(),contactInput.getText());
            else if(temp != null) updateSupplier(temp.getId(),nameInput.getText(),adresseInput.getText(),contactInput.getText());
            tableSuplier.setItems(getSuppliers());
            alert.setText(null);
            stage.close();
        });
        //CANCEL
        Button btnCancel= new Button("Annuler");
        btnCancel.setOnAction(event -> stage.close());
        //
        HBox btnBox = new HBox(btnCancel,btnSave);
        btnBox.setSpacing(20);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox,0,4,2,1);

        Scene scene=new Scene(grid,500,350);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
    private void insertSupplier( String name, String address, String phones){
        String insertSql = "INSERT INTO supliers(name, address, phones) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,name);
            pstmt.setString(2,address);
            pstmt.setString(3,phones);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSupplier( int id, String name, String address, String phones){
        String insertSql = "UPDATE supliers SET name = ?, address = ?, phones = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,name);
            pstmt.setString(2,address);
            pstmt.setString(3,phones);
            pstmt.setInt(4,id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteSupplier(){
        Suplier temp = tableSuplier.getSelectionModel().getSelectedItem();
        if(temp == null) return;
        String insertSql = "DELETE FROM supliers WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setInt(1,temp.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableSuplier.setItems(getSuppliers());
    }

    private void manageProduct(boolean adding){
        Product temp = tableProduct.getSelectionModel().getSelectedItem();

        Stage stage = new Stage();
        stage.setTitle((adding ? "Ajouter ": "Modifier ")+"un Produit");
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(20);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        //CODE
        Label codeLabel = new Label("Code:");
        grid.add(codeLabel, 0, 0);
        codeInput = new TextField();
        codeInput.setPromptText("code");
        grid.add(codeInput, 1, 0);

        //ALERT
        Label alert = new Label();
        alert.setTextFill(Color.RED);
        grid.add(alert,4,0,1,3);
        //NOM
        Label nameLabel = new Label("Nom:");
        grid.add(nameLabel, 0, 1);
        nameInput = new TextField();
        nameInput.setPromptText("Nom");
        grid.add(nameInput, 1, 1);

        //Description
        Label desLabel = new Label("Description:");
        grid.add(desLabel, 0, 2);
        descInput = new TextField();
        nameInput.setPromptText("description");
        grid.add(descInput, 1, 2);
        //COLOR CHOISEBOX TO CHANGE
        colorInput = new ChoiceBox<>();
        colorInput.setItems(colorList);

        //UTILITY CHOICE BOX TO CHANGE
        utilityInput = new ChoiceBox<>();
        utilityInput.setItems(utilityList);

        HBox filterBox = new HBox(colorInput , utilityInput);
        filterBox.setSpacing(20);
        filterBox.setAlignment(Pos.CENTER);
        grid.add(filterBox,0,3,4,1);

        //QUANTITY
        Label quantityLabel = new Label("Quantité:");
        grid.add(quantityLabel, 0, 4);
        quantityInput = new TextField();
        //myNumericField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        //quantityInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        quantityInput.setPromptText("+1000");
        grid.add(quantityInput, 1, 4);

        //WHEIGHT
        Label weightLabel = new Label("Poids:");
        grid.add(weightLabel, 3, 4);
        weightInput = new TextField();
        weightInput.setPromptText("Poids(Kg)");
        grid.add(weightInput, 4, 4);
        //SEUIL
        Label seuilLabel = new Label("Seuil:");
        grid.add(seuilLabel, 0, 5);
        seuilInput = new TextField();
        seuilInput.setPromptText("+10");
        grid.add(seuilInput, 1, 5);

        //*******
        if (!adding){
            if(temp != null){
                codeInput.setText(temp.getCode());
                codeInput.setDisable(true);
                nameInput.setText(temp.getName());
                descInput.setText(temp.getDescription());
                colorInput.setValue(temp.getColor());
                utilityInput.setValue(temp.getUtility());
                quantityInput.setText(temp.getQuantity()+"");
                quantityInput.setDisable(true);
                weightInput.setText(temp.getWeight()+"");
                seuilInput.setText(temp.getSeuil()+"");
            }
        }

        //*************** Buttons ******

        Button btnSave= new Button("Valider");
        btnSave.setOnAction(event -> {
            String msgErr = validateData();
            if(msgErr != null ){
                alert.setText(msgErr);
                return;
            }
            if(adding) insertProduct(codeInput.getText(), nameInput.getText(), descInput.getText(),
                                     colorInput.getValue(), utilityInput.getValue(),
                                     parseInt(quantityInput.getText()),
                                     parseFloat(weightInput.getText()),
                                     parseInt(seuilInput.getText()));
            else if (temp != null) updateProduct(temp.getCode(), nameInput.getText(), descInput.getText(),
                                                 colorInput.getValue(), utilityInput.getValue(),
                                                 parseInt(quantityInput.getText()),
                                                 parseFloat(weightInput.getText()),
                                                 parseInt(seuilInput.getText()));
            tableProduct.setItems(getProducts());
            alert.setText(null);
            stage.close();
        });
        btnSave.setPrefSize(110,30);
        Button btnCancel= new Button("Annuler");
        btnCancel.setPrefSize(110,30);
        btnCancel.setOnAction(event -> {
            btnBuy.setDisable(true);
            btnSell.setDisable(true);
            stage.close();
        });

        HBox btnBox = new HBox(btnCancel,btnSave);
        btnBox.setSpacing(20);
        HBox.setMargin(btnSave,new Insets(0,20,0,0));
        btnBox.setAlignment(Pos.BOTTOM_RIGHT);


        VBox vBox = new VBox(grid,btnBox);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 750,400);
        stage.setScene(scene);
        stage.show();


    }
    private void insertProduct( String code, String name, String description, String color, String utility, int quantity, float weight, int seuil){
        String insertSql = "INSERT INTO products(code, name, description, color, utility, quantity, weight, seuil) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,code);
            pstmt.setString(2,name);
            pstmt.setString(3,description);
            pstmt.setString(4,color);
            pstmt.setString(5,utility);
            pstmt.setInt(6,quantity);
            pstmt.setFloat(7,weight);
            pstmt.setInt(8,seuil);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateProduct( String code, String name, String description, String color, String utility, int quantity, float weight, int seuil){
        String insertSql = "UPDATE products SET name = ?, description = ?, color = ?, utility = ?,quantity = ?, weight = ?, seuil = ? WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,name);
            pstmt.setString(2,description);
            pstmt.setString(3,color);
            pstmt.setString(4,utility);
            pstmt.setInt(5,quantity);
            pstmt.setFloat(6,weight);
            pstmt.setInt(7,seuil);
            pstmt.setString(8,code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void deleteProduct(){

        Product temp = tableProduct.getSelectionModel().getSelectedItem();
        if(temp == null) return;
        String insertSql = "DELETE FROM products WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,temp.getCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableProduct.setItems(getProducts());
    }

    private void updateQuantity(String code, int quantity) {
        String updSql = "UPDATE products SET quantity = quantity + ? WHERE code = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updSql)){
            pstmt.setInt(1, quantity);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionsProduct(boolean achat){
        Product temp = tableProduct.getSelectionModel().getSelectedItem();
        if (temp == null) return;
        //Alert confirmer = new Alert(Alert.AlertType.CONFIRMATION);
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Operation "+ (achat ? "Achat" : "Vente"));
        dialog.setHeaderText(null);
        dialog.setContentText("Veullez introduire la quantite : ");
        //confirmer.getButtonTypes().removeAll(ButtonType.CANCEL,ButtonType.OK);
        //dialog.getButtonTypes().addAll(btnOui,btnNon);
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) {
            int qty = parseInt(result.get());
            if (qty <= 0){
                showMessage("Quantite non valide");
                return;
            }
            if (!achat && qty > temp.getQuantity()) {
                showMessage("Quantite en stock insuffisante");
                return;
            }
            if (achat) {
                updateQuantity(temp.getCode(), qty);
            } else {
                updateQuantity(temp.getCode(), -qty);
            }
            tableProduct.setItems(getProducts());
            btnSell.setDisable(true);
            btnBuy.setDisable(true);
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
        }
    }
    private ObservableList<String> getParamsList(String type){
        String sql = "SELECT value FROM params WHERE name = ? ";
        ObservableList<String> list = FXCollections.observableArrayList();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, type);
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) list.add(rs.getString(1));

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }


    private void insertUser( String user_id, String pwd, int level){
        String insertSql = "INSERT INTO Users(user_id, pwd, level) VALUES(?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,user_id);
            pstmt.setString(2,pwd);
            pstmt.setInt(3,level);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUser( String user_id, String pwd, int level){
        String insertSql = "UPDATE users SET user_id = ?, pwd = ?, level = ? WHERE user_id = ?" ;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,user_id);
            pstmt.setString(2,pwd);
            pstmt.setInt(3,level);
            pstmt.setString(4, user_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*private void insertParams( String name, String value){
        String insertSql = "INSERT INTO params(name, value) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,name);
            pstmt.setString(2,value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateParams( String name, String value){
        String insertSql = "UPDATE params SET name = ?, value = ?" ;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setString(1,name);
            pstmt.setString(2,value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/




    private VBox buildProductTopBox(){
        Label titre = new Label("Produits");
        titre.setFont(Font.font("Arial", FontWeight.BOLD,24));
        TextField codeFiltre = new TextField();
        codeFiltre.setTooltip(new Tooltip("Filtrage par code"));
        codeFiltre.setPromptText("Code");
        TextField nomFiltre = new TextField();
        nomFiltre.setPromptText("Nom");
        ChoiceBox<String> colorFiltre = new ChoiceBox<>();
        colorFiltre.setItems(colorList);
        ChoiceBox<String> utilityFiltre = new ChoiceBox<>();
        utilityFiltre.setItems(utilityList);
        //**************

        ContextMenu test = new ContextMenu();
        MenuItem item = new MenuItem("Clear");
        item.setOnAction(event -> colorFiltre.getSelectionModel().clearSelection());
        test.getItems().add(item);
        colorFiltre.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if(event.getButton() == MouseButton.SECONDARY)
                test.show(colorFiltre, event.getScreenX(),event.getScreenY());
        });

        ContextMenu test1 = new ContextMenu();
        MenuItem item1 = new MenuItem("Clear");
        item1.setOnAction(event -> utilityFiltre.getSelectionModel().clearSelection());
        test1.getItems().add(item1);
        utilityFiltre.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            if(event.getButton() == MouseButton.SECONDARY)
                test1.show(utilityFiltre, event.getScreenX(),event.getScreenY());
        });

        //**************
        Button btnFiltre = new Button("Filtrer");
        btnFiltre.setOnAction(event -> btnFiltreProductHandler(codeFiltre.getText(), nomFiltre.getText(),
                                                                colorFiltre.getValue(), utilityFiltre.getValue()));
        HBox.setMargin(btnFiltre,new Insets(0,0,0,40));
        HBox filtreBox = new HBox(codeFiltre,nomFiltre,colorFiltre,utilityFiltre,btnFiltre);
        filtreBox.setSpacing(10);


        VBox topBox = new VBox(titre,filtreBox);
        topBox.setPadding(new Insets(5,5,5,5));
        topBox.setAlignment(Pos.TOP_CENTER);

        return topBox;
    }

    private void btnFiltreProductHandler(String code, String name, String color, String util) {
        String where = "";
        if(code.length() > 0 ) where += " AND code LIKE '" + code.replace("'","''") +"%'";
        if(name.length() > 0 ) where += " AND name LIKE '" + name.replace("'","''") +"%'";
        if(color != null) where += " AND color = '" + color +"'";
        if(util != null) where += " AND utility = '" + util +"'";
        if(where.length() > 0 ) where = where.substring(5);
        whereProduct = where;
        tableProduct.setItems(getProducts());

    }
    private void btnFilterSupplierHandler(String name, String adress, String contact){
        String where = "";
        if(name.length() > 0) where += " AND name LIKE '" + name.replace("'","''") +"%'";
        if(adress.length() > 0 ) where += " AND address LIKE '" + adress.replace("'","''") +"%'";
        if(contact.length() > 0 ) where += " AND phones LIKE '" + contact.replace("'","''") +"%'";
        if(where.length() > 0 ) where = where.substring(5);
        whereSupplier = where;
        tableSuplier.setItems(getSuppliers());
    }

    private VBox buildSupplierTopBox(){
        Label titre = new Label("Fournisseurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD,24));
        TextField nameFilter = new TextField();
        nameFilter.setTooltip(new Tooltip("Filtrage par nom/raison sociale"));
        nameFilter.setPromptText("Nom/Raison sociale");

        TextField addrFiltre = new TextField();
        addrFiltre.setTooltip(new Tooltip("Filtrage par adresse"));
        addrFiltre.setPromptText("Adresse");

        TextField phoneFiltre = new TextField();
        phoneFiltre.setTooltip(new Tooltip("Filtrage par contact"));
        phoneFiltre.setPromptText("Contact");

        Button btnFiltre = new Button("Filtrer");
        btnFiltre.setOnAction(event -> btnFilterSupplierHandler(nameFilter.getText(), addrFiltre.getText(),
                                                                phoneFiltre.getText()));
        HBox.setMargin(btnFiltre,new Insets(0,0,0,40));
        HBox filtreBox = new HBox(nameFilter, addrFiltre, phoneFiltre, btnFiltre);
        filtreBox.setSpacing(10);

        VBox topBox = new VBox(titre,filtreBox);
        topBox.setPadding(new Insets(5,5,5,5));
        topBox.setAlignment(Pos.TOP_CENTER);
        return topBox;
    }

    private String validateData(){
        String msg = "";
        if(codeInput.getLength() == 0 ) msg = "Le code produit est vide\n";
        if(nameInput.getLength() == 0) msg += "Le nom produit est vide\n";
        if(colorInput.getValue() == null) msg +="La couleur n'est pas selectionnee\n";
        if(utilityInput.getValue() == null) msg +="L'utilite n'est pas selectionnee\n";
        if(weightInput.getLength() == 0) msg += "le poids produit est vide\n";
        else if(parseFloat(weightInput.getText()) <= 0) msg += "Le poids n'est pas valide\n";
        if(quantityInput.getLength() == 0) msg += "la quantite produit est vide\n";
        else if(parseInt(quantityInput.getText()) < 0) msg += "La quantite n'est pas valide\n";
        if(seuilInput.getLength() == 0) msg += "le seuil produit est vide\n";
        else if(parseInt(seuilInput.getText()) < 0) msg += "Le seuil n'est pas valide\n";
        return (msg.length() == 0 ? null : msg);
    }
    //____________________________________________

    private int parseInt(String input){
        try {
            return Integer.parseInt(input);
        }catch (Exception e){
            //e.printStackTrace();
        }
        return -1;
    }

    private float parseFloat(String input){
        try {
            return Float.parseFloat(input);
        }catch (Exception e){
            //e.printStackTrace();
        }
        return -1;
    }

    private void logIn(Stage primaryStage){
        Stage stage = new Stage();
        stage.setTitle("Authentification");
        GridPane layout = new GridPane();
        Label nameLabel = new Label("Utilisateur :");
        Label pwdLabel = new Label("Mot de passe :");
        TextField nameField = new TextField();
        PasswordField pwdField = new PasswordField();
        Button logIn = new Button("Valider");
        Text title = new Text("Authentification");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
        Text result = new Text();
        result.setFill(Color.RED);
        //to show the password
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setVisible(false);
        layout.add(textField,1,2);
        CheckBox showps = new CheckBox();
        layout.add(showps,1,3);
        showps.setOnAction(event -> {
            boolean checked = showps.isSelected();
            if (checked) {
                textField.setText(pwdField.getText());
            } else {
                pwdField.setText(textField.getText());
            }
            textField.setVisible(checked);
            pwdField.setVisible(!checked);
        });

        logIn.setOnAction(event -> {

            String msgErr = authentifier(nameField.getText(), pwdField.getText());
            if(msgErr != null )
                result.setText(msgErr);
            else {
                stage.close();
                primaryStage.show();
            }
        });

        layout.setAlignment(Pos.CENTER_RIGHT);
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(25, 25, 25, 25));


        layout.add(title , 0,0,2,1);
        layout.add(nameLabel, 0,1);
        layout.add(nameField,1,1);
        layout.add(pwdLabel,0,2);
        layout.add(pwdField,1,2);
        layout.add(logIn,1,3);
        GridPane.setHalignment(logIn, HPos.RIGHT);
        layout.add(result,1,4);
        GridPane.setHalignment(result,HPos.RIGHT);
        Scene scene = new Scene(layout, 600, 360);
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<User> getUsers(){
        String sql = "SELECT * FROM users";
        //if(whereSupplier != null && whereSupplier.length() > 0) sql = sql + " WHERE " + whereSupplier;
        ObservableList<User> list = FXCollections.observableArrayList();
        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                list.add(new User(rs.getString(1),
                                  rs.getString(2),
                                  rs.getInt(3)));

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    private String authentifier(String user_id, String pwd){
        if(user_id.length() == 0 || pwd.length() == 0 ) return "Champ utilisateur et/ou mot de passe vide";
        for(User user : userList){
            if(!user_id.equals(user.getUser_id())) continue;
            if(!pwd.equals(user.getPassword())) return "Mot de pass incorrect";
            level.setText("Niveau : " + user.getLevel());
            userId.setText(user.getUser_id());
            return null;
        }
        return "Utilisateur non défini";
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
