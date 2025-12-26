package net.minecraft.core.player;

import java.util.UUID;
import net.minecraft.core.util.helper.UUIDHelper;
import org.jetbrains.annotations.NotNull;

public class Session {
   @NotNull
   public String username;
   @NotNull
   public UUID uuid;
   @NotNull
   public String sessionId;

   public Session(@NotNull String username, @NotNull String uuid, @NotNull String sessionId) {
      this.username = username;
      this.uuid = UUID.fromString(UUIDHelper.untrimUUID(uuid));
      this.sessionId = sessionId;
      UUIDHelper.nameToUUIDMap.put(this.username, this.uuid);
   }
}
