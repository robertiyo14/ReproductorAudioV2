package com.izv.reproductoraudiov2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rober on 04/02/2015.
 */
public class Adaptador extends ArrayAdapter<String> {

    private Context contexto;
    private ArrayList<String> lista;
    private int recurso;
    static LayoutInflater i;


    public static class ViewHolder{
        public TextView titulo;
        public int posicion;
    }

    public Adaptador(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.contexto=context;
        this.lista=objects;
        this.recurso=resource;
        this.i = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("LOG", "" + lista.size());
        ViewHolder vh;
        if(convertView == null){
            convertView = i.inflate(recurso, null);
            vh = new ViewHolder();
            vh.titulo=(TextView)convertView.findViewById(R.id.tvTitulo);
            convertView.setTag(vh);
        }else{
            vh=(ViewHolder)convertView.getTag();
        }
        String s=vh.titulo.getText().toString();
        vh.posicion = position;
        vh.titulo.setText(lista.get(position));
        return convertView;

    }
}
