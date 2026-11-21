package com.leafvibe.leaf1game2d;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.view.MotionEvent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Color;

public class MainActivity extends Activity {
  static { System.loadLibrary("leafengine"); }
  private native void nativeMove(int dx, int dy);
  private native void nativeCollectEgg();
  private native void nativeWinBattle();
  private native void nativeRestore(int coins, int xp, int level, int eggs);
  private native int[] nativeFrame();
  private native int nativeCoins();
  private native int nativeXp();
  private native int nativeLevel();
  private native int nativeEggs();

  private SharedPreferences progress;

  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
    progress = getSharedPreferences("leaf1game2d_progress", MODE_PRIVATE);
    nativeRestore(
      progress.getInt("coins", 500),
      progress.getInt("xp", 0),
      progress.getInt("level", 1),
      progress.getInt("eggs", 0)
    );
    setContentView(new GameView());
  }

  private void saveProgress() {
    if (progress == null) return;
    progress.edit()
      .putInt("coins", nativeCoins())
      .putInt("xp", nativeXp())
      .putInt("level", nativeLevel())
      .putInt("eggs", nativeEggs())
      .apply();
  }

  @Override protected void onPause() {
    saveProgress();
    super.onPause();
  }

  final class GameView extends View {
    final Paint p = new Paint();
    final Rect src = new Rect(0, 0, 480, 270);
    int message = 0;

    GameView() {
      super(MainActivity.this);
      p.setTypeface(Typeface.MONOSPACE);
      p.setFilterBitmap(false);
    }

    void box(Canvas c, int x, int y, int w, int h, int color) {
      p.setColor(color);
      c.drawRect(x, y, x + w, y + h, p);
    }

    void label(Canvas c, String s, int x, int y, int size, int color) {
      p.setColor(color);
      p.setTextSize(size);
      c.drawText(s, x, y, p);
    }

    @Override protected void onDraw(Canvas c) {
      super.onDraw(c);
      c.save();
      c.scale(getWidth() / 480f, getHeight() / 270f);
      int[] frame = nativeFrame();
      if (frame != null && frame.length == 480 * 270) {
        android.graphics.Bitmap bm = android.graphics.Bitmap.createBitmap(frame, 480, 270, android.graphics.Bitmap.Config.ARGB_8888);
        p.setFilterBitmap(false);
        c.drawBitmap(bm, src, new Rect(0, 0, 480, 270), p);
        bm.recycle();
      }
      box(c, 0, 0, 480, 22, 0xDD172331);
      label(c, "LEAF 1 GAME 2D  LV." + nativeLevel(), 8, 16, 12, Color.WHITE);
      label(c, "COINS " + nativeCoins() + "   XP " + nativeXp(), 300, 16, 11, 0xFFFFE58A);
      box(c, 8, 226, 105, 36, 0xDD172331);
      label(c, "▲", 48, 241, 12, Color.WHITE);
      label(c, "◀     ▼     ▶", 20, 257, 12, Color.WHITE);
      box(c, 355, 226, 117, 36, 0xDD172331);
      label(c, "EGG " + nativeEggs(), 363, 249, 11, 0xFFFFE58A);
      label(c, "BOSS", 419, 249, 12, 0xFFFF9A9A);
      if (message > 0) {
        box(c, 130, 95, 220, 54, 0xEE172331);
        label(c, message == 1 ? "EGG COLLECTED +100" : "BATTLE WON +150", 145, 126, 14, Color.WHITE);
      }
      c.restore();
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
      if (e.getAction() != MotionEvent.ACTION_DOWN) return true;
      float x = e.getX() * 480f / getWidth();
      float y = e.getY() * 270f / getHeight();
      if (y > 220 && x < 120) {
        if (x < 75) nativeMove(0, -12);
        else nativeMove(x < 40 ? -12 : x > 78 ? 12 : 0, 12);
      } else if (y > 220 && x > 350) {
        if (x < 415) {
          nativeCollectEgg();
          message = 1;
        } else {
          nativeWinBattle();
          message = 2;
        }
        saveProgress();
      } else {
        nativeMove(x < 240 ? -12 : 12, y < 135 ? -12 : 12);
      }
      invalidate();
      return true;
    }
  }
}
