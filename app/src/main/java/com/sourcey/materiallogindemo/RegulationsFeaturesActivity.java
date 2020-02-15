package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.TRANSPARENT;

public class RegulationsFeaturesActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();
    ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
    int lawClass, lawType, page;
    String pageTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        page = 1;
        Intent intent = getIntent();
        try {
            lawClass = Integer.parseInt(intent.getStringExtra("lawClass"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.regulations_layout2);

        ToggleButton affair = findViewById(R.id.toggleButton);
        ToggleButton org = findViewById(R.id.toggleButton2);
        ToggleButton deal = findViewById(R.id.toggleButton3);

        if (lawClass == 2) {
            deal.setVisibility(View.GONE);
        }
        else if (lawClass == 3) {
            deal.setOnCheckedChangeListener(this);
        }
        else {

            deal.setVisibility(View.GONE);
            org.setVisibility(View.GONE);
            affair.setVisibility(View.GONE);
        }

        if (lawClass == 4)
            lawType = 0;
        else {
            affair.setOnCheckedChangeListener(this);
            org.setOnCheckedChangeListener(this);

            affair.setEnabled(false);
            lawType = 1;
        }

        PostHttpRequest();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        ToggleButton deal = findViewById(R.id.toggleButton3);
        ToggleButton affair = findViewById(R.id.toggleButton);
        ToggleButton org = findViewById(R.id.toggleButton2);

        switch (buttonView.getId()){
            case R.id.toggleButton:
                if (affair.isChecked()) {
                    affair.setTextColor(Color.WHITE);
                    affair.setBackgroundColor(getResources().getColor(R.color.primary));
                    affair.setEnabled(false);

                    org.setTextColor(Color.BLACK);
                    org.setBackgroundColor(TRANSPARENT);
                    org.setEnabled(true);
                    org.setChecked(false);

                    if (lawClass == 3) {
                        deal.setTextColor(Color.BLACK);
                        deal.setBackgroundColor(TRANSPARENT);
                        deal.setEnabled(true);
                        deal.setChecked(false);
                    }

                    idList.clear();
                    nameList.clear();
                    urlList.clear();
                    dataList.clear();
                    lawType = 1;
                    PostHttpRequest();
                }
                break;

            case R.id.toggleButton2:
                if (org.isChecked()){

                    org.setTextColor(Color.WHITE);
                    org.setBackgroundColor(getResources().getColor(R.color.primary));
                    org.setEnabled(false);

                    affair.setTextColor(Color.BLACK);
                    affair.setBackgroundColor(TRANSPARENT);
                    affair.setEnabled(true);
                    affair.setChecked(false);

                    if (lawClass == 3) {
                        deal.setTextColor(Color.BLACK);
                        deal.setBackgroundColor(TRANSPARENT);
                        deal.setEnabled(true);
                        deal.setChecked(false);
                    }

                    idList.clear();
                    nameList.clear();
                    urlList.clear();
                    dataList.clear();
                    lawType = 2;
                    PostHttpRequest();
                }
                break;

            case R.id.toggleButton3:
                if (deal.isChecked()){
                    deal.setTextColor(Color.WHITE);
                    deal.setBackgroundColor(getResources().getColor(R.color.primary));
                    deal.setEnabled(false);

                    affair.setBackgroundColor(TRANSPARENT);
                    affair.setTextColor(Color.BLACK);
                    affair.setEnabled(true);
                    affair.setChecked(false);

                    org.setBackgroundColor(TRANSPARENT);
                    org.setTextColor(Color.BLACK);
                    org.setEnabled(true);
                    org.setChecked(false);

                    idList.clear();
                    nameList.clear();
                    urlList.clear();
                    dataList.clear();
                    lawType = 3;
                    PostHttpRequest();
                }
                break;

            default:
                break;
        }
    }

    public View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button next = findViewById(R.id.nextPage);
            Button prev = findViewById(R.id.prevPage);
            int total = Integer.parseInt(pageTotal);

            switch (v.getId()) {
                case R.id.nextPage:
                    if (page < total)
                    {
                        if (prev.getVisibility() == View.INVISIBLE)
                        {
                            prev.setVisibility(View.VISIBLE);
                            prev.setEnabled(true);
                        }
                        page++;
                        idList.clear();
                        nameList.clear();
                        urlList.clear();
                        dataList.clear();

                        PostHttpRequest();
                    }
                    break;

                case R.id.prevPage:
                    if (page > 1)
                    {
                        if (next.getVisibility() == View.INVISIBLE)
                        {
                            next.setVisibility(View.VISIBLE);
                            next.setEnabled(true);
                        }
                        page--;
                        idList.clear();
                        nameList.clear();
                        urlList.clear();
                        dataList.clear();

                        PostHttpRequest();
                    }
                    break;

                    default:
                        break;
            }
        }
    };

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(RegulationsFeaturesActivity.this, RegulationsPageActivity.class);
            JSONObject temp = dataList.get(position);

            ArrayList<String> files = new ArrayList<String>();
            String[] history = null;

            Bundle data = new Bundle();
            try {
                if (temp.has("postHistory"))
                    history = temp.getString("postHistory").split("\"");
                if (temp.has("files")) {
                    JSONArray filesList = temp.getJSONArray("files");
                    for (int i = 0; i < filesList.length(); i++) {
                        JSONObject rowData = filesList.getJSONObject(i);
                        files.add(rowData.getString("fileName"));
                        files.add(rowData.getString("filePath"));
                    }
                }
                else
                    files.add("none");
                data.putString("name", temp.getString("lawName"));
                data.putStringArray("history", history);
                data.putStringArrayList("files" , files);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("data", data);
            startActivity(intent);
        }
    };

    private void PostHttpRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("lawClass", lawClass);
            json.put("lawType", lawType);
            json.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.regulations_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                            if (response.getString("data").length() > 0) {
                                try {
                                    JSONObject data = response.getJSONObject("data");
                                    pageTotal = (data.get("totalPage").toString());
                                    JSONArray lawArray = data.getJSONArray("laws");
                                    for (int i = 0; i < lawArray.length(); i++) {
                                        JSONObject jsonObject = lawArray.getJSONObject(i);
                                        String id = jsonObject.getString("uuid");
                                        String name = jsonObject.getString("lawName");
                                        dataList.add(lawArray.getJSONObject(i));
                                        idList.add(id);
                                        nameList.add(name);
                                        urlList.add(Integer.toString(i));
                                    }
                                    //找到ListView
                                    ListView list = findViewById(R.id.listview);
                                    //建立Adapter，並將要顯示的結果陣列傳入
                                    WordAdapter adapter = new WordAdapter(nameList);
                                    //將Adapter設定給ListView
                                    list.setAdapter(adapter);
                                    list.setOnItemClickListener(onClickListView);
                                    Button next = findViewById(R.id.nextPage);
                                    Button prev = findViewById(R.id.prevPage);
                                    TextView pageNumber = findViewById(R.id.pageNumber);

                                    if (pageTotal.equals("1"))
                                    {
                                        prev.setVisibility(View.INVISIBLE);
                                        prev.setEnabled(false);
                                        next.setVisibility(View.INVISIBLE);
                                        next.setEnabled(false);
                                    }
                                    else if (page == 1){
                                        prev.setVisibility(View.INVISIBLE);
                                        prev.setEnabled(false);
                                        next.setEnabled(true);
                                        pageNumber.setVisibility(View.VISIBLE);
                                    }
                                    else if (pageTotal.equals(Integer.toString(page)))
                                    {
                                        next.setVisibility(View.INVISIBLE);
                                        next.setEnabled(false);
                                        prev.setEnabled(true);
                                        pageNumber.setVisibility(View.VISIBLE);
                                    }

                                    prev.setOnClickListener(onButtonClick);
                                    next.setOnClickListener(onButtonClick);



                                    String temp = page + "/" + pageTotal;
                                    pageNumber.setText(temp);

                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "response error", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "No return data", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                serverResp.setText("Error getting response");
            }
        }) {
            /* Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() {
                GlobalVariable gv = (GlobalVariable) getApplicationContext();
                authorization = gv.getAuthorization();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", authorization);
                return headers;
            }
        };
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);
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
