package com.bcm.us_project.calen;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// 일정을 수정할때(기존 값들을 받아서 새로 수정하는 값들로 Update)
public class EventUpdateRequest extends StringRequest {

    final static private String URL = "http://webdev.iptime.org/ktw/EventUpdate.php";

    private Map<String, String> parameters;

    public EventUpdateRequest(String userID, String dateName, String location, String userDate, String dateMemo,
                              String name, String locat, String date, String memo, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("dateName", dateName);
        parameters.put("location", location);
        parameters.put("userDate", userDate);
        parameters.put("dateMemo", dateMemo);

        parameters.put("Name", name);
        parameters.put("Locat", locat);
        parameters.put("Date", date);
        parameters.put("Memo", memo);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
