package com.integrador.apptrimonio.Utils;

import com.android.volley.VolleyError;

public interface VolleyInterface {
    void onResponse(String response);
    void onError(VolleyError error);
}
