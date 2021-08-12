package com.firthuns.ormlitelistacompra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firthuns.ormlitelistacompra.BaseDatosHelper.BaseDatosHelper;
import com.firthuns.ormlitelistacompra.MainActivity;
import com.firthuns.ormlitelistacompra.R;
import com.firthuns.ormlitelistacompra.modelos.ListaCompra;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class ListaCompraAdapter extends RecyclerView.Adapter<ListaCompraAdapter.ListaCompraVH> {

    private List<ListaCompra> objects;
    private int resource;
    private  Context context;

    private NumberFormat numberFormat;

    // base de datos ORM lite
    private BaseDatosHelper helper;
    private Dao<ListaCompra, Integer> daoListaCompra;


    // por ultimo creamo un constructor con todos nuestros atributos
    public ListaCompraAdapter(List<ListaCompra> objects, int resource, Context context) {
        this.objects = objects;
        this.resource = resource;
        this.context = context;
        numberFormat = NumberFormat.getCurrencyInstance();

        // Instanciar la base de datos
        helper = OpenHelperManager.getHelper(context, BaseDatosHelper.class);

        if( helper != null){
            try {
                daoListaCompra = helper.getDaoListaCompra();
//                List<ListaCompra> temp = daoListaCompra.queryForAll(); //
//                objects.addAll(daoListaCompra.queryForAll());
//                notifyDataSetChanged();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @NonNull
    @Override
    public ListaCompraVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listaCompraItem = LayoutInflater.from(context). inflate(resource,null);
        // el siguiente codigo, me hace que el contenido del recyclerView, tome el ancjo de la pantalla
        listaCompraItem.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ListaCompraVH(listaCompraItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCompraVH holder, int position) {
        ListaCompra listaCompra = objects.get(position);

        // Asignamos valores a los atributos del holder.

        holder.txtProducto.setText(listaCompra.getNombre());
        holder.txtCantidad.setText(String.valueOf(listaCompra.getCantidad()));
        holder.txtPrecio.setText(numberFormat.format(listaCompra.getPrecio()));


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Boton Borrar en la posicion: " + position, Toast.LENGTH_SHORT).show();

                presentAlertDelete(holder.getAdapterPosition() );

                // hay que notificar a la base de datos su borrado
//                refTareasImpresora.setValue(objects);
            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Modificamos posicion "+position, Toast.LENGTH_SHORT).show();
                editfila(position).show();
                return false;
            }
        });

    }

    private void presentAlertDelete(int position) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Borrar?")
                .setMessage("¿estás seguro?")
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        try {
                            daoListaCompra.delete(objects.get(position));
                            objects.remove(position);
                            ((MainActivity)context).calcularEstadistica();
                            notifyItemRemoved(position);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }


                    }
                }).show();
    }

    private AlertDialog editfila(int position) {
        androidx.appcompat.app.AlertDialog.Builder contructor = new androidx.appcompat.app.AlertDialog.Builder(context);
        View listaCompraAlert = LayoutInflater.from(context).inflate(R.layout.alerta_listacompra, null);
        final EditText txtProducto = listaCompraAlert.findViewById(R.id.txtProductoAlerta);
        final EditText txtCantidad = listaCompraAlert.findViewById(R.id.txtCantidadAlerta);
        final EditText txtPrecio = listaCompraAlert.findViewById(R.id.txtPrecioAlerta);

        txtProducto.setText(objects.get(position).getNombre());


        contructor.setTitle("Modificar una compra");
        contructor.setView(listaCompraAlert);
        contructor.setNegativeButton("Cancelar", null);
        contructor.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListaCompra listaCompra = new ListaCompra(); // declaramos nuestro modelo
                objects.get(position).setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                objects.get(position).setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
//                    objects.set(position,listaCompra);
                try {
                    daoListaCompra.update(objects.get(position));
                    ((MainActivity)context).calcularEstadistica();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                notifyDataSetChanged();

            }
        });

        return contructor.create();
    }

//    private AlertDialog editfila(int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ListaCompraAdapter.class);
//
//        return builder.create();
//    }

    @Override
    public int getItemCount() {
          return objects.size();
    }



    // 1º la  clase ListaCompraVH, no es una clase normal, sino que tyiene que heredar de....
    public class ListaCompraVH extends RecyclerView.ViewHolder {
            TextView txtProducto, txtCantidad, txtPrecio;
            ImageButton btnDelete;
            View card;

        // El View que recibe, es la fila inflada
        public ListaCompraVH(@NonNull View itemView) {
            super(itemView);
            txtProducto = itemView.findViewById(R.id.txtProductoFilaCard);
            txtCantidad = itemView.findViewById(R.id.txtCantidadFilaCard);
            txtPrecio = itemView.findViewById(R.id.txtPrecioFilaCard);
            btnDelete = itemView.findViewById(R.id.imgBtnDelete);
            card = itemView.findViewById(R.id.cardFila);
        }
    }
}
