package com.example.agendaplus.ui.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.agendaplus.Contacto;
import com.example.agendaplus.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.agendaplus.Utiles;
import com.example.agendaplus.configuracion.SQLiteConexion;
import com.example.agendaplus.configuracion.Transacciones;
import com.example.agendaplus.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SQLiteConexion conexion;
    private static final int PERMISSIONS_REQUEST_CALL = 1;
    private static final int CALL_REQUEST = 2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        conexion = new SQLiteConexion(getContext(), Transacciones.NameDatabase, null, 1);

        iniciarProcedimiento_1(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarProcedimiento_1(View root) {
        ArrayList<String> listaString = new ArrayList<>();
        ArrayList<Contacto> lista = new ArrayList<>();

        lista.addAll(obtenerListaContactosDB());

        for (Contacto obj : lista) {
            listaString.add(obj.getNombre() + " - " + obj.getTelefono());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listaString);

        Spinner spContactos = root.findViewById(R.id.spContactos);

        spContactos.setAdapter(adapter);

        Button btnLlamar = root.findViewById(R.id.btnLlamar);

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si el permiso para leer el almacenamiento externo ha sido otorgado
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso no ha sido otorgado, solicitarlo al usuario en tiempo de ejecución
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL);
                } else {
                    int indice = spContactos.getSelectedItemPosition();

                    if (indice < 0) {
                        Toast.makeText(getContext(), "Debe seleccionar un elemento!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Contacto obj = lista.get(indice);

                    // Crear un objeto Builder para el diálogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Acción");

                    // Establecer el mensaje del diálogo
                    builder.setMessage(String.format("Desea llamar a %s con teléfono : %s", obj.getNombre(), obj.getTelefono()));

                    // Agregar un botón "Aceptar" al diálogo
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // Crear un objeto Intent con la acción ACTION_CALL
                            Intent intent = new Intent(Intent.ACTION_CALL);

                            // Establecer el número de teléfono a llamar
                            String telefono = obj.getTelefono();

                            intent.setData(Uri.parse("tel:" + telefono));

                            // Verificar si la aplicación tiene permiso para realizar llamadas telefónicas
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // Si la aplicación no tiene permiso, solicitar permiso
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                            } else {
                                // Si la aplicación tiene permiso, iniciar la llamada
                                startActivity(intent);
                            }
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    // Crear el diálogo y mostrarlo
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });

        Button btnSms = root.findViewById(R.id.btnSms);

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso no ha sido otorgado, solicitarlo al usuario en tiempo de ejecución
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},
                            PERMISSIONS_REQUEST_CALL);
                } else {
                    int indice = spContactos.getSelectedItemPosition();
                    // Número de teléfono del destinatario
                    Contacto obj = lista.get(indice);
                    String phoneNumber = obj.getTelefono();

                    EditText edtMsg = root.findViewById(R.id.edtMsg);
                    // Mensaje que deseas enviar
                    String message = edtMsg.getText().toString();

                    // Obtener instancia de SmsManager
                    SmsManager smsManager = SmsManager.getDefault();

                    try {
                        // Enviar el mensaje
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private ArrayList<Contacto> obtenerListaContactosDB() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contacto obj = null;
        ArrayList<Contacto> lista = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tabla_concacto, null);

        try {
            while (cursor.moveToNext()) {
                obj = new Contacto();
                obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.id_ct)));
                obj.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombre_ct)));
                obj.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono_ct)));
                obj.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.imagen_ct)));

                lista.add(obj);
            }
        } catch (Exception e) {
            lista.clear();
            e.printStackTrace();
        }

        cursor.close();

        return lista;
    }

    private ArrayList<Contacto> getContacts() {
        ArrayList<Contacto> contactList = new ArrayList<>();
        String[] projection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Bitmap photo = null;
                if (photoUri != null) {
                    try {
                        photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                Uri.parse(photoUri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Contacto contact = new Contacto();
                contact.setId(Integer.valueOf(id));
                contact.setNombre(name);
                contact.setTelefono(phoneNumber);
                contact.setImagen(Utiles.comprimir(photo));
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return contactList;
    }
}