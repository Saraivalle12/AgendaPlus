package com.example.agendaplus.ui.agenda;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.agendaplus.ActivityContacto;
import com.example.agendaplus.Contacto;
import com.example.agendaplus.CustomAdapter;
import com.example.agendaplus.R;
import com.example.agendaplus.Utiles;
import com.example.agendaplus.configuracion.SQLiteConexion;
import com.example.agendaplus.configuracion.Transacciones;
import com.example.agendaplus.databinding.FragmentAgendaBinding;

import java.util.ArrayList;

public class AgendaFragment extends Fragment {

    private FragmentAgendaBinding binding;
    private ListView listapersonas;
    private ArrayList<Contacto> lista;
    private SQLiteConexion conexion;

    private int indice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAgendaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.indice = -1;

        iniciarProcedimiento_1(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        ObtenerListaPersonas();

        CustomAdapter adapter = new CustomAdapter(getContext(), lista);
        listapersonas.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarProcedimiento_1(View root) {
        conexion = new SQLiteConexion(getContext(), Transacciones.NameDatabase, null, 1);
        listapersonas = (ListView) root.findViewById(R.id.lista);

        ObtenerListaPersonas();

        CustomAdapter adapter = new CustomAdapter(getContext(), lista);
        listapersonas.setAdapter(adapter);

        listapersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listapersonas.getChildCount(); i++) {
                    View v = listapersonas.getChildAt(i);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }

                View v = listapersonas.getChildAt(position);
                v.setBackgroundColor(Color.YELLOW);

                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
                indice = position;
            }
        });

        Button btnRemover = root.findViewById(R.id.btnEliminar);

        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (indice >= 0) {

                        int cantidadEliminada = eliminarDesdeDB(lista.get(indice).getId());

                        // Verificar si se eliminó correctamente
                        if (cantidadEliminada > 0) {
                            lista.remove(indice);

                            CustomAdapter adapter = new CustomAdapter(getContext(), lista);
                            listapersonas.setAdapter(adapter);
                            indice = -1;

                            Toast.makeText(getContext(), "Eliminado!", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Hubo un error al eliminar, inténtelo de nuevo!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnAgregar = root.findViewById(R.id.btnAgregar);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityContacto.class);

                startActivity(intent);
            }
        });

        /*listapersonas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Verificar si el permiso para leer el almacenamiento externo ha sido otorgado
                /*if (ContextCompat.checkSelfPermission(ActivityLista.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso no ha sido otorgado, solicitarlo al usuario en tiempo de ejecución
                    ActivityCompat.requestPermissions(ActivityLista.this, new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSIONS_REQUEST_CALL);
                } else {
                    if (indice < 0){
                        Toast.makeText(ActivityLista.this, "Debe seleccionar un elemento!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    Contacto obj = lista.get(indice);

                    // Crear un objeto Builder para el diálogo
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLista.this);
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

                            String str = obj.getPais();
                            Pattern pattern = Pattern.compile("\\((.*?)\\)"); // expresión regular
                            Matcher matcher = pattern.matcher(str);

                            Toast.makeText(ActivityLista.this, str, Toast.LENGTH_SHORT).show();
                            if (matcher.find()) {
                                str = matcher.group(1);
                                telefono = str + telefono;
                            }

                            intent.setData(Uri.parse("tel:" + telefono));

                            // Verificar si la aplicación tiene permiso para realizar llamadas telefónicas
                            if (ActivityCompat.checkSelfPermission(ActivityLista.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // Si la aplicación no tiene permiso, solicitar permiso
                                ActivityCompat.requestPermissions(ActivityLista.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
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

                return false;
            }
        });*/
    }

    private void ObtenerListaPersonas() {
        SQLiteDatabase db = conexion.getReadableDatabase();

        lista = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tabla_concacto, null);

        try {
            while (cursor.moveToNext()) {
                Contacto obj = new Contacto();
                obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.id)));
                obj.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombre)));
                obj.setApellido(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.apellido)));
                obj.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.telefono)));
                obj.setEdad(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.edad)));
                obj.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.correo)));
                obj.setGenero(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.genero)).charAt(0));
                obj.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.imagen)));

                lista.add(obj);
            }
        } catch (Exception e) {
            lista.clear();
            e.printStackTrace();
        }

        cursor.close();

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

    private int eliminarDesdeDB(int id) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        // Definir la cláusula WHERE para eliminar el registro deseado
        String seleccion = "id = ?";
        String[] argumentosSeleccion = {"" + id};

        // Eliminar el registro
        int cantidadEliminada = db.delete(
                Transacciones.tabla_concacto,
                seleccion,
                argumentosSeleccion
        );

        return cantidadEliminada;
    }
}