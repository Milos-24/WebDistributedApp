package rmi;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistributerController implements MyClientInterface {
    private ObservableList<RawMaterial> materialsObservableList = FXCollections.observableArrayList();

    @FXML
    TextField companyNameTextField;

    @FXML
    TextField nameTextField;

    @FXML
    TextField quantityTextField;

    @FXML
    TextField priceTextField;

    @FXML
    TableView rawMaterialTableView;
    @FXML
    Button addButton;

    private final String logPath="LogFile.log";

    public static Handler handler;

    {
        try {
            handler = new FileHandler(logPath);
            Logger.getLogger(DistributerController.class.getName()).addHandler(handler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void setTable() {
        rawMaterialTableView.setItems(materialsObservableList);

        TableColumn<RawMaterial, String> idColumnFirst = new TableColumn<>("Name");
        idColumnFirst.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));

        TableColumn<RawMaterial, Integer> idColumnSecond = new TableColumn<>("Quantity");
        idColumnSecond.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().quantity).asObject());

        TableColumn<RawMaterial, Integer> idColumnThird = new TableColumn<>("Price");
        idColumnThird.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().price).asObject());


        rawMaterialTableView.getColumns().setAll(idColumnFirst,idColumnSecond,idColumnThird);
    }
    public void registerButtonClick() {
        System.setProperty("java.security.policy", "./client_policyfile.txt");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try{
            String name = "MyServer";
            Registry registry = LocateRegistry.getRegistry("localhost",1099);


            DistributionInterface server = (DistributionInterface) registry.lookup(name);

            StringBuilder order = new StringBuilder();

            for(RawMaterial rw : materialsObservableList)
            {
                order.append(rw.toString());
            }

            System.out.println(String.valueOf(order));

            server.addingMaterial(companyNameTextField.getText(), String.valueOf(order));

        }
        catch (Exception e)
        {
            Logger.getLogger(DistributerController.class.getName()).setUseParentHandlers(false);
            Logger.getLogger(DistributerController.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
        }

        addButton.setDisable(true);
    }

    public void addButtonClick()
    {
        if(!nameTextField.getText().isEmpty() && !quantityTextField.getText().isEmpty() && !priceTextField.getText().isEmpty())
        {
            materialsObservableList.add(new RawMaterial(nameTextField.getText(),Integer.parseInt(quantityTextField.getText()), Integer.parseInt(priceTextField.getText())));
        }

        setTable();
    }


    @Override
    public void sendClientData(String data) throws RemoteException {

    }
}
