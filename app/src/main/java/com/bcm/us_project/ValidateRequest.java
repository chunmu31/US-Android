package com.bcm.us_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest extends StringRequest {

//    final static private String URL = "https://ktw1524.cafe24.com/UserTestValidate.php";
    final static private String URL = "http://webdev.iptime.org/ktw/userValidate.php";

    private Map<String, String> parameters;

    public ValidateRequest(String userID, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams()  {
        return parameters;
    }

}
