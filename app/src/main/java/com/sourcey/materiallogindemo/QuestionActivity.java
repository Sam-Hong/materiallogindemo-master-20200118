package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Map;

public class QuestionActivity extends AppCompatActivity {
    TextView tv;
    Button submitbutton;
    RadioGroup radio_g;
    RadioButton rb1,rb2,rb3,rb4;
    TextView progress;
    int materialId;

    String authorization;
    String identification;
    RequestQueue requestQueue;
    ArrayList<String> questionsList = new ArrayList<String>();
    ArrayList<String> optList = new ArrayList<String>();
    ArrayList<String> answersList = new ArrayList<String>();

    int flag=0;
    public static int marks=0,correct=0,wrong=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();
        materialId=Integer.parseInt(intent.getStringExtra("materialId"));

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        submitbutton=(Button)findViewById(R.id.button3);
        tv=(TextView) findViewById(R.id.tvque);
        progress=(TextView)findViewById(R.id.progress);
        progress.setText("1");

        radio_g=(RadioGroup)findViewById(R.id.answersgrp);
        rb1=(RadioButton)findViewById(R.id.radioButton);
        rb2=(RadioButton)findViewById(R.id.radioButton2);
        rb3=(RadioButton)findViewById(R.id.radioButton3);
        rb4=(RadioButton)findViewById(R.id.radioButton4);

        PostHttpRequest();

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radio_g.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(getApplicationContext(), "Please select one choice", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton uans = (RadioButton) findViewById(radio_g.getCheckedRadioButtonId());
                String ansText = uans.getText().toString();
//                Toast.makeText(getApplicationContext(), ansText, Toast.LENGTH_SHORT).show();
                if(ansText.equals(optList.get(flag*4+Integer.parseInt(answersList.get(flag))))) {
                    correct++;
//                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                }
                else {
                    wrong++;
//                    Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
                }

                flag++;
                progress.setText(Integer.toString(flag+1));
                if(flag<questionsList.size()) {
                    tv.setText(questionsList.get(flag));
                    rb1.setText(optList.get(flag*4));
                    rb2.setText(optList.get(flag*4+1));
                    rb3.setText(optList.get(flag*4+2));
                    rb4.setText(optList.get(flag*4+3));
                } else {
                    marks=correct;
                    Intent in = new Intent(getApplicationContext(),ResultActivity.class);
                    in.putExtra("materialId",Integer.toString(materialId));
                    startActivity(in);
                }
                radio_g.clearCheck();
            }
        });
    }

    private void PostHttpRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("materialId", materialId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.question_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                            if (response.getString("data").length() > 0) {
                                try {
                                    JSONArray array = response.getJSONArray("data");
                                    int i;
                                    for (i = 0; i < array.length(); i++) {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        String questionText = jsonObject.getString("questionText");
                                        String answer1 = jsonObject.getString("answer1");
                                        String answer2 = jsonObject.getString("answer2");
                                        String answer3 = jsonObject.getString("answer3");
                                        String answer4 = jsonObject.getString("answer4");
                                        String correctAnswerIndex = jsonObject.getString("correctAnswerIndex");
                                        questionsList.add(questionText);
                                        optList.add(answer1);
                                        optList.add(answer2);
                                        optList.add(answer3);
                                        optList.add(answer4);
                                        answersList.add(correctAnswerIndex);
                                    }
                                    tv.setText(questionsList.get(0));
                                    rb1.setText(optList.get(0));
                                    rb2.setText(optList.get(1));
                                    rb3.setText(optList.get(2));
                                    rb4.setText(optList.get(3));
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
