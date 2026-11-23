package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import androidx.documentfile.provider.DocumentFile;
import java.util.*;

public class ProjectManagerActivity extends Activity {
    private static final String PREFS="leafstudio_workspace";
    private LinearLayout list;
    private DocumentFile projects;
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(15,23,42));getWindow().setNavigationBarColor(Color.rgb(15,23,42));
        String saved=getSharedPreferences(PREFS,MODE_PRIVATE).getString("tree_uri",null);
        if(saved==null){new AlertDialog.Builder(this).setTitle("Carte SD non sélectionnée").setMessage("Choisis d'abord un dossier de stockage depuis LeafStudio2D.").setPositiveButton("OK",(d,w)->finish()).show();return;}
        DocumentFile root=DocumentFile.fromTreeUri(this,Uri.parse(saved));
        DocumentFile app=root==null?null:child(root,"LeafStudio2D");
        projects=app==null?null:child(app,"Projects");
        if(projects==null){new AlertDialog.Builder(this).setMessage("Impossible d'accéder au dossier des projets.").setPositiveButton("OK",(d,w)->finish()).show();return;}
        build();refresh();
    }
    private DocumentFile child(DocumentFile p,String name){DocumentFile f=p.findFile(name);return f!=null&&f.isDirectory()?f:p.createDirectory(name);}
    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(12),dp(12),dp(12),dp(12));root.setBackgroundColor(0xff0f172a);
        root.addView(label("MES PROJETS",24,Color.WHITE));
        root.addView(label("Chaque jeu est un dossier autonome : sources, graphismes, données et trois profils.",13,0xff9fb3c8));
        root.addView(button("＋ Créer un projet",()->createProject()));
        root.addView(button("↻ Actualiser",()->refresh()));
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(button("← Retour à LeafStudio2D",()->finish()));setContentView(root);
    }
    private void createProject(){
        EditText input=new EditText(this);input.setSingleLine(true);input.setHint("Nom du projet");
        new AlertDialog.Builder(this).setTitle("Nouveau projet").setView(input).setNegativeButton("Annuler",null).setPositiveButton("Créer",(d,w)->{
            String name=clean(input.getText().toString());if(name.isEmpty()){toast("Nom invalide");return;}
            if(projects.findFile(name)!=null){toast("Un dossier porte déjà ce nom");return;}
            DocumentFile dir=projects.createDirectory(name);if(dir==null){toast("Création impossible");return;}
            getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString("selected_project",name).apply();
            toast("Projet créé : "+name);refresh();
        }).show();
    }
    private String clean(String s){if(s==null)return "";s=s.trim().replaceAll("[^A-Za-z0-9 _-]","").replaceAll("\\s+","_");return s.length()>48?s.substring(0,48):s;}
    private void refresh(){
        if(list==null)return;list.removeAllViews();String selected=getSharedPreferences(PREFS,MODE_PRIVATE).getString("selected_project","FirstGame");
        DocumentFile[] dirs=projects.listFiles();Arrays.sort(dirs,(a,b)->String.valueOf(a.getName()).compareToIgnoreCase(String.valueOf(b.getName())));
        int count=0;for(DocumentFile dir:dirs){if(!dir.isDirectory())continue;count++;final String name=dir.getName();
            LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(8),dp(8),dp(8),dp(8));card.setBackgroundColor(0xff1e293b);
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(5),0,dp(5));list.addView(card,cp);
            card.addView(label((name.equals(selected)?"● ":"")+"📁 "+name,17,Color.WHITE));
            card.addView(label(name.equals(selected)?"Projet actuellement sélectionné":"Projet indépendant",11,0xff94a3b8));
            card.addView(button("Ouvrir ce projet",()->{getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString("selected_project",name).apply();setResult(RESULT_OK);finish();}));
            card.addView(button("Renommer",()->rename(dir)));
            card.addView(button("Supprimer le projet",()->remove(dir)));
        }
        if(count==0)list.addView(label("Aucun projet. Crée ton premier jeu avec le bouton ci-dessus.",14,0xffcbd5e1));
    }
    private void rename(DocumentFile dir){
        EditText input=new EditText(this);input.setSingleLine(true);input.setText(dir.getName());
        new AlertDialog.Builder(this).setTitle("Renommer le projet").setView(input).setNegativeButton("Annuler",null).setPositiveButton("Renommer",(d,w)->{
            String old=dir.getName(),name=clean(input.getText().toString());if(name.isEmpty()||projects.findFile(name)!=null){toast("Nom invalide ou déjà utilisé");return;}
            if(!dir.renameTo(name)){toast("Renommage refusé par le stockage");return;}
            android.content.SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);if(old.equals(p.getString("selected_project","FirstGame")))p.edit().putString("selected_project",name).apply();
            refresh();
        }).show();
    }
    private void remove(DocumentFile dir){
        new AlertDialog.Builder(this).setTitle("Supprimer "+dir.getName()+" ?").setMessage("Le dossier du projet et toutes ses données seront supprimés. Cette action est irréversible. Exporte d'abord un ZIP si tu veux le conserver.").setNegativeButton("Annuler",null).setPositiveButton("Supprimer",(d,w)->{
            String name=dir.getName();if(!deleteTree(dir)){toast("Suppression partielle ou refusée");}else{
                android.content.SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);if(name.equals(p.getString("selected_project","FirstGame")))p.edit().putString("selected_project","FirstGame").apply();
                toast("Projet supprimé");}
            refresh();
        }).show();
    }
    private boolean deleteTree(DocumentFile f){if(f.isDirectory()){for(DocumentFile c:f.listFiles())if(!deleteTree(c))return false;}return f.delete();}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
