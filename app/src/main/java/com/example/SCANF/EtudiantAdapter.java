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

class EtudiantAdapter extends ArrayAdapter<Etudiant> {
    Context context;
    List<Etudiant> Etudiants;


    public EtudiantAdapter(@NonNull Context context, List<Etudiant> arrayList) {
        super(context, R.layout.item_etudiant,arrayList);

        this.context = context;
        this.Etudiants = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etudiant,null,true);

        TextView NomComplet = convertView.findViewById(R.id.nomEtudiant);
        TextView NbrAbsence = convertView.findViewById(R.id.nbrAbs);

        NomComplet.setText(Etudiants.get(position).getNomComplet());
        NbrAbsence.setText(Integer.toString(Etudiants.get(position).getNbrAbsence()));

        return convertView;
    }
}
