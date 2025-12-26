package net.minecraft.core.net;

public class ServerCommandEntry {
   public final String command;
   public final ICommandListener commandListener;

   public ServerCommandEntry(String s, ICommandListener icommandlistener) {
      this.command = s;
      this.commandListener = icommandlistener;
   }
}
