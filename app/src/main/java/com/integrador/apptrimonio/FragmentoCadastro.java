package com.integrador.apptrimonio;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.UserInterface;
import com.integrador.apptrimonio.Utils.Utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class FragmentoCadastro extends Fragment {

    private final ViewPager2 viewPager2;
    private FirebaseAuth mAuth;
    private final Utils utils;

    private EditText inputEmail, inputSenha;
    private TextView inputEmailTop;
    private final Context context;
    private TextView inputSenhaTop;


    public FragmentoCadastro(ViewPager2 vp, Context context) {
        viewPager2 = vp;
        this.context = context;
        utils = new Utils(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_cadastro, container, false);

        inputEmail = view.findViewById(R.id.cadastro_input_email);
        inputSenha = view.findViewById(R.id.cadastro_input_senha);
        inputSenhaTop = view.findViewById(R.id.cadastro_input_senha_top);
        inputEmailTop = view.findViewById(R.id.cadastro_input_email_top);
        Button cadastrar = view.findViewById(R.id.cadastro_botao);
        TextView entrar = view.findViewById(R.id.cadastro_ja_possui);

        //ao clicar em ja possui conta
        entrar.setOnClickListener(v -> viewPager2.setCurrentItem(1, true));

        //ao clicar em cadastrar
        cadastrar.setOnClickListener(v -> fazerCadastro());

        return view;
    }

    private void loginCallback() {
        requireActivity().finish();
    }

    private void fazerCadastro() {
        String email = inputEmail.getText().toString().trim(); //email
        String senha = inputSenha.getText().toString().trim(); //senha

        boolean emailValido = EmailValidator.getInstance().isValid(email); //verifica se o email é valido
        boolean senhaValida = senha.length() >= 8; //verifica se a sneha é valida

        if (!emailValido) {
            inputEmailTop.setTextColor(getResources().getColor(R.color.vermelho));
            Utils.makeSnackbar(getResources().getString(R.string.invEmail), viewPager2.getRootView());
        } else {
            inputEmailTop.setTextColor(getResources().getColor(R.color.verde4));
        }

        if (!senhaValida) {
            inputSenhaTop.setTextColor(getResources().getColor(R.color.vermelho));
            Utils.makeSnackbar(getResources().getString(R.string.invPass), viewPager2.getRootView());
        } else {
            inputSenhaTop.setTextColor(getResources().getColor(R.color.verde4));
        }

        //faz cadastro
        if (senhaValida && emailValido) {
            utils.abrirPopUpCarregando();
            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                utils.fecharPopUpCarregando();
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.registerSucess), Toast.LENGTH_SHORT).show();

                    UserInterface callback = login -> loginCallback();
                    utils.verificarConta(callback);
                } else {
                    Log.w("LOGIN", "signUpWithEmail:failure", task.getException());
                    String mensagem = Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).equalsIgnoreCase("The email address is already in use by another account.") ? getResources().getString(R.string.emailUsedError) : task.getException().getMessage();
                    Utils.makeSnackbar(mensagem, viewPager2.getRootView());
                }
            });
        }
    }
}