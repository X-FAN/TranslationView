package com.xf.translationview;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.xf.tranlationview.TranslationView;


/**
 * Created by X-FAN on 2017/6/1.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final TranslationView translationView = (TranslationView) findViewById(R.id.translation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TranslationView");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.blue:
                        translationView.setShadowColor(ActivityCompat.getColor(StartActivity.this, R.color.blue));
                        break;
                    case R.id.red:
                        translationView.setShadowColor(ActivityCompat.getColor(StartActivity.this, R.color.red));
                        break;
                }
                return true;
            }
        });
        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translationView.show();
            }
        });
        findViewById(R.id.hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translationView.hide();
            }
        });
    }
}
