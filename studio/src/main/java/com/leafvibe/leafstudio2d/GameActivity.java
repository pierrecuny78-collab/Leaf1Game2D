package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.View;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import androidx.documentfile.provider.DocumentFile;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/** Project scene preview and embedded LeafEngine2D native runtime test. */
public class GameActivity extends Activity {
    private TextView status;
    private PixelGameView gameView;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(20, 38, 30));
        getWindow().setNavigationBarColor(Color.rgb(20, 38, 30));
        String mode = getIntent().getStringExtra("mode");
        if ("run".equals(mode)) {
            showNativeRuntime();
        } else {
            showScenePreview();
        }
    }

    private void showNativeRuntime() {
        try {
            // Loading this class loads the shared C++ LeafEngine2D library.
            LeafEngineBridge.nativeRestore(500, 0, 1, 0);
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setBackgroundColor(0xff0f172a);
            TextView title = new TextView(this);
            title.setText("LEAFMOTEUR2D • TEST NATIF");
            title.setTextColor(Color.WHITE);
            title.setTextSize(18);
            title.setGravity(Gravity.CENTER);
            title.setPadding(8, 12, 8, 8);
            root.addView(title);
            status = new TextView(this);
            status.setTextColor(0xffbbf7d0);
            status.setGravity(Gravity.CENTER);
            status.setPadding(6, 4, 6, 8);
            root.addView(status);
            gameView = new PixelGameView();
            root.addView(gameView, new LinearLayout.LayoutParams(-1, 0, 1));
            LinearLayout actions = new LinearLayout(this);
            actions.setGravity(Gravity.CENTER);
            actions.addView(action("Trouver œuf", () -> { LeafEngineBridge.nativeCollectEgg(); refreshStats(); gameView.invalidate(); }));
            actions.addView(action("Éclore", () -> { int rarity = LeafEngineBridge.nativeHatchEgg(); refreshStats(); gameView.invalidate(); if (rarity == 0) status.setText("Aucun œuf disponible : trouve un œuf avant de le faire éclore."); else status.setText("Éclosion : " + new String[]{"", "Commune", "Rare", "Épique", "Légendaire"}[rarity] + " • Collection : " + LeafEngineBridge.nativeCreatureCount() + " créature(s)"); }));
            actions.addView(action("Combat +150", () -> { LeafEngineBridge.nativeWinBattle(); refreshStats(); gameView.invalidate(); }));
            root.addView(actions);
            LinearLayout move = new LinearLayout(this);
            move.setGravity(Gravity.CENTER);
            move.addView(action("←", () -> move(-12, 0)));
            move.addView(action("↑", () -> move(0, -12)));
            move.addView(action("↓", () -> move(0, 12)));
            move.addView(action("→", () -> move(12, 0)));
            root.addView(move);
            TextView hint = new TextView(this);
            hint.setText("Touchez le terrain pour déplacer le personnage. Le moteur C++ est intégré à cet APK.");
            hint.setTextColor(0xffcbd5e1);
            hint.setGravity(Gravity.CENTER);
            hint.setPadding(8, 6, 8, 10);
            root.addView(hint);
            setContentView(root);
            refreshStats();
        } catch (Throwable e) {
            showError("Le moteur natif LeafEngine2D n'a pas pu démarrer : " + e.getMessage());
        }
    }

    private Button action(String text, Runnable click) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setOnClickListener(v -> click.run());
        return b;
    }

    private void move(int dx, int dy) {
        LeafEngineBridge.nativeMove(dx, dy);
        refreshStats();
        if (gameView != null) gameView.invalidate();
    }

    private void refreshStats() {
        if (status != null) status.setText("Niveau " + LeafEngineBridge.nativeLevel()
            + "  •  XP " + LeafEngineBridge.nativeXp()
            + "  •  Pièces " + LeafEngineBridge.nativeCoins()
            + "  •  Œufs " + LeafEngineBridge.nativeEggs()
            + "  •  Collection " + LeafEngineBridge.nativeCreatureCount());
    }

    private final class PixelGameView extends View {
        private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private final Bitmap bitmap = Bitmap.createBitmap(480, 270, Bitmap.Config.ARGB_8888);
        PixelGameView() { super(GameActivity.this); setBackgroundColor(0xff172638); }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int[] pixels = LeafEngineBridge.nativeFrame();
            if (pixels != null && pixels.length == 480 * 270) {
                bitmap.setPixels(pixels, 0, 480, 0, 0, 480, 270);
                canvas.drawBitmap(bitmap, null, new android.graphics.Rect(0, 0, getWidth(), getHeight()), paint);
                postInvalidateDelayed(33);
            }
        }
        @Override public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float nx = event.getX() / Math.max(1f, getWidth());
                float ny = event.getY() / Math.max(1f, getHeight());
                int dx = Math.round((nx - 0.5f) * 70f);
                int dy = Math.round((ny - 0.5f) * 50f);
                move(dx, dy);
                return true;
            }
            return true;
        }
    }

    private void showScenePreview() {
        try {
            String projectValue = getIntent().getStringExtra("project_uri");
            String projectName = getIntent().getStringExtra("project_name");
            if (projectValue == null || projectName == null || projectName.trim().isEmpty())
                throw new Exception("Aucun dossier de projet sélectionné.");
            DocumentFile project = DocumentFile.fromTreeUri(this, Uri.parse(projectValue));
            if (project == null || !project.exists() || !project.isDirectory())
                throw new Exception("Le dossier du projet sélectionné est introuvable.");
            DocumentFile manifest = project.findFile("project.leaf.json");
            if (manifest == null) manifest = project.findFile("project.leaf");
            if (manifest == null) throw new Exception("Manifest du projet absent.");
            JSONObject config = new JSONObject(read(manifest));
            JSONObject runtime = config.optJSONObject("runtime");
            String scenePath = config.optString("entry_scene", "");
            if (scenePath.isEmpty() && runtime != null) scenePath = runtime.optString("entry_scene", "");
            if (scenePath.isEmpty()) scenePath = "scenes/world.leafscene";
            DocumentFile sceneFile = find(project, scenePath);
            if (sceneFile == null || !sceneFile.isFile()) throw new Exception("Scène d'entrée introuvable : " + scenePath);
            JSONObject scene = new JSONObject(read(sceneFile));
            JSONObject root = scene.optJSONObject("root");
            if (root == null) throw new Exception("La scène ne contient pas de nœud root.");
            JSONArray nodes = root.optJSONArray("children");
            if (nodes == null) nodes = new JSONArray();
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            TextView top = new TextView(this);
            top.setText("APERÇU DE SCÈNE • " + projectName);
            top.setTextColor(Color.WHITE);
            top.setGravity(Gravity.CENTER);
            top.setPadding(8, 12, 8, 12);
            layout.addView(top);
            layout.addView(new ProjectScenePreview(projectName, scenePath, nodes),
                new LinearLayout.LayoutParams(-1, 0, 1));
            layout.addView(action("▶ Tester LeafMoteur2D intégré", () -> showNativeRuntime()));
            setContentView(layout);
        } catch (Exception e) {
            showError("Impossible d'ouvrir le projet\n\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        TextView error = new TextView(this);
        error.setText(message);
        error.setTextColor(Color.WHITE);
        error.setTextSize(16);
        error.setGravity(Gravity.CENTER);
        error.setPadding(32, 32, 32, 32);
        error.setBackgroundColor(0xff0f172a);
        setContentView(error);
    }

    private DocumentFile find(DocumentFile root, String path) {
        if (root == null || path == null || path.trim().isEmpty() || path.startsWith("/") || path.contains("..")) return null;
        DocumentFile current = root;
        for (String part : path.split("/")) {
            if (part.isEmpty()) return null;
            current = current.findFile(part);
            if (current == null) return null;
        }
        return current;
    }

    private String read(DocumentFile file) throws Exception {
        try (InputStream in = getContentResolver().openInputStream(file.getUri());
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (in == null) throw new Exception("Lecture impossible : " + file.getName());
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
            return out.toString("UTF-8");
        }
    }

    private final class ProjectScenePreview extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String projectName, scenePath;
        private final JSONArray nodes;
        ProjectScenePreview(String name, String path, JSONArray sceneNodes) {
            super(GameActivity.this); projectName = name; scenePath = path; nodes = sceneNodes;
            setBackgroundColor(0xff172638);
        }
        private int parseColor(String value) {
            try { return Color.parseColor(value); } catch (Exception ignored) { return 0xff67c587; }
        }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float sx = getWidth() / 480f, sy = getHeight() / 270f;
            canvas.save(); canvas.scale(sx, sy);
            paint.setColor(0xff172638); canvas.drawRect(0, 0, 480, 270, paint);
            paint.setColor(0xff29445b); paint.setStrokeWidth(1);
            for (int x = 0; x <= 480; x += 24) canvas.drawLine(x, 0, x, 270, paint);
            for (int y = 0; y <= 270; y += 24) canvas.drawLine(0, y, 480, y, paint);
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.optJSONObject(i);
                if (node == null || !node.optBoolean("visible", true)) continue;
                JSONArray position = node.optJSONArray("position");
                float x = position != null ? (float) position.optDouble(0, 24) : (float) node.optDouble("x", 24);
                float y = position != null ? (float) position.optDouble(1, 32) : (float) node.optDouble("y", 32);
                float width = (float) node.optDouble("width", node.optDouble("size", 32));
                float height = (float) node.optDouble("height", node.optDouble("size", 32));
                paint.setColor(parseColor(node.optString("color", "#67C587")));
                canvas.drawRect(x, y, x + width, y + height, paint);
                paint.setColor(Color.WHITE); paint.setTextSize(9);
                canvas.drawText(node.optString("name", node.optString("type", "Node")), x + 2, y + height + 10, paint);
            }
            paint.setColor(Color.WHITE); paint.setTextSize(13);
            canvas.drawText(projectName + " • " + nodes.length() + " objet(s)", 10, 18, paint);
            paint.setTextSize(9); paint.setColor(0xffb8c7d8);
            canvas.drawText("Scène : " + scenePath, 10, 33, paint);
            canvas.restore();
        }
    }
}
