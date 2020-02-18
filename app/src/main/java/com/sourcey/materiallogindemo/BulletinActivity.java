package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class BulletinActivity extends AppCompatActivity {

    String authorization, pageTotal;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();

    Hashtable<String, ArrayList<String>> fileTable = new Hashtable<String, ArrayList<String>>();
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        Button next = findViewById(R.id.nextPage);
        Button prev = findViewById(R.id.prevPage);

        next.setVisibility(View.VISIBLE);
        next.setEnabled(true);
        next.setOnClickListener(onClickButton);
        prev.setOnClickListener(onClickButton);

        PostHttpRequest(); //將Data寫入listview的程式碼放在此函數內，否則會有Callback時間差的問題，原因在於listener
    }

    public View.OnClickListener onClickButton = new View.OnClickListener() {
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
                        else if (page + 1 == total)
                        {
                            next.setVisibility(View.INVISIBLE);
                            next.setEnabled(false);
                        }
                        page++;
                        idList.clear();
                        nameList.clear();
                        urlList.clear();

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
                        else if (page - 1 == 1)
                        {
                            prev.setVisibility(View.INVISIBLE);
                            prev.setEnabled(false);
                        }
                        page--;
                        idList.clear();
                        nameList.clear();
                        urlList.clear();

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
            if (urlList.get(position).equals("none"))
                Toast.makeText(getBaseContext(), "No PDF file", Toast.LENGTH_LONG).show();
            else if (urlList.get(position).equals("multi"))
            {
                Bundle multiLinks = new Bundle();
                String[] temp = new String[]{"none"};

                multiLinks.putString("name", nameList.get(position));
                multiLinks.putStringArrayList("files", fileTable.get(nameList.get(position)));
                multiLinks.putStringArray("history",temp);
                Intent intent = new Intent(BulletinActivity.this, RegulationsPageActivity.class);
                intent.putExtra("data", multiLinks);
                startActivity(intent);

            }
            else
            {
                Intent intent = new Intent(BulletinActivity.this, WebViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                startActivity(intent);
            }

        }

    };

    private void PostHttpRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.bulletin_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                           // Log.e("here", response.toString());
                            if (response.getString("data").length() > 0) {
                                try {
                                    JSONObject data = response.getJSONObject("data");
                                    pageTotal = data.getString("totalPage");
                                    JSONArray array = data.getJSONArray("bulletins");

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String id = jsonObject.getString("id");
                                        String name = jsonObject.getString("title");
                                        JSONArray filePath = jsonObject.getJSONArray("files");
                                        String url;
                                        if (!filePath.isNull(0) && filePath.length() == 1) {
                                            JSONObject path = filePath.getJSONObject(0);

                                            url = "https://docs.google.com/gview?embedded=true&url=https://www.cga.gov.tw" + path.getString("filePath");
                                        }
                                        else {
                                            url = "none";
                                            if (filePath.length() > 1) {
                                                ArrayList<String> moreFiles = new ArrayList<String>();
                                                for(int x = 0; x < filePath.length();x++)
                                                {
                                                    JSONObject temp = filePath.getJSONObject(x);
                                                    if (!temp.getString("filePath").contains(".doc")) {
                                                        if (temp.getString("name").contains(".pdf"))
                                                            moreFiles.add(temp.getString("name"));
                                                        else
                                                            moreFiles.add(temp.getString("name") + ".pdf");
                                                        moreFiles.add(temp.getString("filePath"));
                                                        Log.e("added to files", "onResponse: " + temp.getString("name") + " = "+ temp.getString("filePath") + "\n");
                                                        url = "multi";
                                                    }
                                                }
                                                if (!moreFiles.isEmpty())
                                                    fileTable.put(name,moreFiles);
                                                Log.e("multi links", "onResponse: " + name );
                                            }
                                        }
                                        idList.add(id);
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

                                    TextView pageNumber = findViewById(R.id.pageNumber);
                                    pageNumber.setVisibility(View.VISIBLE);
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
