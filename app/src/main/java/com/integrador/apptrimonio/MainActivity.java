package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private Button botaoTelaCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);

        botaoTelaCadastro = findViewById(R.id.mainCadastro);

        botaoTelaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Autenticar.class));
            }
        });

        //abre tela de boas vindas caso nunca entrou no app
        if(!sharedPreferences.getBoolean("entrou", false)){
            startActivity(new Intent(MainActivity.this, BoasVindas.class));
            finish();
        }

    }
}