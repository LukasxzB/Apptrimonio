package com.integrador.apptrimonio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

public class AdicionarObjetos extends ActivityBase {

    private boolean inseriuImagem = false;
    private int selecionarFoto = 1;
    private Uri uri;
    private RelativeLayout imagemBackground;
    private EditText nome, categoria, data, valor, local, descricao, sentimental;
    private Button adicionar;

    private ImageView imagem;

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
        if (lingua.equalsIgnoreCase("pt")) {
            imagem.setImageDrawable(getResources().getDrawable(R.drawable.addimg_ptg));
        } else if (lingua.equalsIgnoreCase("es")) {
            imagem.setImageDrawable(getResources().getDrawable(R.drawable.addimg_esp));
        } else {
            imagem.setImageDrawable(getResources().getDrawable(R.drawable.addimg_ing));
        }

        //ao clicar na imagem pra escolher a imagem
        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, selecionarFoto);
        });

        //variaveis de inputs
        imagemBackground = findViewById(R.id.adicionarObjetos_imagem_fundo);
        nome = (EditText)findViewById(R.id.adicionarObjetos_input_nome);
        categoria = (EditText)findViewById(R.id.adicionarObjetos_input_categoria);
        data = (EditText)findViewById(R.id.adicionarObjetos_input_data);
        valor = (EditText)findViewById(R.id.adicionarObjetos_valor);
        local = (EditText)findViewById(R.id.adicionarObjetos_local);
        descricao = (EditText)findViewById(R.id.adicionarObjetos_valorSentimental);
        adicionar = findViewById(R.id.adicionarObjetos_botao);

        //botao adicionar
        adicionar.setOnClickListener(v -> adicionarObjetos());
    }

    private void adicionarObjetos(){

        //valores do input
        String vNome = nome.getText().toString();
        String vCategoria = categoria.getText().toString();
        String vData = data.getText().toString();
        String vValor = valor.getText().toString();
        String vLocal = local.getText().toString();
        String vDescricao = descricao.getText().toString();

        boolean tudoOk = true;

        //verifica se todos os valores existem, caso não exista muda a cor pra vermelho e mostra um Toast
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