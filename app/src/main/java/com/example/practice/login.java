package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.practice.databinding.ActivityLoginBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String ID,PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button 기능
        //1. 사용자가 입력한 값을 string 변수에 저장 (아이디, 비밀번호 둘 다)
        //2. 버튼을 눌렀을때 서버와 통신 시작! --> 버튼을 눌렀을 때 로그인 과정이 시작되기 때문

        //Map , k -> key v -> value
//        Map<String, String> map = new HashMap<>();
//        map.put("이름", "민경");
//        map.put("성별", "여자");
//        map.put("직업", "개발자");
//
//        Log.e("map 결과", map.toString());

        //1단계
        //버튼을 눌렀을때, 사용자가 입력한 값 (아이디, 비밀번호)를 로그에 띄우기
        Button button=findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ID=binding.etId.getText().toString();
                 PW=binding.etPw.getText().toString();
               Log.e("login","ID : "+ID+"   PW : "+PW);

               loginProcess();
            }
        });
    }//

    public void loginProcess(){
        RequestQueue requestQueue=null;

        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(this);
        }

        String url="http://dev.kiki-bus.com/auth/login";
        Log.e("url",url);

        Map<String,String> map = new HashMap<>();
        map.put("loginId",ID);
        map.put("password",PW);

        JSONObject parameter=new JSONObject(map);

        Log.e("p",parameter+"");

        JsonObjectRequest request=new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameter,
                response -> {
                    //서버에서 성공적으로 값을 전달받고 답을 줄때 (RESPONSE가 돌아올때)
                    //서버의 status code가 200 http status code

                    //조건 1
                    //로그인이 성공했을때만 화면이 이동되어야한다 (INTENT 사용)
                    Log.e("response",response+"");

                    try {
                        JSONObject object = response.getJSONObject("object");

                        String name = object.getString("name");
                        String token=object.getString("token");
                        String role=object.getString("role");
                        String companyName=object.getString("companyName");

                        Log.e("name", name);
                        Log.e("token",token);
                        Log.e("role",role);
                        Log.e("companyName",companyName);

//                        int status=response.getInt("status");
//                        Log.e("status",String.valueOf(status));

                        //sharedpreference 설명
                        SharedPreferences pref= getSharedPreferences("mine",MODE_PRIVATE);
                        //위 코드의 NAME은 한 sharedpreference의 이름
                        SharedPreferences.Editor editor=pref.edit();
                        //sharedpreferences.editor는 sharedpreference에 값을 입력하기위해 사용하는 코드
                        editor.putString("name", "minkyung").apply();

                        editor.putString("name",name).apply();
                        editor.putString("token",token).apply();
                        editor.putString("role",role).apply();
                        editor.putString("companyName",companyName).apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Intent intent_main = new Intent(login.this, MainActivity.class);
                    //만약에 이동할 화면으로 전달해야하는 값이 있다면 putExtra 를 사용
                    startActivity(intent_main);
                    finish();
                    Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                },
                error -> {
                    //서버의 응답이 200이 아닌 모든 통신오류 상황일시 실행되는 코드
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse!=null && networkResponse.data!=null) {
                            String jsonerror = new String(networkResponse.data);
                            Log.e("status error", jsonerror);
                            Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다. ", Toast.LENGTH_SHORT).show();
                    }
                    //조건 2
                    //로그인이 실패했을때 토스트 메시지 (로그인 실패) -> 화면 이동이 불가해야함
                }

        );
        request.setShouldCache(false);
        requestQueue.add(request);

    }
}