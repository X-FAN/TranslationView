package com.xf.translationview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by X-FAN on 2017/6/1.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.top).setOnClickListener(this);
        findViewById(R.id.bottom).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.end).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.top:
                intent.setClass(MainActivity.this, TopActivity.class);
                break;
            case R.id.bottom:
                intent.setClass(MainActivity.this, BottomActivity.class);
                break;
            case R.id.start:
                intent.setClass(MainActivity.this, StartActivity.class);
                break;
            case R.id.end:
                intent.setClass(MainActivity.this, EndActivity.class);
                break;
        }
        startActivity(intent);
    }
}
