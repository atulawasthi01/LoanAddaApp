package com.addventure.loanadda.Model;

/**
 * Created by User on 12/21/2017.
 */

public class City {
    String cityName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return getCityName(); // You can add anything else like maybe getDrinkType()
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof City){
            City c = (City)obj;
            if(c.getCityName().equals(cityName))
                return true;
        }

        return false;
    }
}
