package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.InicoAdaptador;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VerificadorPermissoes;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private ViewPager2 viewPager2;
    private SlidingRootNav menuLateral;
    private Button menulateralBotaoEntrar;
    private Dialog popupCarregando;

    private Utils utils;

    //menu lateral
    private TextView texto1Txt, texto2Txt, texto3Txt, texto4Txt, faqTxt, suporteTxt, escanearTxt, adicionarTxt;
    private ImageView texto1Img, texto2Img, texto3Img, texto4Img, faqImg, suporteImg, escanearImg, adicionarImg;

    @Override
    protected void onStart() {
        super.onStart();
        atualizarBotaoEntrarMenuLateral();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);

        utils = new Utils(this);
        utils.verificarConta();

        //define o popup carregando
        popupCarregando = new Dialog(this);
        popupCarregando.setContentView(R.layout.popup_carregando);
        popupCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupCarregando.setCancelable(false);

        //adiciona o menu lateral
        menuLateral = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menulateral)
                .withRootViewScale(0.45f)
                .addDragListener(progress -> {
                    if (progress == 1 && viewPager2.getCurrentItem() == 1) { //caso o usuário abrir o menu enquanto a câmera estiver aberta, a camera fecha
                        viewPager2.setCurrentItem(0, true);
                    }
                })
                .inject();

        //view do menu lateral
        View menuView = menuLateral.getLayout().getRootView();

        //botão de entrar ou sair e listener
        menulateralBotaoEntrar = menuView.findViewById(R.id.menulateral_entrar);
        atualizarBotaoEntrarMenuLateral();

        texto1Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo1);
        texto2Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo2);
        texto3Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo3);
        texto4Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo4);
        texto1Img = menuView.findViewById(R.id.menulateral_textoinfo1);
        texto2Img = menuView.findViewById(R.id.menulateral_textoinfo2);
        texto3Img = menuView.findViewById(R.id.menulateral_textoinfo3);
        texto4Img = menuView.findViewById(R.id.menulateral_textoinfo4);
        texto1Txt.setOnClickListener(v -> abrirTexto(1));
        texto1Img.setOnClickListener(v -> abrirTexto(1));
        texto2Txt.setOnClickListener(v -> abrirTexto(2));
        texto2Img.setOnClickListener(v -> abrirTexto(2));
        texto3Txt.setOnClickListener(v -> abrirTexto(3));
        texto3Img.setOnClickListener(v -> abrirTexto(3));
        texto4Txt.setOnClickListener(v -> abrirTexto(4));
        texto4Img.setOnClickListener(v -> abrirTexto(4));

        //listeners do faq
        faqTxt = menuView.findViewById(R.id.menulateral_faqtitulo);
        faqImg = menuView.findViewById(R.id.menulateral_faq);
        faqTxt.setOnClickListener(v -> abrirFaq());
        faqImg.setOnClickListener(v -> abrirFaq());

        //listeners do suporte
        suporteImg = menuView.findViewById(R.id.menulateral_suporte);
        suporteTxt = menuView.findViewById(R.id.menulateral_suportetitulo);
        suporteImg.setOnClickListener(v -> abrirSuporte());
        suporteTxt.setOnClickListener(v -> abrirSuporte());

        //listeners do botão de adicionar objeto ou requisitar gerenciador
        adicionarImg = menuView.findViewById(R.id.menulateral_adicionar);
        adicionarTxt = menuView.findViewById(R.id.menulateral_adicionartitulo);

        //listeners das opções do menu lateral
        escanearImg = menuView.findViewById(R.id.menulateral_escanear);
        escanearTxt = menuView.findViewById(R.id.menulateral_escaneartitulo);
        escanearTxt.setOnClickListener(v -> abrirCamera());
        escanearImg.setOnClickListener(v -> abrirCamera());

        //cria o listener de clique pra abrir e fechar o menu dentro do fragmento
        View.OnClickListener clickListener = v -> mudarMenu();

        //adiciona o fragmento da tela inicial e camera
        viewPager2 = findViewById(R.id.inicio_viewpager2);
        InicoAdaptador adaptador = new InicoAdaptador(this, clickListener);
        viewPager2.setAdapter(adaptador);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { //callback pra permissao
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adaptador.getItemCount() - 1) {
                    VerificadorPermissoes.verificarPermissoes(MainActivity.this);
                    fecharMenu();
                }
            }
        });
    }

    private void abrirSuporte(){ //abre o aplicativo do email
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "apptrimonio@gmail.com" }); //email que será usado
        intent.putExtra(Intent.EXTRA_SUBJECT, "Apptrimônio - Support"); //titulo do email
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.support))); //abre o email
    }

    private void abrirFaq(){
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "faq");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirTexto(int num){
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "text"+num);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirAdicionarObjeto(){ //abre a tela de adicionar objetos
        startActivity(new Intent(MainActivity.this, AdicionarObjetos.class));
    }

    private void abrirCamera() {
        fecharMenu();
        viewPager2.setCurrentItem(1, true);
    }

    private void mudarMenu() { //abre ou fecha o menu
        if (menuLateral.isMenuOpened()) { //caso estiver aberto fecha
            menuLateral.closeMenu(true);
        } else { //caso estiver fechado abre
            menuLateral.openMenu(true);
        }
    }

    private void fecharMenu() { //fecha o menu caso estiver aberto (usado ao abrir a camera, evitando que ela seja aberta com o menu aberto)
        if (menuLateral.isMenuOpened()) {
            menuLateral.closeMenu(true);
        }
    }

    private void atualizarBotaoEntrarMenuLateral() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não houver usuário logado
            menulateralBotaoEntrar.setText(getResources().getString(R.string.login)); //muda o nome pra entrar
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_verde_escuro)); //muda a cor pra verde
            menulateralBotaoEntrar.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, Autenticar.class)); //abre a tela de autenticar
                utils.verificarConta();
            });
        } else { //caso houver
            menulateralBotaoEntrar.setText(getResources().getString(R.string.logoff)); //muda o nome pra sair
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_vermelho_escuro)); //muda a cor pra vermelha
            menulateralBotaoEntrar.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut(); //faz logout do firebase
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.madeLogoff), Toast.LENGTH_SHORT).show();
                atualizarBotaoEntrarMenuLateral();
                utils.verificarConta();
            });
        }
    }
}