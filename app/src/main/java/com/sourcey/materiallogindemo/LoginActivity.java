package com.sourcey.materiallogindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    boolean loginOrNot;
    String authorization;
    RequestQueue requestQueue;
    static final String REQ_TAG = "VACTIVITY";

    @BindView(R.id.input_identification)
    EditText _identificationText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
//    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        /*_signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQ_TAG);
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String identification = _identificationText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        JSONObject json = new JSONObject();
        try {
            json.put("identification", identification);
            json.put("birthday", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.login_api_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        serverResp.setText("String Response : "+ response.toString());
                        try {
                            if (response.getString("data").length() > 0) {

                                try {
                                    authorization = response.getJSONObject("data").getString("accessToken");
                                    GlobalVariable gv = (GlobalVariable) getApplicationContext();
                                    gv.setAuthorization(authorization);
                                    loginOrNot = true;
                                } catch (Exception e) {
                                    loginOrNot = false;
                                }
                            } else {
                                loginOrNot = false;
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
        });
        /*CookieManager cookieManager = new CookieManager(new PersistentCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);*/
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (loginOrNot) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }
                }, 3000);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        GlobalVariable gv = (GlobalVariable) getApplicationContext();
        gv.setLoginToken(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fail", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String identification = _identificationText.getText().toString();
        String password = _passwordText.getText().toString();

        if (identification.isEmpty()) { // || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _identificationText.setError("enter a valid identification");
            valid = false;
        } else {
            _identificationText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 || password.length() > 10) {
            _passwordText.setError("between 3 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
