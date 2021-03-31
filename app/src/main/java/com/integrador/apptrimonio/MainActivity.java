package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.integrador.apptrimonio.Utils.AutenticadorAdaptador;
import com.integrador.apptrimonio.Utils.InicoAdaptador;

public class MainActivity extends AppCompatActivity {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);

        //abre tela de boas vindas caso nunca entrou no app
        if(!sharedPreferences.getBoolean("entrou", false)){
            startActivity(new Intent(MainActivity.this, BoasVindas.class));
            finish();
        }

        viewPager2 = findViewById(R.id.inicio_viewpager2);
        viewPager2.setAdapter(new InicoAdaptador(this, viewPager2));

    }
}