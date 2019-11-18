package com.emmajerry2016.africlite;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ScreenSaver extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);

        new Handler().postDelayed(new Runnable(){
            @Override
               public void run(){ startActivity(new Intent(getApplicationContext(),MainActivity.class));
               getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               finish();
            }
        },1*1000);
    }
}
