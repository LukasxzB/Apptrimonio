package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.R;

import org.json.JSONArray;

public class User {

    private static User instance;

    //variáveis do usuário
    private int xp;
    private String email;
    private boolean permissaoEditar, permissaoAdicionar, permissaoGerenciador, emailVerificado;
    private JSONArray codigos, objetosAdicionados, objetosVerificados;


    //get instance
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    //getters and setters
    public int getXp() { //retorna a quantidade de xp do usuário
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return 0; }
        return xp;
    }

    public void setXp(int xp) { //seta a quantidade de xp do usuário
        this.xp = xp;
    }

    public int getLevel() { //retorna o nível do usuário ex: nivel 29
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return 0; }
        double level = ((Math.sqrt((2025 + 120 * xp)) - 45) / 30) + 1;
        return (int) level;
    }

    public int getNextLevelXP() { //xp necessário pra atingir o próximo nível ex: nivel 29 ao 30: 450
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return 0; }
        return 15 * (getLevel() + 1);
    }

    private int getNextLevelXPTotal() { //xp TOTAL necessário pra atingir o próximo nível ex: 30 ao 31: 7425
        int level = getLevel();
        return (level * (45 + (15 * level))) / 2;
    }

    public int getXpNextLevelXP() { //xp já obtido para atingir o próximo nível ex: 10
        int nextLevelXpTotal = getNextLevelXPTotal();
        int nextLevelXp = getNextLevelXP();
        int necessario = nextLevelXpTotal - xp - nextLevelXp;
        return Math.abs(necessario);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPermissaoEditar() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return false; }
        return permissaoEditar;
    }

    public void setPermissaoEditar(boolean permissaoEditar) {
        this.permissaoEditar = permissaoEditar;
    }

    public boolean isPermissaoAdicionar() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return false; }
        return permissaoAdicionar;
    }

    public void setPermissaoAdicionar(boolean permissaoAdicionar) {
        this.permissaoAdicionar = permissaoAdicionar;
    }

    public boolean isPermissaoGerenciador() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return false; }
        return permissaoGerenciador;
    }

    public void setPermissaoGerenciador(boolean permissaoGerenciador) {
        this.permissaoGerenciador = permissaoGerenciador;
    }

    public boolean isEmailVerificado() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) { return false; }
        return emailVerificado;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public JSONArray getCodigos() {
        return codigos;
    }

    public void setCodigos(JSONArray codigos) {
        this.codigos = codigos;
    }

    public JSONArray getObjetosAdicionados() {
        return objetosAdicionados;
    }

    public void setObjetosAdicionados(JSONArray objetosAdicionados) {
        this.objetosAdicionados = objetosAdicionados;
    }

    public JSONArray getObjetosVerificados() {
        return objetosVerificados;
    }

    public void setObjetosVerificados(JSONArray objetosVerificados) {
        this.objetosVerificados = objetosVerificados;
    }

    public void adicionarObjetoEscaneado(String idObjeto){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            if(!codigos.toString().contains(idObjeto)){
                xp += 15;
                codigos.put(idObjeto);
            }
        }
    }
}
