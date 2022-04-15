package com.example.practice;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice.databinding.ActivityDriverWorkBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;

public class driverWork extends AppCompatActivity {
    private ActivityDriverWorkBinding binding;
    int i;
    String input;
    private ArrayList<String>idArray;
    private ArrayList<String>dateArray;
    private ArrayList<String>statusArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDriverWorkBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //ArrayList
        idArray=new ArrayList<>();
        dateArray=new ArrayList<>();
        statusArray=new ArrayList<>();

        //날짜 및 시간 형식 지정
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");

        //Date 객체 사용 yyyy-MM 출력
        Date nowdate=new Date();
        String time=simpleDateFormat.format(nowdate);

        Log.e("time",String.valueOf(time));

        //token값 불러오기
        SharedPreferences pref=getSharedPreferences("mine",MODE_PRIVATE);
        String token=pref.getString("token","");
        Log.e("token",String.valueOf(token));

        //Volley 통신 사용
        RequestQueue requestQueue=null;

        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(this);
        }

        String url="http://dev.kiki-bus.com/driver/work?yearMonth="+ time;
        Log.e("url",url);
        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.e("response",response+"");
                    try {
                        JSONArray object=response.getJSONArray("object");
                        Log.e("object",String.valueOf(object));

                        for (i=0; i<object.length(); i++){
                            JSONObject jsonObject= object.getJSONObject(i);
                            String id=jsonObject.getString("id");
                            String date=jsonObject.getString("date");
                            String status=jsonObject.getString("status");

                            idArray.add(id);
                            dateArray.add(date);
                            statusArray.add(status);
                        }
                        Log.e("id",String.valueOf(idArray));
                        Log.e("date",String.valueOf(dateArray));
                        Log.e("status",String.valueOf(statusArray));
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
        Button button=findViewById(R.id.bnt_work);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                input=binding.userInput.getText().toString();
                Log.e("userInput",String.valueOf(input));
                //binding.driverWorkText.setText(input);

                //dateArray 사이즈에 따라 반복문 실행
                for (i=0; i<dateArray.size(); i++){
                    if (input.equals(dateArray.get(i))){
                        //입력한 값과 date에 있는 값과 같을 시 해당 날짜 출력
                        binding.workCheck.setText(dateArray.get(i));
                    }
                    //근무 정보 for문
                    for (i=0; i<statusArray.size(); i++){
                        if (statusArray.contains("WORK")){
                            binding.myWork.setText("근무일 입니다.");
                        }else if (statusArray.contains("LEAVE")){
                            binding.myWork.setText("휴무일 입니다.");
                        }else if (statusArray.contains("ANNUAL")) {
                            binding.myWork.setText("연차입니다.");
                        }else{
                            binding.myWork.setText("다른 조의 근무일 입니다.");
                        }
                    }
                }
            }
        });

    }
}