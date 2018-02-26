package com.addventure.loanadda.Utility;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by rohitp on 2/25/2016.
 */
public interface IResult {
    public void notifySuccess(String requestType, String response);
    public void notifypostSuccess(String requestType, JSONObject response);
    public void notifyError(String requestType, VolleyError error);
}
