package com.addventure.loanadda.Model;

/**
 * Created by User on 12/21/2017.
 */

public class Bank {
    String bankName;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return getBankName(); // You can add anything else like maybe getDrinkType()
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Bank){
            Bank c = (Bank)obj;
            if(c.getBankName().equals(bankName))
                return true;
        }

        return false;
    }
}
