package com.example.SCANF;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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

public class ListeEtudiant extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ListView lvEtudiant;
    SearchView svEtudiant;
    String id;
    Etudiant etudiant;
    EtudiantAdapter etudiantAdapter;
    FloatingActionButton add;
    ArrayList<Etudiant> etudiants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_etudiant);
        add = findViewById(R.id.add);
        svEtudiant = findViewById(R.id.textRech);
        lvEtudiant = findViewById(R.id.item_etudiant);
        ArrayList<Etudiant> etudiants = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            id = extras.getString("idClasse");
        }

        etudiants = retrieveData();
        etudiantAdapter = new EtudiantAdapter(this, etudiants);
        lvEtudiant.setAdapter(etudiantAdapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        svEtudiant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                etudiantAdapter.getFilter().filter(text);
                return false;
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListeEtudiant.this, NouvelleSeance.class));
            }
        });
    }

    public ArrayList<Etudiant> retrieveData(){
        ArrayList<Etudiant> results = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LIST_ETUDIANTS,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("res: "+response);
                        results.clear();
                        try{

                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArrayE = obj.getJSONArray("etudiant");
                            JSONArray jsonArrayS = obj.getJSONArray("abs");

                            for(int i=0;i<jsonArrayE.length();i++){
                                JSONObject objectE = jsonArrayE.getJSONObject(i);

                                int idEtudiant = objectE.getInt("IDETUDIANT");
                                String nom = objectE.getString("NOM");
                                String prenom = objectE.getString("PRENOM");
                                String nomComplet = nom+" "+prenom;
                                int nbrAbs = jsonArrayS.getInt(i);;
                                


                                etudiant = new Etudiant(idEtudiant,nomComplet,nbrAbs);
                                results.add(etudiant);
                                etudiantAdapter.notifyDataSetChanged();

                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListeEtudiant.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("idClasse",id);
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
                startActivity(new Intent(ListeEtudiant.this, HomePage.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.classes:
                startActivity(new Intent(ListeEtudiant.this, ListeClasses.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.seances:
                startActivity(new Intent(ListeEtudiant.this, ListeSeances.class));
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