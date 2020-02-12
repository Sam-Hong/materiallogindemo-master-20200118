package com.sourcey.materiallogindemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebQuizViewActivity extends AppCompatActivity {

    String url;
    int time_to_quiz;
    Button start_quiz;

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_quiz_view);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        time_to_quiz = Integer.parseInt(intent.getStringExtra("time_to_quiz"));

        start_quiz = (Button) findViewById(R.id.start_quiz);
        setButtonTimer(time_to_quiz);
        start_quiz.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WebQuizViewActivity.this,QuestionActivity.class);
                startActivity(intent);
            }
        });

        WebView myWebView = (WebView) findViewById(R.id.web_view);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.setWebViewClient(new WebViewClient());
//        setContentView(myWebView);
        myWebView.loadUrl(url);
    }

    public void setButtonTimer(int time){
        start_quiz.setVisibility(View.INVISIBLE);
        new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                start_quiz.setVisibility(View.VISIBLE);
            }
        }.start();
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
