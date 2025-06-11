package com.bcm.us_project.calen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bcm.us_project.R;

import org.json.JSONObject;

import java.util.Calendar;

// 일정을 추가하는 부분
public class AddEventRegister extends AppCompatActivity {

    private String userID;
    private String dateName;
    private String location;
    private String userDate;
    private String dateMemo;

    private AlertDialog dialog;

    Calendar cal = Calendar.getInstance();
    int y=cal.get(Calendar.YEAR), m=cal.get(Calendar.MONTH), d=cal.get(Calendar.DATE);
    private TextView dateText;
    private DatePickerDialog.OnDateSetListener callbackMethod;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventday);

        final TextView dateText = (TextView) findViewById(R.id.dateText);

        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText locationText = (EditText) findViewById(R.id.locationText);
        final EditText memoDate = (EditText) findViewById(R.id.memoText);

        // 일정 등록시 날짜 입력부분
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickHandler(v);
            }
        });
        this.showDateView();
        this.showDateListener();

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // intent에서 값을 받아올 때 date 값이 있을경우 해당 date값으로 설정
        // date 값이 존재하지 않을 시 빈칸
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        String date = intent.getStringExtra("userDate");

        if (date != null) {
            dateText.setText(date);
        }

        // 일정 저장 버튼 이벤트리스너
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                userID = intent.getStringExtra("userID");
                String dateName = nameText.getText().toString();
                String location = locationText.getText().toString();
                String userDate = dateText.getText().toString();
                String dateMemo = memoDate.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventRegister.this);
                                dialog = builder.setMessage("일정 저장")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                AddEventRequest registerRequest = new AddEventRequest(userID, dateName, location, userDate,
                        dateMemo, responseListener);
                RequestQueue queue = Volley.newRequestQueue(AddEventRegister.this);
                queue.add(registerRequest);

            }
        });

    }


    // CalendarView
    public void showDateView() {
        dateText = (TextView) findViewById(R.id.dateText);
    }

    public void showDateListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateText.setText(year + "-" + (month+1) + "-" + dayOfMonth);
            }
        };
    }

    public void OnClickHandler(View view)
    {
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, y, m, d);

        dialog.show();
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
