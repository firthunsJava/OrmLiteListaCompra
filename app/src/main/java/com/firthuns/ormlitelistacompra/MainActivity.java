package com.firthuns.ormlitelistacompra;

import android.content.DialogInterface;
import android.os.Bundle;

import com.firthuns.ormlitelistacompra.BaseDatosHelper.BaseDatosHelper;
import com.firthuns.ormlitelistacompra.adapters.ListaCompraAdapter;
import com.firthuns.ormlitelistacompra.databinding.ActivityMainBinding;
import com.firthuns.ormlitelistacompra.modelos.ListaCompra;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    //RecyclerView
    private ListaCompraAdapter adapter;
    private int resource = R.layout.fila_listacompra_card;
    private RecyclerView.LayoutManager lm;
    private List<ListaCompra> listadoCompra;


    private NumberFormat formatoImporte;
    float importeTotal;

    // base de datos ORM lite
    private BaseDatosHelper helper;
    private Dao<ListaCompra, Integer> daoListaCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Instanciar la base de datos
        helper = OpenHelperManager.getHelper(this, BaseDatosHelper.class);



// instanciaremos el recycler
        listadoCompra = new ArrayList<>();
        lm = new LinearLayoutManager(this);
        adapter = new ListaCompraAdapter(listadoCompra, resource, this);
        binding.contenedor.recyclerView.setHasFixedSize(true);
        binding.contenedor.recyclerView.setAdapter(adapter);
        binding.contenedor.recyclerView.setLayoutManager(lm);
        binding.contenedor.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        if( helper != null){
            try {
                daoListaCompra = helper.getDaoListaCompra();
//                List<ListaCompra> temp = daoListaCompra.queryForAll(); //
                listadoCompra.addAll(daoListaCompra.queryForAll());
                adapter.notifyDataSetChanged();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }



        formatoImporte = NumberFormat.getCurrencyInstance();

       binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                anyadirCompra().show();

            }
        });
    }

//    PREGUNTAR EDU EL ONRESUME
    @Override
    protected void onResume() {
        super.onResume();

//        binding.contenedor.recyclerView.setAdapter(adapter);
        listadoCompra.clear();

        try {
            listadoCompra.addAll(daoListaCompra.queryForAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    private AlertDialog anyadirCompra() {
            AlertDialog.Builder contructor = new AlertDialog.Builder(this);
            View listaCompraAlert = LayoutInflater.from(this).inflate(R.layout.alerta_listacompra, null);

            final EditText txtProducto = listaCompraAlert.findViewById(R.id.txtProductoAlerta);
            final EditText txtCantidad = listaCompraAlert.findViewById(R.id.txtCantidadAlerta);
            final EditText txtPrecio = listaCompraAlert.findViewById(R.id.txtPrecioAlerta);

            contructor.setTitle("Agrega una compra");
            contructor.setView(listaCompraAlert);
            contructor.setNegativeButton("Cancelar", null);
            contructor.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ListaCompra listaCompra = new ListaCompra(); // declaramos nuestro modelo

                    listaCompra.setNombre(txtProducto.getText().toString());
                    listaCompra.setCantidad(Integer.parseInt(txtCantidad.getText().toString()));
                    listaCompra.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));


//                    listadoCompra.add(listaCompra);

                    if(daoListaCompra != null){
                        try {
                          int lastID =   daoListaCompra.create(listaCompra);
                            listadoCompra.add(listaCompra);
                            listaCompra.setId(lastID);
//                            adapter.notifyDataSetChanged(listaCompra);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                    calcularEstadistica();
                    adapter.notifyDataSetChanged();

                }
            });

        return contructor.create();
    }


   public void calcularEstadistica(){
        importeTotal= 0f;

        for (ListaCompra l : listadoCompra){

            importeTotal += l.getImporteTotal();
        }
       Toast.makeText(MainActivity.this, " "+ importeTotal, Toast.LENGTH_LONG).show();
        binding.contenedor.txtImporteTotalMain.setText(formatoImporte.format(importeTotal));

    }

}