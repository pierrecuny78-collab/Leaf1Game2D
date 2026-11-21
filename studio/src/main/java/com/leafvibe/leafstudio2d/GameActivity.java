package com.leafvibe.leafstudio2d;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.View;
import android.os.Handler;
import android.os.Looper;

/** Small built-in runtime preview. Full LeafScript execution is a later engine milestone. */
public class GameActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(20, 38, 30));
        getWindow().setNavigationBarColor(Color.rgb(20, 38, 30));
        setContentView(new WorldPreview(this));
    }

    private static final class WorldPreview extends View {
        private final Paint p = new Paint();
        private final Handler handler = new Handler(Looper.getMainLooper());
        private long started = System.currentTimeMillis();
        private float heroX = 0;
        private final Runnable frame = new Runnable() {
            @Override public void run() {
                heroX += 3.0f;
                if (getWidth() > 0 && heroX > getWidth() + 40) heroX = -40;
                invalidate();
                handler.postDelayed(this, 120);
            }
        };

        WorldPreview(Activity context) { super(context); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        private void rect(Canvas c, int color, float x, float y, float w, float h) {
            p.setColor(color); c.drawRect(x, y, x+w, y+h, p);
        }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float sx = getWidth() / 480f, sy = getHeight() / 270f;
            c.save(); c.scale(sx, sy);
            rect(c, 0xFF87C7E8, 0, 0, 480, 270);
            rect(c, 0xFF6EAA55, 0, 175, 480, 95);
            rect(c, 0xFF4D843F, 0, 220, 480, 50);
            // Distant hills and a pixel village.
            rect(c, 0xFF6B9B61, 25, 115, 115, 65);
            rect(c, 0xFF6B9B61, 300, 105, 145, 75);
            rect(c, 0xFF9A6747, 65, 135, 65, 55);
            rect(c, 0xFF70432F, 55, 125, 85, 15);
            rect(c, 0xFFFFD66B, 90, 155, 14, 35);
            rect(c, 0xFF9A6747, 175, 145, 55, 45);
            rect(c, 0xFF70432F, 168, 135, 70, 14);
            rect(c, 0xFF6A4C93, 330, 135, 60, 55);
            rect(c, 0xFF4A356E, 320, 125, 80, 15);
            // Animated hero and slime.
            rect(c, 0xFF2C3E50, heroX, 176, 18, 24);
            rect(c, 0xFFFFC99B, heroX+3, 166, 12, 12);
            rect(c, 0xFF4B78C2, heroX+2, 190, 14, 12);
            float slimeX = 255 + (float)Math.sin((System.currentTimeMillis()-started)/700.0)*28;
            rect(c, 0xFF64D98B, slimeX, 185, 25, 17);
            rect(c, Color.WHITE, slimeX+6, 190, 4, 4);
            rect(c, Color.WHITE, slimeX+16, 190, 4, 4);
            p.setColor(Color.WHITE); p.setTextSize(13); c.drawText("BRUMEF EUILLE  •  MONDE VIVANT", 12, 22, p);
            p.setTextSize(10); c.drawText("Aperçu LeafEngine2D • prototype", 12, 38, p);
            c.restore();
        }
        @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); handler.post(frame); }
        @Override protected void onDetachedFromWindow() { handler.removeCallbacks(frame); super.onDetachedFromWindow(); }
    }
}