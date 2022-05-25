package body.loans;

import dto.InvestorDTO;
import dto.LoanDTO;
import dto.PaymentDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.MasterDetailPane;

import java.util.List;
import java.util.stream.Collectors;

public class LoansController {
    @FXML private MasterDetailPane loansInfo;
    private TableView<LoanDTO> loansTable;
    private VBox loansDetails;
    private ObservableList<LoanDTO> loansData;
    private TableColumn<LoanDTO, String> idCol;
    private TableColumn<LoanDTO, String> ownerNameCol;
    private TableColumn<LoanDTO, String> categoryCol ;
    private TableColumn<LoanDTO, Integer> capitalCol;
    private TableColumn<LoanDTO, Integer> totalTimeCol;
    private TableColumn<LoanDTO, Integer> interestPerPaymentCol;
    private TableColumn<LoanDTO, Integer> paymentsMarginCol;
    private TableColumn<LoanDTO, String> statusCol;

    public LoansController() {

    }
    @FXML
    public void initialize(){
        loansTable = new TableView<>();
        loansTable.setEditable(true);

        loansDetails = new VBox();

        if(loansInfo != null) {
            loansInfo.setDetailNode(loansDetails);
            loansInfo.setMasterNode(loansTable);
        }

        idCol = new TableColumn<>("id");
        ownerNameCol = new TableColumn<>("owner");
        categoryCol = new TableColumn<>("category");
        capitalCol = new TableColumn<>("capital");
        interestPerPaymentCol = new TableColumn<>("interest per payment");
        statusCol = new TableColumn<>("status");
        totalTimeCol = new TableColumn<>("total time");
        paymentsMarginCol = new TableColumn<>("payments margin");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        ownerNameCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        capitalCol.setCellValueFactory(new PropertyValueFactory<>("capital"));
        interestPerPaymentCol.setCellValueFactory(new PropertyValueFactory<>("interestPerPayment"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalTimeCol.setCellValueFactory(new PropertyValueFactory<>("totalYazTime"));
        paymentsMarginCol.setCellValueFactory(new PropertyValueFactory<>("paysEveryYaz"));

    }
    public void displayLoans(List<LoanDTO> loans){
        loansDetails.getChildren().clear();
        loansTable.getColumns().clear();
        loansData = FXCollections.observableArrayList();
        loansData.addAll(loans);

        loansTable.setItems(loansData);
        loansTable.getColumns().addAll(idCol, ownerNameCol, categoryCol, capitalCol, totalTimeCol, interestPerPaymentCol, paymentsMarginCol, statusCol);

        loansDetails.getChildren().add(new Label("none"));
        loansTable.setOnMouseClicked(e -> {
            loansDetails.getChildren().clear();
            loansDetails.getChildren().add(new Label((statusDisplay(loansTable.getSelectionModel().getSelectedItem())).toString()));
        });
    }
    private StringBuilder statusDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        res.append(loan.getStatus()).append("\n");
        switch (loan.getStatus()){
            case NEW:
                res.append(loan.getOwnerName());
                break;
            case PENDING:
                res.append(investorsDisplay(loan));
                res.append("Amount raised: ").append(loan.getAmountRaised()).append("Amount remaining: ").append(loan.getAmountRemaining()).append("\n");
                break;
            case ACTIVE:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("Next payment time: ");
                if(loan.getPayments().isEmpty())
                    res.append(loan.getStartTime() + loan.getPaysEveryYaz()).append("\n");
                else
                    res.append(loan.getLastPaymentTime() + loan.getPaysEveryYaz()).append("\n");
                res.append(paymentsDisplay(loan));
                res.append("Total capital paid: ").append(loan.getTotalCapitalPaid()).append("\n");
                res.append("Total interest paid: ").append(loan.getTotalInterestPaid()).append("\n");
                res.append("Total capital remaining: ").append(loan.getTotalCapitalRemaining()).append("\n");
                res.append("Total interest remaining: ").append(loan.getTotalInterestRemaining()).append("\n");
                break;
            case IN_RISK:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("Next payment time: ");
                if(loan.getPayments().isEmpty())
                    res.append(loan.getStartTime() + (loan.getPaysEveryYaz() * loan.getNumberOfUnpaidPayments())).append("\n");
                else
                    res.append(loan.getLastPaymentTime() + (loan.getPaysEveryYaz() * loan.getNumberOfUnpaidPayments())).append("\n");
                res.append(paymentsDisplay(loan));
                res.append("Total capital paid: ").append(loan.getTotalCapitalPaid()).append("\n");
                res.append("Total interest paid: ").append(loan.getTotalInterestPaid()).append("\n");
                res.append("Total capital remaining: ").append(loan.getTotalCapitalRemaining()).append("\n");
                res.append("Total interest remaining: ").append(loan.getTotalInterestRemaining()).append("\n");
                res.append("Total debt: ").append(loan.getDebt()).append(" number of unpaid payments: ").append(loan.getNumberOfUnpaidPayments()).append("\n");
                break;
            case FINISHED:
                res.append(investorsDisplay(loan));
                res.append("Start time: ").append(loan.getStartTime()).append("\n");
                res.append("End time: ").append(loan.getEndTime()).append("\n");
                res.append(paymentsDisplay(loan));
                break;
        }
        return res;
    }
    private StringBuilder paymentsDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        PaymentDTO tmpPayment = null;
        res.append("Payments:\n");
        if(!loan.getPayments().isEmpty()){
            for (Integer key: loan.getPayments().keySet()) {
                tmpPayment = loan.getPayments().get(key);
                res.append("time: ").append(tmpPayment.getTime()).append(" ");
                res.append("capital part: ").append(tmpPayment.getCapitalPart()).append(" ");
                res.append("interest part: ").append(tmpPayment.getInterestPart()).append(" ");
                res.append("total amount: ").append(tmpPayment.getTotalAmount()).append("\n");
            }
        }
        return res;
    }
    private StringBuilder investorsDisplay(LoanDTO loan){
        StringBuilder res = new StringBuilder();
        res.append("Investors:\n");
        for(String key: loan.getInvestors().keySet()){
            InvestorDTO tmpInvestor = loan.getInvestors().get(key);
            res.append(tmpInvestor.getName()).append(" ").append(tmpInvestor.getPartInInvestment() * loan.getCapital()).append("\n");
        }
        return res;
    }

    public void addSelectCol() {
        TableColumn<LoanDTO,CheckBox> selectCol = new TableColumn<>("select");
        selectCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        loansTable.getColumns().add(0, selectCol);
    }

    public List<LoanDTO> getSelectedLoans() {
        return loansData.stream().filter(loan -> loan.getSelect().isSelected()).collect(Collectors.toList());
    }

    public void clear() {
        loansTable.getColumns().clear();
    }
}
