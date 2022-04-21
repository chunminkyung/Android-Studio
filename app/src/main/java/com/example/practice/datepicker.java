package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice.databinding.ActivityDatepickerBinding;
import com.example.practice.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class datepicker extends AppCompatActivity {
    private ActivityDatepickerBinding binding;
    int year, month, day;
    int i;
    private ArrayList<String> idArray;
    private ArrayList<String>dateArray;
    private ArrayList<String>statusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDatepickerBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        Calendar calendar=new GregorianCalendar();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);

        //ArrayList
        idArray=new ArrayList<>();
        dateArray=new ArrayList<>();
        statusArray=new ArrayList<>();

        //날짜 및 시간 형식 지정
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");

        //Date 객체 사용 yyyy-MM 출력
        Date nowdate=new Date();
        String time=simpleDateFormat.format(nowdate);

        //token값 불러오기
        SharedPreferences pref=getSharedPreferences("mine",MODE_PRIVATE);
        String token=pref.getString("token","");

        //Volley 통신 사용
        RequestQueue requestQueue=null;

        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(this);
        }

        String url="http://dev.kiki-bus.com/driver/work?yearMonth="+ time;
        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    //Log.e("response",response+"");
                    try {
                        JSONArray object=response.getJSONArray("object");
                        //Log.e("object",String.valueOf(object));

                        for (i=0; i<object.length(); i++){
                            JSONObject jsonObject= object.getJSONObject(i);
                            String id=jsonObject.getString("id");
                            String date=jsonObject.getString("date");
                            String status=jsonObject.getString("status");

                            idArray.add(id);
                            dateArray.add(date);
                            statusArray.add(status);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    //서버의 응답이 200이 아닌 모든 통신오류 상황일시 실행되는 코드
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse!=null && networkResponse.data!=null) {
                        String jsonerror = new String(networkResponse.data);
                        Log.e("status error", jsonerror);
                        Toast.makeText(getApplicationContext(), "실패 ", Toast.LENGTH_SHORT).show();
                    }
                }

        ){
            //token값 header추가
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer "+ token);
                return headers;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);

        binding.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDatePickerResult();

                //현재 월의 말일 구하기
                Calendar calendar=Calendar.getInstance();
                int dayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                //dateArray 사이즈에 따라 반복문 실행
/*                for (int i=0; i<dateArray.size(); i++){
                    if (dateMessage.equals(dateArray.get(i))){
                        //입력한 값과 date에 있는 값과 같을 시 해당 날짜 출력
                        Log.e("date", "나의 월 근무일!");
                        if (statusArray.get(i).contains("WORK")){
                            binding.date.setText("근무일 입니다.");
                            binding.date.setTextColor(Color.BLACK);
                        }else if (statusArray.get(i).contains("LEAVE")){
                            binding.date.setText("휴무일 입니다.");
                            binding.date.setTextColor(Color.RED);
                        }else if (statusArray.get(i).contains("ANNUAL")) {
                            binding.date.setText("연차입니다.");
                            binding.date.setTextColor(Color.BLUE);
                        }
                        binding.workingId.setText("근무ID : "+idArray.get(i));
                        break;
                    }else{
                        binding.workingId.setText("다른 조의 근무일 입니다.");
                        binding.workingId.setTextColor(Color.GRAY);
                    }
                }*/
            }
        });

        int a = 1;
        String b = "2";
        Log.e("int 더하기 string?", a+b);

    }//end of onCreate


    //선택한 날짜 출력문
    private void processDatePickerResult() {
        String month_string=Integer.toString(month+1);
        String day_string=Integer.toString(day);
        String year_string=Integer.toString(year);
        String dateMessage=(year_string+"-"+month_string+"-"+day_string);
        Toast.makeText(this,"선택날짜 : "+dateMessage,Toast.LENGTH_SHORT).show();
    }


}