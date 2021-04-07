package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.validator.routines.EmailValidator;

public class FragmentoLogin extends Fragment {

    private ViewPager2 viewPager2;

    private EditText inputEmail, inputSenha;
    private Button entrar;
    private TextView cadastrese, esqueceuSenha, inputEmailTop, inputSenhaTop;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public FragmentoLogin(ViewPager2 vp) {
        viewPager2 = vp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragmento_login, container, false);

        inputEmail = view.findViewById(R.id.login_input_email);
        inputSenha = view.findViewById(R.id.login_input_senha);
        inputEmailTop = view.findViewById(R.id.login_input_email_top);
        inputSenhaTop = view.findViewById(R.id.login_input_senha_top);
        entrar = view.findViewById(R.id.login_botao);
        cadastrese = view.findViewById(R.id.login_nao_possui);
        esqueceuSenha = view.findViewById(R.id.login_esqueceu_senha);

        mUser = mAuth.getCurrentUser();

        //ao clicar em entrar
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerLogin();
            }
        });

        //ao clicar em nao possui uma conta
        cadastrese.setOnClickListener(v -> viewPager2.setCurrentItem(0, true));

        //ao clicar em esqueceu a senha
        esqueceuSenha.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.popup_esqueceusenha);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.show();

            EditText dialogEmailInput = dialog.findViewById(R.id.esqueceusenha_input_email);
            TextView dialogEmailInputTop = dialog.findViewById(R.id.esqueceusenha_input_emailtop);
            Button dialogBotao = dialog.findViewById(R.id.esqueceusenha_botao);
            ImageView dialogFechar = dialog.findViewById(R.id.esqueceusenha_fechar);

            //ao clicar em enviar email
            dialogBotao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dialogEmail = dialogEmailInput.getText().toString().trim();
                    boolean dialogEmailValido = EmailValidator.getInstance().isValid(dialogEmail);

                    if(!dialogEmailValido){
                        dialogEmailInputTop.setTextColor(getResources().getColor(R.color.vermelho));
                    }else{
                        Toast.makeText(getContext(), getResources().getString(R.string.sendEmailWait), Toast.LENGTH_SHORT).show();
                        dialogEmailInputTop.setTextColor(getResources().getColor(R.color.verde4));
                        FirebaseAuth.getInstance().sendPasswordResetEmail(dialogEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), getResources().getString(R.string.sendEmailSuccess), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(getContext(), getResources().getString(R.string.sendEmailFail) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

            //ao clicar em fechar
            dialogFechar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        });

        return view;
    }

    private void fazerLogin() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString().trim();

        boolean emailValido = EmailValidator.getInstance().isValid(email);

        if (!emailValido) {
            inputEmailTop.setTextColor(getResources().getColor(R.color.vermelho));
        } else {
            inputEmailTop.setTextColor(getResources().getColor(R.color.verde4));
        }

        //faz login
        if(emailValido){
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "signInWithEmail:success");
                            Toast.makeText(getContext(), getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getResources().getString(R.string.loginFail) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}