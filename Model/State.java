package com.addventure.loanadda.Model;

/**
 * Created by User on 12/21/2017.
 */

public class State {

    String stateName;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return getStateName(); // You can add anything else like maybe getDrinkType()
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof State){
            State c = (State)obj;
            if(c.getStateName().equals(stateName))
                return true;
        }

        return false;
    }
}
