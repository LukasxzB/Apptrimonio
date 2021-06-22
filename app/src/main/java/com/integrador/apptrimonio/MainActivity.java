package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.InicoAdaptador;
import com.integrador.apptrimonio.Utils.UserInterface;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VerificadorPermissoes;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class MainActivity extends ActivityBase {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private ViewPager2 viewPager2;
    private SlidingRootNav menuLateral;
    private Button menulateralBotaoEntrar;

    private Utils utils;

    //menu lateral
    private ConstraintLayout perfil, texto1, texto2, texto3, texto4, faq, suporte, escanear, adicionar, aprovar;

    @Override
    protected void onStart() {
        super.onStart();
        atualizarBotoes();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("apptrimonio", MODE_PRIVATE);

        utils = new Utils(this);
        utils.verificarConta(login -> {
        });

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
        atualizarBotoes();

        //perfil
        perfil = menuView.findViewById(R.id.menulateral_perfil0);
        perfil.setOnClickListener(v -> abrirPerfil());

        //textos informativos
        texto1 = menuView.findViewById(R.id.menulateral_textoinfo10);
        texto1.setOnClickListener(v -> abrirTexto(1));
        texto2 = menuView.findViewById(R.id.menulateral_textoinfo20);
        texto2.setOnClickListener(v -> abrirTexto(2));
        texto3 = menuView.findViewById(R.id.menulateral_textoinfo30);
        texto3.setOnClickListener(v -> abrirTexto(3));
        texto4 = menuView.findViewById(R.id.menulateral_textoinfo40);
        texto4.setOnClickListener(v -> abrirTexto(4));

        //listeners do faq
        faq = menuView.findViewById(R.id.menulateral_faq0);
        faq.setOnClickListener(v -> abrirFaq());

        //listeners do suporte
        suporte = menuView.findViewById(R.id.menulateral_suporte0);
        suporte.setOnClickListener(v -> abrirSuporte());

        //listeners do botão de adicionar objeto
        adicionar = menuView.findViewById(R.id.menulateral_adicionar0);
        adicionar.setOnClickListener(v -> abrirAdicionarObjeto());

        //listeners de escanear
        escanear = menuView.findViewById(R.id.menulateral_escanear0);
        escanear.setOnClickListener(v -> abrirCamera());

        //listerens de aprovar
        aprovar = menuView.findViewById(R.id.menulateral_aprovar0);
        aprovar.setOnClickListener(v -> abrirAprovarObjeto());

        //cria o listener de clique pra abrir e fechar o menu dentro do fragmento
        View.OnClickListener clickListener = v -> mudarMenu();

        //adiciona o fragmento da tela inicial e camera
        viewPager2 = findViewById(R.id.inicio_viewpager2);
        InicoAdaptador adaptador = new InicoAdaptador(this, clickListener, this);
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

    private void abrirPerfil() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, getResources().getString(R.string.loginRequired), Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(MainActivity.this, Perfil.class);
            startActivity(intent);
        }
    }

    private void abrirSuporte() { //abre o aplicativo do email
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"apptrimonio@gmail.com"}); //email que será usado
        intent.putExtra(Intent.EXTRA_SUBJECT, "Apptrimônio - Support"); //titulo do email
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.support))); //abre o email
    }

    private void abrirFaq() {
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "faq");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirTexto(int num) {
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "text" + num);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirAdicionarObjeto() { //abre a tela de adicionar objetos
        startActivity(new Intent(MainActivity.this, AdicionarObjetos.class));
    }

    private void abrirAprovarObjeto() {

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

    private void atualizarBotoes() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não houver usuário logado
            menulateralBotaoEntrar.setText(getResources().getString(R.string.login)); //muda o nome pra entrar
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_verde_escuro)); //muda a cor pra verde
            menulateralBotaoEntrar.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, Autenticar.class)); //abre a tela de autenticar
            });
        } else { //caso houver
            menulateralBotaoEntrar.setText(getResources().getString(R.string.logoff)); //muda o nome pra sair
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_vermelho_escuro)); //muda a cor pra vermelha
            menulateralBotaoEntrar.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut(); //faz logout do firebase
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.madeLogoff), Toast.LENGTH_SHORT).show();
                atualizarBotoes();
            });
        }
    }
}