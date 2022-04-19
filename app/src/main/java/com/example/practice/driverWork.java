package com.example.practice;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
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

//        Button button=findViewById(R.id.bnt_work);
        binding.bntWork.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                input=binding.userInput.getText().toString();
                Log.e("userInput",String.valueOf(input));
                //binding.myWork.setText(input);

                //사용자 입력 값이 달력에 없는 값일 때 사용 변수
                String[] splitInput=input.split("-");
                String[] splitTime=time.split("-");
                //String인 splitInput을 dayOfMont의 integer형식으로 변환해주어야 비교가 가능하다.
                int sInput=Integer.parseInt(splitInput[2]);

                //Calender / 현재 월의 말일 구하기
                Calendar calendar=Calendar.getInstance();
//                calendar.add(Calendar.MONTH,4);        -> 현재 날짜에서 4달을 더해라
                int dayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                Log.e("dayOfMonth",String.valueOf(dayOfMonth));

                //dateArray 사이즈에 따라 반복문 실행
                for (int i=0; i<dateArray.size(); i++){
                    if (input.equals(dateArray.get(i))){
                        //입력한 값과 date에 있는 값과 같을 시 해당 날짜 출력
                        Log.e("date", "나의 월 근무일!");
                        if (statusArray.get(i).contains("WORK")){
                            binding.myWork.setText("근무일 입니다.");
                            binding.myWork.setTextColor(Color.BLACK);
                        }else if (statusArray.get(i).contains("LEAVE")){
                            binding.myWork.setText("휴무일 입니다.");
                            binding.myWork.setTextColor(Color.RED);
                        }else if (statusArray.get(i).contains("ANNUAL")) {
                            binding.myWork.setText("연차입니다.");
                            binding.myWork.setTextColor(Color.BLUE);
                        }
                        binding.workId.setText("근무ID : "+idArray.get(i));
                        break;
                    }else{
                        binding.myWork.setText("다른 조의 근무일 입니다.");
                        binding.myWork.setTextColor(Color.GRAY);
                    }
                }
                if (!splitInput[1].equals(splitTime[1])){
                    Log.e("test",String.valueOf(splitInput[1]));
                    Toast.makeText(getApplicationContext(),"이번 달 근무일정만 확인 가능합니다.\n정확한 일자를 입력해주세요", Toast.LENGTH_SHORT).show();
                    binding.userInput.setText(null);
                }else if (sInput>=dayOfMonth){
                    Log.e("test2",String.valueOf(splitInput[2]));
                    Toast.makeText(getApplicationContext(),"정확한 일자를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    binding.userInput.setText(null);
                }else if (sInput==0 && sInput==00){
                    Toast.makeText(getApplicationContext(),"정확한 일자를 입력해주세요.",Toast.LENGTH_SHORT).show();
                    binding.userInput.setText(null);
                }
            }
        });

        binding.movePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_move=new Intent(driverWork.this,datepicker.class);
                startActivity(intent_move);
                finish();
                Toast.makeText(getApplicationContext(),"이동",Toast.LENGTH_SHORT).show();
            }
        });
    }
}