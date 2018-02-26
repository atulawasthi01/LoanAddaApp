package com.addventure.loanadda.Model;

/**
 * Created by User on 12/11/2017.
 */

public class Loantitle {
    String loanTitle;
    int loantitleImg;

    public Loantitle(String loanTitle, Integer loantitleImg) {
        this.loanTitle=loanTitle;
        this.loantitleImg=loantitleImg;

    }

    public String getLoanTitle() {
        return loanTitle;
    }

    public void setLoanTitle(String loanTitle) {
        this.loanTitle = loanTitle;
    }

    public int getLoantitleImg() {
        return loantitleImg;
    }

    public void setLoantitleImg(int loantitleImg) {
        this.loantitleImg = loantitleImg;
    }
}
