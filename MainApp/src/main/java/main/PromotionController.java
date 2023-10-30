package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.UsersController.handler;

public class PromotionController {

    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }
    private static final int PORT = 20000;
    private static final String HOST = "224.0.0.11";

    @FXML
    TextArea promotionTextArea;

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

    public void usersClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("users.fxml")));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Users");
        stage.show();
    }

    public void sendClick(ActionEvent event)
    {
        System.out.println("Multicast server pokrenut...");
        MulticastSocket socket = null;
        byte[] buf = new byte[6];
        try {
            socket = new MulticastSocket();
            InetAddress address = InetAddress.getByName(HOST);
            socket.joinGroup(address);

            String msg = promotionTextArea.getText() + "   sent: " + new Date().toString();
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
            socket.send(packet);


        } catch (IOException e) {
            Logger.getLogger(PromotionController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());

        }
    }

}
