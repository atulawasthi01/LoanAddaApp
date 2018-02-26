package com.addventure.loanadda.Utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.addventure.loanadda.AppController;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 12/27/2017.
 */

public class Json_Volley {
    Context context;
    public JSONObject responseStr;
    String login_url = AppConfig.BaseUrl + "otpverifynew";

    public Json_Volley(Context context) {
        this.context=context;
    }

    public JSONObject getLogin(JSONObject js) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, login_url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        responseStr=response;


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,""+error,Toast.LENGTH_SHORT).show();
                VolleyLog.d("errorrr", error.getMessage());
                //Log.d("errorrr", error.getMessage());

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq);

        return responseStr;

    }
}
