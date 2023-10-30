package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.*;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController extends Application {
    private final String logPath="LogFile.log";

    public static Handler handler;

    {
        try {
            handler = new FileHandler(logPath);
            Logger.getLogger(LoginController.class.getName()).addHandler(handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8443;
    private static final String KEY_STORE_PATH = "./keystore.jks";
    private static final String KEY_STORE_PASSWORD = "securemdp";

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login!");
        scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void login(ActionEvent event)
    {
        if(usernameTextField.getText().isEmpty() || passwordPasswordField.getText().isEmpty())
            return;

        System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASSWORD);

        try {
            // Create an SSLSocketFactory
            SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // Create an SSLSocket and connect to the server
            SSLSocket clientSocket = (SSLSocket) sf.createSocket(HOST, PORT);

            // Get input and output streams for communication with the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            // Send a message to the server
            writer.println(usernameTextField.getText() + "|" + passwordPasswordField.getText());

            // Receive and print the server's response

            String serverResponse = reader.readLine();

            if(serverResponse.equals("OK"))
            {
                Platform.runLater(()->
                {
                    try {
                        loginSuccess(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            clientSocket.close();

            } catch (IOException e) {
            Logger.getLogger(LoginController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }
    }

    public void loginClick(ActionEvent event) throws IOException {
        Thread thread = new Thread(()->login(event));

        thread.start();
    }

    public void loginSuccess(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("orders.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("Orders!");
        stage.show();
    }
}
