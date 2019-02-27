package com.emmajerry2016.africlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Search_engine extends AppCompatActivity {
private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);
        webView=findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl("https://www.google.co.in");
    }
}
