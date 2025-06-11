package com.bcm.us_project.calen;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DateDeleteRequest extends StringRequest {

    final static private String URL = "http://webdev.iptime.org/ktw/DateDelete.php";

    private Map<String, String> parameters;

    public DateDeleteRequest(String userID, String dateName, String location, String userDate, String dateMemo,
                             Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("dateName", dateName);
        parameters.put("location", location);
        parameters.put("userDate", userDate);
        parameters.put("dateMemo", dateMemo);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
