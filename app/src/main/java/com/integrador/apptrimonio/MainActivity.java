package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.integrador.apptrimonio.Utils.AutenticadorAdaptador;
import com.integrador.apptrimonio.Utils.InicoAdaptador;
import com.integrador.apptrimonio.Utils.VerificadorPermissoes;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class MainActivity extends AppCompatActivity {

    //sharedpreferences
    private SharedPreferences sharedPreferences;

    //gadgets
    private ViewPager2 viewPager2;
    private ImageView botaoMenuLateral;
    private SlidingRootNav menuLateral;
    private Button menulateralBotaoEntrar;

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
                .inject();

        View menuView = menuLateral.getLayout().getRootView(); //view do menu lateral
        menulateralBotaoEntrar = menuView.findViewById(R.id.menulateral_entrar);

        //muda o botao e adiciona o listener do menu entrar
        atualizarBotaoEntrarMenuLateral();

        //cria o listener de clique pra abrir e fechar o menu dentro do fragmento
        View.OnClickListener clickListener = v -> mudarMenu();

        //adiciona o fragmento da tela inicial e camera
        viewPager2 = findViewById(R.id.inicio_viewpager2);
        InicoAdaptador adaptador = new InicoAdaptador(this, viewPager2, clickListener);
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

    private void mudarMenu() { //abre ou fecha o menu
        if (menuLateral.isMenuOpened()) { //caso estiver aberto fecha
            menuLateral.closeMenu();
        } else { //caso estiver fechado abre
            menuLateral.openMenu();
        }
    }

    private void fecharMenu() { //fecha o menu caso estiver aberto (usado ao abrir a camera, evitando que ela seja aberta com o menu aberto)
        if (menuLateral.isMenuOpened()) {
            menuLateral.closeMenu();
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