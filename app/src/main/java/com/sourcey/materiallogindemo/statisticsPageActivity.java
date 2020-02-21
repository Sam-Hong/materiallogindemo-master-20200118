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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class statisticsPageActivity extends AppCompatActivity {

    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<String> urlList = new ArrayList<String>();
    private ArrayList<ArrayList<String>> listOfFiles = new ArrayList<>();
    int balance = 0;

    String parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        Intent intent = getIntent();
        parentId = intent.getStringExtra("id");

        PostHttpRequest(); //將Data寫入listview的程式碼放在此函數內，否則會有Callback時間差的問題，原因在於listener
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(urlList.get(position).equals("more files")) {
                Bundle data = new Bundle();
                String[] extra = new String[1];
                extra[0] = "none";
                data.putString("name", nameList.get(position));
                sortFiles(listOfFiles.get(position - balance));
                data.putStringArrayList("files", listOfFiles.get(position - balance));
                data.putStringArray("history", extra);
                Intent intent = new Intent(statisticsPageActivity.this, RegulationsPageActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
            }
            else if (!urlList.get(position).equals("none"))
            {
                Intent intent = new Intent(statisticsPageActivity.this, WebViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                startActivity(intent);
            }
        }
    };

    private void PostHttpRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", parentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.statistics_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                            // Log.e("here", response.toString());
                            if (response.getString("data").length() > 0) {
                                try {
                                    JSONArray array = response.getJSONArray("data");
                                    //Log.e("json", "onResponse: " + response.getString("data") );
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String type = jsonObject.getString("showType");
                                        String name = jsonObject.getString("title");
                                        String url;
                                        if (jsonObject.getString("id").equals("134110"))
                                            type = "1";
                                        if (type.equals("3")) {
                                            balance++;
                                            url= "https://drive.google.com/gview?embedded=true&url=https://www.cga.gov.tw" + jsonObject.getString("filePath");
                                        } else if (type.equals("2")) {
                                            balance++;
                                            url = "none";
                                        } else {
                                            url = "more files";
                                            JSONArray other = jsonObject.getJSONArray("files");
                                            ArrayList<String> moreFiles = new ArrayList<String>();
                                            for (int x = 0; x < other.length(); x++) {
                                                JSONObject data = other.getJSONObject(x);
                                                moreFiles.add(data.getString("name") + ".pdf");
                                                moreFiles.add(data.getString("filePath"));
                                            }
                                            listOfFiles.add(moreFiles);
                                        }
                                        nameList.add(name);
                                        urlList.add(url);
                                    }
                                    //找到ListView
                                    ListView list = (ListView) findViewById(R.id.listview);
                                    //建立Adapter，並將要顯示的結果陣列傳入
                                    WordAdapter adapter = new WordAdapter(nameList);
                                    //將Adapter設定給ListView
                                    list.setAdapter(adapter);
                                    list.setOnItemClickListener(onClickListView);
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

    private void sortFiles (ArrayList<String> files)
    {
        TreeMap<String,String> sortList = new TreeMap<>();
        ArrayList<String> tempList = new ArrayList<>();

        for(int x = 0; x < files.size(); x += 2)
            sortList.put(files.get(x),files.get(x+1));
        Set tree = sortList.entrySet();

        for (Object o : tree) {
            Map.Entry me = (Map.Entry) o;
            tempList.add(me.getKey().toString());
        }
        Collections.sort(tempList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(o1);
                Matcher m2 = p.matcher(o2);

                while (m.find() && m2.find())
                {
                    if (Integer.parseInt(m.group()) > Integer.parseInt(m2.group())) {
                        return 1;
                    }
                    else if (Integer.parseInt(m.group()) < Integer.parseInt(m2.group())) {
                        return -1;
                    }
                }
                if (o1.length() > o2.length())
                    return 1;
                else if (o1.length() < o2.length())
                    return -1;
                if (m.hitEnd() && m2.hitEnd())
                    return 0;
                else
                    return -1;
            }
        });

        files.clear();

        for (int x = 0; x < tempList.size(); x++) {
            files.add(tempList.get(x));
            files.add(sortList.get(tempList.get(x)));
        }
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
