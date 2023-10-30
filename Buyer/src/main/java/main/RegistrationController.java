package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegistrationController {

    public static final String BASE_URL = "http://localhost:8080/FactoryBackend/api/auth";

    @FXML
    TextField usernameTextField;

    @FXML
    TextField companyTextField;

    @FXML
    TextField addressTextField;

    @FXML
    TextField phoneNumberTextField;

    @FXML
    TextField passwordTextField;

    @FXML
    TextField confirmPasswordTextField;

    public void backClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signin.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("Sign In!");
        stage.show();
    }

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    public void registrationClick(ActionEvent event) throws IOException
    {

        if(usernameTextField.getText().isEmpty() || companyTextField.getText().isEmpty() || addressTextField.getText().isEmpty()
                || passwordTextField.getText().isEmpty() || !isNumeric(phoneNumberTextField.getText()) || confirmPasswordTextField.getText().isEmpty())
        {
            return;
        }
        else {

            User user = new User(companyTextField.getText(), addressTextField.getText(), Long.parseLong(phoneNumberTextField.getText()), usernameTextField.getText(), passwordTextField.getText(), false);


            try {
                // priprema i otvaranje HTTP zahtjeva
                URL url = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST"); // slicno za PUT
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
                Logger.getLogger(RegistrationController.class.getName()).setUseParentHandlers(false);
                Logger.getLogger(RegistrationController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
            }

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signin.fxml")));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
            stage.setResizable(false);
            stage.setTitle("Sign In!");
            stage.show();

        }

    }

}
