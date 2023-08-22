package com.example.SCANF;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Singin extends AppCompatActivity {

    private TextInputLayout mail;
    private TextInputLayout passwd;
    private Button btnConnecter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_in);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomePage.class));
        }

        btnConnecter = findViewById(R.id.btnConnecter);
        passwd = findViewById(R.id.passwd);
        mail = findViewById(R.id.mail);



        btnConnecter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        //first getting the values
        final String email = mail.getEditText().getText().toString();
        final String password = passwd.getEditText().getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(email)) {
            mail.setError("Champs e-mail est vide");
            mail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwd.setError("Champs Mot de passe est vide");
            passwd.requestFocus();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");

        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("msg")) {

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("professeur");

                        //creating a new user object
                        Professeur professeur = new Professeur(
                                userJson.getInt("IDPROFESSEUR"),
                                userJson.getString("NOMCOMPLET"),
                                userJson.getString("EMAIL"),
                                userJson.getString("MOBILE")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(professeur);

                        progressDialog.dismiss();

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Email et/ou Mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                    }
            }
        },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Singin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Singin.this);
        requestQueue.add(request);
        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }
}