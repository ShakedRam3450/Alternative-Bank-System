package body.admin;

import body.loans.LoansController;
import dto.CustomerDTO;
import dto.InvestorDTO;
import dto.LoanDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.MainController;
import org.controlsfx.control.*;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.io.File;
import java.util.List;
import java.util.Map;

public class AdminBodyController {

    @FXML private LoansController loansComponentController;
    @FXML private MasterDetailPane loansComponent;
    @FXML private Button increaseYazBTN;
    @FXML private Button loadFileBTN;

    @FXML private TableView<CustomerDTO> customersTable;
    TableColumn<CustomerDTO,String> nameCol;
    TableColumn<CustomerDTO,Double> balanceCol;
    TableColumn<CustomerDTO,String> numOfBorrowerLoansCol;
    TableColumn<CustomerDTO,Integer> numOfLenderLoansCol;

    private MainController mainController;

    @FXML
    public void initialize() {
        customersTable.setEditable(true);

        nameCol = new TableColumn<>("name");
        balanceCol = new TableColumn<>("balance");
        numOfBorrowerLoansCol = new TableColumn<>("numOfBorrowerLoans");
        numOfLenderLoansCol = new TableColumn<>("numOfLenderLoans");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        numOfBorrowerLoansCol.setCellValueFactory(new PropertyValueFactory<>("numOfBorrowerLoans"));
        numOfLenderLoansCol.setCellValueFactory(new PropertyValueFactory<>("numOfLenderLoans"));


    }
    @FXML
    void loadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select words file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(mainController.getPrimaryStage());
        if (selectedFile == null)
            return;

        String absolutePath = selectedFile.getAbsolutePath();
        mainController.readFile(selectedFile,absolutePath);
    }

    @FXML
    void increaseYaz(ActionEvent event) {
        mainController.increaseYaz();

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void displayInfo(List<LoanDTO> loans, Map<String,CustomerDTO> customers){
        customersTable.getColumns().clear();
        displayCustomers(customers);
        loansComponentController.displayLoans(loans);
    }

    private void displayCustomers(Map<String,CustomerDTO> customers){
        ObservableList<CustomerDTO> customerData = FXCollections.observableArrayList();
        customerData.addAll(customers.values());

        customersTable.setItems(customerData);
        customersTable.getColumns().addAll(nameCol, balanceCol, numOfBorrowerLoansCol, numOfLenderLoansCol);
    }


}
