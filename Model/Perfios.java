package com.addventure.loanadda.Model;

/**
 * Created by User on 1/4/2018.
 */

public class Perfios {
    String bankName,bankValue;

    public Perfios(String bankName, String bankValue) {
        this.bankName=bankName;
        this.bankValue=bankValue;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankValue() {
        return bankValue;
    }

    public void setBankValue(String bankValue) {
        this.bankValue = bankValue;
    }

    @Override
    public String toString() {
        return getBankName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Perfios){
            Perfios c = (Perfios )obj;
            if(c.getBankName().equals(bankName) && c.getBankValue()==bankValue ) return true;
        }

        return false;
    }
}
