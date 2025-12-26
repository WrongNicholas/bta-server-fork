package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketRecipeSync extends Packet {
   public String recipe;
   public long maxRecipes;

   public PacketRecipeSync(RecipeEntryBase<?, ?, ?> recipe, long maxRecipes) {
      this.recipe = DataLoader.serializeRecipe(recipe);
      this.maxRecipes = maxRecipes;
   }

   public PacketRecipeSync() {
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.recipe = dis.readUTF();
      this.maxRecipes = dis.readLong();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeUTF(this.recipe);
      dos.writeLong(this.maxRecipes);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleSyncedRecipe(this);
   }

   @Override
   public int getEstimatedSize() {
      return this.recipe.getBytes().length;
   }
}
