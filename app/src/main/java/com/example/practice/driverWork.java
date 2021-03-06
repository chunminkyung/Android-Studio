package com.example.practice;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActivityChooserView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Map;

public class driverWork extends AppCompatActivity {
    private ActivityDriverWorkBinding binding;
    int i;
    int sInput;
    String input;
    private ArrayList<String>idArray;
    private ArrayList<String>dateArray;
    private ArrayList<String>statusArray;
    int year,month,dayOfMonth;
    String datestring;
    private AlertDialog dialog;

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

        //?????? ??? ?????? ?????? ??????
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM");

        //Date ?????? ?????? yyyy-MM ??????
        Date nowdate=new Date();
        String time=simpleDateFormat.format(nowdate);

        Log.e("time",String.valueOf(time));

        //token??? ????????????
        SharedPreferences pref=getSharedPreferences("mine",MODE_PRIVATE);
        String token=pref.getString("token","");
        Log.e("token",String.valueOf(token));

        AlertDialog.Builder builder=new AlertDialog.Builder(driverWork.this);
        builder.setMessage("????????? ???????????? ??????????????????");

        //?????? ??????
        builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("test","??????");
//                        Intent intent=new Intent(driverWork.this,driverWork.class);
//                        startActivity(intent);
//                        finish();
                        dialog.dismiss();
                    }
                });
        //????????? ??????
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e("test","??????");
                        dialog.dismiss();
                    }
                });

        dialog = builder.create();


        //Volley ?????? ??????
        RequestQueue requestQueue=null;

        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(this);
        }

        String url=myApplication.api_url+"driver/work?yearMonth="+ time;
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
                    //????????? ????????? 200??? ?????? ?????? ???????????? ???????????? ???????????? ??????
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse!=null && networkResponse.data!=null) {
                        String jsonerror = new String(networkResponse.data);
                        Log.e("status error", jsonerror);
                        Toast.makeText(getApplicationContext(), "?????? ", Toast.LENGTH_SHORT).show();
                    }
                }

        ){
            //token??? header??????
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
                Log.e("dayofmonth",dayOfMonth+"");

                //????????? ?????? ?????? ????????? ?????? ?????? ??? ?????? ??????
                String[] splitTime=time.split("-");
                if (input.equals("")){
                    Toast.makeText(getApplicationContext(),"????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                }else{
                    String[] splitInput=input.split("-");
                    sInput=Integer.parseInt(splitInput[2]);

                    //Calender / ?????? ?????? ?????? ?????????
                    Calendar calendar=Calendar.getInstance();
//                calendar.add(Calendar.MONTH,4);        -> ?????? ???????????? 4?????? ?????????
                    int maxDayOfMonth=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    Log.e("maxDayOfMonth",String.valueOf(maxDayOfMonth));

                    if (!splitInput[1].equals(splitTime[1])){
                        Log.e("???????????? ????????? ???",String.valueOf(splitInput[1]));
                        Toast.makeText(getApplicationContext(),"?????? ??? ??????????????? ?????? ???????????????.\n????????? ????????? ??????????????????", Toast.LENGTH_SHORT).show();
                        binding.userInput.setText("");
                        binding.workId.setVisibility(View.INVISIBLE);
                        binding.myWork.setVisibility(View.INVISIBLE);
                    }else if (sInput>maxDayOfMonth){
                        Log.e("test2",String.valueOf(sInput));
                        Toast.makeText(getApplicationContext(),"????????? ????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                        binding.userInput.setText("");
                        binding.workId.setVisibility(View.INVISIBLE);
                        binding.myWork.setVisibility(View.INVISIBLE);
                    } else if (sInput==0){
                        Toast.makeText(getApplicationContext(),"????????? ????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                        binding.userInput.setText("");
                        binding.workId.setVisibility(View.INVISIBLE);
                        binding.myWork.setVisibility(View.INVISIBLE);
                    }


                    //dateArray ???????????? ?????? ????????? ??????
                    for (int i=0; i<dateArray.size(); i++){
                        if (input.equals(dateArray.get(i))){
                            //????????? ?????? date??? ?????? ?????? ?????? ??? ?????? ?????? ??????
                            Log.e("date", "?????? ??? ?????????!");
                            if (statusArray.get(i).contains("WORK")){
                                binding.myWork.setText("????????? ?????????.");
                                binding.myWork.setTextColor(Color.BLACK);
                            }else if (statusArray.get(i).contains("LEAVE")){
                                binding.myWork.setText("????????? ?????????.");
                                binding.myWork.setTextColor(Color.RED);
                            }else if (statusArray.get(i).contains("ANNUAL")) {
                                binding.myWork.setText("???????????????.");
                                binding.myWork.setTextColor(Color.BLUE);
                            }
                            binding.workId.setText("??????ID : "+idArray.get(i));
                            binding.workId.setVisibility(View.VISIBLE);
                            binding.myWork.setVisibility(View.VISIBLE);
                            break;
                        }else{
                            binding.myWork.setText("?????? ?????? ????????? ?????????.");
                            binding.myWork.setTextColor(Color.GRAY);
                            binding.workId.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                //String??? splitInput??? dayOfMont??? integer???????????? ?????????????????? ????????? ????????????.

            }
        });

        //?????? ???????????????
        Calendar calendar=new GregorianCalendar();
        //Calendar calendar = Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener, year, month, dayOfMonth);
        //year, month, dayofMonth??? ????????? ???, DatePickerDialog??? default??? ????????????????????? ????????? set ??????.

        binding.movePage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        
    }//end of onCrete


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            datestring=String.format("%d-%02d-%02d",year,(monthOfYear+1),dayOfMonth);
            Toast.makeText(getApplicationContext(), datestring, Toast.LENGTH_SHORT).show();
            Log.e("dateString",datestring);
            //dialogshow ??????
            Boolean dialogshow = true;
            for (int i = 0; i<dateArray.size(); i++) {
                //???????????? ????????? ????????? ?????? ???????????? ????????? ????????????
                if (datestring.equals(dateArray.get(i))) {
                    //dialogshow ??????X
                    dialogshow = false;
                    Log.e("date", "?????? ??? ?????????!");
                    if (statusArray.get(i).contains("WORK")) {
                        binding.myWork.setText("????????? ?????????.");
                        binding.myWork.setTextColor(Color.BLACK);
                    } else if (statusArray.get(i).contains("LEAVE")) {
                        binding.myWork.setText("????????? ?????????.");
                        binding.myWork.setTextColor(Color.RED);
                    } else if (statusArray.get(i).contains("ANNUAL")) {
                        binding.myWork.setText("???????????????.");
                        binding.myWork.setTextColor(Color.BLUE);
                    }
                    binding.workId.setText("??????ID : " + idArray.get(i));
                    binding.workId.setVisibility(View.VISIBLE);
                    binding.myWork.setVisibility(View.VISIBLE);
                    break;
                } else {
                    //dialogshow ??????
                    dialogshow = true;
                }
            }

            if (dialogshow) {
                dialog.show();
                binding.myWork.setVisibility(View.INVISIBLE);
                binding.workId.setVisibility(View.INVISIBLE);
                Log.e("test","else");
            }
        }
    };
    //?????? ?????? ??????
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}