package com.example.agendaplus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Utiles {
    public static String comprimir(Bitmap imagen) {
        if (imagen == null){
            return "";
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imagenBytes = stream.toByteArray();

        String encoded = Base64.encodeToString(imagenBytes, Base64.DEFAULT);
        return encoded;
    }

    public static Bitmap descomprimir(String encodedString){
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }
}
