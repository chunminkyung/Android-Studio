package com.example.practice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
    private long backBntTime=0;
    SharedPreferences.Editor editor;

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
        editor=pref.edit();

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

        String url=myApplication.api_url+"user-info";
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


        binding.logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("로그아웃을 진행하시겠습니까?");

                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int whichButton) {
                                Intent i=new Intent(MainActivity.this,login.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(),"로그아웃",Toast.LENGTH_SHORT).show();
                                editor.putBoolean("autoLogin",false).apply();
                                }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .show();
            }
        });

        binding.mySchedule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,CheckSchedule.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(),"이동",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onBackPressed(){
        long curTime=System.currentTimeMillis();
        long gapTime=curTime-backBntTime;

        if (0<=gapTime&&2000>=gapTime){
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }else{
            backBntTime=curTime;
            Toast.makeText(this,"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
        return;
    }
}