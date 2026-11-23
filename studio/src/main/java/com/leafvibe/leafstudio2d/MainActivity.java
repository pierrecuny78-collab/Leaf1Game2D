package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import androidx.documentfile.provider.DocumentFile;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class MainActivity extends Activity {
    private static final int PICK_TREE=501, PICK_ZIP=502, SAVE_ZIP=503, PICK_ASSET=510, PROJECT_MANAGER=520;
    private static final String PREFS="leafstudio_workspace";
    private LinearLayout root, content;
    private TextView status, currentFile;
    private EditText editor;
    private DocumentFile storageRoot, projectsRoot, projectRoot;
    private String activePath="scripts/main.leaf";
    private boolean fullExplorer=false;
    private final LinkedHashMap<String,String> cache=new LinkedHashMap<>();
    private int activeProfile=1;

    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);return t;}
    private Button btn(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
    private LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    private LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private void say(String s){if(status!=null)status.setText(s);}
    private void log(String s){if(status!=null)status.setText(s);}

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(15,23,42));
        getWindow().setNavigationBarColor(Color.rgb(15,23,42));
        String saved=getSharedPreferences(PREFS,MODE_PRIVATE).getString("tree_uri",null);
        if(saved!=null){try{Uri u=Uri.parse(saved);storageRoot=DocumentFile.fromTreeUri(this,u);if(storageRoot!=null&&storageRoot.canWrite())openWorkspace();}catch(Exception ignored){}}
        dashboard();
    }

    private void shell(String title,String subtitle){
        root=vertical();root.setBackgroundColor(0xff0f172a);
        TextView h=label(title,21,Color.WHITE);h.setTypeface(Typeface.DEFAULT_BOLD);h.setPadding(dp(14),dp(12),dp(8),dp(3));root.addView(h);
        TextView sub=label(subtitle,12,0xff9fb3c8);sub.setPadding(dp(14),0,dp(8),dp(10));root.addView(sub);
        content=vertical();ScrollView sc=new ScrollView(this);sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        status=label("LeafStudio2D 0.4.0 • hors ligne • stockage SD/ZIP",11,0xff86efac);status.setPadding(dp(12),dp(7),dp(8),dp(7));root.addView(status);
        setContentView(root);
    }
    private void add(String text,Runnable action){content.addView(btn(text,action),new LinearLayout.LayoutParams(-1,dp(48)));}
    private void divider(){View v=new View(this);v.setBackgroundColor(0xff334155);content.addView(v,new LinearLayout.LayoutParams(-1,dp(1)));}
    private void dashboard(){
        shell("LEAF STUDIO 2D","Atelier de création 2D • projets multi-moteurs • Android / PC");
        TextView intro=label("Espace de travail\n"+(projectRoot==null?"Aucun projet SD sélectionné":"Projet : "+projectRoot.getName()),14,Color.WHITE);intro.setPadding(dp(14),dp(10),dp(14),dp(10));content.addView(intro);
        add("📁 Choisir le dossier de la carte SD",()->pickTree());
        add("🗂 Mes projets : créer / ouvrir / renommer / supprimer",()->startActivityForResult(new Intent(this,ProjectManagerActivity.class),PROJECT_MANAGER));
        add("📦 Importer un projet ZIP",()->pickZip());
        add("📤 Exporter le projet en ZIP",()->exportZip());
        add("▶ Ouvrir l'espace de travail",()->workspace());
        divider();
        add("🗂 Vue simple / explorateur complet",()->{fullExplorer=!fullExplorer;workspace();});
        add("🎨 Éditeur de scènes et cartes",()->sceneStudio());
        add("🧱 Gestionnaire de ressources et assets",()->assetStudio());
        add("📜 Scripts par catégories",()->scriptStudio());
        add("🕹 Atelier RPG : héros, classes, objets, ennemis, boss",()->rpgStudio());
        add("🎞 Spritesheets et animations",()->animationStudio());
        add("📜 Quêtes, dialogues et événements",()->questStudio());
        add("🧩 Logique visuelle et extensions",()->logicStudio());
        add("🧪 Analyseur de projet et console",()->analyzer());
        add("👤 Profils et sauvegardes (3)",()->profiles());
        add("🎮 Bibliothèque des APK",()->startActivity(new Intent(this,GameLibraryActivity.class)));
        add("▶ Aperçu de démonstration",()->startActivity(new Intent(this,GameActivity.class)));
        add("⚙ Préférences et informations",()->settings());
    }

    private void pickTree(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i,PICK_TREE);
    }
    private void pickZip(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/zip");
        startActivityForResult(i,PICK_ZIP);
    }
    private void exportZip(){
        if(!ready())return;
        Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/zip");i.putExtra(Intent.EXTRA_TITLE,projectRoot.getName()+"-LeafStudio2D.zip");startActivityForResult(i,SAVE_ZIP);
    }
    @Override protected void onActivityResult(int req,int result,Intent data){
        super.onActivityResult(req,result,data);
        if(req==PROJECT_MANAGER){if(result==RESULT_OK){openWorkspace();dashboard();}return;}
        if(result!=RESULT_OK||data==null||data.getData()==null)return;
        Uri u=data.getData();
        try{
            if(req==PICK_TREE){
                int flags=data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(u,flags);
                storageRoot=DocumentFile.fromTreeUri(this,u);getSharedPreferences(PREFS,MODE_PRIVATE).edit().putString("tree_uri",u.toString()).apply();openWorkspace();dashboard();
            }else if(req==PICK_ZIP){if(!ready())return;importZip(u);workspace();}
            else if(req==SAVE_ZIP){writeZip(u);say("ZIP exporté sur le stockage choisi.");}
            else if(req==PICK_ASSET){if(!ready())return;copyAsset(u);assetStudio();}
        }catch(Exception e){say("Erreur : "+e.getMessage());}
    }
    private void openWorkspace(){
        if(storageRoot==null)return;
        DocumentFile base=childDir(storageRoot,"LeafStudio2D");projectsRoot=childDir(base,"Projects");
        String selected=getSharedPreferences(PREFS,MODE_PRIVATE).getString("selected_project","FirstGame");
        projectRoot=childDir(projectsRoot,selected);
        ensureStructure();say("Projet prêt sur le stockage sélectionné.");
    }
    private DocumentFile childDir(DocumentFile p,String n){DocumentFile d=p.findFile(n);return d!=null&&d.isDirectory()?d:p.createDirectory(n);}
    private DocumentFile childFile(DocumentFile p,String n,String mime){DocumentFile f=p.findFile(n);return f!=null&&f.isFile()?f:p.createFile(mime,n);}
    private boolean ready(){if(projectRoot==null){say("Choisis d'abord le dossier de la carte SD.");return false;}return true;}
    private void ensureDir(String path){DocumentFile p=projectRoot;for(String s:path.split("/"))p=childDir(p,s);}
    private DocumentFile find(String path,boolean create){
        if(projectRoot==null)return null;String[] parts=path.split("/");DocumentFile p=projectRoot;
        for(int i=0;i<parts.length-1;i++){DocumentFile d=p.findFile(parts[i]);if(d==null&&create)d=p.createDirectory(parts[i]);if(d==null)return null;p=d;}
        DocumentFile f=p.findFile(parts[parts.length-1]);if(f==null&&create)f=p.createFile(mime(path),parts[parts.length-1]);return f;
    }
    private String mime(String p){if(p.endsWith(".json")||p.endsWith(".leafscene"))return "application/json";if(p.endsWith(".png"))return "image/png";if(p.endsWith(".jpg"))return "image/jpeg";return "text/plain";}
    private String readDoc(DocumentFile f)throws Exception{InputStream in=getContentResolver().openInputStream(f.getUri());ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();return out.toString("UTF-8");}
    private void writeDoc(DocumentFile f,String s)throws Exception{OutputStream out=getContentResolver().openOutputStream(f.getUri(),"wt");out.write(s.getBytes("UTF-8"));out.flush();out.close();}
    private void createIfMissing(String path,String value)throws Exception{DocumentFile f=find(path,false);if(f==null){f=find(path,true);if(f!=null)writeDoc(f,value);}}
    private void ensureStructure(){
        try{
            String[] dirs={"scenes","scripts","scripts/gameplay","scripts/graphics","scripts/ui","scripts/systems","scripts/ai","scripts/quests","scripts/data","assets/sprites","assets/tilesets","assets/audio","assets/fonts","data/heroes","data/classes","data/items","data/equipment","data/companions","data/enemies","data/bosses","data/quests","data/dialogues","data/skills","data/recipes","data/biomes","data/buildings","data/decorations","animations","ui","plugins","backups","data/profiles","saves/profile_1","saves/profile_2","saves/profile_3"};
            for(String d:dirs)ensureDir(d);
            createIfMissing("game_settings.json","{\n  \"settings_version\": 1, \"envelope_number\": 1, \"player_level\": 1, \"health\": 100, \"max_health\": 100, \"experience\": 0, \"currency\": 0, \"production_per_minute\": 2, \"auto_simulation\": false, \"background_mode\": \"disabled\"\n}\n");
            createIfMissing("project.leaf.json","{\n  \"format\": \"leaf-project\",\n  \"schema_version\": 1,\n  \"name\": \"FirstGame\",\n  \"engine\": \"LeafEngine2D\",\n  \"entry_scene\": \"scenes/world.leafscene\",\n  \"supported_engines\": [\"LeafEngine2D\", \"Godot\", \"Other\"]\n}\n");
            createIfMissing("scripts/main.leaf","# LeafScript — point d'entrée du projet\nextends Node2D\n\nfunc _ready():\n    print(\"Bienvenue dans LeafStudio2D\")\n");
            createIfMissing("scenes/world.leafscene","{\n  \"scene_format\": \"leaf-scene\", \"format_version\": 1, \"scene_id\": \"world\",\n  \"root\": {\"type\": \"Node2D\", \"name\": \"World\", \"children\": []}\n}\n");
            for(int n=1;n<=3;n++)createIfMissing("data/profiles/profile_"+n+".json",profileTemplate(n));
            createIfMissing("data/items/items.json","{\n  \"items\": []\n}\n");
            createIfMissing("data/heroes/heroes.json","{\n  \"heroes\": []\n}\n");
            createIfMissing("data/enemies/enemies.json","{\n  \"enemies\": []\n}\n");
            createIfMissing("data/quests/quests.json","{\n  \"quests\": []\n}\n");
            createIfMissing("animations/animations.json","{\n  \"animations\": []\n}\n");
        }catch(Exception e){say("Initialisation incomplète : "+e.getMessage());}
    }
    private String profileTemplate(int n){return "{\n  \"profile_id\": "+n+",\n  \"player_name\": \"Joueur "+n+"\",\n  \"level\": 1, \"experience\": 0, \"health\": 100, \"max_health\": 100,\n  \"stamina\": 100, \"currency\": 0, \"play_time_seconds\": 0,\n  \"inventory\": [], \"equipment\": {}, \"unlocked_areas\": [], \"completed_quests\": [],\n  \"created_at\": \"\", \"updated_at\": \"\"\n}\n";}

    private void workspace(){
        if(!ready())return;ensureStructure();shell("ESPACE DE TRAVAIL","Vue "+(fullExplorer?"Explorateur complet":"Simple")+" • "+projectRoot.getName());
        add("← Tableau de bord",()->dashboard());add("＋ Nouveau script",()->{String p="scripts/script_"+System.currentTimeMillis()+".leaf";try{createIfMissing(p,"# Nouveau script LeafScript\n");editFile(p);}catch(Exception e){say(e.getMessage());}});
        add("Enregistrer le fichier ouvert",()->saveEditor());
        add("Basculer vue simple / complète",()->{fullExplorer=!fullExplorer;workspace();});
        currentFile=label("Fichiers du projet",13,0xff7dd3fc);content.addView(currentFile);
        listFiles(projectRoot,"",0);
        if(editor!=null){content.addView(editor,new LinearLayout.LayoutParams(-1,dp(260)));}
    }
    private void listFiles(DocumentFile dir,String prefix,int depth){
        DocumentFile[] fs=dir.listFiles();Arrays.sort(fs,(a,b)->a.getName().compareToIgnoreCase(b.getName()));
        for(DocumentFile f:fs){
            String path=prefix.isEmpty()?f.getName():prefix+"/"+f.getName();
            if(f.isDirectory()){
                if(fullExplorer||depth==0)add("📁 "+path+"/",()->{fullExplorer=true;workspace();});
                if(fullExplorer)listFiles(f,path,depth+1);
            }else if(fullExplorer||depth<2){add("📄 "+path,()->editFile(path));}
        }
    }
    private void editFile(String path){
        try{DocumentFile f=find(path,false);if(f==null){say("Fichier introuvable");return;}activePath=path;String text=readDoc(f);
            shell("ÉDITEUR DE FICHIER",path);add("← Retour à l'explorateur",()->workspace());
            editor=new EditText(this);editor.setGravity(Gravity.TOP|Gravity.LEFT);editor.setTextSize(13);editor.setTypeface(Typeface.MONOSPACE);editor.setTextColor(0xffe2e8f0);editor.setBackgroundColor(0xff1e293b);editor.setPadding(dp(10),dp(10),dp(10),dp(10));editor.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);editor.setText(text);
            content.addView(editor,new LinearLayout.LayoutParams(-1,0,1));
            add("💾 Enregistrer sans écraser d'autres fichiers",()->saveEditor());
            add("🔎 Vérification JSON / structure",()->validateEditor());
        }catch(Exception e){say("Lecture impossible : "+e.getMessage());}
    }
    private void saveEditor(){if(editor==null)return;try{DocumentFile f=find(activePath,true);writeDoc(f,editor.getText().toString());say("Enregistré sur le stockage SD : "+activePath);}catch(Exception e){say("Échec sauvegarde : "+e.getMessage());}}
    private void validateEditor(){
        if(editor==null)return;
        String source=editor.getText().toString();
        try{
            if(activePath.endsWith(".json")||activePath.endsWith(".leafscene")){
                org.json.JSONTokener tokener=new org.json.JSONTokener(source);
                Object parsed=tokener.nextValue();
                if(tokener.nextClean()!=0)throw new Exception("contenu supplémentaire après le JSON");
                if(activePath.endsWith(".leafscene")){
                    if(!(parsed instanceof org.json.JSONObject))throw new Exception("la scène doit être un objet JSON");
                    org.json.JSONObject scene=(org.json.JSONObject)parsed;
                    if(!scene.has("root"))throw new Exception("racine de scène manquante");
                    say("✓ Scène valide : JSON analysé et racine présente.");
                }else say("✓ JSON valide : syntaxe analysée intégralement.");
                return;
            }
            if(activePath.endsWith(".leaf")){
                int braces=0,parens=0,brackets=0;boolean quoted=false,escaped=false;
                for(int i=0;i<source.length();i++){
                    char ch=source.charAt(i);
                    if(quoted){if(escaped)escaped=false;else if(ch=='\\')escaped=true;else if(ch=='"')quoted=false;continue;}
                    if(ch=='"'){quoted=true;continue;}
                    if(ch=='{')braces++;if(ch=='}')braces--;
                    if(ch=='(')parens++;if(ch==')')parens--;
                    if(ch=='[')brackets++;if(ch==']')brackets--;
                    if(braces<0||parens<0||brackets<0)throw new Exception("fermeture inattendue à la position "+i);
                }
                if(quoted||braces!=0||parens!=0||brackets!=0)throw new Exception("chaîne ou délimiteurs non équilibrés");
                say("✓ Contrôle lexical LeafScript réussi. Attention : ce contrôle ne remplace pas encore le compilateur LeafScript.");
                return;
            }
            say("ℹ Aucun validateur spécialisé pour cette extension.");
        }catch(Exception e){say("✗ Vérification échouée : "+e.getMessage());}
    }

    private void sceneStudio(){
        if(!ready())return;shell("ÉDITEUR DE SCÈNES","Scènes, objets, calques et collisions — édition du format LeafScene");
        add("← Tableau de bord",()->dashboard());add("Ouvrir world.leafscene",()->editFile("scenes/world.leafscene"));
        add("＋ Ajouter un objet à la scène",()->addSceneObject("Node2D","NewObject"));
        add("＋ Ajouter un sprite",()->addSceneObject("Sprite2D","NewSprite"));
        add("＋ Ajouter une zone de collision",()->addSceneObject("CollisionShape2D","NewCollision"));
        add("＋ Ajouter un calque",()->addSceneObject("TileMapLayer","NewLayer"));
        add("Créer une nouvelle scène",()->{try{createIfMissing("scenes/scene_"+System.currentTimeMillis()+".leafscene","{\n  \"scene_format\": \"leaf-scene\", \"format_version\": 1, \"root\": {\"type\": \"Node2D\", \"children\": []}\n}\n");say("Nouvelle scène créée.");}catch(Exception e){say(e.getMessage());}});
    }
    private void addSceneObject(String type,String name){
        try{DocumentFile f=find("scenes/world.leafscene",false);String s=readDoc(f);int pos=s.lastIndexOf("]");if(pos<0){say("Scène invalide : tableau children absent.");return;}String item="{\"type\":\""+type+"\",\"name\":\""+name+"\",\"position\":[0,0],\"scale\":[1,1],\"rotation\":0,\"visible\":true}";
            String before=s.substring(0,pos).trim();String insert=(before.endsWith("[")?"":",")+ "\n    "+item+"\n  ";s=s.substring(0,pos)+insert+s.substring(pos);writeDoc(f,s);say("Objet "+name+" ajouté à world.leafscene.");
        }catch(Exception e){say("Ajout impossible : "+e.getMessage());}
    }

    private void scriptStudio(){
        if(!ready())return;
        shell("SCRIPTS DU JEU","Choisis une catégorie pour créer ou modifier les scripts Leaf");
        add("← Tableau de bord",()->dashboard());
        String[][] cats={{"Gameplay","scripts/gameplay"},{"Graphismes et affichage","scripts/graphics"},{"Interface et menus","scripts/ui"},{"Systèmes et statistiques","scripts/systems"},{"IA et comportements","scripts/ai"},{"Quêtes et dialogues","scripts/quests"},{"Données et configuration","scripts/data"}};
        for(String[] cat:cats){
            final String folder=cat[1],title=cat[0];
            add("📂 "+title,()->{
                shell(title,folder);
                add("← Catégories",()->scriptStudio());
                add("＋ Nouveau script",()->createCategorizedScript(folder));
                add("Ouvrir l'explorateur",()->{fullExplorer=true;workspace();});
                listFiles(directory(folder),folder,1);
            });
        }
    }
    private DocumentFile directory(String path){
        DocumentFile f=projectRoot;
        for(String part:path.split("/")){DocumentFile next=f.findFile(part);if(next==null)next=f.createDirectory(part);f=next;}
        return f;
    }
    private void createCategorizedScript(String folder){
        EditText input=new EditText(this);input.setSingleLine(true);input.setHint("nom_du_script");
        new android.app.AlertDialog.Builder(this).setTitle("Créer un script").setView(input).setNegativeButton("Annuler",null).setPositiveButton("Créer",(d,w)->{
            String n=input.getText().toString().trim().replaceAll("[^A-Za-z0-9_-]","_");if(n.isEmpty())n="nouveau_script";
            String path=folder+"/"+n+".leaf";
            try{if(find(path,false)!=null){say("Ce script existe déjà.");return;}createIfMissing(path,"# Script LeafScript du projet\\n");editFile(path);}
            catch(Exception e){say("Création impossible : "+e.getMessage());}
        }).show();
    }

    private void assetStudio(){
        if(!ready())return;shell("GESTIONNAIRE D'ASSETS","Images, textures, audio, polices et ressources du jeu");add("← Tableau de bord",()->dashboard());
        add("Importer une image / audio / police",()->pickAnyAsset());
        add("Ouvrir sprites",()->openFolder("assets/sprites"));add("Ouvrir tilesets",()->openFolder("assets/tilesets"));add("Ouvrir audio",()->openFolder("assets/audio"));add("Ouvrir fonts",()->openFolder("assets/fonts"));
        add("Créer fiche de ressource",()->{try{createIfMissing("assets/asset_catalog.json","{\n  \"assets\": []\n}\n");editFile("assets/asset_catalog.json");}catch(Exception e){say(e.getMessage());}});
    }
    private void pickAnyAsset(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");startActivityForResult(i,PICK_ASSET);}
    private void openFolder(String path){fullExplorer=true;workspace();}
    private void rpgStudio(){
        if(!ready())return;shell("ATELIER RPG","Données structurées pour les systèmes de jeu");add("← Tableau de bord",()->dashboard());
        String[][] entries={{"Héros","data/heroes/heroes.json"},{"Classes","data/classes/classes.json"},{"Objets / ressources","data/items/items.json"},{"Équipements","data/equipment/equipment.json"},{"Compagnons","data/companions/companions.json"},{"Ennemis","data/enemies/enemies.json"},{"Boss","data/bosses/bosses.json"},{"Compétences","data/skills/skills.json"},{"Recettes / artisanat","data/recipes/recipes.json"},{"Biomes","data/biomes/biomes.json"},{"Bâtiments","data/buildings/buildings.json"},{"Décorations","data/decorations/decorations.json"}};
        for(String[] e:entries)add("✎ "+e[0],()->openData(e[1],e[0]));
    }
    private void openData(String path,String label){try{createIfMissing(path,"{\n  \""+path.substring(path.lastIndexOf('/')+1).replace(".json","")+"\": []\n}\n");editFile(path);}catch(Exception e){say(label+" : "+e.getMessage());}}
    private void animationStudio(){if(!ready())return;shell("ATELIER D'ANIMATION","Spritesheets, animations image par image et paramètres de lecture");add("← Tableau de bord",()->dashboard());add("Modifier le catalogue d'animations",()->editFile("animations/animations.json"));add("Créer une animation",()->{try{String p="animations/animation_"+System.currentTimeMillis()+".json";createIfMissing(p,"{\n  \"name\": \"NewAnimation\", \"fps\": 8, \"loop\": true, \"frames\": []\n}\n");editFile(p);}catch(Exception e){say(e.getMessage());}});}
    private void questStudio(){if(!ready())return;shell("QUÊTES ET DIALOGUES","Arbres de dialogue, objectifs, déclencheurs et récompenses");add("← Tableau de bord",()->dashboard());add("Quêtes",()->openData("data/quests/quests.json","Quêtes"));add("Dialogues",()->openData("data/dialogues/dialogues.json","Dialogues"));add("Événements et déclencheurs",()->openData("data/events/events.json","Événements"));}
    private void logicStudio(){if(!ready())return;shell("LOGIQUE ET EXTENSIONS","Éditeur de règles événementielles et catalogue de modules");add("← Tableau de bord",()->dashboard());add("Créer un graphe logique JSON",()->{try{String p="data/logic/graph_"+System.currentTimeMillis()+".json";createIfMissing(p,"{\n  \"nodes\": [], \"connections\": []\n}\n");editFile(p);}catch(Exception e){say(e.getMessage());}});add("Catalogue d'extensions",()->openData("plugins/manifest.json","Extensions"));}
    private void profiles(){
        if(!ready())return;shell("PROFILS JOUEUR","Trois emplacements indépendants par projet • sauvegardes séparées");add("← Tableau de bord",()->dashboard());
        for(int n=1;n<=3;n++){final int id=n;add("Profil "+n+(activeProfile==n?" • actif":""),()->{activeProfile=id;profiles();});add("Modifier les statistiques du profil "+n,()->editFile("data/profiles/profile_"+id+".json"));add("Ouvrir sauvegardes profil "+n,()->openFolder("saves/profile_"+id));}
        add("Créer une sauvegarde de profil actif",()->{try{String path="saves/profile_"+activeProfile+"/save_"+System.currentTimeMillis()+".json";createIfMissing(path,readDoc(find("data/profiles/profile_"+activeProfile+".json",false)));say("Sauvegarde créée : "+path);}catch(Exception e){say("Sauvegarde impossible : "+e.getMessage());}});
        add("Restaurer une sauvegarde (ouvrir explorateur)",()->{fullExplorer=true;workspace();});
    }
    private void analyzer(){
        if(!ready())return;shell("ANALYSEUR ET CONSOLE","Contrôles de structure, ressources et compatibilité");add("← Tableau de bord",()->dashboard());
        String[] req={"project.leaf.json","scenes/world.leafscene","scripts/main.leaf","data/profiles/profile_1.json","data/profiles/profile_2.json","data/profiles/profile_3.json","assets/sprites","assets/audio","animations"};
        int ok=0;for(String p:req){DocumentFile f=find(p,false);boolean exists=f!=null;if(exists)ok++;TextView t=label((exists?"✓ ":"✗ ")+p,13,exists?0xff86efac:0xfffca5a5);t.setPadding(dp(12),dp(6),dp(8),dp(6));content.addView(t);}
        TextView result=label("Éléments présents : "+ok+"/"+req.length+"\nL'analyse vérifie les fichiers attendus. Elle ne certifie pas l'exécution des scripts ni la compatibilité de chaque moteur.",13,Color.WHITE);result.setPadding(dp(12),dp(12),dp(12),dp(12));content.addView(result);
        add("Ouvrir les paramètres du projet",()->editFile("project.leaf.json"));
    }
    private org.json.JSONObject loadJsonFile(String path)throws Exception{
        DocumentFile f=find(path,false);
        if(f==null)return new org.json.JSONObject();
        return new org.json.JSONObject(readDoc(f));
    }
    private void saveJsonFile(String path,org.json.JSONObject data)throws Exception{
        DocumentFile f=find(path,true);
        if(f==null)throw new IOException("Impossible de créer "+path);
        writeDoc(f,data.toString(2)+"\\n");
    }
    private void editNumberSetting(String key,String title,boolean profile){
        if(!ready())return;
        String path=profile?"data/profiles/profile_"+activeProfile+".json":"game_settings.json";
        try{
            org.json.JSONObject data=loadJsonFile(path);
            EditText input=new EditText(this);input.setSingleLine(true);input.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
            input.setText(String.valueOf(data.opt(key)));
            new android.app.AlertDialog.Builder(this).setTitle(title).setView(input).setNegativeButton("Annuler",null).setPositiveButton("Enregistrer",(d,w)->{
                try{
                    String raw=input.getText().toString().trim();
                    if(raw.isEmpty())throw new Exception("Valeur vide");
                    if(raw.contains("."))data.put(key,Double.parseDouble(raw));else data.put(key,Long.parseLong(raw));
                    if(key.equals("max_health")&&data.optLong("health",0)>data.optLong("max_health",0))data.put("health",data.optLong("max_health",0));
                    saveJsonFile(path,data);say("Paramètre enregistré : "+key+" → "+raw);
                }catch(Exception e){say("Valeur invalide : "+e.getMessage());}
            }).show();
        }catch(Exception e){say("Lecture impossible : "+e.getMessage());}
    }
    private void settings(){
        if(!ready())return;
        shell("PARAMÈTRES DU JEU","Réglages directs • projet "+projectRoot.getName()+" • profil "+activeProfile);
        add("← Tableau de bord",()->dashboard());
        add("Numéro d'enveloppe",()->editNumberSetting("envelope_number","Numéro d'enveloppe",false));
        add("Niveau initial",()->editNumberSetting("player_level","Niveau initial",false));
        add("Santé actuelle",()->editNumberSetting("health","Santé actuelle",false));
        add("Santé maximale",()->editNumberSetting("max_health","Santé maximale",false));
        add("Expérience",()->editNumberSetting("experience","Expérience",false));
        add("Monnaie",()->editNumberSetting("currency","Monnaie",false));
        add("Production par minute",()->editNumberSetting("production_per_minute","Production par minute",false));
        divider();
        add("Modifier les statistiques du profil "+activeProfile,()->profiles());
        add("Niveau du profil actif",()->editNumberSetting("level","Niveau du profil actif",true));
        add("XP du profil actif",()->editNumberSetting("experience","XP du profil actif",true));
        add("Santé du profil actif",()->editNumberSetting("health","Santé du profil actif",true));
        add("Monnaie du profil actif",()->editNumberSetting("currency","Monnaie du profil actif",true));
        add("Activer / désactiver la simulation automatique",()->toggleAutoSimulation());
        add("Choisir / prévisualiser le fond animé Leaf",()->configureLiveWallpaper());
        add("Modifier le fichier complet game_settings.json",()->editFile("game_settings.json"));
        add("Changer le dossier SD",()->pickTree());
        add("À propos des fonds d'écran",()->new android.app.AlertDialog.Builder(this)
            .setTitle("Fond animé Leaf")
            .setMessage("Android exige une validation de ta part dans son sélecteur de fonds d'écran. LeafStudio2D ne peut pas changer ton fond d'écran en silence. Le fond Leaf World actuel est une animation de démonstration indépendante : il ne reflète pas encore la scène active du jeu.")
            .setPositiveButton("OK",null).show());
    }

    private void configureLiveWallpaper(){
        try{
            Intent intent=new Intent(android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(android.app.WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new android.content.ComponentName(this,LeafLiveWallpaperService.class));
            startActivity(intent);
        }catch(Exception e){
            try{startActivity(new Intent(android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));}
            catch(Exception ignored){say("Le sélecteur de fonds d'écran animés Android est indisponible.");}
        }
    }

    private void toggleAutoSimulation(){
        boolean running=getSharedPreferences("leaf_runtime",MODE_PRIVATE).getBoolean("auto_running",false);
        Intent i=new Intent(this,GameRuntimeService.class);i.putExtra("profile_id",activeProfile);
        if(running){
            stopService(i);
            getSharedPreferences("leaf_runtime",MODE_PRIVATE).edit().putBoolean("auto_running",false).apply();
            say("Simulation automatique arrêtée.");
        }else{
            if(android.os.Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);
            getSharedPreferences("leaf_runtime",MODE_PRIVATE).edit().putBoolean("auto_running",true).apply();
            say("Simulation automatique démarrée : progression des données, sans affichage graphique en fond.");
        }
    }

    private void copyAsset(Uri uri){
        try{
            String displayName=null;
            Cursor cursor=getContentResolver().query(uri,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null);
            if(cursor!=null){try{if(cursor.moveToFirst())displayName=cursor.getString(0);}finally{cursor.close();}}
            if(displayName==null||displayName.trim().isEmpty())displayName="asset_"+System.currentTimeMillis()+".bin";
            displayName=displayName.replaceAll("[^A-Za-z0-9._-]","_");
            if(displayName.length()>100)displayName=displayName.substring(displayName.length()-100);
            String path="assets/imported/"+displayName;
            if(find(path,false)!=null){int dot=displayName.lastIndexOf('.');String base=dot>0?displayName.substring(0,dot):displayName;String ext=dot>0?displayName.substring(dot):"";path="assets/imported/"+base+"_imported_"+System.currentTimeMillis()+ext;}
            DocumentFile out=find(path,true);
            InputStream in=getContentResolver().openInputStream(uri);
            OutputStream os=getContentResolver().openOutputStream(out.getUri(),"w");
            if(in==null||os==null)throw new IOException("Flux de fichier indisponible");
            byte[] b=new byte[8192];int n;try{while((n=in.read(b))>0)os.write(b,0,n);}finally{in.close();os.close();}
            say("Ressource importée sans conversion : "+path);
        }catch(Exception e){say("Import de ressource impossible : "+e.getMessage());}
    }
    private String safeZipPath(String raw){
        if(raw==null||raw.length()==0||raw.length()>240)return null;
        String name=raw.replace('\\','/');
        if(name.startsWith("/")||name.matches("^[A-Za-z]:.*"))return null;
        StringBuilder clean=new StringBuilder();
        for(String part:name.split("/")){
            if(part.length()==0||part.equals(".")||part.equals(".."))return null;
            if(part.indexOf(':')>=0||part.indexOf('\\')>=0)return null;
            if(clean.length()>0)clean.append('/');
            clean.append(part);
        }
        return clean.toString();
    }
    private DocumentFile uniqueImportTarget(String path){
        DocumentFile existing=find(path,false);
        if(existing==null)return find(path,true);
        int slash=path.lastIndexOf('/');String parent=slash>=0?path.substring(0,slash+1):"";
        String file=slash>=0?path.substring(slash+1):path;
        int dot=file.lastIndexOf('.');String base=dot>0?file.substring(0,dot):file;
        String ext=dot>0?file.substring(dot):"";
        for(int i=1;i<1000;i++){DocumentFile candidate=find(parent+base+"_imported_"+i+ext,false);if(candidate==null)return find(parent+base+"_imported_"+i+ext,true);}
        return null;
    }
    private void importZip(Uri uri)throws Exception{
        InputStream raw=getContentResolver().openInputStream(uri);
        if(raw==null)throw new IOException("Archive ZIP illisible");
        ZipInputStream zin=new ZipInputStream(raw);ZipEntry e;byte[] buf=new byte[8192];
        int count=0;long total=0;int entries=0;
        try{
            while((e=zin.getNextEntry())!=null){
                if(++entries>5000)throw new IOException("Archive trop volumineuse : plus de 5000 entrées");
                String name=safeZipPath(e.getName());
                if(name==null){zin.closeEntry();continue;}
                if(e.isDirectory()){ensureDir(name);zin.closeEntry();continue;}
                DocumentFile out=uniqueImportTarget(name);
                if(out==null)throw new IOException("Impossible de créer "+name);
                OutputStream os=getContentResolver().openOutputStream(out.getUri(),"wt");
                if(os==null)throw new IOException("Impossible d'écrire "+name);
                int n;long current=0;
                try{
                    while((n=zin.read(buf))>0){
                        current+=n;total+=n;
                        if(current>50L*1024*1024||total>200L*1024*1024)throw new IOException("Limite de taille ZIP dépassée");
                        os.write(buf,0,n);
                    }
                }finally{os.close();}
                count++;zin.closeEntry();
            }
        }finally{zin.close();}
        ensureStructure();
        say("Import ZIP terminé : "+count+" fichiers. Les fichiers existants ont été conservés ; les doublons sont renommés.");
    }
    private void writeZip(Uri uri)throws Exception{
        OutputStream raw=getContentResolver().openOutputStream(uri);ZipOutputStream zout=new ZipOutputStream(raw);zipDir(projectRoot,"",zout);zout.finish();zout.close();
    }
    private void zipDir(DocumentFile dir,String prefix,ZipOutputStream zout)throws Exception{
        for(DocumentFile f:dir.listFiles()){String path=prefix.isEmpty()?f.getName():prefix+"/"+f.getName();if(f.isDirectory()){zipDir(f,path,zout);}else{zout.putNextEntry(new ZipEntry(path));InputStream in=getContentResolver().openInputStream(f.getUri());byte[] b=new byte[8192];int n;while((n=in.read(b))>0)zout.write(b,0,n);in.close();zout.closeEntry();}}
    }
}
