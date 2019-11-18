package com.emmajerry2016.africlite;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class TransferChat extends AppCompatActivity {
    private Button    button2;
    private EditText editText;
    private EditText editText2;
    private TextView powered;
    private Toolbar transferChatToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_chat);

        transferChatToolbar=findViewById(R.id.transferChat_toolbar);
        setSupportActionBar(transferChatToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Transfer Chat");


        powered=findViewById(R.id.powered);
        powered.setMovementMethod(LinkMovementMethod.getInstance());

        editText=findViewById(R.id.editText);
        editText2=findViewById(R.id.editText2);

        button2=findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String textMessage=editText2.getText().toString();
                //An sms configuration
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"+editText.getText().toString()));
                intent.putExtra("sms_body",editText2.getText().toString());
                if(textMessage.isEmpty()){
                    Toast.makeText(TransferChat.this,"Kindly insert your chat",Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(intent);
                    Toast.makeText(TransferChat.this,"Chat is about to be transisted now", Toast.LENGTH_SHORT).show();
                }

            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.setError("Only for phone no:");
            }
        });
    }
}

