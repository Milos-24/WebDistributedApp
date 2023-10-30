package main;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Product;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.UsersController.handler;

public class ProductsController  implements Initializable {
    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }

    private ObservableList<Product> productsObservableList = FXCollections.observableArrayList();
    @FXML
    private TableView<Product> productTableView;
    public static final String BASE_URL = "http://localhost:8080/FactoryBackend/api/";

    @FXML
    TextField nameTextField;

    @FXML
    TextField idTextField;

    @FXML
    TextField quantityTextField;

    @FXML
    TextField descriptionTextField;

    public void rawMaterialClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("rawmaterial.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Raw Material");
        stage.show();
    }

    public void promotionClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("promotion.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Promotion");
        stage.show();
    }

    public void ordersClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("orders.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Orders");
        stage.show();
    }

    public void usersClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("users.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Users");
        stage.show();
    }

    public void getProducts()
    {
        try{
            URL url = new URL(BASE_URL+"products/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            String json = "";
            while ((output = br.readLine()) != null) {
                json+=output;
            }

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray productsArray = jsonObject.getJSONArray("Products");

                for (int i = 0; i < productsArray.length(); i++) {
                    String productString = productsArray.getString(i);
                    String[] parts = productString.split("\\|");
                    if (parts.length == 4) {
                        Product product = new Product(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3]);
                        productsObservableList.add(product);
                    }
                }


            } catch (JSONException e) {
                Logger.getLogger(OrdersController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
            }

            br.close();
            conn.disconnect();

        }catch (Exception e)
        {
            Logger.getLogger(OrdersController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }
    }

    public void setTable()
    {
        productTableView.setItems(productsObservableList);

        TableColumn<Product, String> idColumnFirst = new TableColumn<>("Name");
        idColumnFirst.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Product, Integer> idColumnSecond = new TableColumn<>("Id");
        idColumnSecond.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Product, Integer> idColumnThird = new TableColumn<>("Quantity");
        idColumnThird.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVolume()).asObject());

        TableColumn<Product, String> idColumnFourth = new TableColumn<>("Description");
        idColumnFourth.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));



        productTableView.getColumns().setAll(idColumnFirst,idColumnSecond,idColumnThird,idColumnFourth);

    }

    public void createProductClick()
    {
        if(nameTextField.getText().isEmpty() || idTextField.getText().isEmpty() || quantityTextField.getText().isEmpty()
                || descriptionTextField.getText().isEmpty() || !isNumeric(idTextField.getText()) || !isNumeric(quantityTextField.getText()))
        {
            return;
        }
        else
        {
            Product product = new Product(nameTextField.getText(), Integer.parseInt(idTextField.getText()), Integer.parseInt(quantityTextField.getText()), descriptionTextField.getText());


            try {
                // priprema i otvaranje HTTP zahtjeva
                URL url = new URL(BASE_URL + "products/" );
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST"); // slicno za PUT
                conn.setRequestProperty("Content-Type", "application/json");
                // podaci za body dio zahtjeva
                JSONObject input = new JSONObject(product);
                // slanje body dijela
                OutputStream os = conn.getOutputStream();
                os.write(input.toString().getBytes());
                os.flush();
                // prijem odgovora na zahtjev
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                os.close();
                conn.disconnect();
            } catch (Exception e) {
                Logger.getLogger(ProductsController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
            }

            productsObservableList.add(product);
            setTable();
        }
    }
    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public void editProductClick()
    {
        if(nameTextField.getText().isEmpty() || idTextField.getText().isEmpty() || quantityTextField.getText().isEmpty()
                || descriptionTextField.getText().isEmpty() || !isNumeric(idTextField.getText()) || !isNumeric(quantityTextField.getText()))
        {
            return;
        }
        else
        {
            Product product=null;

            for(Product p : productsObservableList) {
                if (p.getId() == Integer.parseInt(idTextField.getText()))
                {

                    p.setId(Integer.parseInt(idTextField.getText()));
                    p.setVolume(Integer.parseInt(quantityTextField.getText()));
                    p.setName(nameTextField.getText());
                    p.setDescription(descriptionTextField.getText());
                    product=p;
                    System.out.println(product);
                }
            }



            if(product!=null) {
                try {
                    // priprema i otvaranje HTTP zahtjeva
                    URL url = new URL(BASE_URL + "products/" + product.getId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("PUT"); // slicno za PUT
                    conn.setRequestProperty("Content-Type", "application/json");
                    // podaci za body dio zahtjeva
                    JSONObject input = new JSONObject(product);
                    // slanje body dijela
                    OutputStream os = conn.getOutputStream();
                    os.write(input.toString().getBytes());
                    os.flush();
                    // prijem odgovora na zahtjev
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                    }
                    os.close();
                    conn.disconnect();
                } catch (Exception e) {
                    Logger.getLogger(ProductsController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                }
            }

            System.out.println(product);

            setTable();
        }
    }

    public void deleteProductClick()
    {

            Product product = productTableView.getSelectionModel().getSelectedItem();
            productTableView.getItems().remove(productTableView.getSelectionModel().getSelectedItem());

            if(product!=null) {
                try {
                    // priprema i otvaranje HTTP zahtjeva
                    URL url = new URL(BASE_URL + "products/" + product.getId());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Content-Type", "application/json");
                    // podaci za body dio zahtjeva
                    JSONObject input = new JSONObject(product);
                    // slanje body dijela
                    OutputStream os = conn.getOutputStream();
                    os.write(input.toString().getBytes());
                    os.flush();
                    // prijem odgovora na zahtjev
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                    }
                    os.close();
                    conn.disconnect();
                } catch (Exception e) {
                    Logger.getLogger(ProductsController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                }
            }



            setTable();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getProducts();
        setTable();
    }
}
