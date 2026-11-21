#include <jni.h>
#include "leaf/engine.hpp"

static leaf::Engine game;
static leaf::PixelSurface surface(leaf::Engine::kWidth, leaf::Engine::kHeight);

extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeMove(JNIEnv*, jobject, jint x, jint y) { game.move_player(x, y); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCollectEgg(JNIEnv*, jobject) { game.collect_egg(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeWinBattle(JNIEnv*, jobject) { game.win_battle(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeRestore(JNIEnv*, jobject, jint coins, jint xp, jint level, jint eggs) {
  game.restore_progress(coins, xp, level, eggs);
}
extern "C" JNIEXPORT jintArray JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeFrame(JNIEnv* env, jobject) {
  game.render(surface);
  jintArray a = env->NewIntArray(static_cast<jsize>(surface.pixels().size()));
  if (a) env->SetIntArrayRegion(a, 0, static_cast<jsize>(surface.pixels().size()), reinterpret_cast<const jint*>(surface.pixels().data()));
  return a;
}
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCoins(JNIEnv*, jobject) { return game.save().coins; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeXp(JNIEnv*, jobject) { return game.save().xp; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeLevel(JNIEnv*, jobject) { return game.save().player_level; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeEggs(JNIEnv*, jobject) { return game.save().eggs_collected; }
