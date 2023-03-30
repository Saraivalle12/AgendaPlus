package com.example.agendaplus;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.agendaplus.configuracion.SQLiteConexion;
import com.example.agendaplus.configuracion.Transacciones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegistro extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACT = 2;
    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Spinner spGeneros = findViewById(R.id.generos);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeneros.setAdapter(adapter);

        Button btnGuardar = findViewById(R.id.btnSgte);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iniciarProcedimiento_1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnPicker = findViewById(R.id.btnPicker);

        btnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    iniciarProcedimiento_2();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iniciarProcedimiento_3();
        iniciarProcedimiento_4();
    }

    // Manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso es otorgado, crear un Intent para abrir la galería de imágenes
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                // Iniciar la actividad de selección de archivos
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
            } else {
                // Si el permiso es denegado, mostrar un mensaje al usuario o tomar otra acción
                Toast.makeText(this, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Obtener la imagen seleccionada y manipularla
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtener la imagen seleccionada
            Uri uri = data.getData();

            try {
                // Convertir la imagen seleccionada a un objeto Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = findViewById(R.id.imgPersona);
                // Manipular la imagen según sea necesario
                Drawable drawable = new BitmapDrawable(getResources(), bitmap); // Convierte el objeto Bitmap en un objeto Drawable

                imageView.setImageDrawable(drawable);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void iniciarProcedimiento_1() {
        EditText edtNombre = findViewById(R.id.edtNombre);
        EditText edtApellido = findViewById(R.id.edtApellido);
        EditText edtTelefono = findViewById(R.id.edtTelefono);
        EditText edtEdad = findViewById(R.id.edtEdad);
        EditText edtCorreo = findViewById(R.id.edtCorreo);
        Spinner spGenero = findViewById(R.id.generos);

        String nombre = edtNombre.getText().toString();
        String apellido = edtApellido.getText().toString();
        String telefono = edtTelefono.getText().toString();
        String edad = edtEdad.getText().toString();
        String correo = edtCorreo.getText().toString();
        String sexo = (String) spGenero.getSelectedItem();

        if (!esValido(nombre, apellido, edad, telefono)){
            return ;
        }
        // Obtener el ImageView
        ImageView imageView = findViewById(R.id.imgPersona);

        // Obtener el Drawable de la imagen
        Drawable drawable = imageView.getDrawable();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        Usuario us = new Usuario(nombre, apellido, telefono, Integer.valueOf(edad), sexo.charAt(0), correo, Utiles.comprimir(bitmap));

        Long id = guardar(us);

        if (id != -1) {
            Intent i = new Intent(this, MainActivity.class);

            i.putExtra("id_usuario", Integer.valueOf(String.valueOf(id)));

            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "IHa fallado el ingreso, inténtelo nuevamente.", Toast.LENGTH_SHORT).show();

        }
    }

    private void iniciarProcedimiento_2() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Si la versión de Android es menor que la API 22 (Lollipop 5.1), solicitar el permiso
            // Here, thisActivity is the current activity
            // Verificar si el permiso para leer el almacenamiento externo ha sido otorgado
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no ha sido otorgado, solicitarlo al usuario en tiempo de ejecución
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // Si el permiso es otorgado, crear un Intent para abrir la galería de imágenes
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");

                // Iniciar la actividad de selección de archivos
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
            }
        } else {
            // Si la versión de Android es igual o mayor que la API 22 (Lollipop 5.1), no es necesario solicitar el permiso
            // Si el permiso es otorgado, crear un Intent para abrir la galería de imágenes
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");

            // Iniciar la actividad de selección de archivos
            startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
        }
    }

    private void iniciarProcedimiento_3(){
       Usuario usuario = obtenerUsuario(1);

       if (usuario == null){

       } else{
           Intent i = new Intent(this, MainActivity.class);

           i.putExtra("id_usuario", 1);

           startActivity(i);
           finish();
       }
    }

    private void iniciarProcedimiento_4() {
        int REQUEST_READ_CONTACTS = 123;
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                REQUEST_READ_CONTACTS);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Si la versión de Android es menor que la API 22 (Lollipop 5.1), solicitar el permiso
            // Here, thisActivity is the current activity
            // Verificar si el permiso para leer el almacenamiento externo ha sido otorgado
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no ha sido otorgado, solicitarlo al usuario en tiempo de ejecución
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACT);
            } else {
            }
        } else {
        }


    }
    private Usuario obtenerUsuario(long id) {
        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        Usuario retorno = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tabla_usuario, null);

        try {
            while (cursor.moveToNext()) {
                Usuario obj = new Usuario();
                obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.id)));
                obj.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombre)));
                obj.setApellido(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.apellido)));
                obj.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
                obj.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.edad)));
                obj.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.correo)));
                obj.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.genero)).charAt(0));
                obj.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.imagen)));

                if (obj.getId() == id) {
                    retorno = obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.close();
        return retorno;
    }
    private Long guardar(Usuario obj) {
        Long id = -1l;

        try {
            SQLiteConexion conexion = new SQLiteConexion(this,
                    Transacciones.NameDatabase,
                    null,
                    1);

            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();

            valores.put(Transacciones.nombre, obj.getNombre());
            valores.put(Transacciones.apellido, obj.getApellido());
            valores.put(Transacciones.edad, obj.getEdad());
            valores.put(Transacciones.correo, obj.getCorreo());
            valores.put(Transacciones.telefono, obj.getTelefono());
            valores.put(Transacciones.genero, String.valueOf(obj.getGenero()));
            valores.put(Transacciones.imagen, obj.getImagen());

            id = db.insert(Transacciones.tabla_usuario, Transacciones.id, valores);

        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    private boolean esValido(String nombre, String apellido, String edad, String telefono) {
        boolean band = true;

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Debe escribir un nombre!", Toast.LENGTH_SHORT).show();
            band = false;
        }

        if (apellido.isEmpty()) {
            Toast.makeText(this, "Debe escribir un apellido!", Toast.LENGTH_SHORT).show();
            band = false;
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "Debe escribir un teléfono!", Toast.LENGTH_SHORT).show();
            band = false;
        }

        if (edad.isEmpty()) {
            Toast.makeText(this, "Debe ingresar una edad!", Toast.LENGTH_SHORT).show();
            band = false;
        }

        String regex = "^\\s*(\\+(\\d{1,3}))?\\s*(\\d{1,4}\\s*){1,3}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telefono);

        if (matcher.matches()) {

        } else {
            band = false;
            Toast.makeText(this, "Debe escribir un número telefónico correcto!", Toast.LENGTH_SHORT).show();
        }

        return band;
    }
}
