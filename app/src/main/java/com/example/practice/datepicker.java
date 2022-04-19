package com.example.practice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.practice.databinding.ActivityDatepickerBinding;
import com.example.practice.databinding.ActivityLoginBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class datepicker extends AppCompatActivity {
    private ActivityDatepickerBinding binding;
    private int year, month, day;

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

        binding.datePicker.init(year,month,day,mOnClick());

    }

    private DatePicker.OnDateChangedListener mOnClick() {
        Intent intent=new Intent();
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        setResult(RESULT_OK,intent);
        finish();
        return null;
    }

    DatePicker.OnDateChangedListener mOnDateChangedListener=new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int yy, int mm, int dd) {
            year=yy;
            month=mm;
            day=dd;
        }
    };
}