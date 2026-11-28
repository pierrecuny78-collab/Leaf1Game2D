package com.leafvibe.leafstudio2d;

/**
 * Java bridge to the native LeafEngine2D library bundled inside LeafStudio2D.
 * The engine is compiled from the shared ../engine C++ source tree.
 */
public final class LeafEngineBridge {
    static {
        System.loadLibrary("leafengine");
    }

    private LeafEngineBridge() {}

    public static native void nativeMove(int dx, int dy);
    public static native void nativeCollectEgg();
    public static native int nativeHatchEgg();
    public static native void nativeRestoreHatched(int count);
    public static native int nativeHatched();
    public static native int nativeCreatureCount();
    public static native int nativeLastHatchRarity();
    public static native void nativeWinBattle();
    public static native void nativeRestore(int coins, int xp, int level, int eggs);
    public static native int[] nativeFrame();
    public static native int nativeCoins();
    public static native int nativeXp();
    public static native int nativeLevel();
    public static native int nativeEggs();
}
