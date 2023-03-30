package com.example.agendaplus.ui.perfil;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.agendaplus.R;
import com.example.agendaplus.Usuario;
import com.example.agendaplus.Utiles;
import com.example.agendaplus.configuracion.SQLiteConexion;
import com.example.agendaplus.configuracion.Transacciones;
import com.example.agendaplus.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private Usuario usuario;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.usuario = null;

        Integer id = (int) getActivity().getIntent().getSerializableExtra("id_usuario");

        cargarInformacion(root, id);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void cargarInformacion(View root, long id) {

        usuario = obtenerUsuario(id);

        TextView tvNombre = root.findViewById(R.id.tvNombre);
        TextView tvApellido = root.findViewById(R.id.tvApellido);
        TextView tvTelefono = root.findViewById(R.id.tvTelefono);
        TextView tvGenero = root.findViewById(R.id.tvGenero);
        TextView tvEdad = root.findViewById(R.id.tvEdad);
        TextView tvCorreo = root.findViewById(R.id.tvCorre);
        ImageView foto = root.findViewById(R.id.imgPersona);

        tvNombre.setText(usuario.getNombre());
        tvApellido.setText(usuario.getApellido());
        tvTelefono.setText(usuario.getTelefono());
        tvGenero.setText(usuario.getGenero() + "");
        tvEdad.setText(usuario.getEdad() + "");
        tvCorreo.setText(usuario.getCorreo());

        if (usuario.getImagen().isEmpty()){
            foto.setBackgroundResource(R.drawable.user_default);
        } else{
            foto.setImageBitmap(Utiles.descomprimir(usuario.getImagen()));
        }

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(Utiles.descomprimir(usuario.getImagen()));
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(imageView);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private Usuario obtenerUsuario(long id) {
        SQLiteConexion conexion = new SQLiteConexion(getContext(), Transacciones.NameDatabase, null, 1);
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

}