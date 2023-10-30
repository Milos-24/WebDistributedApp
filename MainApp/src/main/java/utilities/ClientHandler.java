package utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.MaterialoptionsController;
import main.UsersController;
import model.FactoryUser;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.UsersController.handler;

public class ClientHandler implements Runnable {
    private final SSLSocket clientSocket;

    public ClientHandler(SSLSocket clientSocket) {
        this.clientSocket = clientSocket;
    }
    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }

    @Override
    public void run() {
        try {
            // Get input and output streams for communication with the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

            // Read data from the client and send a response
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received from client: " + clientMessage);
                String[] credentials = clientMessage.split("\\|");


                if(isValidUser(credentials[0],credentials[1]))
                {
                    writer.println("OK");
                    System.out.println("success");
                }
                else {
                    writer.println("NOTOK");
                    System.out.println("fail");
                }
            }

            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            Logger.getLogger(ClientHandler.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    private boolean isValidUser(String username1, String password)
    {
        System.out.println(username1);
        System.out.println(password);


        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the "users" array directly into a List of User objects
            List<FactoryUser> userList = objectMapper.readValue(
                    new File("src"+File.separator+"main"+File.separator+"resources"+File.separator+"main"+File.separator+"factory_users.json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FactoryUser.class)
            );

            for(FactoryUser user : userList)
            {
                if(username1.equals(user.getUsername()) && password.equals(user.getPassword()))
                    return true;
            }



        } catch (IOException e) {
            Logger.getLogger(ClientHandler.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }

        return false;
    }

}