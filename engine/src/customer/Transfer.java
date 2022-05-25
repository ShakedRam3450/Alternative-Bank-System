package customer;

import dto.TransferDTO;

public class Transfer {
    private int time;
    private double amount;
    private double balanceBefore;
    private double balanceAfter;
    private TransferType transferType;

    public enum TransferType{
        WITHDRAWAL{
            @Override
            public String toString() {
                return "-";
            }
        },
        DEPOSIT{
            @Override
            public String toString() {
                return "+";
            }
        };
    }
    public Transfer(int time, double amount, double balanceBefore, double balanceAfter,TransferType transferType){
        this.time = time;
        this.balanceAfter = balanceAfter;
        this.balanceBefore = balanceBefore;
        this.amount = amount;
        this.transferType = transferType;
    }

    @Override
    public String toString() {
        return  "time=" + time +
                ", amount=" + transferType + amount +
                ", balanceBefore=" + balanceBefore +
                ", balanceAfter=" + balanceAfter;

    }

    public int getTime() {
        return time;
    }
    public double getAmount() {
        return amount;
    }
    public double getBalanceBefore() {
        return balanceBefore;
    }
    public double getBalanceAfter() {
        return balanceAfter;
    }
    public TransferType getTransferType() {
        return transferType;
    }
}

