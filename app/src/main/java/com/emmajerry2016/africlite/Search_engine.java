package com.emmajerry2016.africlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class Search_engine extends AppCompatActivity {
private WebView webView;
private Toolbar searchEngineToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_engine);

        searchEngineToolbar=findViewById(R.id.searchEngine_toolbar);
        setSupportActionBar(searchEngineToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);//back button
        getSupportActionBar().setTitle("Search engine AfricLite");

        webView=findViewById(R.id.webView);
        /* String mUrl = "https://www.google.com/";
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl(mUrl);
        webView.startViewTransition(mUrl);
        */

                 /***Loading the google chrome***/
          webView.getSettings().setJavaScriptEnabled(true);
                      // Set WebView client
          webView.setWebChromeClient(new WebChromeClient());

          webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
                     // Load the webpage
           webView.loadUrl("http://google.com/");

    }
}
