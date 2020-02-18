package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetActivity extends AppCompatActivity {

    boolean editOrNot;
    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";

    @BindView(R.id.input_new_password)
    EditText _NewPasswordText;
    @BindView(R.id.input_confirm_password)
    EditText _ConfirmPasswordText;
    @BindView(R.id.btn_change_password)
    Button _ChangePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        ButterKnife.bind(this);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        _ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });
    }

    public void edit() {
        if (!validate()) {
            onEditFailed();
            return;
        }

        _ChangePasswordButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String newPassword = _NewPasswordText.getText().toString();
        String confirmPassword = _ConfirmPasswordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        if (newPassword.equals(confirmPassword)) {
            JSONObject json = new JSONObject();
            try {
                json.put("password", newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getResources().getString(R.string.reset_api_url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("data").length() > 0) {
                                    authorization = response.getJSONObject("data").getString("accessToken");
                                    GlobalVariable gv = (GlobalVariable) getApplicationContext();
                                    gv.setAuthorization(authorization);
                                    editOrNot = true;
                                } else {
                                    editOrNot = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ResetActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
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

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (editOrNot) {
                            onEditSuccess();
                        } else {
                            onEditFailed();
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);

    }

    public void onEditSuccess() {
        _ChangePasswordButton.setEnabled(true);
        Toast toast = Toast.makeText(this, "Password Change Success", Toast.LENGTH_LONG);
        toast.show();
        finish();
    }

    public void onEditFailed() {
        Toast.makeText(getBaseContext(), "Edit failed", Toast.LENGTH_LONG).show();
        _ChangePasswordButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String NewPassword = _NewPasswordText.getText().toString();
        String ConfirmPassword = _ConfirmPasswordText.getText().toString();

        if (NewPassword.isEmpty()) {
            _NewPasswordText.setError("enter a new password");
            valid = false;
        } else {
            _NewPasswordText.setError(null);
        }

        if (ConfirmPassword.isEmpty()) {
            _ConfirmPasswordText.setError("enter a confirmed password");
            valid = false;
        } else {
            _ConfirmPasswordText.setError(null);
        }

        return valid;
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
