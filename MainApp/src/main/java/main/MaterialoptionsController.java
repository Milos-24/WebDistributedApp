package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.RawMaterial;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static main.UsersController.handler;

public class MaterialoptionsController implements Initializable {
    {
        // ime logger-a je naziv klase
        Logger.getLogger(MaterialoptionsController.class.getName()).addHandler(handler);
    }
    private ObservableList<RawMaterial> materialsObservableList = FXCollections.observableArrayList();

    @FXML
    TableView materialTableView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTable();
    }

    public void setTable() {
        readRawMaterialsFromFile("register.txt");
        for(RawMaterial r : materialsObservableList)
        {
            System.out.println(r.toString());
        }

        materialTableView.setItems(materialsObservableList);

        TableColumn<RawMaterial, String> idColumn = new TableColumn<>("Company Name");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().companyName));


        TableColumn<RawMaterial, String> idColumnFirst = new TableColumn<>("Name");
        idColumnFirst.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name));

        TableColumn<RawMaterial, Integer> idColumnSecond = new TableColumn<>("Quantity");
        idColumnSecond.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().quantity).asObject());

        TableColumn<RawMaterial, Integer> idColumnThird = new TableColumn<>("Price");
        idColumnThird.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().price).asObject());


        materialTableView.getColumns().setAll(idColumn, idColumnFirst,idColumnSecond,idColumnThird);
    }



    public void readRawMaterialsFromFile(String filename) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String companyName = parts[0].trim();
                    String name = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());
                    int price = Integer.parseInt(parts[3].trim());

                    RawMaterial material = new RawMaterial(companyName, name, quantity, price);
                    materialsObservableList.add(material);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(MaterialoptionsController.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());

        }
    }

    public void orderButtonClick()
    {
        new Alert(Alert.AlertType.CONFIRMATION,"Congrats on the order!").show();
    }


}
