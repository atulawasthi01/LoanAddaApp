package com.addventure.loanadda.Model;

/**
 * Created by User on 12/21/2017.
 */

public class Branch {
    String branchName;

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return getBranchName(); // You can add anything else like maybe getDrinkType()
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof City){
            Branch c = (Branch)obj;
            if(c.getBranchName().equals(branchName))
                return true;
        }

        return false;
    }
}
