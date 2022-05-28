package header;

import dto.CustomerDTO;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import main.MainController;

import java.io.IOException;
import java.util.Map;

public class HeaderController {

    @FXML private ComboBox<String> userCB;
    @FXML private ComboBox<String> skinCB;
    @FXML private Label filePath;
    @FXML private Label time;

    private MainController mainController;

    public ComboBox<String> getUserCB() {
        return userCB;
    }

    @FXML
    public void initialize(){
        userCB.setValue("Admin");
        userCB.getItems().add("Admin");
        time.setText("1");

        skinCB.setValue("default");
        skinCB.getItems().addAll("default", "cool", "veryCool");

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

   @FXML
    void switchUser(ActionEvent event) throws IOException {
        mainController.changeUser(userCB.getValue());
    }
    public void initCustomers(Map<String, CustomerDTO> customers){
        ObservableList<String> userCBList = userCB.getItems();
        userCBList.clear();
        userCBList.add("Admin");
        userCB.setValue("Admin");
        userCB.getItems().addAll(customers.keySet());
    }
    public void displayFilePath(String path){
        filePath.setText(path);
    }

    public Label getTimeLabel() {
        return time;
    }
}