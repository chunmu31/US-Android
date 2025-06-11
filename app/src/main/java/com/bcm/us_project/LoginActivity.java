package com.bcm.us_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

// 로그인 Acitivty

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;

    public static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        context = this;

        TextView registerButton = (TextView) findViewById(R.id.registerButton);

        // 회원가입 버튼 이벤트 리스너
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        // 로그인 버튼 이벤트 리스너
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPassword", userPassword);

                                LoginActivity.this.startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.")
                                        .setNegativeButton("다시 시도", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }

        });

    }

    private long lastTimeBackPressed;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500)    {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한 번 더 눌러 종료합니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
