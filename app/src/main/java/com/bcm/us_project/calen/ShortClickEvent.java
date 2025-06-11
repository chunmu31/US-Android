package com.bcm.us_project.calen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bcm.us_project.R;

import org.json.JSONObject;

import java.util.Calendar;

// 일정 RecyclerView에서 해당 리스트중 한번 클릭했을때 발생하는 event
public class ShortClickEvent extends AppCompatActivity {

    Button backBtn;

    EditText nameText;
    EditText locationText;
    TextView dateText;
    EditText memoText;

    Calendar cal = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener callbackMethod;
    int y=cal.get(Calendar.YEAR), m=cal.get(Calendar.MONTH), d=cal.get(Calendar.DATE);

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_click_event);

        backBtn = (Button) findViewById(R.id.backBtn);

        nameText = (EditText) findViewById(R.id.nameText);
        locationText = (EditText) findViewById(R.id.locationText);
        dateText = (TextView) findViewById(R.id.dateText);
        memoText = (EditText) findViewById(R.id.memoText);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 날짜지정
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickHandler(v);
            }
        });
        this.showDateView();
        this.showDateListener();

        final Intent intent = getIntent();
        String dateName = intent.getStringExtra("dateName");
        String location = intent.getStringExtra("location");
        String userDate = intent.getStringExtra("userDate");
        String dateMemo = intent.getStringExtra("dateMemo");

        nameText.setText(dateName);
        locationText.setText(location);
        dateText.setText(userDate);
        memoText.setText(dateMemo);

        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String userID = intent.getStringExtra("userID");
                String dateName = intent.getStringExtra("dateName");
                String location = intent.getStringExtra("location");
                String userDate = intent.getStringExtra("userDate");
                String dateMemo = intent.getStringExtra("dateMemo");

                String name = nameText.getText().toString();
                String locat = locationText.getText().toString();
                String date = dateText.getText().toString();
                String memo = memoText.getText().toString();

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShortClickEvent.this);
                                dialog = builder.setMessage("일정 수정")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                onResume();
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };

                EventUpdateRequest eventUpdateRequest = new EventUpdateRequest(
                        userID, dateName, location, userDate, dateMemo, name, locat, date, memo, listener
                );
                RequestQueue queue = Volley.newRequestQueue(ShortClickEvent.this);
                queue.add(eventUpdateRequest);

            }

        });

    }

    //Calendar
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
}
