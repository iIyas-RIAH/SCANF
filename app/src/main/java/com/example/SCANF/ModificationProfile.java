package com.example.SCANF;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ModificationProfile extends AppCompatActivity {

    private TextInputLayout nomComplet;
    private TextInputLayout mail;
    private TextInputLayout mobile;
    private TextInputLayout passwd;
    private TextInputLayout passwd2;
    Professeur professeur;
    Bitmap bitmap;
    String encodedImage;
    Button btnModification, btnSelectImage;
    ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modification_profile);
        professeur = SharedPrefManager.getInstance(this).getProfesseur();

        nomComplet = findViewById(R.id.nomComplet);
        mail = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        passwd = findViewById(R.id.passwd);
        passwd2 = findViewById(R.id.passwd2);

        nomComplet.getEditText().setText(professeur.getNomComplet());
        mail.getEditText().setText(professeur.getEmail());
        mobile.getEditText().setText(professeur.getMobile());

        btnSelectImage = findViewById(R.id.btnImageProfil);
        btnModification = findViewById(R.id.btnModification);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(result);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imageStore(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });


        btnModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { updateData(); }
        });
    }

    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    public void updateData() {

        final String name = nomComplet.getEditText().getText().toString();
        final String email = mail.getEditText().getText().toString();
        final String phone = mobile.getEditText().getText().toString();
        final String password = passwd.getEditText().getText().toString();
        final String password2 = passwd2.getEditText().getText().toString();

        if (!password.equals(password2)) {
            Toast.makeText(ModificationProfile.this, "Les mots de passe ne sont pas identiques", Toast.LENGTH_SHORT).show();
        }


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mise à jour...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getBoolean("msg")) {

                                //creating a new user object
                                Professeur prof = new Professeur(
                                        professeur.getID_Professeur(),
                                        name,
                                        email,
                                        phone
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(prof);

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), HomePage.class));
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ModificationProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }){

            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new HashMap<String,String>();

                params.put("id",Integer.toString(professeur.getID_Professeur()));
                params.put("name",name);
                params.put("email",email);
                params.put("mobile",phone);
                params.put("password",password);
                params.put("image",encodedImage);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ModificationProfile.this);
        requestQueue.add(request);
    }


    public void back(View view) {
        onBackPressed();
    }
}