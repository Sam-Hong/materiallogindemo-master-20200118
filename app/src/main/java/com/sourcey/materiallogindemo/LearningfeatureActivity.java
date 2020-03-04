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
import java.util.HashMap;
import java.util.Map;

public class LearningfeatureActivity extends AppCompatActivity {

    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();
    ArrayList<String> durationList = new ArrayList<>();
    int parentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        Intent intent = getIntent();
        try {
            parentID = Integer.parseInt(intent.getStringExtra("parentId"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        PostHttpRequest();
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parentID==15 && position==2){
                Intent intent = new Intent(LearningfeatureActivity.this, ImageViewActivity.class);
                startActivity(intent);
            } else if (parentID==15&&(position==3||position==4||position==5||position==6||position==8||position==9)){
                Intent intent = new Intent(LearningfeatureActivity.this, WebViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                startActivity(intent);
            } else if (parentID==16&&position==1){
                Intent intent = new Intent(LearningfeatureActivity.this, WebViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                startActivity(intent);
            } else if (parentID==14&&position==0){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(1));
                startActivity(intent);
            } else if (parentID==14&&position==1){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(2));
                startActivity(intent);
            } else if (parentID==14&&position==2){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(3));
                startActivity(intent);
            } else if (parentID==14&&position==3){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(4));
                startActivity(intent);
            } else if (parentID==15&&position==0){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(5));
                startActivity(intent);
            } else if (parentID==15&&position==1){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(6));
                startActivity(intent);
            } else if (parentID==15&&position==7){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(12));
                startActivity(intent);
            } else if (parentID==16&&position==0){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(14));
                startActivity(intent);
            } else if (parentID==18&&position==0){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(16));
                startActivity(intent);
            }
            else if (parentID==18&&position==1){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(17));
                startActivity(intent);
            }
            else if (parentID==18&&position==2){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(18));
                startActivity(intent);
            }
            else if (parentID==18&&position==3){
                Intent intent = new Intent(LearningfeatureActivity.this, WebQuizViewActivity.class);
                intent.putExtra("url", urlList.get(position));
                intent.putExtra("time_to_quiz", durationList.get(position));
                intent.putExtra("materialId",Integer.toString(19));
                startActivity(intent);
            }
        }
    };

    private void PostHttpRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("parentId", parentID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.learning_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                            if (response.getString("data").length() > 0) {
                                try {
                                    JSONArray array = response.getJSONArray("data");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String id = jsonObject.getString("id");
                                        String name = jsonObject.getString("name");
                                        String url = jsonObject.getString("url");
                                        if (url.contains("https")) {
                                            String[] temp = url.split("\"");
                                            for(String x : temp)
                                            {
                                                if (x.contains("https"))
                                                    url = x;
                                            }
                                        }
                                        if (jsonObject.getString("hasQuestions").equals("1"))
                                        {
                                            String time = jsonObject.getString("confirmDuration");
                                            durationList.add(time);
                                        }
                                        else
                                            durationList.add("none");
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
