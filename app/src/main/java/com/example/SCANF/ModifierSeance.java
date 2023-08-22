package com.example.SCANF;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModifierSeance extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    TextView dateSeance;
    TextView timeSeance;
    private Spinner spinner;
    ArrayAdapter<String> classeAdapter;
    ArrayList<String> classes = new ArrayList<>();
    private TextView matiereSeance;
    int hour, minute;
    Button btnEnregistrer;
    Professeur professeur;
    Seance seance = new Seance();
    private String choixSpinner;
    private String choixDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifier_seance);
        professeur = SharedPrefManager.getInstance(this).getProfesseur();

        btnEnregistrer = findViewById(R.id.btnEnregistrer);
        spinner = findViewById(R.id.spinnerClasse);
        dateSeance = findViewById(R.id.DateSeance);
        timeSeance = findViewById(R.id.TimeSeance);
        matiereSeance = findViewById(R.id.MatiereSeance);
        initDatePicker();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URLs.URL_LIST_CLASSES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            JSONArray jsonArray = response.getJSONArray("classe");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);

                                String libelle = object.getString("LIBELLE");
                                classes.add(libelle);
                                classeAdapter = new ArrayAdapter<>(ModifierSeance.this, R.layout.spinner_item, classes);
                                classeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(classeAdapter);

                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ModifierSeance.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choixSpinner = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            seance.setID_Seance(extras.getInt("idSeance"));
            seance.setDate(extras.getString("date"));
            seance.setTemps(extras.getString("temps"));
            seance.setMatiere(extras.getString("matiere"));
        }

        System.out.println("temps"+seance.getTemps().getClass());


        dateSeance.setText(seance.getDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        timeSeance.setText(seance.getTemps().substring(0,5));
        matiereSeance.setText(seance.getMatiere());



        btnEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(Integer.toString(seance.getID_Seance()));
            }
        });
    }



    private void updateData(String idSeance) {

        choixDate = dateSeance.getText().toString();

        if (choixDate.charAt(6) == '-') choixDate = choixDate.substring(0,5) + "0" + choixDate.substring(5);
        if (choixDate.length() == 9) choixDate = choixDate.substring(0,8) +"0" + choixDate.substring(8);
        final String classe = choixSpinner;
        final String date = choixDate;
        final String heure = timeSeance.getText().toString();
        final String matiere = matiereSeance.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mise à jour...");

        if(classe.isEmpty()){
            Toast.makeText(this, "Choisissez une classe", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(date.isEmpty()){
            Toast.makeText(this, "Entrez la date", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(heure.isEmpty()){
            Toast.makeText(this, "Entrez l'heure", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(matiere.isEmpty()){
            Toast.makeText(this, "Entrez la matiere", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_SEANCES,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("res" + response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                System.out.println("obj" + obj.getBoolean("msg"));
                                if (obj.getBoolean("msg")) {

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
                    Toast.makeText(ModifierSeance.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("idseance", idSeance);
                    params.put("idprof",Integer.toString(professeur.getID_Professeur()));
                    params.put("classe", classe);
                    params.put("date", date);
                    params.put("heure", heure);
                    params.put("matiere", matiere);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ModifierSeance.this);
            requestQueue.add(request);
        }
    }

    private String getTodayDate(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar. YEAR);
        int month = cal.get (Calendar.MONTH);
        month = month + 1;
        int day = cal.get (Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                dateSeance.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar. YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetlistener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
    }

    public String makeDateString(int day, int month, int year) {
        return day+" "+getMonthFormat(month)+" "+year;
    }

    private String getMonthFormat(int month) {
        if(month==1)
            return "JANV";
        if(month==2)
            return "FÉVR";
        if(month==3)
            return "MARS";
        if(month==4)
            return "AVR";
        if(month==5)
            return "MAI";
        if(month==6)
            return "JUIN";
        if(month==7)
            return "JUILL";
        if(month==8)
            return "AOÛT";
        if(month==9)
            return "SEPT";
        if(month==10)
            return "OCT";
        if(month==11)
            return "NOV";
        if(month==12)
            return "DÉC";

        return "JAN";
    }

    public void popupDate(View view) {
        datePickerDialog.show();
    }

    public void popupTime(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                timeSeance.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }

    public void back(View view) {
        onBackPressed();
    }
}