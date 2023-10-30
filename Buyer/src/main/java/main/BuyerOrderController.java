package main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
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
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ConnectionFactoryUtil;

import java.beans.XMLEncoder;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyerOrderController implements Initializable {

    private static final String QUEUE_NAME = "orders";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private ObservableList<Product> productsObservableList = FXCollections.observableArrayList();
    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TextField emailTextField;
    public static final String BASE_URL = "http://localhost:8080/FactoryBackend/api/";

    public void promotionsClick(ActionEvent event)throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("promotions.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("New Promotion!");
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getProducts();
        setTable();
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
                e.printStackTrace();
            }

            br.close();
            conn.disconnect();

        }catch (Exception e)
        {
            Logger.getLogger(BuyerOrderController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(BuyerOrderController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
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

    public void orderClick(ActionEvent event) throws IOException, TimeoutException {
        List<Product> orderedProducts = new ArrayList<>();
        String data="";
        orderedProducts=productTableView.getSelectionModel().getSelectedItems();

        if(emailTextField.getText().isEmpty() || !isValidEmail(emailTextField.getText()))
        {
            Alert a =new Alert(Alert.AlertType.WARNING,"Email can't be empty!");
            a.show();
            return;
        }

        for(Product p : orderedProducts)
        {
            if(p.getVolume()==0)
            {
                Alert a =new Alert(Alert.AlertType.WARNING,"One or more selected items are not in stock at the moment");
                a.show();
                return;
            }
        }


        for(Product p : orderedProducts)
        {
            data+=p.toString();
        }
        data+=" Email address:" + emailTextField.getText();

        serializeWithXML(data);

        Connection connection = ConnectionFactoryUtil.createConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);



        String filePath = "xml.out"; // Replace with the path to your XML file
        String xmlContent="";
        try {
            // Read the XML file into a string
            xmlContent = new String(Files.readAllBytes(Paths.get(filePath)));

            // Print the XML content as a string
            System.out.println("XML content as a string:\n" + xmlContent);
        } catch (IOException e) {
            System.err.println("Error reading the XML file: " + e.getMessage());
        }





        channel.basicPublish("", QUEUE_NAME, null, xmlContent.getBytes("UTF-8"));


        channel.close();
        connection.close();



    }

    public static void serializeWithXML(String data) {

        try {
            XMLEncoder encoder = new XMLEncoder(new FileOutputStream(new File("xml.out")));
            encoder.writeObject(data);
            encoder.close();
        } catch (Exception e) {
            Logger.getLogger(BuyerOrderController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(BuyerOrderController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    public void logOutClick(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signin.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("Sign in!");
        stage.show();
    }


    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
