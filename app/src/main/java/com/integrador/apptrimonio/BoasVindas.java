package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.integrador.apptrimonio.Utils.BoasVindasAdaptador;
import com.integrador.apptrimonio.Utils.BoasVindasItem;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class BoasVindas extends AppCompatActivity {

    private BoasVindasAdaptador boasVindasAdaptador;
    private Button botao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);

        //abre a tela inicial caso ja entrou
        if (getSharedPreferences("apptrimonio", MODE_PRIVATE).getBoolean("entrou", false)) {
            startActivity(new Intent(BoasVindas.this, MainActivity.class)); //abre a tela inicial
            finish();
        }

        botao = findViewById(R.id.botaoBoasVindas);

        //inicia os itens de boas vindas
        setupItensBoasVindas();
        ViewPager2 boasVindasViewPager = findViewById(R.id.boasVindasViewPager);
        boasVindasViewPager.setAdapter(boasVindasAdaptador);
        setupBoasVindasIndicadores();

        //dots indicator
        DotsIndicator dotsIndicator = findViewById(R.id.inicio_dots);
        dotsIndicator.setViewPager2(boasVindasViewPager);

        boasVindasViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        boasVindasViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position == boasVindasAdaptador.getItemCount() -1){
                    botao.setText(getResources().getString(R.string.start));
                }else{
                    botao.setText(getResources().getString(R.string.next));
                }
            }
        });

        botao.setOnClickListener(v -> {
            if (boasVindasViewPager.getCurrentItem() + 1 < boasVindasAdaptador.getItemCount()) {
                boasVindasViewPager.setCurrentItem(boasVindasViewPager.getCurrentItem() + 1);
            } else {
                //atualiza o shared pois entrou no app pela primeira vez
                SharedPreferences sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("entrou");
                editor.putBoolean("entrou", true);
                editor.apply();

                //abre a tela incial
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        });
    }

    private void setupItensBoasVindas() {
        List<BoasVindasItem> itens = new ArrayList<>();

        BoasVindasItem bemvindo = new BoasVindasItem();
        bemvindo.setTitulo(getResources().getString(R.string.wWelcome));
        bemvindo.setDescricao(getResources().getString(R.string.wWelcomeD));
        bemvindo.setImagem(R.drawable.boasvindas_imagem1);

        BoasVindasItem cadastro = new BoasVindasItem();
        cadastro.setTitulo(getResources().getString(R.string.wRegister));
        cadastro.setDescricao(getResources().getString(R.string.wRegiterD));
        cadastro.setImagem(R.drawable.boasvindas_imagem2);

        BoasVindasItem escaneie = new BoasVindasItem();
        escaneie.setTitulo(getResources().getString(R.string.wScan));
        escaneie.setDescricao(getResources().getString(R.string.wScanD));
        escaneie.setImagem(R.drawable.boasvindas_imagem3);

        itens.add(bemvindo);
        itens.add(cadastro);
        itens.add(escaneie);

        boasVindasAdaptador = new BoasVindasAdaptador(itens);
    }

    private void setupBoasVindasIndicadores() {
        ImageView[] indicadores = new ImageView[boasVindasAdaptador.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicadores.length; i++) {
            indicadores[i] = new ImageView(getApplicationContext());
            indicadores[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.boasvindas_indicador_off));
            indicadores[i].setLayoutParams(layoutParams);
        }
    }
}