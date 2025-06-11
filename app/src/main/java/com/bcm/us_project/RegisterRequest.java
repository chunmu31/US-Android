package com.bcm.us_project;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// 회원가입요청을 보내는 액티비티

public class RegisterRequest extends StringRequest {

    //접속할 서버의 주소
//    final static private String URL = "https://ktw1524.cafe24.com/UserTestRegister.php";
    final static private String URL = "http://webdev.iptime.org/ktw/userRegister.php";

    //값들을 담을 부분을
    private Map<String, String> parameters;

    //생성자 부분(Response는 응답을 받을 수 있도록)
    public RegisterRequest(String userName, String userID, String userPassword, String userGender,
                           String userEmail, String userDate, Response.Listener<String> listener) {

        //해당 URL에 값들을 포스트방식으로 해당요청을 숨겨서 보내준다
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userName", userName);
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userGender", userGender);
        parameters.put("userEmail", userEmail);
        parameters.put("userDate", userDate);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
