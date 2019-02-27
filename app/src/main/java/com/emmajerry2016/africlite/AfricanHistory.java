package com.emmajerry2016.africlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AfricanHistory extends AppCompatActivity {
private TextView linkToAfricanHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_african_history);
        linkToAfricanHistory=findViewById(R.id.link_to_africanHistory);
        linkToAfricanHistory.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
