package com.example.andrei.igestrecognito.tutorial;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.andrei.igestrecognito.R;
import com.example.andrei.igestrecognito.tutorial.CustomSwipeAdapter;

public class TutorialActivity extends AppCompatActivity {


    ViewPager viewPager;
    CustomSwipeAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_background);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new CustomSwipeAdapter(this);
        viewPager.setAdapter(adapter);

    }
}