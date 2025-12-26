package net.minecraft.core;

public enum BuildChannel {
   RELEASE,
   PRERELEASE,
   DEVELOPMENT,
   NIGHTLY;

   public boolean isUnstableBuild() {
      return this != RELEASE;
   }

   public String modifyVersionString(String version) {
      if (this == RELEASE || this == PRERELEASE) {
         return version;
      } else if (this == DEVELOPMENT) {
         return version + " INDEV";
      } else {
         String channel = this.name().toLowerCase();
         channel = channel.substring(0, 1).toUpperCase() + channel.substring(1);
         return version + " [" + channel + "]";
      }
   }
}
