package com.leafvibe.leafstudio2d;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.documentfile.provider.DocumentFile;
import org.json.JSONObject;
import java.io.*;

public class GameRuntimeService extends Service {
    private static final String CHANNEL="leaf_game_runtime";
    private static final int NOTIFICATION_ID=2402;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private long lastTick;
    private int profileId=1;
    private final Runnable ticker=new Runnable(){
        @Override public void run(){
            long now=System.currentTimeMillis();
            long elapsed=Math.max(0L,Math.min((now-lastTick)/1000L,300L));
            if(elapsed>0){applyProjectTick(elapsed);lastTick=now;}
            handler.postDelayed(this,15000L);
        }
    };
    @Override public void onCreate(){
        super.onCreate();
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel channel=new NotificationChannel(CHANNEL,"Simulation Leaf",NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Indique que la simulation de progression du projet est active.");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId){
        if(intent!=null)profileId=Math.max(1,Math.min(3,intent.getIntExtra("profile_id",1)));
        getSharedPreferences("leaf_runtime",MODE_PRIVATE).edit().putBoolean("auto_running",true).putInt("active_profile",profileId).apply();
        Intent open=new Intent(this,MainActivity.class);
        PendingIntent pending=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);
        Notification notification=builder.setContentTitle("LeafStudio2D — simulation automatique")
            .setContentText("Mise à jour du profil "+profileId+" du projet sélectionné.")
            .setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pending).setOngoing(true).build();
        startForeground(NOTIFICATION_ID,notification);
        lastTick=System.currentTimeMillis();handler.removeCallbacks(ticker);handler.post(ticker);
        return START_STICKY;
    }
    private void applyProjectTick(long seconds){
        try{
            android.content.SharedPreferences prefs=getSharedPreferences("leafstudio_workspace",MODE_PRIVATE);
            String tree=prefs.getString("tree_uri",null);
            String project=prefs.getString("selected_project","FirstGame");
            if(tree==null)return;
            DocumentFile root=DocumentFile.fromTreeUri(this,Uri.parse(tree));
            DocumentFile studio=root==null?null:root.findFile("LeafStudio2D");
            DocumentFile projects=studio==null?null:studio.findFile("Projects");
            DocumentFile game=projects==null?null:projects.findFile(project);
            if(game==null)return;
            DocumentFile settings=game.findFile("game_settings.json");
            DocumentFile profiles=game.findFile("data");
            DocumentFile profileDir=profiles==null?null:profiles.findFile("profiles");
            DocumentFile profile=profileDir==null?null:profileDir.findFile("profile_"+profileId+".json");
            if(profile==null)return;
            JSONObject config=readJson(settings);
            JSONObject data=readJson(profile);
            double rate=config==null?0.0:Math.max(0.0,config.optDouble("production_per_minute",0.0));
            long play=data.optLong("play_time_seconds",0L)+seconds;
            long currency=data.optLong("currency",0L)+(long)Math.floor(rate*seconds/60.0);
            data.put("play_time_seconds",play);data.put("currency",currency);data.put("updated_at",System.currentTimeMillis());
            writeJson(profile,data);
            getSharedPreferences("leaf_runtime",MODE_PRIVATE).edit().putLong("last_tick",System.currentTimeMillis()).apply();
        }catch(Exception e){android.util.Log.e("LeafRuntime","Impossible de mettre à jour le profil",e);}
    }
    private JSONObject readJson(DocumentFile file)throws Exception{
        if(file==null)return new JSONObject();
        InputStream in=getContentResolver().openInputStream(file.getUri());
        if(in==null)return new JSONObject();
        ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;
        try{while((n=in.read(b))>0)out.write(b,0,n);}finally{in.close();}
        return new JSONObject(out.toString("UTF-8"));
    }
    private void writeJson(DocumentFile file,JSONObject data)throws Exception{
        OutputStream out=getContentResolver().openOutputStream(file.getUri(),"wt");
        if(out==null)throw new IOException("Fichier profil non accessible");
        try{out.write((data.toString(2)+"\\n").getBytes("UTF-8"));out.flush();}finally{out.close();}
    }
    @Override public void onDestroy(){
        handler.removeCallbacks(ticker);
        getSharedPreferences("leaf_runtime",MODE_PRIVATE).edit().putBoolean("auto_running",false).apply();
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent){return null;}
}