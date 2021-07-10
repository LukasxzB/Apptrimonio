package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class FragmentoLogin extends Fragment {

    private final ViewPager2 viewPager2;

    private EditText inputEmail, inputSenha;
    private TextView inputEmailTop, inputSenhaTop;

    private FirebaseAuth mAuth;
    private final Utils utils;

    private Dialog dialogEsqueceuSenha;

    public FragmentoLogin(ViewPager2 vp, Context context) {
        viewPager2 = vp;
        utils = new Utils(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        dialogEsqueceuSenha = new Dialog(getContext());
        dialogEsqueceuSenha.setContentView(R.layout.popup_esqueceusenha);
        dialogEsqueceuSenha.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEsqueceuSenha.setCancelable(true);

        //ao clicar em fechar
        dialogEsqueceuSenha.findViewById(R.id.esqueceusenha_fechar).setOnClickListener(v12 -> dialogEsqueceuSenha.dismiss());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragmento_login, container, false);

        inputEmail = view.findViewById(R.id.login_input_email);
        inputSenha = view.findViewById(R.id.login_input_senha);
        inputEmailTop = view.findViewById(R.id.login_input_email_top);
        inputSenhaTop = view.findViewById(R.id.login_input_senha_top);
        Button entrar = view.findViewById(R.id.login_botao);
        TextView cadastrese = view.findViewById(R.id.login_nao_possui);
        TextView esqueceuSenha = view.findViewById(R.id.login_esqueceu_senha);

        //ao clicar em entrar
        entrar.setOnClickListener(v -> fazerLogin());

        //ao clicar em nao possui uma conta
        cadastrese.setOnClickListener(v -> viewPager2.setCurrentItem(0, true));

        //ao clicar em esqueceu a senha
        esqueceuSenha.setOnClickListener(v -> esqueceuSenha());

        return view;
    }

    private void loginCallback() {
        requireActivity().finish();
    }

    private void fazerLogin() {
        String email = inputEmail.getText().toString().trim(); //email
        String senha = inputSenha.getText().toString().trim(); //senha

        boolean emailValido = EmailValidator.getInstance().isValid(email); //caso o email for valido
        boolean senhaValida = senha.length() >= 8; //caso a senha tiver mais de 8 caracteres

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

        //faz login
        if (emailValido && senhaValida) {
            utils.abrirPopUpCarregando();
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(requireActivity(), task -> {
                        utils.fecharPopUpCarregando();
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "signInWithEmail:success");
                            Toast.makeText(getContext(), getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();

                            UserInterface callback = login -> loginCallback();
                            utils.verificarConta(callback);

                        } else {
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            String erro = Objects.requireNonNull(task.getException()).getMessage();
                            String mensagem = getResources().getString(R.string.loginError);
                            assert erro != null;
                            if (erro.equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                mensagem = getResources().getString(R.string.emailNotUsedError);
                            } else if (erro.equalsIgnoreCase("The password is invalid or the user does not have a password.")) {
                                mensagem = getResources().getString(R.string.passError);
                            } else if (erro.equalsIgnoreCase("The user account has been disabled by an administrator.")) {
                                mensagem = getResources().getString(R.string.loginBanned);
                            }

                            Utils.makeSnackbar(mensagem, viewPager2.getRootView());
                        }
                    });
        }
    }

    private void esqueceuSenha() {

        dialogEsqueceuSenha.show();

        EditText dialogEmailInput = dialogEsqueceuSenha.findViewById(R.id.esqueceusenha_input_email);
        TextView dialogEmailInputTop = dialogEsqueceuSenha.findViewById(R.id.esqueceusenha_input_emailtop);
        Button dialogBotao = dialogEsqueceuSenha.findViewById(R.id.esqueceusenha_botao);

        //ao clicar em enviar email
        dialogBotao.setOnClickListener(v1 -> {
            String dialogEmail = dialogEmailInput.getText().toString().trim();
            boolean dialogEmailValido = EmailValidator.getInstance().isValid(dialogEmail);

            if (!dialogEmailValido) {
                dialogEmailInputTop.setTextColor(getResources().getColor(R.color.vermelho));
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.sendEmailWait), Toast.LENGTH_SHORT).show();
                dialogEmailInputTop.setTextColor(getResources().getColor(R.color.verde4));
                FirebaseAuth.getInstance().sendPasswordResetEmail(dialogEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.sendEmailSuccess), Toast.LENGTH_SHORT).show();
                        dialogEsqueceuSenha.dismiss();
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.sendEmailFail) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}