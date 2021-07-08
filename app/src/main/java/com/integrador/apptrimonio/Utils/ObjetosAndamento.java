package com.integrador.apptrimonio.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ObjetosAndamento {

    private static ObjetosAndamento instance;
    private JSONArray objetos;

    public static ObjetosAndamento getInstance() {
        if (instance == null) {
            instance = new ObjetosAndamento();
        }
        return instance;
    }

    public void setObjetos(JSONArray objetos) {
        this.objetos = objetos;
    }

    public int getLength() {

        if (objetos == null) {
            return 0;
        }

        return objetos.length();
    }

    public JSONObject getNextObject() throws JSONException {
        return objetos.length() > 0 ? objetos.getJSONObject(0) : null;
    }

    public void removerObjeto() {
        if (objetos.length() > 0) {
            objetos.remove(0);
        }
    }
}
