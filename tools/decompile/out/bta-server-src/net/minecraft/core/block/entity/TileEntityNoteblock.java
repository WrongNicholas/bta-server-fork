package net.minecraft.core.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.BlockLogicNote;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;

@Deprecated
public class TileEntityNoteblock extends TileEntity {
   public byte note = 0;

   @Override
   public void tick() {
      if (this.worldObj.getBlock(this.x, this.y, this.z) == Blocks.NOTEBLOCK) {
         this.worldObj.setBlockMetadata(this.x, this.y, this.z, BlockLogicNote.setNote(0, this.note));
      }

      this.invalidate();
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      tag.putByte("note", this.note);
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      this.note = MathHelper.clamp(tag.getByte("note"), (byte)0, (byte)24);
   }
}
