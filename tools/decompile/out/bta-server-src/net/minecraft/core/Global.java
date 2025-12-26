package net.minecraft.core;

public class Global {
   public static final int TICKS_PER_SECOND = 20;
   public static final int DAY_LENGTH_TICKS = 24000;
   public static final String VERSION = Version.BUILD_CHANNEL.modifyVersionString("7.3_04");
   public static final long BUILD_TIME = 1750509510534L;
   public static final BuildChannel BUILD_CHANNEL = Version.BUILD_CHANNEL;
   public static final int CURRENT_SAVE_VERSION = 19134;
   public static final int CURRENT_CHUNK_VERSION = 2;
   public static boolean isServer = false;
   public static final boolean DISABLE_OLD_WORLDS = false;
   public static final int TEXTURE_PACK_FORMAT = 3;
   public static final boolean NET_DEBUG = false;
   public static final boolean ASSERTIONS_ENABLED;
   public static MinecraftAccessor accessor;
   public static final int MP_PROTOCOL_VERSION = 29444;
   static final boolean $assertionsDisabled = !Global.class.desiredAssertionStatus();

   static {
      boolean assertions = false;
      if (!$assertionsDisabled) {
         assertions = true;
         if (false) {
            throw new AssertionError();
         }
      }

      ASSERTIONS_ENABLED = assertions;
   }
}
