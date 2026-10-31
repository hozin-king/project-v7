package app.morphe.manager.domain.pv7

/**
 * PV7 can inspect any APK, but the bundled host was built for legacy ARMv7
 * Unity 4.6 Mono guests. Unity 5.6 Mono guests are an EXPERIMENTAL variant:
 * the gate below lets them through so the 4.6 host can be tried against
 * them, but the host may fail to launch, render, or play them correctly.
 * Generic core modules do not add a new native engine or supply a
 * Cocos2d-x JNI/GL runtime.
 */
object Pv7GuestBackend {
    fun canBuild(target: Pv7AppTarget): Boolean =
        target.engine?.id == "unity" &&
            (target.engine.version == null || target.engine.version.startsWith("4.6.") ||
                target.engine.version.startsWith("5.6.")) &&
            target.abis.any { it == "armeabi" || it == "armeabi-v7a" }

    /** True only for the experimental Unity 5.6 Mono variant, not for 4.6. */
    fun isExperimentalUnity56(target: Pv7AppTarget): Boolean =
        target.engine?.id == "unity" &&
            (target.engine.version?.startsWith("5.6.") == true)

    fun explanation(target: Pv7AppTarget): String = when (target.engine?.id) {
        "cocos2dx" ->
            "Cocos2d-x detected (32-bit native libgame.so). PV7 can inspect this APK, " +
                "but its bundled runtime currently supports legacy Unity 4.6 Mono only. " +
                "Cocos2d-x requires a separate native/JNI/graphics compatibility backend."
        "unity" -> if (isExperimentalUnity56(target)) {
            "Experimental Unity 5.6 Mono guest: the bundled host was built for " +
                "legacy Unity 4.6 Mono and may fail to launch, render, or play " +
                "this game correctly. This is not a tested profile."
        } else {
            "This Unity build does not match the legacy ARMv7 Unity 4.6 Mono " +
                "runtime the current PV7 host supports. No compatible backend is available."
        }
        else ->
            "This APK's native game engine is not supported by the current PV7 " +
                "wrapper. You can inspect modules, but building a working patched APK " +
                "requires a compatible runtime backend."
    }
}
