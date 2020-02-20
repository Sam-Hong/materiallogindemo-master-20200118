package com.sourcey.materiallogindemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewActivity extends AppCompatActivity {

    String url;
    CountDownTimer timer;
    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        final WebView myWebView = (WebView) findViewById(R.id.web_view);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(url);
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Loading please wait...");
        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.e("countdown", "onTick: " + millisUntilFinished / 1000 );
                loading.show();
                if (myWebView.getContentHeight() != 0) {
                    timer.cancel();
                    loading.dismiss();
                }
            }

            @Override
            public void onFinish() {
                if (myWebView.getContentHeight() == 0) {
                    //Log.e("reloading", "onFinish: " + url);
                    myWebView.loadUrl(url);
                    timer.start();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if (id == R.id.logout) {
            GlobalVariable gv = (GlobalVariable) getApplicationContext();
            gv.setLoginToken(false);
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
