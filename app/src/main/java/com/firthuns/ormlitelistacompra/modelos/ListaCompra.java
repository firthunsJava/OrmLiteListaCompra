package com.firthuns.ormlitelistacompra.modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "listaCompra")
public class ListaCompra implements Parcelable {

    @DatabaseField(generatedId =  true, columnName = "id_compra")
    private int id;
    @DatabaseField(canBeNull = false)
    private String nombre;
    @DatabaseField(canBeNull = false)
    private float precio;
    @DatabaseField(columnName = "cantidad_compra")
    private  int cantidad;
    @DatabaseField()
    private float importeTotal;

    public ListaCompra() {
    }

    public ListaCompra(String nombre, float precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.importeTotal =  precio*cantidad;
    }

    public void recalcularImporteTotal(){
        this.importeTotal = cantidad + importeTotal;
    }

    protected ListaCompra(Parcel in) {
        nombre = in.readString();
        precio = in.readFloat();
        cantidad = in.readInt();
        importeTotal = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeFloat(precio);
        dest.writeInt(cantidad);
        dest.writeFloat(importeTotal);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaCompra> CREATOR = new Creator<ListaCompra>() {
        @Override
        public ListaCompra createFromParcel(Parcel in) {
            return new ListaCompra(in);
        }

        @Override
        public ListaCompra[] newArray(int size) {
            return new ListaCompra[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getImporteTotal() {

        importeTotal = cantidad * precio;
        return importeTotal;
    }

    public void setImporteTotal(int cantidad, float precio) {
        this.importeTotal = cantidad * precio;
//        this.importeTotal = importeTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //    public void setImporteTotal(float importeTotal) {
//        this.importeTotal = importeTotal;
//    }
}
