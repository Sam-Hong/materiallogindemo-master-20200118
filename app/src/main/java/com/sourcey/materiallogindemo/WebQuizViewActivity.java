package com.sourcey.materiallogindemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import java.util.Timer;
import java.util.TimerTask;

public class WebQuizViewActivity extends AppCompatActivity {

    String url;
    int time_to_quiz;
    String materialId;
    Button start_quiz;
    CountDownTimer timer;
    private Timer autoLogoutTimer;

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_quiz_view);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        time_to_quiz = Integer.parseInt(intent.getStringExtra("time_to_quiz"));
        materialId=intent.getStringExtra("materialId");

        start_quiz = (Button) findViewById(R.id.start_quiz);
        setButtonTimer(time_to_quiz);
        start_quiz.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WebQuizViewActivity.this,QuestionActivity.class);
                intent.putExtra("materialId",materialId);
                startActivity(intent);
            }
        });

        final WebView myWebView = (WebView) findViewById(R.id.web_view);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        myWebView.setWebViewClient(new WebViewClient());
//        setContentView(myWebView);
        myWebView.loadUrl(url);

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("載入中,請稍後...");
        timer = new CountDownTimer(5000, 1000) {
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
    protected void onPause() {
        super.onPause();

        autoLogoutTimer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        autoLogoutTimer.schedule(logoutTimeTask, 300000); //auto logout in 5 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogoutTimer != null) {
            autoLogoutTimer.cancel();
            Log.i("Main", "cancel timer");
            autoLogoutTimer = null;
        }
    }

    private class LogOutTimerTask extends TimerTask {
        @Override
        public void run() {
            //redirect user to login screen
            Intent i = new Intent(WebQuizViewActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            GlobalVariable gv = (GlobalVariable) getApplicationContext();
            gv.setLoginToken(false);
            startActivity(i);
            finish();
        }
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
