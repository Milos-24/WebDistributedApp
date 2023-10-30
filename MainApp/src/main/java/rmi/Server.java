package rmi;

import main.MaterialoptionsController;
import main.OrdersController;
import model.RawMaterial;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.UsersController.handler;

public class Server implements DistributionInterface{
    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }
    public void start() {
        System.setProperty("java.security.policy", "./server_policyfile.txt");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try{
            Server server = new Server();
            DistributionInterface stub = (DistributionInterface) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MyServer", stub);
        }
        catch (Exception e)
        {
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());

        }
    }
    public void parseInput(String input, String company) {
        Pattern pattern = Pattern.compile("name=([^,]+), quantity=(\\d+), price=(\\d+)");
        Matcher matcher = pattern.matcher(input);
        List<RawMaterial> materials = new ArrayList<>();
        while (matcher.find()) {
            String name = matcher.group(1).trim();
            int quantity = Integer.parseInt(matcher.group(2).trim());
            int price = Integer.parseInt(matcher.group(3).trim());

            RawMaterial item = new RawMaterial(company,name, quantity, price);
            System.out.println(item.toString());
            materials.add(item);
        }

        writeRawMaterialsToFile("register.txt", materials);

    }

    public static void writeRawMaterialsToFile(String filename, List<RawMaterial> rawMaterials) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            for (RawMaterial material : rawMaterials) {
                writer.println(material.companyName + "," + material.name + "," + material.quantity + "," + material.price);
            }
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }
    }



    @Override
    public void addingMaterial(String company, String order)
    {
        parseInput(order,company);
    }


}
