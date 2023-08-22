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

public class ListeClasses extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    ListView lvClasses;
    FloatingActionButton add;
    public static ArrayList<Classe> classes = new ArrayList<>();
    Classe classe;
    ClasseAdapter classeAdapter;
    Professeur professeur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_classes);
        add = findViewById(R.id.add);
        professeur = SharedPrefManager.getInstance(this).getProfesseur();

        classes = retrieveData();

        lvClasses = findViewById(R.id.item_classes);
        classeAdapter = new ClasseAdapter(this, classes);
        lvClasses.setAdapter(classeAdapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.classes);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListeClasses.this, NouvelleSeance.class));
            }
        });



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                startActivity(new Intent(ListeClasses.this, HomePage.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.classes:
                return true;
            case R.id.seances:
                startActivity(new Intent(ListeClasses.this, ListeSeances.class));
                overridePendingTransition(0, 0);
                return true;
            case R.id.exit:
                exit();
        }
        return false;
    }

    public ArrayList<Classe> retrieveData(){
        ArrayList<Classe> results = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_LIST_CLASSES_BY_PROF,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        results.clear();
                        System.out.println(response);
                        try{

                            JSONObject obj = new JSONObject(response);
                            JSONArray jsonArray = obj.getJSONArray("classe");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                int idclasse = object.getInt("IDCLASSE");
                                String libelle = object.getString("LIBELLE");

                                classe = new Classe(idclasse,libelle);
                                results.add(classe);
                                classeAdapter.notifyDataSetChanged();

                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListeClasses.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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



    public void listerEtudiant(View view){
        lvClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                Classe selItem = (Classe) lvClasses.getItemAtPosition(position);
                System.out.println(selItem.toString());
                int value = selItem.getID_Classe();
                System.out.println(value);
                Intent intent = new Intent(ListeClasses.this, ListeEtudiant.class);
                intent.putExtra("idClasse", Integer.toString(value));
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ListeClasses.this, HomePage.class));
    }
}