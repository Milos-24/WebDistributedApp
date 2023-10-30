package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {
    private final String logPath="LogFile.log";
    public static Handler handler;

    {
        try {
            handler = new FileHandler(logPath);
            Logger.getLogger(HelloApplication.class.getName()).addHandler(handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String BASE_URL = "http://localhost:8080/FactoryBackend/api/auth";

    @FXML
    TextField usernameTextField;

    @FXML
    PasswordField passwordPasswordField;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign In!");
        scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    public void registrationClick(ActionEvent event) throws IOException {


        Parent root = FXMLLoader.load(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("registration.fxml"))));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("Registration!");
        stage.show();
    }

    public void loginClick(ActionEvent event) throws IOException {

        List<User> users = new ArrayList<>();
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();


        try{
            URL url = new URL(BASE_URL);
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
                            userJson.getBoolean("activated")
                    );


                    users.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            br.close();
            conn.disconnect();

        }catch (Exception e)
        {
            Logger.getLogger(HelloApplication.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }


        for(User u : users)
        {
            if(username.equals(u.username) && password.equals(u.password))
            {
                if(u.activated==true && u.blocked==false)
                {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("buyerOrder.fxml")));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.setTitle("Sign In!");
                    stage.show();
                }
            }
        }




    }

    public static void main(String[] args) {
        launch();
    }
}