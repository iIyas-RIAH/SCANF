package com.example.SCANF;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

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
import java.util.Objects;

public class ListeEtudiantSeance extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ListView lvEtudiant;
    SearchView svEtudiant;
    TextView totalAbs;
    String id;
    Etudiant etudiant;
    EtudiantSeanceAdapter etudiantAdapter;
    FloatingActionButton add;
    ArrayList<Etudiant> etudiants = new ArrayList<>();
    int tAbs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_etudiant_seance);
        add = findViewById(R.id.add);
        svEtudiant = findViewById(R.id.textRech);
        lvEtudiant = findViewById(R.id.item_etudiant_seance);
        totalAbs = findViewById(R.id.totalAbs);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            id = extras.getString("idSeance");
        }

        etudiants = retrieveData();

        etudiantAdapter = new EtudiantSeanceAdapter(this, etudiants);
        lvEtudiant.setAdapter(etudiantAdapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        svEtudiant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ListeEtudiantSeance.this.etudiantAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListeEtudiantSeance.this.etudiantAdapter.getFilter().filter(newText);
                return false;
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListeEtudiantSeance.this, NouvelleSeance.class));
            }
        });
    }

    public ArrayList<Etudiant> retrieveData(){
        ArrayList<Etudiant> results = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LIST_ETUDIANTS_BY_SEANCE,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res: "+response);
                        results.clear();

                        try{

                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("etudiant");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                int idEtudiant = object.getInt("IDETUDIANT");
                                String nom = object.getString("NOM");
                                String prenom = object.getString("PRENOM");
                                String nomComplet = nom+" "+prenom;
                                int stat = object.getInt("STAT");
                                tAbs += stat;

                                etudiant = new Etudiant(idEtudiant,nomComplet,stat);
                                results.add(etudiant);
                                etudiantAdapter.notifyDataSetChanged();

                            }
                            totalAbs.setText(Integer.toString(tAbs));

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListeEtudiantSeance.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("idSeance",id);
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
        return results;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                startActivity(new Intent(ListeEtudiantSeance.this, HomePage.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.classes:
                startActivity(new Intent(ListeEtudiantSeance.this, ListeClasses.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.seances:
                startActivity(new Intent(ListeEtudiantSeance.this, ListeSeances.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.exit:
                exit();
        }
        return false;
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

    public void back(View view) {
        onBackPressed();
    }
}