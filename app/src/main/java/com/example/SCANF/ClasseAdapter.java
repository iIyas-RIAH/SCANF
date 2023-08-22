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

class ClasseAdapter extends ArrayAdapter<Classe> {
    Context context;
    List<Classe> Classes;


    public ClasseAdapter(@NonNull Context context, List<Classe> arrayList) {
        super(context, R.layout.item_classes,arrayList);

        this.context = context;
        this.Classes = arrayList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classes,null,true);

        TextView Libelle = convertView.findViewById(R.id.Libelle);

        Libelle.setText(Classes.get(position).getLibelle());

        return convertView;
    }
}
