package com.example.SCANF;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class EtudiantSeanceAdapter extends ArrayAdapter<Etudiant> {
    Context context;
    List<Etudiant> Etudiants;

    public EtudiantSeanceAdapter(@NonNull Context context, List<Etudiant> arrayList) {
        super(context, R.layout.item_etudiant,arrayList);

        this.context = context;
        this.Etudiants = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etudiant_seance,null,true);

        TextView NomComplet = convertView.findViewById(R.id.nomEtudiant);
        TextView SitAbs = convertView.findViewById(R.id.SitAbs);

        NomComplet.setText(Etudiants.get(position).getNomComplet());
        if(Etudiants.get(position).getNbrAbsence() == 1){
            SitAbs.setText("Absent");
        }else if(Etudiants.get(position).getNbrAbsence() == 0){
            SitAbs.setText("Présent");
        }
        return convertView;
    }
}

