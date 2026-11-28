#include <jni.h>
#include "leaf/engine.hpp"

static leaf::Engine game;
static leaf::PixelSurface surface(leaf::Engine::kWidth, leaf::Engine::kHeight);

static jint hatch() { return static_cast<jint>(game.hatch_egg()); }

extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeMove(JNIEnv*, jobject, jint x, jint y) { game.move_player(x, y); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCollectEgg(JNIEnv*, jobject) { game.collect_egg(); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeHatchEgg(JNIEnv*, jobject) { return hatch(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeRestoreHatched(JNIEnv*, jobject, jint count) { game.restore_hatched(count); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeHatched(JNIEnv*, jobject) { return game.save().eggs_hatched; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCreatureCount(JNIEnv*, jobject) { return static_cast<jint>(game.save().creatures.size()); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeLastHatchRarity(JNIEnv*, jobject) { return game.save().last_hatch_rarity; }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeWinBattle(JNIEnv*, jobject) { game.win_battle(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeRestore(JNIEnv*, jobject, jint coins, jint xp, jint level, jint eggs) {
  game.restore_progress(coins, xp, level, eggs);
}
extern "C" JNIEXPORT jintArray JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeFrame(JNIEnv* env, jobject) {
  game.update(1.0 / 30.0);
  game.render(surface);
  jintArray a = env->NewIntArray(static_cast<jsize>(surface.pixels().size()));
  if (a) env->SetIntArrayRegion(a, 0, static_cast<jsize>(surface.pixels().size()), reinterpret_cast<const jint*>(surface.pixels().data()));
  return a;
}
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeCoins(JNIEnv*, jobject) { return game.save().coins; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeXp(JNIEnv*, jobject) { return game.save().xp; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeLevel(JNIEnv*, jobject) { return game.save().player_level; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leaf1game2d_MainActivity_nativeEggs(JNIEnv*, jobject) { return game.save().eggs_collected; }

// LeafStudio2D bridge: the same native LeafEngine2D library is embedded in the
// Studio APK. These JNI entry points intentionally use the Studio package.
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeMove(JNIEnv*, jclass, jint x, jint y) { game.move_player(x, y); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeCollectEgg(JNIEnv*, jclass) { game.collect_egg(); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeHatchEgg(JNIEnv*, jclass) { return hatch(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeRestoreHatched(JNIEnv*, jclass, jint count) { game.restore_hatched(count); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeHatched(JNIEnv*, jclass) { return game.save().eggs_hatched; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeCreatureCount(JNIEnv*, jclass) { return static_cast<jint>(game.save().creatures.size()); }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeLastHatchRarity(JNIEnv*, jclass) { return game.save().last_hatch_rarity; }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeWinBattle(JNIEnv*, jclass) { game.win_battle(); }
extern "C" JNIEXPORT void JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeRestore(JNIEnv*, jclass, jint coins, jint xp, jint level, jint eggs) {
  game.restore_progress(coins, xp, level, eggs);
}
extern "C" JNIEXPORT jintArray JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeFrame(JNIEnv* env, jclass) {
  game.update(1.0 / 30.0);
  game.render(surface);
  jintArray a = env->NewIntArray(static_cast<jsize>(surface.pixels().size()));
  if (a) env->SetIntArrayRegion(a, 0, static_cast<jsize>(surface.pixels().size()), reinterpret_cast<const jint*>(surface.pixels().data()));
  return a;
}
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeCoins(JNIEnv*, jclass) { return game.save().coins; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeXp(JNIEnv*, jclass) { return game.save().xp; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeLevel(JNIEnv*, jclass) { return game.save().player_level; }
extern "C" JNIEXPORT jint JNICALL Java_com_leafvibe_leafstudio2d_LeafEngineBridge_nativeEggs(JNIEnv*, jclass) { return game.save().eggs_collected; }
