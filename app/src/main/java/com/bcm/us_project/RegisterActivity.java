package com.bcm.us_project;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Pattern;

// 회원가입을 처리하는 액티비티

public class RegisterActivity extends AppCompatActivity {

    private String userName;
    private String userID;
    private String userPassword;
    private String userGender;
    private String userEmail;
    private String userDate;

    private AlertDialog dialog;
    private boolean validate = false;

    Calendar cal = Calendar.getInstance();
    int y=cal.get(Calendar.YEAR), m=cal.get(Calendar.MONTH), d=cal.get(Calendar.DATE);
    private TextView makeDate;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final TextView makeDate = (TextView) findViewById(R.id.makeDate);

        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        userGender = ((RadioButton) findViewById(genderGroupID)).getText().toString();

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton genderButton = (RadioButton) findViewById(i);
                userGender = genderButton.getText().toString();
            }
        });

        // ID 부분에 한글입력 x
        idText.setFilters(new InputFilter[]{inputFilter});

        // 중복체크 버튼
        final Button validateButton = (Button) findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idText.getText().toString();
                if (validate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디가 빈 칸 입니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //중복체크
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용가능한 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();

                                //사용가능한 아이디일 경우 수정이 불가능하게
                                idText.setEnabled(false);
                                validate = true;
//                                idText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("존재하는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);

            }
        });


        //회원가입시 중복체크하기
        Button registerButton = (Button) findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nameText.getText().toString();
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userEmail = emailText.getText().toString();
                String userDate = makeDate.getText().toString();

                //중복체크를 하지 않은 경우
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("중복 체크를 눌러주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //공백이 하나라도 존재할 경우
                if (userName.equals("") || userID.equals("") || userPassword.equals("") || userEmail.equals("")
                        || userGender.equals("") || userDate.equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸이 있습니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                //회원가입을 시도했을 때
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원가입 성공!")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .create();
                                dialog.show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원가입에 실패했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userName, userID, userPassword, userGender,
                        userEmail, userDate, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickHandler(v);
            }
        });

        this.showDateView();
        this.showDateListener();

    }
    // ~oncreate


    // Calender
    public void showDateView() {
        makeDate = (TextView) findViewById(R.id.makeDate);
    }

    public void showDateListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                makeDate.setText(year + "-" + (month+1) + "-" + dayOfMonth);
            }
        };
    }

    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, y, m, d);

        dialog.show();
    }

    // ID 한글입력x 필터
    public InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]*$");
            if(!ps.matcher(source).matches()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                dialog = builder.setMessage("아이디는 한글입력이 불가합니다.")
                        .setPositiveButton("확인", null)
                        .create();
                dialog.show();
                return "";
            }
            return null;
        }

    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

