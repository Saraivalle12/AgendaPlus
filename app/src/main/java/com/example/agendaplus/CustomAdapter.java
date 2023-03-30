package com.example.agendaplus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Contacto> data;
    private List<Integer> mSelectedPositions = new ArrayList<>();

    private RecyclerView rv;
    public CustomAdapter(Context context, RecyclerView rv, ArrayList<Contacto> data) {
        this.context = context;
        this.data = data;
        this.rv = rv;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Contacto contacto = data.get(holder.getAdapterPosition());
        holder.bind(contacto);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void toggleSelection(int position) {
        if (mSelectedPositions.contains(position)) {
            mSelectedPositions.remove(Integer.valueOf(position));
        } else {
            mSelectedPositions.add(position);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNombre;
        public TextView tvTel;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tvILnombre);
            tvTel = view.findViewById(R.id.tvILTelefono);
            imageView = view.findViewById(R.id.imgILPhoto);
            linearLayout = view.findViewById(R.id.list_item_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        for (int i = 0; i < rv.getChildCount(); i++) {
                            View childView = rv.getChildAt(i);

                            LinearLayout ly = childView.findViewById(R.id.list_item_layout);

                            ly.setBackgroundColor(Color.TRANSPARENT);
                        }

                        linearLayout.setBackgroundColor(Color.YELLOW);
                }
            });
        }

        public void bind(Contacto contacto) {
            tvNombre.setText(contacto.getNombre());

            tvTel.setText(contacto.getTelefono());
            if (!contacto.getImagen().isEmpty()) {
                imageView.setImageBitmap(Utiles.descomprimir(contacto.getImagen()));
            }
        }
    }
}
