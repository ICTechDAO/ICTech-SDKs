package com.icplaza.sdk.common;

public class AccountInfo implements Cloneable {
    /**
     */
    private Long accountNumber;
    /**
     */
    private Long sequenceNumber;

    @Override
    public AccountInfo clone() {
        try {
            return (AccountInfo) super.clone();
        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
        }
        return null;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
