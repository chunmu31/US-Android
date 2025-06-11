package com.bcm.us_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bcm.us_project.calen.AddEventRegister;
import com.bcm.us_project.calen.DateDeleteRequest;
import com.bcm.us_project.calen.EventDecorator;
import com.bcm.us_project.calen.EventList;
import com.bcm.us_project.calen.OneDayDecorator;
import com.bcm.us_project.calen.SaturdayDecorator;
import com.bcm.us_project.calen.ShortClickEvent;
import com.bcm.us_project.calen.SundayDecorator;
import com.bcm.us_project.calen.EventListAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<EventList> eventList;

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView materialCalendarView;

    String subID, dateName, location, userDate, dateMemo;

    String selectDate;

    private AlertDialog alertDialog;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        eventList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Fragment에서 상단 메뉴를 나타냄
        setHasOptionsMenu(true);

        // 일정 list를 RecyclerView로 나타냄
        recyclerView = (RecyclerView) v.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(0);
        adapter = new EventListAdapter(eventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // DividerItemDecoration : recyclerView에서 각 리스트 항목별 구분선 추가
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.line));
        recyclerView.addItemDecoration(dividerItemDecoration);

        materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calendarView);
        ((AppCompatActivity)getActivity()).setSupportActionBar((Toolbar) v.findViewById(R.id.toolbar));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(1990, 0, 1 )) //달력의 시작
                .setMaximumDate(CalendarDay.from(2999, 11, 32)) //달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        // 달력에서 날짜부분을 선택 했을시 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                selectDate = Year + "-" + Month + "-" + Day;

                // 로그인했을 시 ID값을 가지고 옴
                Bundle bundle = getActivity().getIntent().getExtras();
                String userID = bundle.getString("userID");

                // ReadAsyncTask로 전달할 url값
                String url = "http://webdev.iptime.org/ktw/ReadCal.php";

                // ""는 key값, 뒤에 오는 Value값 (AsyncTask로 값을 전달)
                ReadAsyncTask readAsyncTask = new ReadAsyncTask();
                readAsyncTask.execute(url, "userID",userID, "userDate",selectDate);

                materialCalendarView.clearSelection();
            }
        });

        // RecyclerView 짧게 한번 클릭했을시 Click Event 리스너(수정)
        adapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                // 리스트에 저장된 Data값들을 ShortClickEvent.class로 전달
                // 그 값들을 Text로 붙여주기 위해서
                EventList item = adapter.getItem(pos);
                Intent intent = new Intent(getActivity(), ShortClickEvent.class);
                intent.putExtra("userID", item.getUserID());
                intent.putExtra("dateName", item.getDateName());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("userDate", item.getUserDate());
                intent.putExtra("dateMemo", item.getDateMemo());
                startActivity(intent);
            }
        });

        // RecyclerView 길게 누르고 있을 때 Click Event 리스너(삭제)
        adapter.setOnItemLongClickListener(new EventListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int pos) {
                EventList item = adapter.getItem(pos);
                final String userID = item.getUserID();
                final String dateName = item.getDateName();
                final String location = item.getLocation();
                final String userDate = item.getUserDate();
                final String dateMemo = item.getDateMemo();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                alertDialog = builder.setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            boolean success = jsonObject.getBoolean("success");
                                            if (success) {
                                                //성공했을시 새로고침
                                                onResume();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                DateDeleteRequest dateDeleteRequest = new DateDeleteRequest(
                                        userID, dateName, location, userDate, dateMemo, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(getActivity());
                                queue.add(dateDeleteRequest);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();
                alertDialog.show();
            }
        });

        return v;
    }

    // 달력에 일정이 있거나 추가 되었을 시 AsyncTask로 표시하는 부분
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        ApiSimulator(ArrayList<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            // 특정날짜 달력에 점표시해주는곳
            // 월은 0이 1월 년,일은 그대로
            // string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.size(); i ++){
                String[] time = Time_Result.get(i).split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (((AppCompatActivity)getActivity()).isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(
                    new EventDecorator(Color.RED, calendarDays, CalendarFragment.this));
        }

    }


    // 달력에 날짜를 선택했을 시 발생하는 AsyncTask
    // 일정이 있을 때 -> 수정하는 부분으로 전달
    // 일정이 없을 때 -> 등록하는 부분으로 전달
    public class ReadAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String serverUrl = (String) params[0]; // 요청보내는 url 값 저장

            String key = (String) params[1];
            String value = (String) params[2];

            String key1 = (String) params[3];
            String value1 = (String) params[4];

            String postParam = key + "=" + value + "&" + key1 + "=" + value1 ;

            try {
                URL url = new URL(serverUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParam.getBytes("UTF-8"));

                outputStream.flush();
                outputStream.close();

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // 일정이 없을 시 -> 등록
            Bundle bundle = getActivity().getIntent().getExtras();
            String logID = bundle.getString("userID");

            if (result.equals("{\"response\":[]}")) {
                Intent intent = new Intent(getActivity(), AddEventRegister.class);
                intent.putExtra("userID", logID);
                intent.putExtra("userDate", selectDate);
                startActivity(intent);
            }

            // 일정이 있을 시 -> 수정
            Intent intent = getActivity().getIntent();
            intent.putExtra("userList", result);

            try {
                JSONObject jsonObject = new JSONObject(intent.getStringExtra("userList"));
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject obj = jsonArray.getJSONObject(0);
                String readID = obj.getString("userID");
                String readName = obj.getString("dateName");
                String readLocation = obj.getString("location");
                String readMemo = obj.getString("dateMemo");

                Intent intent1 = new Intent(getActivity(), ShortClickEvent.class);
                intent1.putExtra("userID", readID);
                intent1.putExtra("dateName", readName);
                intent1.putExtra("location", readLocation);
                intent1.putExtra("userDate", selectDate);
                intent1.putExtra("dateMemo", readMemo);
                startActivity(intent1);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    // Fragment의 상단메뉴를 나타냄
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.addList:
                add();
                return true;
            case android.R.id.home:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 상단메뉴에서 추가 부분을 눌렀을 때
    private void add() {
        Intent intent1 = new Intent(getActivity(), AddEventRegister.class);
        Bundle bundle = getActivity().getIntent().getExtras();
        String logID = bundle.getString("userID");
        intent1.putExtra("userID", logID);
        startActivity(intent1);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 기존 eventList항목 초기화(일정추가할시 중복현상)
        eventList.clear();

        // 달력에 추가된 부분을 한번 초기화 한후 다시 reset
        materialCalendarView.removeDecorators();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);


        // 각 ID에 저장된 일정 리스트 들을 불러오는 AsyncTask
        class EventTask extends AsyncTask<Void, Void, String> {

            String target;

            @Override
            protected void onPreExecute() {
//                target = "https://ktw1524.cafe24.com/ReadList.php";
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
                Log.d("bcm", "result1 : " + result1);

                Intent intent = getActivity().getIntent();
                intent.putExtra("eventList", result1);

                Bundle bundle2 = new Bundle();
                bundle2.putString("eventList", result1);

                ArrayList<String> arrayList = new ArrayList<>();

                try{
                    Bundle bundle = getActivity().getIntent().getExtras();
                    String logID = bundle.getString("userID");

                    JSONObject jsonObject = new JSONObject(bundle.getString("eventList"));
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        subID = object.getString("userID");
                        dateName = object.getString("dateName");
                        location = object.getString("location");
                        userDate = object.getString("userDate");
                        dateMemo = object.getString("dateMemo");

                        if (subID.equals(logID)) {
                            EventList event = new EventList(subID, dateName, location, userDate, dateMemo);
                            eventList.add(event);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            arrayList.add(userDate);
                        }
                        count++;
                    }
                    new ApiSimulator(arrayList).executeOnExecutor(Executors.newSingleThreadExecutor());

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        EventTask eventTask = new EventTask();
        eventTask.execute();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
