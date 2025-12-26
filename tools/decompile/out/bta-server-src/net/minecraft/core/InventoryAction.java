package net.minecraft.core;

public enum InventoryAction {
   CLICK_LEFT(false),
   CLICK_RIGHT(false),
   DROP_HELD_SINGLE(false),
   DROP_HELD_STACK(false),
   HOTBAR_ITEM_SWAP(false),
   DRAG_ITEMS_ALL(false),
   DRAG_ITEMS_ONE(false),
   PICKUP_SIMILAR(false),
   MOVE_STACK(false),
   MOVE_SINGLE_ITEM(false),
   MOVE_ALL(false),
   MOVE_SIMILAR(false),
   DROP(false),
   INTERACT_SLOT(false),
   INTERACT_GRABBED(false),
   EQUIP_ARMOR(false),
   SORT(false),
   LOCK(false),
   CREATIVE_GRAB(true),
   CREATIVE_MOVE(true),
   CREATIVE_DELETE(true),
   CREATIVE_DRAG(true);

   private static final InventoryAction[] actions = new InventoryAction[32];
   private int id;
   private final boolean requireCreative;

   private static void setId(InventoryAction action, int id) {
      actions[id] = action;
      action.id = id;
   }

   private InventoryAction(boolean requireCreative) {
      this.requireCreative = requireCreative;
   }

   public int getId() {
      return this.id;
   }

   public boolean requireCreative() {
      return this.requireCreative;
   }

   public static InventoryAction get(int id) {
      return id >= 0 && id < actions.length ? actions[id] : null;
   }

   static {
      setId(CLICK_LEFT, 0);
      setId(CLICK_RIGHT, 1);
      setId(DROP_HELD_SINGLE, 2);
      setId(DROP_HELD_STACK, 3);
      setId(HOTBAR_ITEM_SWAP, 4);
      setId(DRAG_ITEMS_ALL, 5);
      setId(DRAG_ITEMS_ONE, 6);
      setId(PICKUP_SIMILAR, 7);
      setId(MOVE_STACK, 8);
      setId(MOVE_SINGLE_ITEM, 9);
      setId(MOVE_ALL, 10);
      setId(MOVE_SIMILAR, 11);
      setId(DROP, 12);
      setId(CREATIVE_GRAB, 13);
      setId(CREATIVE_MOVE, 14);
      setId(CREATIVE_DELETE, 15);
      setId(CREATIVE_DRAG, 20);
      setId(INTERACT_SLOT, 16);
      setId(INTERACT_GRABBED, 17);
      setId(EQUIP_ARMOR, 18);
      setId(SORT, 19);
      setId(LOCK, 20);
   }
}
