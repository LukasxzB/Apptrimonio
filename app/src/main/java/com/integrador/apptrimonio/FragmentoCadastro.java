package com.integrador.apptrimonio;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

public class FragmentoCadastro extends Fragment {

    private ViewPager2 viewPager2;
    private FirebaseAuth mAuth;

    private EditText inputEmail, inputSenha;
    private Button cadastrar;
    private TextView entrar, inputEmailTop, inputSenhaTop;

    public FragmentoCadastro(ViewPager2 vp) {
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
        View view = inflater.inflate(R.layout.fragment_fragmento_cadastro, container, false);

        inputEmail = view.findViewById(R.id.cadastro_input_email);
        inputSenha = view.findViewById(R.id.cadastro_input_senha);
        inputSenhaTop = view.findViewById(R.id.cadastro_input_senha_top);
        inputEmailTop = view.findViewById(R.id.cadastro_input_email_top);
        cadastrar = view.findViewById(R.id.cadastro_botao);
        entrar = view.findViewById(R.id.cadastro_ja_possui);

        //ao clicar em ja possui conta
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(1, true);
            }
        });

        //ao clicar em cadastrar
        cadastrar.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String senha = inputSenha.getText().toString().trim();

            boolean emailValido = EmailValidator.getInstance().isValid(email);
            boolean senhaValida = inputSenha.getText().toString().trim().length() >= 8 && inputSenha.getText().toString() != null;

            if (!emailValido) {
                inputEmailTop.setTextColor(getResources().getColor(R.color.vermelho));
            } else {
                inputEmailTop.setTextColor(getResources().getColor(R.color.verde4));
            }

            if (!senhaValida) {
                inputSenhaTop.setTextColor(getResources().getColor(R.color.vermelho));
            } else {
                inputSenhaTop.setTextColor(getResources().getColor(R.color.verde4));
            }

            //faz cadastro
            if (senhaValida && emailValido) {
                mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), getResources().getString(R.string.registerSucess), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Log.w("LOGIN", "signUpWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getResources().getString(R.string.registerFail) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}