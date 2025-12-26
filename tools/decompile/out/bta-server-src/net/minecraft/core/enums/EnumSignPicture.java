package net.minecraft.core.enums;

import org.jetbrains.annotations.Nullable;

public enum EnumSignPicture {
   NONE(0, "sign.image.none", "minecraft:sign/none"),
   ARROW_RIGHT(1, "sign.image.arrow_right", "minecraft:sign/arrow_right"),
   ARROW_LEFT(2, "sign.image.arrow_left", "minecraft:sign/arrow_left"),
   ARROW_DOWN(3, "sign.image.arrow_down", "minecraft:sign/arrow_down"),
   ARROW_UP(4, "sign.image.arrow_up", "minecraft:sign/arrow_up"),
   CAUTION(5, "sign.image.caution", "minecraft:sign/caution"),
   MOBS(6, "sign.image.mobs", "minecraft:sign/mobs"),
   TREES(7, "sign.image.trees", "minecraft:sign/trees"),
   CHEST(8, "sign.image.chest", "minecraft:sign/chest"),
   PICKAXE(9, "sign.image.pickaxe", "minecraft:sign/pickaxe"),
   WOLF(10, "sign.image.wolf", "minecraft:sign/wolf"),
   SWORD(11, "sign.image.sword", "minecraft:sign/sword"),
   HOE(12, "sign.image.hoe", "minecraft:sign/hoe"),
   AXE(13, "sign.image.axe", "minecraft:sign/axe"),
   SHOVEL(14, "sign.image.shovel", "minecraft:sign/shovel"),
   BOWL(15, "sign.image.bowl", "minecraft:sign/bowl"),
   TNT(16, "sign.image.tnt", "minecraft:sign/tnt"),
   MOB_FARM(17, "sign.image.mob_farm", "minecraft:sign/mob_farm"),
   MINE(18, "sign.image.mine", "minecraft:sign/mine"),
   REDSTONE(19, "sign.image.redstone", "minecraft:sign/redstone"),
   FARM(20, "sign.image.farm", "minecraft:sign/farm"),
   BARRICADE(21, "sign.image.barricade", "minecraft:sign/barricade"),
   QUESTION_MARK(22, "sign.image.question_mark", "minecraft:sign/question_mark"),
   FACE_SMILE(23, "sign.image.face_smile", "minecraft:sign/face_smile"),
   FACE_SAD(24, "sign.image.face_sad", "minecraft:sign/face_sad"),
   FACE_CRYING(25, "sign.image.face_crying", "minecraft:sign/face_crying"),
   FACE_GASP(26, "sign.image.face_gasp", "minecraft:sign/face_gasp"),
   FACE_THINKING(27, "sign.image.face_thinking", "minecraft:sign/face_thinking"),
   FACE_CONFUSED(28, "sign.image.face_confused", "minecraft:sign/face_confused"),
   BIG_SIGN(29, "sign.image.big_sign", "minecraft:sign/big_sign"),
   MOB_ZOMBIE(30, "sign.image.mob_zombie", "minecraft:sign/mob_zombie"),
   MOB_SKELETON(31, "sign.image.mob_skeleton", "minecraft:sign/mob_skeleton"),
   MOB_CREEPER(32, "sign.image.mob_creeper", "minecraft:sign/mob_creeper"),
   ONE_PLAYER(33, "sign.image.one_player", "minecraft:sign/one_player"),
   TWO_PLAYERS(34, "sign.image.two_players", "minecraft:sign/two_players"),
   THREE_PLAYERS(35, "sign.image.three_players", "minecraft:sign/three_players"),
   CROSS(36, "sign.image.cross", "minecraft:sign/cross"),
   CRAFT(37, "sign.image.craft", "minecraft:sign/craft"),
   AFK(38, "sign.image.afk", "minecraft:sign/afk"),
   BRB(39, "sign.image.brb", "minecraft:sign/brb"),
   HEART(40, "sign.image.heart", "minecraft:sign/heart"),
   ARROWS_LEFT_RIGHT(41, "sign.image.arrows_left_right", "minecraft:sign/arrows_left_right"),
   ARROWS_UP_DOWN(42, "sign.image.arrows_up_down", "minecraft:sign/arrows_up_down"),
   CHECK(43, "sign.image.check", "minecraft:sign/check"),
   EXIT(44, "sign.image.exit", "minecraft:sign/exit");

   private static EnumSignPicture[] vals = values();
   private final int id;
   private final String languageKey;
   private final String texKey;

   private EnumSignPicture(int id, String languageKey, String textureKey) {
      this.id = id;
      this.languageKey = languageKey;
      this.texKey = textureKey;
   }

   public int getId() {
      return this.id;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }

   public String getTextureKey() {
      return this.texKey;
   }

   @Nullable
   public static EnumSignPicture fromId(int id) {
      return id >= 0 && id < vals.length ? vals[id] : null;
   }
}
