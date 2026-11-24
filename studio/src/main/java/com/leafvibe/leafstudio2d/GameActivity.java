package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.view.Gravity;
import androidx.documentfile.provider.DocumentFile;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 * Project scene preview. This deliberately loads the selected project's own scene;
 * it does not pretend to execute LeafScript until the interpreter is available.
 */
public class GameActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(20, 38, 30));
        getWindow().setNavigationBarColor(Color.rgb(20, 38, 30));
        try {
            String treeValue = getIntent().getStringExtra("storage_tree_uri");
            String projectName = getIntent().getStringExtra("project_name");
            if (treeValue == null || projectName == null || projectName.trim().isEmpty()) {
                throw new Exception("Aucun projet sélectionné. Reviens dans LeafStudio2D et ouvre un projet.");
            }
            DocumentFile tree = DocumentFile.fromTreeUri(this, Uri.parse(treeValue));
            DocumentFile app = child(tree, "LeafStudio2D");
            DocumentFile projects = child(app, "Projects");
            DocumentFile project = projects == null ? null : projects.findFile(projectName);
            if (project == null || !project.isDirectory()) throw new Exception("Le dossier du projet sélectionné est introuvable.");
            DocumentFile manifest = project.findFile("project.leaf.json");
            if (manifest == null) manifest = project.findFile("project.leaf");
            if (manifest == null) throw new Exception("Manifest du projet absent (project.leaf.json ou project.leaf).");
            JSONObject config = new JSONObject(read(manifest));
            String scenePath = config.optString("entry_scene", "scenes/world.leafscene");
            DocumentFile sceneFile = find(project, scenePath);
            if (sceneFile == null || !sceneFile.isFile()) throw new Exception("Scène d'entrée introuvable : " + scenePath);
            JSONObject scene = new JSONObject(read(sceneFile));
            JSONObject root = scene.optJSONObject("root");
            if (root == null) throw new Exception("La scène ne contient pas de nœud root.");
            JSONArray nodes = root.optJSONArray("children");
            if (nodes == null) nodes = new JSONArray();
            setContentView(new ProjectScenePreview(projectName, scenePath, nodes));
        } catch (Exception e) {
            TextView error = new TextView(this);
            error.setText("Impossible d'ouvrir le projet\n\n" + e.getMessage());
            error.setTextColor(Color.WHITE);
            error.setTextSize(16);
            error.setGravity(Gravity.CENTER);
            error.setPadding(32, 32, 32, 32);
            error.setBackgroundColor(0xff0f172a);
            setContentView(error);
        }
    }

    private DocumentFile child(DocumentFile parent, String name) {
        return parent == null ? null : parent.findFile(name);
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
        private final String projectName;
        private final String scenePath;
        private final JSONArray nodes;

        ProjectScenePreview(String name, String path, JSONArray sceneNodes) {
            super(GameActivity.this);
            projectName = name;
            scenePath = path;
            nodes = sceneNodes;
            setBackgroundColor(0xff172638);
        }

        private int parseColor(String value) {
            try { return Color.parseColor(value); }
            catch (Exception ignored) { return 0xff67c587; }
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float sx = getWidth() / 480f;
            float sy = getHeight() / 270f;
            canvas.save();
            canvas.scale(sx, sy);
            paint.setColor(0xff172638);
            canvas.drawRect(0, 0, 480, 270, paint);
            paint.setColor(0xff29445b);
            paint.setStrokeWidth(1);
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
                paint.setColor(Color.WHITE);
                paint.setTextSize(9);
                canvas.drawText(node.optString("name", node.optString("type", "Node")), x + 2, y + height + 10, paint);
            }
            paint.setColor(Color.WHITE);
            paint.setTextSize(13);
            canvas.drawText(projectName + " • " + nodes.length() + " objet(s)", 10, 18, paint);
            paint.setTextSize(9);
            paint.setColor(0xffb8c7d8);
            canvas.drawText("Aperçu de la scène : " + scenePath, 10, 33, paint);
            canvas.drawText("Les scripts Leaf ne sont pas encore exécutés par cet aperçu.", 10, 47, paint);
            canvas.restore();
        }
    }
}
