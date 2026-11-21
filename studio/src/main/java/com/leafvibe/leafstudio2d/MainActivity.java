package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    private LinearLayout root, fileBar;
    private EditText editor;
    private TextView console, currentFile, status;
    private final LinkedHashMap<String,String> files = new LinkedHashMap<>();
    private String activeFile = "scripts/main.leaf";
    private File projectDir;

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); return t;
    }
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false);
        b.setOnClickListener(v -> action.run()); return b;
    }
    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private void log(String message) {
        if (console == null) return;
        String old = console.getText().toString();
        console.setText(old + (old.isEmpty() ? "" : "\n") + message);
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(15, 23, 42));
        getWindow().setNavigationBarColor(Color.rgb(15, 23, 42));
        projectDir = new File(getFilesDir(), "LeafProjects/FirstGame");
        loadProject();
        buildUi();
        log("[LeafStudio2D] Éditeur prêt — mode prototype 0.1.0-alpha");
        log("[Projet] " + projectDir.getAbsolutePath());
    }

    private void loadProject() {
        new File(projectDir, "scripts").mkdirs();
        new File(projectDir, "scenes").mkdirs();
        new File(projectDir, "assets").mkdirs();
        String[] names = {"scripts/main.leaf", "scenes/world.leafscene", "project.leaf.json"};
        for (String name : names) {
            File f = new File(projectDir, name);
            if (f.exists()) {
                try { files.put(name, read(f)); } catch (Exception ignored) {}
            }
        }
        if (!files.containsKey("scripts/main.leaf")) {
            files.put("scripts/main.leaf",
                "# LeafScript — script du jeu\n" +
                "extends Node2D\n\n" +
                "func _ready():\n" +
                "    print(\"Bienvenue dans mon jeu !\")\n\n" +
                "func _process(delta):\n" +
                "    # Ajouter ici la logique du jeu\n" +
                "    pass\n");
        }
        if (!files.containsKey("scenes/world.leafscene"))
            files.put("scenes/world.leafscene", "{\n  \"scene_format\": \"leaf-scene\",\n  \"format_version\": 1,\n  \"scene_id\": \"world\",\n  \"root\": {\"type\": \"Node2D\", \"name\": \"World\", \"children\": []}\n}\n");
        if (!files.containsKey("project.leaf.json"))
            files.put("project.leaf.json", "{\n  \"name\": \"FirstGame\",\n  \"engine\": \"LeafEngine2D\",\n  \"engine_api\": \"0.1\",\n  \"entry_scene\": \"scenes/world.leafscene\"\n}\n");
        saveAll();
    }

    private String read(File f) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream in = new FileInputStream(f);
        byte[] buf = new byte[4096]; int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close(); return out.toString("UTF-8");
    }
    private void write(File f, String value) throws Exception {
        File parent = f.getParentFile(); if (parent != null) parent.mkdirs();
        FileOutputStream out = new FileOutputStream(f);
        out.write(value.getBytes("UTF-8")); out.close();
    }
    private void saveAll() {
        try {
            for (Map.Entry<String,String> e : files.entrySet()) write(new File(projectDir, e.getKey()), e.getValue());
            if (status != null) status.setText("Enregistré localement");
            log("[SAVE] " + files.size() + " fichiers enregistrés");
        } catch (Exception e) { log("[ERREUR SAVE] " + e.getMessage()); }
    }

    private void buildUi() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(15, 23, 42));
        TextView title = text("LEAF STUDIO 2D", 20, Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD); title.setPadding(dp(14), dp(12), dp(8), dp(4));
        root.addView(title);
        TextView subtitle = text("Éditeur de jeux • Code-first • Android / PC", 12, 0xFF9FB3C8);
        subtitle.setPadding(dp(14), 0, dp(8), dp(8)); root.addView(subtitle);

        LinearLayout actions = row();
        actions.addView(button("＋ Script", () -> newScript()), new LinearLayout.LayoutParams(0, dp(48), 1));
        actions.addView(button("Enregistrer", () -> { files.put(activeFile, editor.getText().toString()); saveAll(); }), new LinearLayout.LayoutParams(0, dp(48), 1));
        actions.addView(button("Vérifier", () -> validateCode()), new LinearLayout.LayoutParams(0, dp(48), 1));
        root.addView(actions);
        LinearLayout runtimeActions = row();
        runtimeActions.addView(button("▶ Exécuter", () -> {
            files.put(activeFile, editor.getText().toString());
            saveAll();
            startActivity(new Intent(this, GameActivity.class));
        }), new LinearLayout.LayoutParams(0, dp(50), 1));
        runtimeActions.addView(button("🔄 Mode automatique", () -> toggleAutoMode()),
            new LinearLayout.LayoutParams(0, dp(50), 1));
        root.addView(runtimeActions);

        currentFile = text(activeFile, 13, 0xFF7DD3FC);
        currentFile.setPadding(dp(14), dp(8), dp(8), dp(8)); root.addView(currentFile);
        fileBar = row(); fileBar.setPadding(dp(4), 0, dp(4), 0); root.addView(fileBar);
        refreshFileBar();

        editor = new EditText(this);
        editor.setGravity(Gravity.TOP | Gravity.LEFT);
        editor.setTextSize(13); editor.setTypeface(Typeface.MONOSPACE);
        editor.setTextColor(0xFFE2E8F0); editor.setHintTextColor(0xFF64748B);
        editor.setBackgroundColor(0xFF1E293B);
        editor.setPadding(dp(12), dp(12), dp(12), dp(12));
        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editor.setSingleLine(false); editor.setHorizontallyScrolling(true);
        editor.setText(files.get(activeFile));
        LinearLayout.LayoutParams editorParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        editorParams.setMargins(dp(8), dp(4), dp(8), dp(4)); root.addView(editor, editorParams);

        LinearLayout consoleHeader = row();
        TextView consoleTitle = text("CONSOLE / LOGS", 12, 0xFF86EFAC);
        consoleTitle.setTypeface(Typeface.DEFAULT_BOLD); consoleTitle.setPadding(dp(12), dp(8), dp(8), dp(8));
        consoleHeader.addView(consoleTitle, new LinearLayout.LayoutParams(0, -2, 1));
        consoleHeader.addView(button("Effacer", () -> console.setText("")));
        root.addView(consoleHeader);
        console = text("", 11, 0xFFCBD5E1); console.setTypeface(Typeface.MONOSPACE);
        console.setBackgroundColor(0xFF020617); console.setPadding(dp(10), dp(8), dp(10), dp(8));
        ScrollView logScroll = new ScrollView(this); logScroll.addView(console);
        root.addView(logScroll, new LinearLayout.LayoutParams(-1, dp(112)));
        status = text("Projet local • aucun accès réseau requis", 10, 0xFF94A3B8);
        status.setPadding(dp(12), dp(6), dp(8), dp(8)); root.addView(status);
        setContentView(root);
    }

    private void toggleAutoMode() {
        boolean running = getSharedPreferences("leaf_runtime", MODE_PRIVATE)
                .getBoolean("auto_running", false);
        Intent intent = new Intent(this, GameRuntimeService.class);
        if (running) {
            stopService(intent);
            getSharedPreferences("leaf_runtime", MODE_PRIVATE).edit()
                    .putBoolean("auto_running", false).apply();
            log("[AUTO] Simulation arrêtée.");
            status.setText("Mode automatique arrêté");
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 26) startForegroundService(intent);
            else startService(intent);
            getSharedPreferences("leaf_runtime", MODE_PRIVATE).edit()
                    .putBoolean("auto_running", true).apply();
            log("[AUTO] Simulation activée. Notification permanente affichée.");
            status.setText("Mode automatique actif en arrière-plan");
        }
    }

    private void refreshFileBar() {
        fileBar.removeAllViews();
        for (String name : files.keySet()) {
            Button b = new Button(this); b.setText(name.substring(name.lastIndexOf('/') + 1));
            b.setTextSize(10); b.setAllCaps(false);
            b.setOnClickListener(v -> {
                files.put(activeFile, editor.getText().toString());
                activeFile = name; currentFile.setText(name); editor.setText(files.get(name));
                log("[FILE] Ouverture " + name);
            });
            fileBar.addView(b);
        }
    }

    private void newScript() {
        files.put(activeFile, editor.getText().toString());
        String name = "scripts/script" + (files.size()) + ".leaf";
        files.put(name, "# Nouveau script LeafScript\nextends Node2D\n\nfunc _ready():\n    pass\n");
        activeFile = name; currentFile.setText(name); editor.setText(files.get(name));
        refreshFileBar(); log("[FILE] Créé " + name);
    }

    private void validateCode() {
        String code = editor.getText().toString();
        files.put(activeFile, code);
        int opens = 0; boolean inString = false; char quote = 0;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if ((c == '"' || c == '\'') && (i == 0 || code.charAt(i - 1) != '\\')) {
                if (!inString) { inString = true; quote = c; }
                else if (quote == c) inString = false;
            }
            if (!inString && c == '{') opens++;
            if (!inString && c == '}') opens--;
            if (opens < 0) { log("[ERREUR] Accolade fermante inattendue à la position " + i); return; }
        }
        if (opens != 0) { log("[ERREUR] Accolades non équilibrées"); return; }
        if (inString) { log("[AVERTISSEMENT] Chaîne de caractères non terminée"); return; }
        log("[OK] Vérification structurelle réussie : " + activeFile);
        log("[INFO] Le moteur LeafScript et l'exécution des scripts seront intégrés dans une prochaine version.");
        status.setText("Vérification terminée — exécution non disponible");
    }
}
