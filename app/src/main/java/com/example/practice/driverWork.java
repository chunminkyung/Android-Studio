package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class driverWork extends AppCompatActivity {
    private ActivityDriverWorkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDriverWorkBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //날짜 및 시간 형식 지정
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");

        //Date 객체 사용 yyyy-MM 출력
        Date date=new Date();
        String time=simpleDateFormat.format(date);

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


    }
}