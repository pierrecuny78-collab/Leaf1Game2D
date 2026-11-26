package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import android.graphics.BitmapFactory;
import android.content.ClipData;
import java.io.InputStream;
import androidx.documentfile.provider.DocumentFile;
import java.util.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ProjectManagerActivity extends Activity {
    private static final String PREFS="leafstudio_workspace";
    private LinearLayout list;
    private DocumentFile projects;
    private String draftGenre="RPG",draftName="",draftDescription="",iconTargetProject="";
    private static final int PICK_PROJECT_ICON=731, PICK_EXISTING_PROJECT=732;
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(15,23,42));getWindow().setNavigationBarColor(Color.rgb(15,23,42));
        String saved=getSharedPreferences(PREFS,MODE_PRIVATE).getString("tree_uri",null);
        if(saved!=null){
            try{
                DocumentFile storage=DocumentFile.fromTreeUri(this,Uri.parse(saved));
                DocumentFile app=storage==null?null:storage.findFile("LeafStudio2D");
                projects=app==null?null:app.findFile("Projects");
                if(projects!=null&&!projects.isDirectory())projects=null;
            }catch(Exception ignored){projects=null;}
        }
        // Le gestionnaire doit aussi fonctionner sans dossier LeafStudio2D/Projects :
        // cibler un dossier de jeu externe ne doit créer ni déplacer aucune arborescence.
        build();refresh();
    }
    private DocumentFile child(DocumentFile p,String name){if(p==null)return null;DocumentFile f=p.findFile(name);return f!=null&&f.isDirectory()?f:p.createDirectory(name);}
    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(12),dp(12),dp(12),dp(12));root.setBackgroundColor(0xff0f172a);
        root.addView(label("MES PROJETS",24,Color.WHITE));
        root.addView(label("Chaque jeu est un dossier autonome : sources, graphismes, données et trois profils.",13,0xff9fb3c8));
        root.addView(button("＋ Créer un projet vierge LeafStudio2D",()->createProject()));
        root.addView(button("📂 Cibler un jeu existant (sans copie ni nouvelle arborescence)",()->pickExistingProject()));
        root.addView(label("Un jeu existant reste dans son dossier d'origine. Ne crée pas de projet vierge avant de le cibler.",12,0xff86efac));
        root.addView(button("↻ Actualiser",()->refresh()));
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        root.addView(button("← Retour à LeafStudio2D",()->finish()));setContentView(root);
    }
    private void createProject(){
        if(projects==null||!projects.exists()||!projects.isDirectory()){
            new AlertDialog.Builder(this).setTitle("Stockage des nouveaux projets")
                .setMessage("Pour créer un projet vierge, choisis d'abord le dossier de stockage LeafStudio2D dans le tableau de bord. Pour ouvrir un jeu déjà existant, utilise le bouton « Cibler un jeu existant » : aucun dossier LeafStudio2D/Projects n'est nécessaire.")
                .setPositiveButton("Compris",null).show();
            return;
        }
        draftGenre="RPG";draftName="";draftDescription="";showGenreStep();
    }
    private void pickExistingProject(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i,PICK_EXISTING_PROJECT);
    }
    private void showGenreStep(){
        String[] genres={"RPG","Plateforme","Gestion / Construction","Stratégie","Simulation","Aventure","Tower Defense","Survie / Crafting","Projet personnalisé"};
        new AlertDialog.Builder(this).setTitle("1/4 • Quel type de jeu ?")
            .setSingleChoiceItems(genres,0,(d,w)->{draftGenre=genres[w];})
            .setNegativeButton("Annuler",null).setPositiveButton("Suivant",(d,w)->showNameStep()).show();
    }
    private void showNameStep(){
        EditText input=new EditText(this);input.setSingleLine(true);input.setHint("Ex. Mon aventure");
        new AlertDialog.Builder(this).setTitle("2/4 • Nom du jeu").setView(input)
            .setNegativeButton("Retour",(d,w)->showGenreStep()).setPositiveButton("Suivant",(d,w)->{
                draftName=clean(input.getText().toString());if(draftName.isEmpty()){toast("Nom invalide");showNameStep();return;}
                if(projects.findFile(draftName)!=null){toast("Un projet porte déjà ce nom");showNameStep();return;}
                showDescriptionStep();
            }).show();
    }
    private void showDescriptionStep(){
        EditText input=new EditText(this);input.setMinLines(4);input.setGravity(android.view.Gravity.TOP);input.setHint("Décris librement ton jeu : univers, personnages, objectifs, gameplay...");
        new AlertDialog.Builder(this).setTitle("3/4 • Description du jeu").setMessage("Tu pourras compléter cette description plus tard dans LeafGameBlueprint.")
            .setView(input).setNegativeButton("Retour",(d,w)->showNameStep()).setPositiveButton("Suivant",(d,w)->{draftDescription=input.getText().toString().trim();showConfirmStep();}).show();
    }
    private void showConfirmStep(){
        new AlertDialog.Builder(this).setTitle("4/4 • Vérifier le projet")
            .setMessage("Nom : "+draftName+"\nGenre : "+draftGenre+"\n\nDescription : "+(draftDescription.isEmpty()?"À compléter":draftDescription))
            .setNegativeButton("Retour",(d,w)->showDescriptionStep()).setPositiveButton("Créer le projet",(d,w)->createProjectFiles()).show();
    }
    private void createProjectFiles(){
        try{
            if(projects==null)throw new Exception("Choisis d'abord le stockage LeafStudio2D pour créer un projet vierge.");
            DocumentFile dir=projects.createDirectory(draftName);if(dir==null)throw new Exception("Création du dossier impossible");
            String[] folders={"scenes","scripts","assets","assets/characters","assets/enemies","assets/environments","assets/ui","assets/icons","assets/audio","prefabs","animations","effects","data","plugins","saves","saves/profile_1","saves/profile_2","saves/profile_3","data/profiles","exports","backup"};
            for(String folder:folders){DocumentFile current=dir;for(String part:folder.split("/")){DocumentFile next=current.findFile(part);if(next==null)next=current.createDirectory(part);if(next==null)throw new Exception("Création impossible : "+folder);current=next;}}
            org.json.JSONObject project=new org.json.JSONObject();project.put("format","leaf-project");project.put("format_version",1);project.put("name",draftName);project.put("genre",draftGenre);project.put("description",draftDescription);project.put("created_at",System.currentTimeMillis());project.put("engine","LeafEngine2D");project.put("entry_scene","scenes/world.leafscene");
            writeText(dir,"project.leaf",project.toString(2));
            DocumentFile data=dir.findFile("data");org.json.JSONObject resources=new org.json.JSONObject();resources.put("resources",new org.json.JSONArray());writeText(data,"resources.json",resources.toString(2));
            DocumentFile dataFolder=dir.findFile("data");
            DocumentFile profiles=dataFolder==null?null:dataFolder.findFile("profiles");
            if(profiles==null)throw new Exception("Dossier data/profiles introuvable");
            for(int profile=1;profile<=3;profile++){org.json.JSONObject save=new org.json.JSONObject();save.put("profile_id",profile);save.put("display_name","Profil "+profile);save.put("level",1);save.put("experience",0);save.put("health",100);save.put("currency",0);save.put("inventory",new org.json.JSONArray());save.put("characters",new org.json.JSONArray());save.put("quests",new org.json.JSONArray());writeText(profiles,"profile_"+profile+".json",save.toString(2));}
            DocumentFile scripts=dir.findFile("scripts");writeText(scripts,"main.leaf","# Point d'entrée du projet "+draftName+"\n# Les scripts Leaf sont des données/logiques déclaratives tant que le runtime correspondant n'est pas activé.\n");
            writeText(dir,"game_settings.json",new org.json.JSONObject().put("player_level",1).put("health",100).put("max_health",100).put("experience",0).put("currency",0).put("envelope_number",1).toString(2));
            DocumentFile scenes=dir.findFile("scenes");org.json.JSONObject scene=new org.json.JSONObject();scene.put("format","leaf-scene");scene.put("version",1);org.json.JSONObject root=new org.json.JSONObject();root.put("name","Root");root.put("type","Node2D");root.put("children",new org.json.JSONArray());scene.put("root",root);writeText(scenes,"world.leafscene",scene.toString(2));
            getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString("selected_project",draftName).remove("selected_project_uri").apply();
            toast("Projet "+draftName+" créé avec son squelette "+draftGenre);refresh();
        }catch(Exception e){toast("Erreur de création : "+e.getMessage());}
    }
    private void writeText(DocumentFile folder,String name,String content)throws Exception{
        DocumentFile file=folder.findFile(name);if(file==null)file=folder.createFile("application/json",name);if(file==null)throw new Exception("Fichier impossible : "+name);
        try(OutputStream out=getContentResolver().openOutputStream(file.getUri(),"wt")){if(out==null)throw new Exception("Ouverture impossible : "+name);out.write((content+"\n").getBytes(StandardCharsets.UTF_8));}
    }
    private String clean(String s){if(s==null)return "";s=s.trim().replaceAll("[^A-Za-z0-9 _-]","").replaceAll("\\s+","_");return s.length()>48?s.substring(0,48):s;}
    private void refresh(){
        if(list==null)return;list.removeAllViews();String selected=getSharedPreferences(PREFS,MODE_PRIVATE).getString("selected_project","");
        if(projects==null||!projects.exists()||!projects.isDirectory()){
            list.addView(label("Aucun espace de projets vierges n'est configuré. Tu peux quand même cibler un jeu existant avec le bouton ci-dessus.",14,0xffcbd5e1));
            return;
        }
        DocumentFile[] dirs=projects.listFiles();Arrays.sort(dirs,(a,b)->String.valueOf(a.getName()).compareToIgnoreCase(String.valueOf(b.getName())));
        int count=0;for(DocumentFile dir:dirs){if(!dir.isDirectory())continue;count++;final String name=dir.getName();
            LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(8),dp(8),dp(8),dp(8));card.setBackgroundColor(0xff1e293b);
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(5),0,dp(5));list.addView(card,cp);
            LinearLayout heading=new LinearLayout(this);heading.setOrientation(0);heading.setGravity(android.view.Gravity.CENTER_VERTICAL);
            ImageView iconView=new ImageView(this);iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            DocumentFile iconFile=null;DocumentFile assetsDir=dir.findFile("assets");DocumentFile iconDir=assetsDir==null?null:assetsDir.findFile("icons");
            if(iconDir!=null){for(String iconName:new String[]{"project_icon.png","project_icon.jpg","project_icon.webp"}){DocumentFile candidate=iconDir.findFile(iconName);if(candidate!=null&&candidate.isFile()){iconFile=candidate;break;}}}
            if(iconFile!=null){try(InputStream in=getContentResolver().openInputStream(iconFile.getUri())){if(in!=null){android.graphics.Bitmap bitmap=BitmapFactory.decodeStream(in);if(bitmap!=null)iconView.setImageBitmap(bitmap);}}catch(java.io.IOException ignored){iconView.setImageResource(android.R.drawable.ic_menu_gallery);}}else iconView.setImageResource(android.R.drawable.ic_menu_gallery);
            LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(52),dp(52));ip.rightMargin=dp(10);heading.addView(iconView,ip);
            heading.addView(label((name.equals(selected)?"● ":"")+name,17,Color.WHITE),new LinearLayout.LayoutParams(0,-2,1));card.addView(heading);
            card.addView(button("🖼 Personnaliser l'icône (PNG / JPG / WebP)",()->pickProjectIcon(name)));
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
                android.content.SharedPreferences p=getSharedPreferences(PREFS,MODE_PRIVATE);if(name.equals(p.getString("selected_project","")))p.edit().putString("selected_project","").apply();
                toast("Projet supprimé");}
            refresh();
        }).show();
    }
    private boolean deleteTree(DocumentFile f){if(f.isDirectory()){for(DocumentFile c:f.listFiles())if(!deleteTree(c))return false;}return f.delete();}
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(result!=RESULT_OK||data==null||data.getData()==null)return;
        if(request==PICK_EXISTING_PROJECT){
            try{
                Uri uri=data.getData();
                int flags=data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri,flags);
                DocumentFile selected=DocumentFile.fromTreeUri(this,uri);
                if(selected==null||!selected.isDirectory())throw new Exception("Dossier invalide");
                DocumentFile manifest=selected.findFile("project.leaf.json");
                if(manifest==null)manifest=selected.findFile("project.leaf");
                if(manifest==null)throw new Exception("Ce dossier ne contient pas project.leaf.json ni project.leaf. Sélectionne le dossier racine du jeu, pas scenes/ ni scripts/.");
                String name=selected.getName();
                if(name==null||name.trim().isEmpty())name="Projet externe";
                getSharedPreferences(PREFS,MODE_PRIVATE).edit()
                    .putString("selected_project",name)
                    .putString("selected_project_uri",uri.toString())
                    .apply();
                toast("Dossier ciblé directement : "+name+" (aucune copie créée)");
                setResult(RESULT_OK);finish();
            }catch(Exception e){toast("Impossible de cibler ce dossier : "+e.getMessage());}
            return;
        }
        if(request!=PICK_PROJECT_ICON)return;
        try{
            DocumentFile dir=projects.findFile(iconTargetProject);
            if(dir==null||!dir.isDirectory())throw new Exception("Projet introuvable");
            DocumentFile assets=child(dir,"assets");DocumentFile icons=child(assets,"icons");
            String mime=getContentResolver().getType(data.getData());String ext="png";
            if(mime!=null&&mime.equalsIgnoreCase("image/jpeg"))ext="jpg";
            else if(mime!=null&&mime.equalsIgnoreCase("image/webp"))ext="webp";
            else if(mime!=null&&!mime.equalsIgnoreCase("image/png"))throw new Exception("Format non pris en charge : PNG, JPG ou WebP");
            for(String old:new String[]{"project_icon.png","project_icon.jpg","project_icon.webp"}){DocumentFile existing=icons.findFile(old);if(existing!=null)existing.delete();}
            DocumentFile target=icons.createFile(mime==null?"image/png":mime,"project_icon."+ext);
            if(target==null)throw new Exception("Création de l'icône impossible");
            try(InputStream in=getContentResolver().openInputStream(data.getData());OutputStream out=getContentResolver().openOutputStream(target.getUri(),"wt")){
                if(in==null||out==null)throw new Exception("Lecture ou écriture impossible");
                byte[] buffer=new byte[8192];int n;while((n=in.read(buffer))>0)out.write(buffer,0,n);
            }
            toast("Icône du projet mise à jour");refresh();
        }catch(Exception e){toast("Icône non importée : "+e.getMessage());}
    }
    private void pickProjectIcon(String project){
        iconTargetProject=project;
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("image/*");
        i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/png","image/jpeg","image/webp"});
        startActivityForResult(i,PICK_PROJECT_ICON);
    }
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
