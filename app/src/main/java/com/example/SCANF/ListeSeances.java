package com.example.SCANF;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListeSeances extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ListView lvSeances;
    FloatingActionButton add;
    ArrayList<Seance> seances = new ArrayList<>();
    Seance seance;
    SeanceAdapter seanceAdapter;
    Professeur professeur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_seances);
        professeur = SharedPrefManager.getInstance(this).getProfesseur();

        add = findViewById(R.id.add);
        lvSeances = findViewById(R.id.item_seances);
        seances = retrieveData();

        seanceAdapter = new SeanceAdapter(this, seances);

        lvSeances.setAdapter(seanceAdapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.seances);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListeSeances.this, NouvelleSeance.class));
            }
        });

        lvSeances.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Seance selItem = (Seance) lvSeances.getItemAtPosition(position);
                int value = selItem.getID_Seance();
                Intent intent = new Intent(ListeSeances.this, ListeEtudiantSeance.class);
                intent.putExtra("idSeance", Integer.toString(value));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                startActivity(new Intent(ListeSeances.this, HomePage.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.classes:
                startActivity(new Intent(ListeSeances.this, ListeClasses.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.seances:
                return true;
            case R.id.exit:
                exit();
        }
        return false;
    }


    public ArrayList<Seance> retrieveData(){
        ArrayList<Seance> results = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LIST_SEANCES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                Toast.makeText(ListeSeances.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void listerEtudiantSeance(View view){
        lvSeances.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Seance selItem = (Seance) lvSeances.getSelectedItem();
                int value = selItem.getID_Seance();
                Intent intent = new Intent(ListeSeances.this, ListeEtudiantSeance.class);
                intent.putExtra("idSeance", Integer.toString(value));
                startActivity(intent);
            }
        });    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ListeSeances.this, HomePage.class));
    }
}