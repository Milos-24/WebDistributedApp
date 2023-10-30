package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.ClientHandler;
import utilities.OrderDataHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.UsersController.handler;

public class OrdersController {

    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }
    private static final int AUTHPORT = 8443;
    private static final int DATAPORT = 10001;
    private static final String KEY_STORE_PATH = "./keystore.jks";
    private static final String KEY_STORE_PASSWORD = "securemdp";

    @FXML
    private Button listenButton;

    public void listenClick() {
        Thread authServerThread = new Thread(() -> startAuthServer());
        Thread dataServerThread = new Thread(()-> startDataServer());


        authServerThread.start();
        dataServerThread.start();

        listenButton.setDisable(true);
    }


    //server for order data entries
    public void startDataServer()
    {
        try {
            System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
            System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PASSWORD);

            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket ss = ssf.createServerSocket(DATAPORT);

            System.out.println("Server started. Listening on port " + DATAPORT);

            // Accept client connections
            while (true) {
                // Accept client connections
                SSLSocket clientSocket = (SSLSocket) ss.accept();

                // Handle client communication in a separate thread
                Thread orderDataHandler = new Thread(new OrderDataHandler(clientSocket));
                orderDataHandler.start();
            }
        } catch (Exception e) {
            Logger.getLogger(OrdersController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }
    }



    //server for authentication
    public void startAuthServer()
    {
        try {
            System.setProperty("javax.net.ssl.keyStore", KEY_STORE_PATH);
            System.setProperty("javax.net.ssl.keyStorePassword", KEY_STORE_PASSWORD);

            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket ss = ssf.createServerSocket(AUTHPORT);

            System.out.println("Server started. Listening on port " + AUTHPORT);

            // Accept client connections
            while (true) {
                // Accept client connections
                SSLSocket clientSocket = (SSLSocket) ss.accept();

                // Handle client communication in a separate thread
                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (Exception e) {
            Logger.getLogger(OrdersController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());

        }
    }

    public void rawMaterialClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("rawMaterial.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Products");
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

    public void promotionClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("promotion.fxml")));
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

}
