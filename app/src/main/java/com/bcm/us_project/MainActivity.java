package com.bcm.us_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.bcm.us_project.calen.AddEventRegister;
import com.bcm.us_project.image.DeleteRequestRight;
import com.bcm.us_project.image.DeleteRequestLeft;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{

    RelativeLayout homeLayout;
    RelativeLayout homeLayout2;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final int REQ_CODE_PROFILE_IMAGE = 101;
    final int REQ_CODE_PROFILE2_IMAGE = 102;

    CalendarFragment calendarFragment = new CalendarFragment();

    CircleImageView profile_left, profile_right;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int CROP_FROM_CAMERA = 2;
    private Uri mImageCaptureUri;


    /* 해당 ID에 배경사진 저장 */
//    String ServerUploadPath = "https://ktw1524.cafe24.com/update.php";
    String ServerUploadPath = "http://webdev.iptime.org/ktw/UpdateHome.php";
    Bitmap bitmap;
    ProgressDialog progressDialog;
    boolean check = true;
    String ImagePath = "image_path";
    String UserID = "userID";
    /* ~~ */

    /* 해당 ID에 저장된 배경사진을 불러오기 */
    ImageView homeImage;

    /* 원형 프로필 사진 */
//    String ServerUploadPath2 = "https://ktw1524.cafe24.com/UpdateProfileLeft.php";
    String ServerUploadPath2 = "http://webdev.iptime.org/ktw/UpdateProfileLeft.php";
//    String ServerUploadPath3 = "https://ktw1524.cafe24.com/UpdateProfileRight.php";
    String ServerUploadPath3 = "http://webdev.iptime.org/ktw/UpdateProfileRight.php";

    String ImagePath2 = "profileLeft_path";
    String ImagePath3 = "profileRight_path";
    Bitmap bitmap2, bitmap3;
    String userID2, userProfileLeft2, userProfileRight2, homeImage2, userDate;

//    String backgroundHome = "https://ktw1524.cafe24.com/image/normal.png";
//    String backgroundProfileLeft = "https://ktw1524.cafe24.com/image/profile/left_normal.png";
//    String backgroundProfileRight = "https://ktw1524.cafe24.com/image/profile/right_normal.png";
    String backgroundHome = "http://webdev.iptime.org/ktw/image/normal.png";
    String backgroundProfileLeft = "http://webdev.iptime.org/ktw/image/profile/left_normal.png";
    String backgroundProfileRight = "http://webdev.iptime.org/ktw/image/profile/right_normal.png";

    TextView textDate;

    private AlertDialog dialog;
    String deleteID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Main이미지 & 만난 날짜 계산 호출
        final BackgroundTask task = new BackgroundTask();
        task.execute();

        // 일정 리스트 부분 호출
//        new EventTask().execute();

        // 프로필 부분쪽 layout
        homeLayout = (RelativeLayout) findViewById(R.id.homeLayout);

        // 만난 날짜를 표시하는 layout
        homeLayout2 = (RelativeLayout) findViewById(R.id.homeLayout2);

        // Main에서 전체 배경화면
        homeImage = (ImageView) findViewById(R.id.homeImage);

        // 하단 네비게이션 바
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        // 원형프로필 사진(CircleImageView 라이브러리 사용)
        profile_left = (CircleImageView) findViewById(R.id.profile_left);
        profile_right = (CircleImageView) findViewById(R.id.profile_right);

        // 원형프로필 좌&우 를 클릭했을시 이벤트리스너
        profile_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onButtonClickLeft(v);
                }
        });
        profile_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onButtonClickRight(v);
                }
        });

        // 만난 날짜 표시하는 TextView
        textDate = (TextView) findViewById(R.id.textView6);

        // 로그인 ID값을 AddEventRegister.class로 전달
        Intent intent1 = new Intent(getApplicationContext(), AddEventRegister.class);
        intent1.getStringExtra("userID");

        // 로그인 ID값을 받아서 삭제했을 때 ID값으로 지정전달
        Intent intent = getIntent();
        deleteID = intent.getStringExtra("userID");

        } // oncrete 끝나는 지점



    // 하단 네비게이션바에서 생성된 아이콘을 클릭했을시 이벤트리스너
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (menuitem.getItemId()) {

                // 사용자의 앨범부분을 요청해 열람
                case R.id.navigation_add:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                    break;

                // Main으로 돌아옴
                case R.id.navigation_home:
                    homeLayout.setVisibility(View.VISIBLE);
                    homeLayout2.setVisibility(View.VISIBLE);
                    transaction.hide(calendarFragment);
                    transaction.hide(calendarFragment);
                    transaction.commit();
                    break;

                // 기념일(일정) 부분으로 Fragment이동
                case R.id.navigation_anniversary:
                    homeLayout.setVisibility(View.GONE);
                    homeLayout2.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, calendarFragment).commit();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.show(calendarFragment);
                    transaction.commit();
                    break;

            }
            return true;
        }
    }


    // 프로필 Left 사진 Delete요청
    private void deletePhotoActionLeft() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("사진 삭제")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        DeleteRequestLeft deleteRequestLeft = new DeleteRequestLeft(deleteID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(deleteRequestLeft);

        // 이미지가 삭제되었을때 기본으로 설정된 Image 주소값을 불러옴
        Glide.with(getApplicationContext())
                .asBitmap()
                .signature(new ObjectKey(System.currentTimeMillis()))
                .load(backgroundProfileLeft)
                .into(profile_left);
    }

    // 프로필 Right 사진 Delete요청
    private void deletePhotoActionRight() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("사진 삭제")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        DeleteRequestRight deleteRequestRight = new DeleteRequestRight(deleteID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(deleteRequestRight);

        Glide.with(getApplicationContext())
                .asBitmap()
                .signature(new ObjectKey(System.currentTimeMillis()))
                .load(backgroundProfileRight)
                .into(profile_right);
    }

    // 프로필 사진선택을 눌렀을시 전달되는 이벤트 값(좌&우)
    private void choicePhotoActionLeft() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQ_CODE_PROFILE_IMAGE);
    }
    private void choicePhotoActionRight() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQ_CODE_PROFILE2_IMAGE);
    }

    // 왼쪽 프로필사진클릭시 Dialog창
    public void onButtonClickLeft(View v) {
        final CharSequence[] items = {"프로필 사진 선택", "프로필 사진 삭제"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog);
        builder
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choicePhotoActionLeft();
                                break;
                            case 1:
                                deletePhotoActionLeft();
                                break;
                        }
                    }
                })
                .show();
    }
    // 오른쪽 프로필사진클릭시 Dialog창
    public void onButtonClickRight(View v) {
        final CharSequence[] items = {"프로필 사진 선택", "프로필 사진 삭제"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        builder
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choicePhotoActionRight();
                                break;
                            case 1:
                                deletePhotoActionRight();
                                break;
                        }
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 원형프로필 이미지
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후 이미지를 넘겨 받는다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후 임시 파일을 삭제
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    profile_left.setImageBitmap(photo);
                    profile_right.setImageBitmap(photo);
                }
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }
                break;
            }
            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정
                // 이후에 이미지 크롭 어플리케이션을 호출
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 120);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
        }
        // ~원형프로필이미지

        super.onActivityResult(requestCode, resultCode, data);

        // left profile 이미지 서버전송
        if (requestCode == REQ_CODE_PROFILE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    profile_left.setImageBitmap(bitmap2);
                    ProfileLeftImageUploadToServerFunction();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // right profile 이미지 서버전송
        if (requestCode == REQ_CODE_PROFILE2_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    profile_right.setImageBitmap(bitmap3);
                    ProfileRightImageUploadToServerFunction();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* 배경사진 */
        //requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    homeImage.setImageBitmap(bitmap);
                    /* 여기 함수를 넣음으로써 사진선택후 배경화면 지정될때 url 전송 */
                    ImageUploadToServerFunction();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    // 이미지 and 만난 날 asynctask
    public class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
//            target = "https://ktw1524.cafe24.com/GetJson2.php";
            target = "http://webdev.iptime.org/ktw/GetJson.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = getIntent();
            intent.putExtra("userList", result);
            final String userID = intent.getStringExtra("userID");

            try {
                JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID2 = object.getString("userID");
                    homeImage2 = object.getString("userImage");
                    userProfileLeft2 = object.getString("userProfileLeft");
                    userProfileRight2 = object.getString("userProfileRight");
                    userDate = object.getString("userDate");

                    if (userID2.equals(userID)) {
                        break;
                    }
                    count++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (homeImage2.equals("null")) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(backgroundHome)
                        .into(homeImage);
            } else {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(homeImage2)
                        .into(homeImage);
            }

            if (userProfileLeft2.equals("null")) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(backgroundProfileLeft)
                        .into(profile_left);
            } else {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(userProfileLeft2)
                        .into(profile_left);
            }

            if (userProfileRight2.equals("null")) {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(backgroundProfileRight)
                        .into(profile_right);
            } else {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        // 캐시에 저장x
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .load(userProfileRight2)
                        .into(profile_right);
            }

            // 만날 날짜를 계산하는 부분
            long now = System.currentTimeMillis();
            Date nowDate = new Date(now);
            String todayDate = nowDate.toString();

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = formatter.parse(userDate);
                java.util.Date utilDate2 = formatter.parse(todayDate);

                long diff = utilDate2.getTime() - utilDate.getTime();
                long diffDay = (diff / (24 * 60 * 60 * 1000)) + 1;

                String dDay = Long.toString(diffDay);
                textDate.setText(dDay + "일");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    // 기념일 & 일정 부분을 미리 호출하여 CalendarFragment로 전달
    public class EventTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
//            target = "https://ktw1524.cafe24.com/ReadList.php";
            target = "http://webdev.iptime.org/ktw/ReadList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(String result1) {
            Intent intent = getIntent();
            intent.putExtra("eventList", result1);

            Intent send = new Intent(getApplicationContext(), CalendarFragment.class);
            Intent send2 = new Intent(getApplicationContext(), CalendarFragment.class);
            send.putExtra("eventList", result1);
            send2.putExtra("eventList", result1);
            Bundle bundle = new Bundle();
            bundle.putString("eventList", result1);

            calendarFragment.setArguments(bundle);
            calendarFragment.setArguments(bundle);
        }
    }


    // Main HomeImage 서버 업로드 요청
    final public void ImageUploadToServerFunction() {
        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        // Image 주소값설정하여 서버에 저장요청
        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(UserID,userID);
                HashMapParams.put(ImagePath, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    // Main Left Profile Image 서버 업로드 요청
    final public void ProfileLeftImageUploadToServerFunction() {
        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this, "Image is Uploading",
                        "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(UserID,userID);
                HashMapParams.put(ImagePath2, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath2, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    // Main Left Profile Image 서버 업로드 요청
    final public void ProfileRightImageUploadToServerFunction() {
        ByteArrayOutputStream byteArrayOutputStreamObject;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this, "Image is Uploading",
                        "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(UserID,userID);
                HashMapParams.put(ImagePath3, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath3, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    // POST방식으로 서버에 요청을 보내 이미지들을 저장
    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(10000);
                httpURLConnectionObject.setConnectTimeout(10000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(
                        new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check) {
                    check = false;
                } else {
                    stringBuilderObject.append("&");
                    stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                    stringBuilderObject.append("=");
                    stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
                }
            }
            return stringBuilderObject.toString();
        }
    }

    /* 뒤로가기 */
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
