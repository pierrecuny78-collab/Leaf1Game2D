package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.widget.*;
import android.content.pm.PackageInfo;
import java.io.*;
import java.util.*;

/** Local APK library for games managed by the LeafStudio2D workspace. */
public class GameLibraryActivity extends Activity {
  private static final int PICK_APK=4102;
  private LinearLayout list;
  private TextView status;
  private File gamesDir;
  private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
  private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
  private Button btn(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    getWindow().setStatusBarColor(Color.rgb(15,23,42));
    getWindow().setNavigationBarColor(Color.rgb(15,23,42));
    gamesDir=new File(getFilesDir(),"LeafGames/APK");gamesDir.mkdirs();
    build();refresh();
  }
  private void build(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(12),dp(12),dp(12),dp(12));root.setBackgroundColor(Color.rgb(15,23,42));
    root.addView(label("LEAF GAMES",22,Color.WHITE));
    root.addView(label("Bibliothèque locale des jeux APK",13,0xff9fb3c8));
    root.addView(btn("＋ Importer un APK",()->pick()));
    root.addView(btn("↻ Actualiser",()->refresh()));
    root.addView(btn("Ouvrir le dépôt des jeux sur GitHub",()->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/pierrecuny78-collab/Leaf1Game2D")))));
    status=label("Stockage privé de LeafStudio2D",12,0xff86efac);root.addView(status);
    ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);scroll.addView(list);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    root.addView(btn("← Retour à LeafStudio2D",()->finish()));
    setContentView(root);
  }
  private void pick(){
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/vnd.android.package-archive");
    try{startActivityForResult(i,PICK_APK);}catch(Exception e){status.setText("Sélecteur indisponible : "+e.getMessage());}
  }
  @Override protected void onActivityResult(int req,int result,Intent data){
    super.onActivityResult(req,result,data);
    if(req==PICK_APK&&result==RESULT_OK&&data!=null&&data.getData()!=null)copyApk(data.getData());
  }
  private void copyApk(Uri uri){
    try{
      String name="jeu-"+System.currentTimeMillis()+".apk";File out=new File(gamesDir,name);
      InputStream in=getContentResolver().openInputStream(uri);FileOutputStream os=new FileOutputStream(out);
      byte[] buf=new byte[8192];int n;while((n=in.read(buf))>0)os.write(buf,0,n);
      in.close();os.close();status.setText("APK importé : "+name);refresh();
    }catch(Exception e){status.setText("Échec import APK : "+e.getMessage());}
  }
  private void refresh(){
    list.removeAllViews();File[] files=gamesDir.listFiles((d,n)->n.toLowerCase(Locale.ROOT).endsWith(".apk"));
    if(files==null||files.length==0){list.addView(label("Aucun APK importé. Utilise « Importer un APK » ou récupère les APK compilés depuis le dépôt Leaf1Game2D.",14,0xffcbd5e1));return;}
    Arrays.sort(files,(a,b)->Long.compare(b.lastModified(),a.lastModified()));
    for(File f:files)addCard(f);
    status.setText(files.length+" APK dans la bibliothèque • stockage interne privé");
  }
  private void addCard(File f){
    PackageInfo pi=null;String appName=f.getName(),pkg="Package non reconnu",version="";
    try{pi=getPackageManager().getPackageArchiveInfo(f.getAbsolutePath(),0);if(pi!=null){pi.applicationInfo.sourceDir=f.getAbsolutePath();pi.applicationInfo.publicSourceDir=f.getAbsolutePath();CharSequence l=pi.applicationInfo.loadLabel(getPackageManager());appName=l==null?f.getName():l.toString();pkg=pi.packageName;version=pi.versionName==null?"":pi.versionName;}}catch(Exception ignored){}
    LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(10),dp(8),dp(10),dp(8));card.setBackgroundColor(0xff1e293b);
    LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(5),0,dp(5));list.addView(card,cp);
    card.addView(label(appName,17,Color.WHITE));card.addView(label(pkg+"  •  "+version,11,0xff94a3b8));card.addView(label(f.getName()+"  •  "+(f.length()/1024)+" Ko",11,0xff94a3b8));
    LinearLayout actions=new LinearLayout(this);actions.setOrientation(0);
    if(pi!=null){final String packageName=pi.packageName;actions.addView(btn("▶ Lancer si installé",()->launch(packageName)),new LinearLayout.LayoutParams(0,dp(46),1));}
    actions.addView(btn("Supprimer",()->{f.delete();refresh();}),new LinearLayout.LayoutParams(0,dp(46),1));card.addView(actions);
  }
  private void launch(String pkg){
    try{
      Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
      if(i==null){new android.app.AlertDialog.Builder(this).setTitle("Jeu non installé").setMessage("Cet APK est importé dans la bibliothèque, mais son application n'est pas installée. Installe-le avec Android, puis réessaie.").setPositiveButton("OK",null).show();return;}
      startActivity(i);
    }catch(Exception e){status.setText("Impossible de lancer le jeu : "+e.getMessage());}
  }
}
