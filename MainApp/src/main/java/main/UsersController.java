package main;

import javafx.application.Application;
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
import javafx.stage.Stage;
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
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersController extends Application implements Initializable {

    private final String logPath="LogFile.log";

    public static Handler handler;

    {
        try {
            handler = new FileHandler(logPath);
            Logger.getLogger(UsersController.class.getName()).addHandler(handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ObservableList<User> usersObservableList = FXCollections.observableArrayList();
    @FXML
    private TableView<User> usersTable;
    public static final String BASE_URL = "http://localhost:8080/FactoryBackend/api/";
    public void rawMaterialClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("rawmaterial.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Raw Material");
        stage.show();
    }

    public void productsClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("products.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Products");
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

    public void promotionClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("promotion.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Users");
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(UsersController.class.getResource("users.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
        getUsers();
    }

    public void setTable()
    {
        usersTable.setItems(usersObservableList);
        TableColumn<User, String> idColumnFirst = new TableColumn<>("Company Name");
        idColumnFirst.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().companyName));

        TableColumn<User, String> idColumnSecond = new TableColumn<>("Address");
        idColumnSecond.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().address));

        TableColumn<User, Integer> idColumnThird = new TableColumn<>("Phone Number");
        idColumnThird.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().phoneNumber).asObject());

        TableColumn<User, String> idColumnFourth = new TableColumn<>("Username");
        idColumnFourth.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username));

        TableColumn<User, String> idColumnFifth = new TableColumn<>("Password");
        idColumnFifth.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().password));

        TableColumn<User, Boolean> idColumnSixth = new TableColumn<>("Activated");
        idColumnSixth.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().activated));

        TableColumn<User, Boolean> idColumnSeventh = new TableColumn<>("Blocked");
        idColumnSeventh.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().blocked));


        usersTable.getColumns().setAll(idColumnFirst,idColumnSecond,idColumnThird,idColumnFourth,idColumnFifth,idColumnSixth,idColumnSeventh);
    }
    public void blockUserClick()
    {
        User user = usersTable.getSelectionModel().getSelectedItem();
        usersTable.getSelectionModel().getSelectedItem().blocked=true;
        if(user!=null) {
            try {
                Logger.getLogger(UsersController.class.getName()).addHandler(handler);
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                // priprema i otvaranje HTTP zahtjeva
                URL url = new URL(BASE_URL + "auth/block/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT"); // slicno za PUT
                conn.setRequestProperty("Content-Type", "application/json");
                // podaci za body dio zahtjeva
                JSONObject input = new JSONObject(user);
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
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }



        setTable();
    }

    public void deleteUserClick()
    {
        User user = usersTable.getSelectionModel().getSelectedItem();
        usersTable.getItems().remove(usersTable.getSelectionModel().getSelectedItem());

        if(user!=null) {
            try {
                Logger.getLogger(UsersController.class.getName()).addHandler(handler);
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                // priprema i otvaranje HTTP zahtjeva
                URL url = new URL(BASE_URL + "auth/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                // podaci za body dio zahtjeva
                JSONObject input = new JSONObject(user);
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
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }



        setTable();
    }

    public void activateUserClick()
    {
        User user = usersTable.getSelectionModel().getSelectedItem();
        usersTable.getSelectionModel().getSelectedItem().activated=true;
        if(user!=null) {
            try {
                Logger.getLogger(UsersController.class.getName()).addHandler(handler);
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                // priprema i otvaranje HTTP zahtjeva
                URL url = new URL(BASE_URL + "auth/activate/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT"); // slicno za PUT
                conn.setRequestProperty("Content-Type", "application/json");
                // podaci za body dio zahtjeva
                JSONObject input = new JSONObject(user);
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
                Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }



        setTable();
    }

    public void getUsers()
    {

        try{

            Logger.getLogger(UsersController.class.getName()).addHandler(handler);
            Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);

            URL url = new URL(BASE_URL+"auth/");
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
                JSONArray usersArray = jsonObject.getJSONArray("Users");

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
                    User user = new User(
                            userJson.getString("companyName"),
                            userJson.getString("address"),
                            userJson.getInt("phoneNumber"),
                            userJson.getString("username"),
                            userJson.getString("password"),
                            userJson.getBoolean("activated"),
                            userJson.getBoolean("blocked")
                    );


                    usersObservableList.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            br.close();
            conn.disconnect();

        }catch (Exception e)
        {
            Logger.getLogger(UsersController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getUsers();
        setTable();
    }
}
