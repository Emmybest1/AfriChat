package com.emmajerry2016.africlite;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TransferChatActivity extends AppCompatActivity {
    private Button    button2;
    private EditText editText;
    private EditText editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_chat);

        editText=findViewById(R.id.editText);
        editText2=findViewById(R.id.editText2);

        button2=findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //An sms configuration
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"+editText.getText().toString()));
                intent.putExtra("sms_body",editText2.getText().toString());
                startActivity(intent);
                Toast.makeText(TransferChatActivity.this,"Message transfered successfully",Toast.LENGTH_SHORT).show();
            }
        });

    }
    }

