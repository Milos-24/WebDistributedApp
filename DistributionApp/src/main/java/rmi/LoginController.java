package rmi;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login!");
        scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void loginClick(ActionEvent event) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource("distributerMenu.fxml"))));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("Distributer!");
        stage.show();
    }

}
