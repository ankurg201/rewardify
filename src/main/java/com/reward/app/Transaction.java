package com.reward.app;

public class Transaction {
    private String customerId;
    private double amountSpent;
    private String transactionDate;

    public Transaction(String customerId, double amountSpent, String transactionDate) {
        this.customerId = customerId;
        this.amountSpent = amountSpent;
        this.transactionDate = transactionDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}

