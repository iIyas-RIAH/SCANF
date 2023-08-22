package com.example.SCANF;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class SeanceAdapter extends ArrayAdapter<Seance> {

    Context context;
    List<Seance> Seances;


    public SeanceAdapter(@NonNull Context context, List<Seance> arrayList) {
        super(context, R.layout.item_seance,arrayList);

        this.context = context;
        this.Seances = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seance,null,true);

        TextView classe = convertView.findViewById(R.id.Classe);
        TextView dates = convertView.findViewById(R.id.dates);
        TextView temps = convertView.findViewById(R.id.temps);
        TextView matiere = convertView.findViewById(R.id.matiere);

        classe.setText(Seances.get(position).getClasse());
        dates.setText(Seances.get(position).getDate());

        SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm");
        try {
            temps.setText(sdfs.format(sdfs.parse(Seances.get(position).getTemps())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        matiere.setText(Seances.get(position).getMatiere());

        return convertView;
    }
}

