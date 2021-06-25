package com.integrador.apptrimonio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.zxing.common.BitMatrix;
import com.integrador.apptrimonio.Utils.ActivityBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Locale;

public class AdicionarObjetos extends ActivityBase {

    private boolean inseriuImagem = false;
    private int selecionarFoto = 1;
    private Uri uri;
    private RelativeLayout imagemBackground;
    private EditText nome, categoria, data, valor, local, descricao, sentimental, descricaoImagem;
    private Button adicionar;

    private ImageView imagem;

    private ImageView[] linguas = new ImageView[3];

    private int linguaSelecionada = 0; //0 = ingles, 1 portugues 2 espanhol

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_objetos);

        //botao voltar
        ImageView voltar = findViewById(R.id.adicionarObjetos_voltar);
        voltar.setOnClickListener(v -> finish());

        //muda a imagem padrao do objeto por causa da lingua
        imagem = findViewById(R.id.adicionarObjetos_imagem);
        String lingua = getResources().getConfiguration().locale.getLanguage().trim();
        imagem.setImageDrawable(lingua.equalsIgnoreCase("pt") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ptg, null) : lingua.equalsIgnoreCase("es") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_esp, null) : ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ing, null));

        //ao clicar na imagem pra escolher a imagem
        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, selecionarFoto);
        });

        //variaveis de inputs
        imagemBackground = findViewById(R.id.adicionarObjetos_imagem_fundo);
        nome = findViewById(R.id.adicionarObjetos_input_nome);
        categoria = findViewById(R.id.adicionarObjetos_input_categoria);
        data = findViewById(R.id.adicionarObjetos_input_data);
        valor = findViewById(R.id.adicionarObjetos_valor);
        local = findViewById(R.id.adicionarObjetos_local);
        descricao = findViewById(R.id.adicionarObjetos_input_descricao);
        adicionar = findViewById(R.id.adicionarObjetos_botao);
        sentimental = findViewById(R.id.adicionarObjetos_valorSentimental);
        descricaoImagem = findViewById(R.id.adicionarObjetos_descricaoImg);
        linguas[1] = findViewById(R.id.adicionarObjetos_lingua_pt);
        linguas[0] = findViewById(R.id.adicionarObjetos_lingua_en);
        linguas[2] = findViewById(R.id.adicionarObjetos_lingua_es);

        linguas[1].setOnClickListener(v -> mudarLingua(1));
        linguas[0].setOnClickListener(v -> mudarLingua(0));
        linguas[2].setOnClickListener(v -> mudarLingua(2));

        linguaSelecionada = Locale.getDefault().getLanguage().equalsIgnoreCase("pt") ? 1 : Locale.getDefault().getLanguage().equalsIgnoreCase("es") ? 2 : 0;
        mudarLingua(linguaSelecionada);

        //botao adicionar
        adicionar.setOnClickListener(v -> adicionarObjetos());
    }

    private void mudarLingua(int lingua) {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        ColorMatrix matrix2 = new ColorMatrix();
        matrix2.setSaturation(1);
        ColorMatrixColorFilter filter2 = new ColorMatrixColorFilter(matrix2);

        linguaSelecionada = lingua;

        for (int i = 0; i < linguas.length; i++) {
            if (i == linguaSelecionada) {
                linguas[i].setColorFilter(filter2);
            } else {
                linguas[i].setColorFilter(filter);
            }
        }

    }

    private void adicionarObjetos() {

        //valores do input
        String vNome = nome.getText().toString().trim();
        String vCategoria = categoria.getText().toString().trim();
        String vData = data.getText().toString().trim();
        String vValor = valor.getText().toString().trim();
        String vLocal = local.getText().toString().trim();
        String vDescricao = descricao.getText().toString().trim();
        String vLingua = linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" : "en";
        String vImgDesc = descricaoImagem.getText().toString().trim();
        String vValorSen = sentimental.getText().toString().trim();

        boolean tudoOk = true;

        //verifica se todos os valores existem, caso não exista muda a cor pra vermelho e mostra um snackbar
        if (vNome.equals("") || vCategoria.equals("") || vValor.equals("") || !inseriuImagem || vLocal.equals("") || vImgDesc.equals("")) {
            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selecionarFoto && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            inseriuImagem = true;
            Glide.with(this).load(uri).transform(new CircleCrop()).into(imagem);
        }
    }
}