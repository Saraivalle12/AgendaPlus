package com.example.agendaplus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Contacto> data;

    public CustomAdapter(Context context, ArrayList<Contacto> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        TextView tvNombre = (TextView) view.findViewById(R.id.tvILnombre);
        tvNombre.setText(data.get(position).getNombre());

        TextView tvTel = (TextView) view.findViewById(R.id.tvILTelefono);
        tvTel.setText(data.get(position).getTelefono());

        ImageView imageView = view.findViewById(R.id.imgILPhoto);

        if (data.get(position).getImagen().isEmpty()){
            imageView.setBackgroundResource(R.drawable.user_default);
        } else{
            imageView.setImageBitmap(Utiles.descomprimir(data.get(position).getImagen()));
        }


        return view;
    }
}
