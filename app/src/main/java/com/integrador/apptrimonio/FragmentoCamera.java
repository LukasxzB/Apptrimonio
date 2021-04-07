package com.integrador.apptrimonio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class FragmentoCamera extends Fragment {

    private ViewPager2 viewPager2;
    private CompoundBarcodeView scannerView;

    private ImageView swipeUp;

    public FragmentoCamera(ViewPager2 vp) {
        viewPager2 = vp;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragmento_camera, container, false);

        scannerView = view.findViewById(R.id.camera_scanner); //pega o scanner view
        scannerView.decodeContinuous(scannerCallback); //define o callback
        scannerView.getChildAt(2).setVisibility(View.INVISIBLE); //remove o texto orginal

        swipeUp = view.findViewById(R.id.camera_swipe);
        Glide.with(this).load(R.drawable.swipeup).into(swipeUp);
        swipeUp.setRotationX(180f);

        return view;
    }

    private BarcodeCallback scannerCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                scannerView.setStatusText(result.getText());
            }

            //Do something with code result
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