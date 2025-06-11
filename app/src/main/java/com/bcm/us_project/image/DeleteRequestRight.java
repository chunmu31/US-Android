package com.bcm.us_project.image;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteRequestRight extends StringRequest {

//    final static private String URL = "http://ktw1524.cafe24.com/RightDelete.php";
    final static private String URL = "http://webdev.iptime.org/ktw/RightDelete.php";

    private Map<String, String> parameters;

    public DeleteRequestRight(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
