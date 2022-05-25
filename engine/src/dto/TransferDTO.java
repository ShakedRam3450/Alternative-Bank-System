package dto;

import customer.Transfer;

public class TransferDTO {
    private int time;
    private double amount;
    private double balanceBefore;
    private double balanceAfter;
    private Transfer.TransferType transferType;

    public TransferDTO(Transfer transfer){
        this.time = transfer.getTime();
        this.amount = transfer.getAmount();
        this.balanceBefore = transfer.getBalanceBefore();
        this.balanceAfter = transfer.getBalanceAfter();
        this.transferType = transfer.getTransferType();
    }

    public int getTime() {
        return time;
    }

    public Transfer.TransferType getTransferType() {
        return transferType;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }
}
