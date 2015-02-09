package com.izv.reproductoraudiov2;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class Principal extends Activity {

    private static final int RECORD=0;
    private ArrayList canciones;
    private ArrayList titulos;
    private Adaptador ad;
    private TextView tvTitulo;
    private boolean reproduciendo;
    private Button btPlay;
    private int cancionActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        btPlay = (Button)findViewById(R.id.btPlay);
        canciones = new ArrayList();
        titulos = new ArrayList();
        reproduciendo = false;
        tvTitulo = (TextView)findViewById(R.id.tvTitulo);
        ad= new Adaptador(this, R.layout.detalle,getData());
        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), canciones.get(i).toString(),Toast.LENGTH_LONG).show();
                tvTitulo.setText(titulos.get(i).toString());
                Intent intent = new Intent(Principal.this, ServicioAudio.class);
                intent.setAction(ServicioAudio.STOP);
                startService(intent);
                intent = new Intent(Principal.this, ServicioAudio.class);
                intent.putExtra("cancion", canciones.get(i).toString());
                intent.setAction(ServicioAudio.ADD);
                startService(intent);
                intent = new Intent(Principal.this, ServicioAudio.class);
                intent.setAction(ServicioAudio.PLAY);
                startService(intent);
                cancionActual = i;
                reproduciendo = true;
            }
        });
        registerForContextMenu(lv);
    }

    private ArrayList<String> getData() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proyeccion = null;
        String condicion = null;
        String[] parametros = null;
        String orden = null;
        Cursor c = getContentResolver().query(
                uri,
                proyeccion,
                condicion,
                parametros,
                orden);
        String ruta = "";
        String titulo = "";
        c.getColumnNames();
        c.moveToFirst();
        int i = 0;
        while(!c.isAfterLast()){
            ruta = c.getString(c.getColumnIndex("_data"));
            titulo = c.getString(c.getColumnIndex("title"));
            canciones.add(ruta);
            titulos.add(titulo);
            c.moveToNext();
            i++;
        }
        return titulos;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addSong) {
//            add();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pause(View v){
        if(reproduciendo){
            reproduciendo = false;
            btPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else {
            reproduciendo = true;
            btPlay.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        Intent intent = new Intent(this, ServicioAudio.class);
        intent.setAction(ServicioAudio.PAUSE);
        startService(intent);
    }

    public void next(View v){
        if(cancionActual<canciones.size()-1){
            cancionActual++;
            tvTitulo.setText(titulos.get(cancionActual).toString());
            Intent intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.STOP);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.putExtra("cancion", canciones.get(cancionActual).toString());
            intent.setAction(ServicioAudio.ADD);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.PLAY);
            startService(intent);
            reproduciendo = true;
        }else{
            cancionActual = 0;
            tvTitulo.setText(titulos.get(0).toString());
            Intent intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.STOP);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.putExtra("cancion", canciones.get(0).toString());
            intent.setAction(ServicioAudio.ADD);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.PLAY);
            startService(intent);
            reproduciendo = true;
        }
    }

    public void prev(View v){
        if(cancionActual != 0){
            cancionActual--;
            tvTitulo.setText(titulos.get(cancionActual).toString());
            Intent intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.STOP);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.putExtra("cancion", canciones.get(cancionActual).toString());
            intent.setAction(ServicioAudio.ADD);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.PLAY);
            startService(intent);
            reproduciendo = true;
        }else{
            cancionActual = canciones.size()-1;
            tvTitulo.setText(titulos.get(cancionActual).toString());
            Intent intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.STOP);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.putExtra("cancion", canciones.get(cancionActual).toString());
            intent.setAction(ServicioAudio.ADD);
            startService(intent);
            intent = new Intent(Principal.this, ServicioAudio.class);
            intent.setAction(ServicioAudio.PLAY);
            startService(intent);
            reproduciendo = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ServicioAudio.class));
    }

    public void record(View v){
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, RECORD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RECORD) {
            Uri uri = data.getData();
            ad.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ad.notifyDataSetChanged();
    }

    public void rdm(View v){
        cancionActual =(int)Math.floor(Math.random()*(0-((canciones.size()-1)+1))+((canciones.size()-1)));
        tvTitulo.setText(titulos.get(cancionActual).toString());
        Intent intent = new Intent(Principal.this, ServicioAudio.class);
        intent.setAction(ServicioAudio.STOP);
        startService(intent);
        intent = new Intent(Principal.this, ServicioAudio.class);
        intent.putExtra("cancion", canciones.get(cancionActual).toString());
        intent.setAction(ServicioAudio.ADD);
        startService(intent);
        intent = new Intent(Principal.this, ServicioAudio.class);
        intent.setAction(ServicioAudio.PLAY);
        startService(intent);
        reproduciendo = true;
    }
}
