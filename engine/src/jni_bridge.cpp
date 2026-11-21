#include <jni.h>
#include "leaf/engine.hpp"
#include <memory>
#include <vector>
static leaf::Engine game;
static leaf::PixelSurface surface(leaf::Engine::kWidth, leaf::Engine::kHeight);
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeMove(JNIEnv*, jobject, jint x, jint y){ game.move_player(x,y); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCollectEgg(JNIEnv*, jobject){ game.collect_egg(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeWinBattle(JNIEnv*, jobject){ game.win_battle(); }
extern "C" JNIEXPORT jintArray JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeFrame(JNIEnv* env, jobject){ game.render(surface); jintArray a=env->NewIntArray(static_cast<jsize>(surface.pixels().size())); if(a) env->SetIntArrayRegion(a,0,static_cast<jsize>(surface.pixels().size()),reinterpret_cast<const jint*>(surface.pixels().data())); return a; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCoins(JNIEnv*, jobject){ return game.save().coins; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeXp(JNIEnv*, jobject){ return game.save().xp; }
