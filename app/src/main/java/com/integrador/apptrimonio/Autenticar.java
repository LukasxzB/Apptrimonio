package com.integrador.apptrimonio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.integrador.apptrimonio.Utils.AutenticadorAdaptador;

public class Autenticar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticar);

        ViewPager2 viewPager = findViewById(R.id.autenticar_viewpager2);
        viewPager.setAdapter(new AutenticadorAdaptador(this, viewPager));
    }

}