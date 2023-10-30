package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PromotionsController implements Initializable {

    private static final int PORT = 20000;
    private static final String HOST = "224.0.0.11";

    private Thread t;

    @FXML
    TextArea promotionsTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        t = new Thread(new Runnable() {
            @Override
            public void run() {

                MulticastSocket socket = null;
                byte[] buffer = new byte[256];
                try {
                    socket = new MulticastSocket(PORT);
                    InetAddress address = InetAddress.getByName(HOST);
                    socket.joinGroup(address);
                    while(true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        promotionsTextArea.setText(received);
                    }
                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
                // code goes here.
            }
        });


        t.start();


    }

    public void newOrderClick(ActionEvent event) throws IOException {
        t.interrupt();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("buyerOrder.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //scene.getStylesheets().addAll(this.getClass().getResource("app.css").toExternalForm());
        stage.setResizable(false);
        stage.setTitle("New Order!");
        stage.show();
    }


}
