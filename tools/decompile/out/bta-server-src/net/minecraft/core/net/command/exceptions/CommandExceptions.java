package net.minecraft.core.net.command.exceptions;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.lang.I18n;

public class CommandExceptions {
   private static final SimpleCommandExceptionType INCOMPLETE_ARGUMENT = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.exceptions.incomplete")
   );
   private static final SimpleCommandExceptionType NOT_IN_WORLD = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.exceptions.not_in_world")
   );
   private static final SimpleCommandExceptionType EMPTY_SELECTOR = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.empty")
   );
   private static final SimpleCommandExceptionType INVALID_SELECTOR = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.generic")
   );
   private static final SimpleCommandExceptionType SINGLE_ENTITY_ONLY = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.single_entity_only")
   );
   private static final SimpleCommandExceptionType SINGLE_PLAYER_ONLY = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.single_player_only")
   );
   private static final SimpleCommandExceptionType PLAYER_ONLY = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.player_only")
   );
   private static final SimpleCommandExceptionType SINGLE_PLAYER_WORLD_ONLY = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.exceptions.single_player_world_only")
   );
   private static final SimpleCommandExceptionType MULTIPLAYER_WORLD_ONLY = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.exceptions.multiplayer_world_only")
   );
   private static final SimpleCommandExceptionType VOLUME_TOO_LARGE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.exceptions.volume_too_large")
   );
   private static final SimpleCommandExceptionType EXPECTED_END_OF_METADATA = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKeyAndFormat("command.exceptions.metadata.unterminated")
   );
   private static final SimpleCommandExceptionType EXPECTED_END_OF_TAG = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKeyAndFormat("command.exceptions.tag.unterminated")
   );
   private static final DynamicCommandExceptionType EXPECTED_TAG_KEY_VALUE = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.exceptions.tag.valueless", value)
   );

   public static SimpleCommandExceptionType incomplete() {
      return INCOMPLETE_ARGUMENT;
   }

   public static SimpleCommandExceptionType notInWorld() {
      return NOT_IN_WORLD;
   }

   public static SimpleCommandExceptionType emptySelector() {
      return EMPTY_SELECTOR;
   }

   public static SimpleCommandExceptionType invalidSelector() {
      return INVALID_SELECTOR;
   }

   public static SimpleCommandExceptionType singleEntityOnly() {
      return SINGLE_ENTITY_ONLY;
   }

   public static SimpleCommandExceptionType singlePlayerOnly() {
      return SINGLE_PLAYER_ONLY;
   }

   public static SimpleCommandExceptionType playerOnly() {
      return PLAYER_ONLY;
   }

   public static SimpleCommandExceptionType singlePlayerWorldOnly() {
      return SINGLE_PLAYER_WORLD_ONLY;
   }

   public static SimpleCommandExceptionType multiplayerWorldOnly() {
      return MULTIPLAYER_WORLD_ONLY;
   }

   public static SimpleCommandExceptionType volumeTooLarge() {
      return VOLUME_TOO_LARGE;
   }

   public static SimpleCommandExceptionType expectedEndOfTag() {
      return EXPECTED_END_OF_TAG;
   }

   public static SimpleCommandExceptionType expectedEndOfMetadata() {
      return EXPECTED_END_OF_METADATA;
   }

   public static DynamicCommandExceptionType expectedTagKeyValue() {
      return EXPECTED_TAG_KEY_VALUE;
   }
}
