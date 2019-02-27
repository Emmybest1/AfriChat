package com.emmajerry2016.africlite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Developer extends AppCompatActivity {
private Button      button;
private TextView textView2;
private TextView textView4;
private TextView textView5;
private TextView textView8;
private TextView textView;
private TextView textView9;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        textView=findViewById(R.id.textView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView2=findViewById(R.id.textView2);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());

        textView4=findViewById(R.id.textView4);
        textView4.setMovementMethod(LinkMovementMethod.getInstance());

        textView5=findViewById(R.id.textView5);
        textView5.setMovementMethod(LinkMovementMethod.getInstance());

        textView9=findViewById(R.id.textView9);
        textView9.setMovementMethod(LinkMovementMethod.getInstance());

        textView8=findViewById(R.id.textView8);
        textView8.setText(Html.fromHtml("<a href=\"mailto:emmajerry2016@gmail.com\">...</a>"));
        //textView8.setText(Html.fromHtml("<a href=\"mailto:emmajerry2016@gmail.com\">....</a>"));
       // textView.setText(Html.fromHtml("<a href=\"mailto:emmajerry2016@gmail.com\">send feedback</a>"));
        textView8.setMovementMethod(LinkMovementMethod.getInstance());


        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }
        public void openMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    }
