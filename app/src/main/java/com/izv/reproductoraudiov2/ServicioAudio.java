package com.izv.reproductoraudiov2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServicioAudio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    private MediaPlayer mp;
    public static final String PLAY = "play";
    public static final String STOP = "stop";
    public static final String ADD = "add";
    public static final String PAUSE = "pause";
    private String rutaCancion = null;
    private boolean reproducir;
    private List<String> canciones;
    private enum Estados{
        idle,
        initialized,
        preparing,
        prepared,
        started,
        paused,
        completed,
        stopped,
        end,
        error
    };
    private Estados estado;
    /*********************************/
    /***********CONSTRUCTOR***********/
    /*********************************/

    public ServicioAudio() {
    }
    /*******************************************/
    /***********Metodos Sobreescritos***********/
    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int r = am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        estado = Estados.idle;
    }

    /*******************************************/

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mp.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
//                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mp.setVolume(0.1f, 0.1f);
                break;
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        estado = Estados.prepared;
        if(reproducir){
            mp.start();
            estado = Estados.started;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        estado = Estados.completed;
//        mp.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Log ","aaaaaa");
        String action = intent.getAction();
        Log.v("Log ","Accion: "+action);
        String dato = intent.getStringExtra("cancion");
        Log.v("Log ","Dato: "+rutaCancion+"-"+dato);
        if(action.equals("play")){
            play();
        }else if(action.equals("add")){
            add(dato);
        }else if(action.equals("stop")){
            stop();
        }else if(action.equals("pause")){
            pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /****************************************/
    /***********Metodos de Control***********/
    /****************************************/
    private void play(){
        Log.v("Log ","PLAY");
        if(rutaCancion != null){
            if(estado == Estados.error){
                estado = Estados.idle;
            }
            if(estado==Estados.idle){
                reproducir = true;
                try {
                    mp.setDataSource(rutaCancion);
                    estado = Estados.initialized;
                } catch (IOException e) {
                    estado = Estados.error;
                    e.printStackTrace();
                }
            }
            if(estado==Estados.initialized || estado == Estados.stopped){
                reproducir = true;
                mp.prepareAsync();
                estado = Estados.preparing;
            }else if(estado ==  Estados.preparing){
                reproducir = true;
            }else if(estado==Estados.prepared ||
                    estado == Estados.started ||
                    estado == Estados.paused ||
                    estado == Estados.completed){
                mp.start();
                estado = Estados.started;
            }
        }
    }

    private void stop(){
        if(estado == Estados.prepared ||
                estado == Estados.started ||
                estado == Estados.paused ||
                estado == Estados.completed){
            mp.stop();
            estado = Estados.stopped;
            mp.reset();
            estado = Estados.idle;
        }
        reproducir = false;
    }

    private void add(String cancion){
        this.rutaCancion = cancion;
        Log.v("LOG ",cancion);
    }

    private void pause(){
        if(estado == Estados.started){
            mp.pause();
            estado = Estados.paused;
        }else if(estado == Estados.paused){
            mp.start();
            estado = Estados.started;
        }else if(estado == Estados.completed){
            mp.start();
            estado = Estados.started;
        }
    }
}
