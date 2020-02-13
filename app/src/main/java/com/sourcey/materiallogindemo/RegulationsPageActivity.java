package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

public class RegulationsPageActivity extends AppCompatActivity {

    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    String name;
    String[] history;
    ArrayList<String> firstView = new ArrayList<String>();
    ArrayList<String> files = new ArrayList<String>();
    ArrayList<String> url = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulations_page);
        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
        if (data != null) {
            name = data.getString("name");
            history = data.getStringArray("history");
            files = data.getStringArrayList("files");
        }
        else
            Toast.makeText(getBaseContext(), "No data", Toast.LENGTH_LONG).show();
        TextView title = findViewById(R.id.Title);
        title.setText(name);

        if (files != null)
        {
            for (int x = 0; x < files.size(); x++) {
                if (files.get(x).contains(".pdf")){
                    firstView.add(files.get(x));
                    url.add("http://docs.google.com/gview?embedded=true&url=http://www.cga.gov.tw" + files.get(x + 1));
                }
            }
            if (url.isEmpty())
            {
                for (int x = 0; x < history.length; x++)
                {
                    if (history[x].contains(".pdf"))
                        url.add("http://docs.google.com/gview?embedded=true&url=" + history[x]);
                    if (history[x].contains("(pdf)"))
                        firstView.add("海岸巡防機關執行臺灣地區漁港及遊艇港安全檢查作業規定(pdf)");
                }
            }
        }
        else
            firstView.add("none");

        ListView download = findViewById(R.id.Download);
        WordAdapter downAdapter = new WordAdapter(firstView);
        download.setAdapter(downAdapter);
        download.setOnItemClickListener(onClickListView);
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(RegulationsPageActivity.this, WebViewActivity.class);
            intent.putExtra("url", url.get(position));
            startActivity(intent);
        }
    };

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
