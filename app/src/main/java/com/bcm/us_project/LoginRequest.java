package com.bcm.us_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// Login 요청
public class LoginRequest extends StringRequest {

//    final static private String URL = "https://ktw1524.cafe24.com/UserTestLogin.php";
    final static private String URL = "http://webdev.iptime.org/ktw/userLogin.php";

    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
