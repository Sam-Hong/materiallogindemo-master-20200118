package com.sourcey.materiallogindemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class specialshipActivity extends AppCompatActivity {

    String authorization, pageTotal;
    RequestQueue requestQueue;
    Context context;
    static final String REQ_TAG = "VACTIVITY";
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<ArrayList<String>> listChild = new ArrayList<ArrayList<String>>();
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialship_layout);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
        context = this;

        final SearchView numberSearchView = findViewById(R.id.numberSearch);
        numberSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberSearchView.setIconified(false);
            }
        });
        numberSearchView.setOnQueryTextListener(onQueryListener);

        PostHttpRequest(false, 0);
    }

    public SearchView.OnQueryTextListener onQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            try {
                if (query.length() == 7) {
                    int ship = Integer.parseInt(query);
                    page = 1;
                    PostHttpRequest(true, ship);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getBaseContext(), "Number error", Toast.LENGTH_LONG).show();
            }

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty())
                PostHttpRequest(false, 0);
            return false;
        }
    };

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

                        PostHttpRequest(false, 0);
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

                        PostHttpRequest(false,0);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private void PostHttpRequest(Boolean Search , int shipNum) {
        JSONObject json = new JSONObject();
        try {
            json.put("page", page);
            if (Search)
                json.put("number" , shipNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.specialship_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                             //Log.e("here", response.toString());
                            if (response.getString("data").length() > 0) {
                                try {
                                    if (!idList.isEmpty() && !listChild.isEmpty())
                                    {
                                        idList.clear();
                                        listChild.clear();
                                    }
                                    JSONObject data = response.getJSONObject("data");
                                    pageTotal = data.get("totalPage").toString();
                                    JSONArray array = data.getJSONArray("ships");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String id = jsonObject.getString("number");
                                        String name;
                                        if (jsonObject.has("name"))
                                            name = jsonObject.getString("name");
                                        else
                                            name = "No name";
                                        String status = jsonObject.getString("howToDo");
                                        ArrayList<String> child = new ArrayList<String>();

                                        idList.add(id);
                                        child.add(name);
                                        child.add(status);
                                        listChild.add(child);

                                    }

                                    Button next = findViewById(R.id.nextPage);
                                    Button prev = findViewById(R.id.prevPage);
                                    TextView pageNumber = findViewById(R.id.pageNumber);

                                    if (!pageTotal.equals("1") && !pageTotal.equals("0")) {
                                        pageNumber.setVisibility(View.VISIBLE);
                                        String temp = page + "/" + pageTotal;
                                        pageNumber.setText(temp);
                                    }
                                    else
                                    {
                                        pageNumber.setVisibility(View.INVISIBLE);
                                    }
                                    if (pageTotal.equals("1") || pageTotal.equals("0"))
                                    {
                                        prev.setVisibility(View.INVISIBLE);
                                        prev.setEnabled(false);
                                        next.setVisibility(View.INVISIBLE);
                                        next.setEnabled(false);
                                    }
                                    else if (page == 1){
                                        prev.setVisibility(View.INVISIBLE);
                                        prev.setEnabled(false);
                                        if (next.getVisibility() == View.INVISIBLE)
                                            next.setVisibility(View.VISIBLE);
                                        next.setEnabled(true);
                                    }
                                    else if (pageTotal.equals(Integer.toString(page)))
                                    {
                                        next.setVisibility(View.INVISIBLE);
                                        next.setEnabled(false);
                                        prev.setEnabled(true);
                                    }

                                    prev.setOnClickListener(onClickButton);
                                    next.setOnClickListener(onClickButton);

                                    specialAdapter listAdapter = new specialAdapter(context, idList, listChild);
                                    ExpandableListView expListView = findViewById(R.id.ImoList);
                                    expListView.setAdapter(listAdapter);

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
