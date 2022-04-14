package com.example.practice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.practice.databinding.ActivityMainBinding;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences pref=getSharedPreferences("mine",MODE_PRIVATE);
        String name = pref.getString("name", "");
        String companyName=pref.getString("companyName","");
        String role=pref.getString("role","");
        String token=pref.getString("token","");

        Log.e("name",String.valueOf(name));
        Log.e("companyName",String.valueOf(companyName));
        Log.e("role",String.valueOf(role));

        binding.name.setText(name);
        binding.companyName.setText(companyName);
        binding.role.setText(role);

        if (role.equals("ROLE_DRIVER")){
            binding.IF.setText("승무원 계정입니다.");
        }else if (role.equals("ROLE_ADMIN")&&role.equals("ROLE_MANAGER")){
            binding.IF.setText("관리자 계정입니다.");
        }

        Button button=findViewById(R.id.bnt_workSchedule);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_workSchedule=new Intent(MainActivity.this, driverWork.class);
                startActivity(intent_workSchedule);
                finish();
            }
        });


        //API 불러오기 부분
        RequestQueue requestQueue=null;

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(this);
        }

        String url="http:/dev.kiki-bus.com/user-info";
        Log.e("url",url);


        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.e("response",response+"");
                },
                error -> {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse!=null && networkResponse.data!=null) {
                        String jsonerror = new String(networkResponse.data);
                        Log.e("status error", jsonerror);
                    }
                }
        ){
            //token값 불러오기
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