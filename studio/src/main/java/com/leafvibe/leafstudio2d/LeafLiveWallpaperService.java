package com.leafvibe.leafstudio2d;

import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

/** Lightweight animated home-screen wallpaper. Android controls activation and preview. */
public class LeafLiveWallpaperService extends WallpaperService {
    @Override public Engine onCreateEngine(){return new LeafEngine();}
    private final class LeafEngine extends Engine {
        private final Handler handler=new Handler(Looper.getMainLooper());
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean visible=false;
        private float hero=0;
        private final Runnable drawTask=new Runnable(){@Override public void run(){drawFrame();}};
        @Override public void onVisibilityChanged(boolean v){visible=v;if(v)drawFrame();else handler.removeCallbacks(drawTask);}
        @Override public void onSurfaceChanged(SurfaceHolder holder,int format,int width,int height){super.onSurfaceChanged(holder,format,width,height);drawFrame();}
        @Override public void onSurfaceDestroyed(SurfaceHolder holder){visible=false;handler.removeCallbacks(drawTask);super.onSurfaceDestroyed(holder);}
        @Override public void onDestroy(){visible=false;handler.removeCallbacks(drawTask);super.onDestroy();}
        private void rect(Canvas c,int color,float x,float y,float w,float h){paint.setColor(color);c.drawRect(x,y,x+w,y+h,paint);}
        private void drawFrame(){
            handler.removeCallbacks(drawTask);if(!visible)return;
            SurfaceHolder holder=getSurfaceHolder();Canvas c=null;
            try{
                c=holder.lockCanvas();if(c!=null){
                    float w=c.getWidth(),h=c.getHeight(),sx=w/480f,sy=h/854f;
                    c.save();c.scale(sx,sy);
                    rect(c,0xff87c7e8,0,0,480,854);
                    rect(c,0xffffd889,335,100,75,75);
                    rect(c,0xff709b69,0,430,480,180);
                    rect(c,0xff54884d,0,550,480,304);
                    rect(c,0xff638e5d,20,320,170,140);rect(c,0xff638e5d,280,290,180,170);
                    rect(c,0xff9a6747,65,385,80,95);rect(c,0xff70432f,55,370,100,20);rect(c,0xffffd66b,98,420,18,60);
                    rect(c,0xff9a6747,320,390,70,90);rect(c,0xff70432f,310,375,90,18);
                    float x=hero%540-40;
                    rect(c,0xff2c3e50,x,520,22,32);rect(c,0xffffc99b,x+4,505,14,16);rect(c,0xff4b78c2,x+3,550,17,18);
                    float slime=250+(float)Math.sin(System.currentTimeMillis()/700.0)*32;
                    rect(c,0xff64d98b,slime,540,34,22);rect(c,Color.WHITE,slime+8,546,5,5);rect(c,Color.WHITE,slime+22,546,5,5);
                    paint.setColor(Color.WHITE);paint.setTextSize(18);c.drawText("LEAF WORLD",18,38,paint);
                    c.restore();
                }
            }catch(Exception ignored){}finally{if(c!=null)holder.unlockCanvasAndPost(c);}
            hero+=4;if(visible)handler.postDelayed(drawTask,100);
        }
    }
}
