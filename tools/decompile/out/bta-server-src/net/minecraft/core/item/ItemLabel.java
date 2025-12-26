package net.minecraft.core.item;

import com.b100.utils.StringUtils;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class ItemLabel extends Item {
   protected ItemLabel(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
   }

   @Override
   public ItemStack onUseItem(ItemStack stack, World world, Player player) {
      if (stack.hasCustomName()) {
         return stack;
      } else {
         int slot = -1;

         for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            if (player.inventory.mainInventory[i] == stack) {
               slot = i;
               break;
            }
         }

         if (slot != -1) {
            player.displayLabelEditorScreen(stack, slot);
         }

         return stack;
      }
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      if (itemStack.hasCustomName()) {
         blockX += direction.getOffsetX();
         blockY += direction.getOffsetY();
         blockZ += direction.getOffsetZ();
         AABB box = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1, blockY + 1, blockZ + 1);

         for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, box)) {
            if (!(entity instanceof Player) && entity instanceof Mob && !entity.hadNicknameSet) {
               if (itemStack.hasCustomColor()) {
                  ((Mob)entity).chatColor = itemStack.getCustomColor();
               }

               ((Mob)entity).setNickname(StringUtils.substring(itemStack.getCustomName(), 0, 32));
               break;
            }
         }
      }
   }
}
