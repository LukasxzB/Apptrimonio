package com.integrador.apptrimonio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class FragmentoCamera extends Fragment {

    private CompoundBarcodeView scannerView;
    private boolean carregandoCodigo = false;

    public FragmentoCamera() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragmento_camera, container, false);

        scannerView = view.findViewById(R.id.camera_scanner); //pega o scanner view
        scannerView.decodeContinuous(scannerCallback); //define o callback
        scannerView.getChildAt(2).setVisibility(View.INVISIBLE); //remove o texto orginal

        ImageView swipeUp = view.findViewById(R.id.camera_swipe);
        Glide.with(this).load(R.drawable.swipeup).into(swipeUp);
        swipeUp.setRotationX(180f);

        return view;
    }

    private final BarcodeCallback scannerCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(!carregandoCodigo){ //boolean pra evitar que o código seja escaneado várias vezes ao mesmo tempo
                carregandoCodigo = true; //irá rodar apenas uma vez, depois de concluir a pesquisa irá rodar novamente

                String resultado = result.getText();
                String codigo = resultado;

                boolean valido = true;

                if(!resultado.contains("#$") || !resultado.contains("$#") || !resultado.contains("apptrimonio-")){ //caso não conter #$ $# e apptrimonio
                    valido = false;
                }

                try {
                    codigo = resultado.substring(resultado.indexOf("#$"), resultado.lastIndexOf("$#")).replace("#$apptrimonio-", "");
                }catch (Exception e){
                    valido = false;
                }

                carregandoCodigo = false;
                Toast.makeText(getContext(), codigo, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        scannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.pause();
    }
}