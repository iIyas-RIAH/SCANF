package com.example.SCANF;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ResultAbs extends AppCompatActivity {

    ImageView resultAbsPhoto;
    String url, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_abs);

        resultAbsPhoto = findViewById(R.id.resultAbsPhoto);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            id = extras.getString("idSeance");
        }

        url = URLs.URL_RESULT_ABS_IMG+"/"+id+".png";
        Picasso.with(this)
                .load(url)
                .into(resultAbsPhoto);

    }
}