package com.example.agendaplus.ui.agenda;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView listapersonas;
    private static ArrayList<Contacto> lista = new ArrayList<>();
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

        lista.clear();

        lista.addAll(ObtenerListaPersonas());
        lista.addAll(getContacts());

        CustomAdapter adapter = new CustomAdapter(getContext(), listapersonas, lista);
        listapersonas.setAdapter(adapter);

        listapersonas.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                if (childView != null) {
                    if (childView != null) {
                        int index = rv.getChildAdapterPosition(childView);
                        indice = index;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarProcedimiento_1(View root) {
        conexion = new SQLiteConexion(getContext(), Transacciones.NameDatabase, null, 1);
        listapersonas = root.findViewById(R.id.lista);
        listapersonas.setLayoutManager(new LinearLayoutManager(getContext()));

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

                            CustomAdapter adapter = new CustomAdapter(getContext(), listapersonas, lista);
                            listapersonas.setAdapter(adapter);
                            indice = -1;

                            Toast.makeText(getContext(), "Eliminado!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (indice >= 0) {
                        
                        //int cantidadEliminada = eliminarContactoPorId(getContext(), String.valueOf(lista.get(indice).getId()));

                        // Verificar si se eliminó correctamente
                        if (deleteContact(getContext(), lista.get(indice).getTelefono(), lista.get(indice).getNombre())) {
                            lista.remove(indice);

                            CustomAdapter adapter = new CustomAdapter(getContext(), listapersonas, lista);
                            listapersonas.setAdapter(adapter);
                            indice = -1;

                            Toast.makeText(getContext(), "Eliminado!", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getContext(), "No se eliminó el contacto del tel.", Toast.LENGTH_SHORT).show();
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
    }

    private ArrayList<Contacto> ObtenerListaPersonas() {
        SQLiteDatabase db = conexion.getReadableDatabase();

        ArrayList<Contacto> lista = new ArrayList<>();

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
            Toast.makeText(getContext(), "Error: no se pude obtener la lista de personas.", Toast.LENGTH_SHORT).show();
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

                boolean existe = false;

                for (Contacto elemento : contactList){
                    if (elemento.getNombre().equals(name)){
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    Contacto contact = new Contacto();
                    contact.setId(Integer.valueOf(id));
                    contact.setNombre(name);
                    contact.setTelefono(phoneNumber);
                    contact.setImagen(Utiles.comprimir(photo));

                    contactList.add(contact);

                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return contactList;
    }
    
    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
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