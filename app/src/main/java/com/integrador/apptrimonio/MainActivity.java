package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.InicoAdaptador;
import com.integrador.apptrimonio.Utils.VerificadorPermissoes;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragListener;

public class MainActivity extends AppCompatActivity {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private ViewPager2 viewPager2;
    private SlidingRootNav menuLateral;
    private Button menulateralBotaoEntrar;

    //menu lateral
    private TextView texto1Txt, texto2Txt, texto3Txt, faqTxt, suporteTxt, escanearTxt, adicionarTxt;
    private ImageView texto1Img, texto2Img, texto3Img, faqImg, suporteImg, escanearImg, adicionarImg;

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

        View menuView = menuLateral.getLayout().getRootView(); //view do menu lateral
        menulateralBotaoEntrar = menuView.findViewById(R.id.menulateral_entrar);
        texto1Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo1);
        texto2Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo2);
        texto3Txt = menuView.findViewById(R.id.menulateral_textoinfotitulo3);
        texto1Img = menuView.findViewById(R.id.menulateral_textoinfo1);
        texto2Img = menuView.findViewById(R.id.menulateral_textoinfo2);
        texto3Img = menuView.findViewById(R.id.menulateral_textoinfo3);
        faqTxt = menuView.findViewById(R.id.menulateral_faqtitulo);
        faqImg = menuView.findViewById(R.id.menulateral_faq);
        suporteImg = menuView.findViewById(R.id.menulateral_suporte);
        suporteTxt = menuView.findViewById(R.id.menulateral_suportetitulo);
        escanearImg = menuView.findViewById(R.id.menulateral_escanear);
        escanearTxt = menuView.findViewById(R.id.menulateral_escaneartitulo);
        adicionarImg = menuView.findViewById(R.id.menulateral_adicionar);
        adicionarTxt = menuView.findViewById(R.id.menulateral_adicionartitulo);

        //listeners das opções do menu lateral
        escanearTxt.setOnClickListener(v -> abrirCamera());
        escanearImg.setOnClickListener(v -> abrirCamera());

        //muda o botao e adiciona o listener do menu entrar
        atualizarBotaoEntrarMenuLateral();

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
            menulateralBotaoEntrar.setText(getResources().getString(R.string.login));
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_verde_escuro));
            menulateralBotaoEntrar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Autenticar.class)));
        } else { //caso houver
            menulateralBotaoEntrar.setText(getResources().getString(R.string.logoff));
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_vermelho_escuro));
            menulateralBotaoEntrar.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                atualizarBotaoEntrarMenuLateral();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.madeLogoff), Toast.LENGTH_SHORT).show();
            });
        }
    }
}