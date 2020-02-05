package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private boolean login_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        login_token = gv.getLoginToken();
        if (login_token != true) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void regulations(View view) {
    }

    public void bulletin(View view) {
        Intent intent=new Intent(MainActivity.this,BulletinActivity.class);
        startActivity(intent);
    }

    public void statistics(View view) {
    }

    public void servicejob(View view) {
        Intent intent=new Intent(MainActivity.this,ServicejobActivity.class);
        startActivity(intent);
    }

    public void specialship(View view) {
    }

    public void learning(View view) {
        Intent intent=new Intent(MainActivity.this,LearningstageActivity.class);
        startActivity(intent);
    }

    public void commonship(View view) {
        Intent intent=new Intent(MainActivity.this,CommonshipActivity.class);
        startActivity(intent);
    }

    public void reset(View view) {
        Intent intent=new Intent(MainActivity.this,ResetActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            GlobalVariable gv = (GlobalVariable) getApplicationContext();
            gv.setLoginToken(false);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

}
