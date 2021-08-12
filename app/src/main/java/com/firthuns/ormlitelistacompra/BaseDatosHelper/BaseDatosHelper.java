package com.firthuns.ormlitelistacompra.BaseDatosHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.firthuns.ormlitelistacompra.configuraciones.Configuracion;
import com.firthuns.ormlitelistacompra.modelos.ListaCompra;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class BaseDatosHelper extends OrmLiteSqliteOpenHelper {

    // 3)   Integer-> cable primaria
    // cada TABLA va acompañado de su DAO
    private Dao<ListaCompra, Integer> daoListaCompra;
    // UNA VEZ CREADO LA LINEA ANTERIOR, VAMOS SOBRE daoListaCompra(SHOW ACTION  CREAREL METODO GETTER)

//1)
    public BaseDatosHelper(Context context) {
        super(context, Configuracion.DB_NAME, null, Configuracion.DB_VERSION);
    }

//    2)
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
            // Aqui ya no hace falta crear las tablas, lo hace automativa el orm
        try {

            TableUtils.createTable(connectionSource, ListaCompra.class);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {    }

//    4)
    public Dao<ListaCompra, Integer> getDaoListaCompra() throws SQLException {

        if (daoListaCompra == null) {
            daoListaCompra = this.getDao(ListaCompra.class);
        }

        return daoListaCompra;
    }
}
