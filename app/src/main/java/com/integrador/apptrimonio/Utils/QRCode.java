package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.integrador.apptrimonio.R;

import java.util.HashMap;
import java.util.Map;

public class QRCode {

    public static Bitmap generateQRCode(String codigo, Context context) {
        int width = 1250;
        int height = 1250;

        //parametros
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        return createQRCode(codigo, charset, hintMap, height, width, context);
    }

    private static Bitmap createQRCode(String codigo, String charset, Map<EncodeHintType, ErrorCorrectionLevel> hintMap, int height, int width, Context context) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(codigo.getBytes(charset), charset), BarcodeFormat.QR_CODE, width, height, hintMap);

            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[matrixWidth * matrixHeight];

            // Gradient color draw from top to bottom
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {// QR code color
                        int red = (int) (92 - (92.0 - 242.0) / height * (y + 1));
                        int green = (int) (216 - (216.0 - 83.0) / height * (y + 1));
                        int blue = (int) (111 - (111.0 - 83.0) / height * (y + 1));
                        int colorInt = Color.argb(255, red, green, blue);
                        pixels[y * width + x] = bitMatrix.get(x, y) ? colorInt : 16777215;// 0x000000:0xffffff
                    } else {
                        pixels[y * width + x] = 0xffffffff;// background color
                    }
                }
            }


            Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);

            Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_apptrimonio_round);
            return merge(resize(overlay), bitmap);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            return null;
        }

    }

    private static Bitmap resize(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 375) / width;
        float scaleheight = ((float) 375) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleheight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private static Bitmap merge(Bitmap overlay, Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combinados = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combinados);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combinados;
    }
}
