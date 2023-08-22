package com.example.SCANF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomePage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_PIC_REQUEST = 1046;
    BottomNavigationView bottomNavigationView;
    ListView lvSeancesProfile;
    FloatingActionButton add;
    TextView nomComplet;
    CircleImageView photoProfil;
    ArrayList<Seance> seances = new ArrayList<>();
    Seance seance;
    SeanceAdapter seanceAdapter;
    Professeur professeur;
    String url;
    ActivityResultLauncher<String> mGetContent;
    Bitmap AbsImg;
    String encodedImage;
    int index;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        professeur = SharedPrefManager.getInstance(this).getProfesseur();

        nomComplet = findViewById(R.id.tv_nom);
        photoProfil = findViewById(R.id.iv_photo);
        add = findViewById(R.id.add);
        lvSeancesProfile = findViewById(R.id.item_seances);

        nomComplet.setText(professeur.getNomComplet());

        seances = retrieveData();

        seanceAdapter = new SeanceAdapter(this, seances);
        lvSeancesProfile.setAdapter(seanceAdapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        registerForContextMenu(lvSeancesProfile);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, NouvelleSeance.class));
            }
        });



        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(result);
                    AbsImg = BitmapFactory.decodeStream(inputStream);
                    imageStore(AbsImg);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        url = URLs.URL_IMAGE_PROFILE+"/"+professeur.getID_Professeur()+".jpeg";
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.backup)
                .into(photoProfil);

    }

    public void listerEtudiantSeance(View view){
        lvSeancesProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Seance selItem = (Seance) lvSeancesProfile.getItemAtPosition(position);
                System.out.println(selItem.toString());
                int value = selItem.getID_Seance();
                System.out.println(value);
                Intent intent = new Intent(HomePage.this, ListeEtudiantSeance.class);
                intent.putExtra("idSeance", Integer.toString(value));
                startActivity(intent);
            }
        });

    }




    public void edit(View view){
        startActivity(new Intent(HomePage.this, ModificationProfile.class));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home:
                return true;
            case R.id.classes:
                startActivity(new Intent(HomePage.this, ListeClasses.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.seances:
                startActivity(new Intent(HomePage.this, ListeSeances.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.exit:
                exit();
        }
        return false;
    }


    public ArrayList<Seance> retrieveData(){
        ArrayList<Seance> results = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LIST_SEANCES_WEEKLY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res: "+response);
                        results.clear();
                        try{

                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("seance");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                int idseance = object.getInt("IDSEANCE");
                                String classe = object.getString("LIBELLE");
                                int idprof = object.getInt("IDPROFESSEUR");
                                String date = object.getString("DATE");
                                String heure = object.getString("HEURE");
                                String matiere = object.getString("MATIERE");

                                seance = new Seance(idseance,classe,idprof,date,heure,matiere);
                                results.add(seance);
                                seanceAdapter.notifyDataSetChanged();

                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("IDPROFESSEUR",Integer.toString(professeur.getID_Professeur()));
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
        return results;
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo);
        contextMenu.add(0, view.getId(), 0, "Modifier");
        contextMenu.add(0, view.getId(), 0, "Supprimer");
        contextMenu.add(0, view.getId(), 0, "Faire Absence");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        System.out.println(item.getTitle());
        if(item.getTitle()=="Modifier"){
            index = info.position;
            Intent intent = new Intent(HomePage.this, ModifierSeance.class);
            intent.putExtra("idSeance",seances.get(index).getID_Seance());
            intent.putExtra("classe",seances.get(index).getClasse());
            intent.putExtra("date",seances.get(index).getDate());
            intent.putExtra("temps",seances.get(index).getTemps());
            intent.putExtra("matiere",seances.get(index).getMatiere());
            startActivity(intent);
        }
        if(item.getTitle()=="Supprimer"){
            index = info.position;
            deleteData(seances.get(index).getID_Seance());
        }
        if(item.getTitle()=="Faire Absence"){
            index = info.position;
            launchCamera();
        }
        return true;
    }


    private void launchCamera() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            Uri photoUri = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bitmap image = (Bitmap) data.getExtras().get("data");
            imageStore(image);
        }
        FaireAbsenceImage(seances.get(index).getID_Seance());
    }


    private void imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

        byte[] imageBytes = stream.toByteArray();

        encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    private void FaireAbsenceImage(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");

        progressDialog.show();

        final String idSeance = Integer.toString(id);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_FAIRE_ABS_IMG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("res : "+response);
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("msg")) {
                                progressDialog.dismiss();

                                finish();
                                startActivity(new Intent(HomePage.this, ResultAbs.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new HashMap<>();
                params.put("idSeance", idSeance);
                params.put("image",encodedImage);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


    private void deleteData(int id) {

        final String idSeance = Integer.toString(id);
        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_DELETE_SEANCES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("Data Deleted")){
                            Toast.makeText(HomePage.this, "Seance Supprimer", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(HomePage.this, "Erreur", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomePage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new HashMap<>();
                params.put("idSeance", idSeance);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }




    public void exit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage ("Êtes-vous sûr de vouloir quitter?")
                .setCancelable (false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialoginterface, int i) {
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialogInterface, int i){
                         dialogInterface.cancel ();
                    }
                });
            AlertDialog alertDialog = builder.create ();
            alertDialog.show();
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;
        public LoadImage(ImageView photoProfil) {
            this.imageView = photoProfil;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}