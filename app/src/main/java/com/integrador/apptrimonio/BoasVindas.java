package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.integrador.apptrimonio.Utils.BoasVindasAdaptador;
import com.integrador.apptrimonio.Utils.BoasVindasItem;

import java.util.ArrayList;
import java.util.List;

public class BoasVindas extends AppCompatActivity {

    private BoasVindasAdaptador boasVindasAdaptador;
    private LinearLayout boasVindasLinearLayoutIndicadores;
    private Button botao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        boasVindasLinearLayoutIndicadores = findViewById(R.id.boasVindasLinearLayout);
        botao = findViewById(R.id.botaoBoasVindas);

        //inicia os itens de boas vindas
        setupItensBoasVindas();
        ViewPager2 boasVindasViewPager = findViewById(R.id.boasVindasViewPager);
        boasVindasViewPager.setAdapter(boasVindasAdaptador);
        setupBoasVindasIndicadores();
        atualizarIndicadores(0);

        boasVindasViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                atualizarIndicadores(position);
            }
        });

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boasVindasViewPager.getCurrentItem() + 1 < boasVindasAdaptador.getItemCount()) {
                    boasVindasViewPager.setCurrentItem(boasVindasViewPager.getCurrentItem()+1);
                }else{
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
            boasVindasLinearLayoutIndicadores.addView(indicadores[i]);
        }
    }

    private void atualizarIndicadores(int index) {
        int childCount = boasVindasLinearLayoutIndicadores.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) boasVindasLinearLayoutIndicadores.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.boasvindas_indicador_on));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.boasvindas_indicador_off));
            }
        }

        if(index == boasVindasAdaptador.getItemCount()-1){
            botao.setText(getResources().getString(R.string.start));
        }else{
            botao.setText(getResources().getString(R.string.next));
        }
    }
}