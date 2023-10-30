package utilities;

import main.MaterialoptionsController;
import main.OrdersController;
import main.UsersController;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.UsersController.handler;

public class OrderDataHandler implements Runnable{
    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }
    private final SSLSocket clientSocket;

    public OrderDataHandler(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            // Get input and output streams for communication with the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            // Read data from the client and send a response
            String clientMessage;
            String finalOutput="";
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received from client: " + clientMessage);
                finalOutput+=clientMessage;
                //client message contains info about order and is to be written into orderinfofile

                writer.println("OK");
            }
            write(finalOutput);
            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            Logger.getLogger(OrderDataHandler.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(OrderDataHandler.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    private void write(String orderData)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDate = dateFormat.format(new Date());

        String fileName = "order" + currentDate + ".txt";

        try {
          FileWriter fileWriter = new FileWriter(fileName);

           fileWriter.write(orderData);

          fileWriter.close();

          System.out.println("File '" + fileName + "' has been created and written successfully.");

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
