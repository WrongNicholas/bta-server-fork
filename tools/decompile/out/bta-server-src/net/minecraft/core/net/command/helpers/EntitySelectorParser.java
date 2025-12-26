package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntitySelectorParser {
   private static final SimpleCommandExceptionType EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.options.unterminated")
   );
   private static final DynamicCommandExceptionType EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.valueless", value)
   );
   public static final BiConsumer<Entity, List<? extends Entity>> ORDER_RANDOM = (sender, entities) -> Collections.shuffle(entities);
   public static final BiConsumer<Entity, List<? extends Entity>> ORDER_ARBITRARY = (sender, entities) -> {};
   public static final BiConsumer<Entity, List<? extends Entity>> ORDER_NEAREST = (sender, entities) -> entities.sort(
      (firstEntity, secondEntity) -> Double.compare(firstEntity.distanceTo(sender), secondEntity.distanceTo(sender))
   );
   public static final BiConsumer<Entity, List<? extends Entity>> ORDER_FURTHEST = (sender, entities) -> entities.sort(
      (firstEntity, secondEntity) -> Double.compare(secondEntity.distanceTo(sender), firstEntity.distanceTo(sender))
   );
   private final SimpleCommandExceptionType SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.invalid_selector.selectors_not_allowed")
   );
   private final StringReader reader;
   private int startPosition;
   private Predicate<Entity> predicate = entity -> true;
   private int maxResults;
   private boolean includesEntities;
   private BiConsumer<Entity, List<? extends Entity>> order;
   @Nullable
   private Class<? extends Entity> limitToType;
   private boolean typeInverse = false;
   private boolean currentEntity;
   private String entityId;
   private String playerName;
   private MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
   private Double x;
   private Double y;
   private Double z;
   private Double deltaX;
   private Double deltaY;
   private Double deltaZ;
   private final boolean allowSelectors;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = CommandHelper.NO_SUGGESTIONS;
   private boolean hasGamemodeEquals = false;
   private boolean hasGamemodeNotEquals = false;
   private boolean isSorted = false;
   private boolean hasLimit = false;
   private boolean hasType = false;
   private boolean hasNameEquals = false;
   private boolean hasNameNotEquals = false;
   private boolean hasNbt = false;
   private boolean hasNotNbt = false;

   public EntitySelectorParser(StringReader reader) {
      this(reader, true);
   }

   public EntitySelectorParser(StringReader reader, boolean allowSelectors) {
      this.reader = reader;
      this.allowSelectors = allowSelectors;
   }

   private void parseSelector() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestSelector;
      if (!this.reader.canRead()) {
         throw CommandExceptions.invalidSelector().createWithContext(this.reader);
      } else {
         switch (this.reader.read()) {
            case 'a':
               this.maxResults = Integer.MAX_VALUE;
               this.includesEntities = false;
               this.order = ORDER_ARBITRARY;
               this.setLimitToType(Player.class);
               this.currentEntity = false;
               break;
            case 'e':
               this.maxResults = Integer.MAX_VALUE;
               this.includesEntities = true;
               this.order = ORDER_ARBITRARY;
               this.currentEntity = false;
               break;
            case 'p':
               this.maxResults = 1;
               this.includesEntities = false;
               this.order = ORDER_NEAREST;
               this.setLimitToType(Player.class);
               this.currentEntity = false;
               break;
            case 'r':
               this.maxResults = 1;
               this.includesEntities = false;
               this.order = ORDER_RANDOM;
               this.setLimitToType(Player.class);
               this.currentEntity = false;
               break;
            case 's':
               this.maxResults = 1;
               this.includesEntities = true;
               this.order = ORDER_ARBITRARY;
               this.currentEntity = true;
               break;
            default:
               throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.reader);
         }

         this.suggestions = this::suggestOpenOptions;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
         }

         if (this.reader.canRead() && this.reader.peek() != ' ') {
            this.suggestions = CommandHelper.NO_SUGGESTIONS;
         }
      }
   }

   private void parseNameOrEntityId() throws CommandSyntaxException {
      if (this.reader.canRead()) {
         this.suggestions = this::suggestName;
      }

      String string = this.reader.readString();
      this.maxResults = 1;
      if (string.startsWith("entity.")) {
         this.includesEntities = true;
         this.entityId = string;
      } else {
         this.includesEntities = false;
         this.playerName = string;
      }
   }

   public EntitySelector parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestNameOrSelector;
      if (this.reader.canRead() && this.reader.peek() == '@') {
         if (!this.allowSelectors) {
            throw this.SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
         }

         this.parseSelector();
      } else {
         this.parseNameOrEntityId();
      }

      return this.getSelector();
   }

   private void parseOptions() throws CommandSyntaxException {
      this.suggestions = this::suggestOptionsKey;
      this.reader.skipWhitespace();

      while (this.reader.canRead() && this.reader.peek() != ']') {
         this.reader.skipWhitespace();
         int i = this.reader.getCursor();
         String key = this.reader.readString();
         EntitySelectorOptions.Modifier modifier = EntitySelectorOptions.get(this, key, i);
         this.reader.skipWhitespace();
         if (!this.reader.canRead() || this.reader.peek() != '=') {
            this.reader.setCursor(i);
            throw EXPECTED_OPTION_VALUE.createWithContext(this.reader, key);
         }

         this.reader.skip();
         this.reader.skipWhitespace();
         this.suggestions = CommandHelper.NO_SUGGESTIONS;
         modifier.handle(this);
         this.reader.skipWhitespace();
         this.suggestions = this::suggestOptionsNextOrClose;
         if (this.reader.canRead()) {
            if (this.reader.peek() != ',') {
               if (this.reader.peek() != ']') {
                  throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
               }
               break;
            }

            this.reader.skip();
            this.suggestions = this::suggestOptionsKey;
         }
      }

      if (!this.reader.canRead()) {
         throw EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
      } else {
         this.reader.skip();
         this.suggestions = CommandHelper.NO_SUGGESTIONS;
      }
   }

   public EntitySelector getSelector() {
      AABB aABB;
      if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
         if (this.distance.getMax() != null) {
            double d = (Double)this.distance.getMax();
            aABB = AABB.getTemporaryBB(-d, -d, -d, d + 1.0, d + 1.0, d + 1.0);
         } else {
            aABB = null;
         }
      } else {
         aABB = this.createAabb(this.deltaX == null ? 0.0 : this.deltaX, this.deltaY == null ? 0.0 : this.deltaY, this.deltaZ == null ? 0.0 : this.deltaZ);
      }

      Function<Vec3, Vec3> position = this.x == null && this.y == null && this.z == null
         ? vec3 -> vec3
         : vec3 -> Vec3.getTempVec3(this.x == null ? vec3.x : this.x, this.y == null ? vec3.y : this.y, this.z == null ? vec3.z : this.z);
      return new EntitySelector(
         this.maxResults,
         this.includesEntities,
         this.order,
         this.limitToType,
         this.typeInverse,
         this.currentEntity,
         this.predicate,
         this.entityId,
         this.playerName,
         this.distance,
         position,
         aABB
      );
   }

   private AABB createAabb(double d, double e, double f) {
      boolean bl = d < 0.0;
      boolean bl2 = e < 0.0;
      boolean bl3 = f < 0.0;
      double g = bl ? d : 0.0;
      double h = bl2 ? e : 0.0;
      double i = bl3 ? f : 0.0;
      double j = (bl ? 0.0 : d) + 1.0;
      double k = (bl2 ? 0.0 : e) + 1.0;
      double l = (bl3 ? 0.0 : f) + 1.0;
      return AABB.getTemporaryBB(g, h, i, j, k, l);
   }

   public void addPredicate(Predicate<Entity> predicate) {
      this.predicate = this.predicate.and(predicate);
   }

   public boolean shouldInvertValue() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == '!') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   public StringReader getReader() {
      return this.reader;
   }

   private static void fillSelectorSuggestions(SuggestionsBuilder builder) {
      builder.suggest("@p", () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.nearest_player"));
      builder.suggest("@a", () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.all_players"));
      builder.suggest("@r", () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.random_player"));
      builder.suggest("@s", () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.self"));
      builder.suggest("@e", () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.all_entities"));
   }

   public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
      this.suggestions = biFunction;
   }

   private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      consumer.accept(suggestionsBuilder);
      fillSelectorSuggestions(suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
      consumer.accept(suggestionsBuilder2);
      return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
   }

   private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
      fillSelectorSuggestions(suggestionsBuilder2);
      suggestionsBuilder.add(suggestionsBuilder2);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf('['));
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      EntitySelectorOptions.suggestNames(this, suggestionsBuilder);
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf(','));
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      return suggestionsBuilder.buildFuture();
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }

   public BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> getSuggestions() {
      return this.suggestions;
   }

   public boolean hasGamemodeEquals() {
      return this.hasGamemodeEquals;
   }

   public void setHasGamemodeEquals(boolean bl) {
      this.hasGamemodeEquals = bl;
   }

   public boolean hasGamemodeNotEquals() {
      return this.hasGamemodeNotEquals;
   }

   public void setHasGamemodeNotEquals(boolean bl) {
      this.hasGamemodeNotEquals = bl;
   }

   public boolean isSorted() {
      return this.isSorted;
   }

   public void setSorted(boolean bl) {
      this.isSorted = bl;
   }

   public boolean hasLimit() {
      return this.hasLimit;
   }

   public void setHasLimit(boolean bl) {
      this.hasLimit = bl;
   }

   public boolean hasType() {
      return this.limitToType != null;
   }

   public boolean isTypeInverse() {
      return this.typeInverse;
   }

   public void setTypeInverse(boolean bl) {
      this.typeInverse = bl;
   }

   public MinMaxBounds.Doubles getDistance() {
      return this.distance;
   }

   public void setDistance(MinMaxBounds.Doubles distance) {
      this.distance = distance;
   }

   public boolean hasNameEquals() {
      return this.hasNameEquals;
   }

   public void setHasNameEquals(boolean bl) {
      this.hasNameEquals = bl;
   }

   public boolean hasNameNotEquals() {
      return this.hasNameNotEquals;
   }

   public void setHasNameNotEquals(boolean bl) {
      this.hasNameNotEquals = bl;
   }

   public void setMaxResults(int maxResults) {
      this.maxResults = maxResults;
   }

   public void setIncludesEntities(boolean includesEntities) {
      this.includesEntities = includesEntities;
   }

   public void setOrder(BiConsumer<Entity, List<? extends Entity>> order) {
      this.order = order;
   }

   public void setLimitToType(Class<? extends Entity> type) {
      this.limitToType = type;
   }

   public void setCurrentEntity(boolean currentEntity) {
      this.currentEntity = currentEntity;
   }

   public boolean hasNbt() {
      return this.hasNbt;
   }

   public void setHasNbt(boolean bl) {
      this.hasNbt = bl;
   }

   public boolean hasNotNbt() {
      return this.hasNotNbt;
   }

   public void setHasNotNbt(boolean bl) {
      this.hasNotNbt = bl;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public void setDeltaX(double deltaX) {
      this.deltaX = deltaX;
   }

   public void setDeltaY(double deltaY) {
      this.deltaY = deltaY;
   }

   public void setDeltaZ(double deltaZ) {
      this.deltaZ = deltaZ;
   }

   public Double getX() {
      return this.x;
   }

   public Double getY() {
      return this.y;
   }

   public Double getZ() {
      return this.z;
   }

   public Double getDeltaX() {
      return this.deltaX;
   }

   public Double getDeltaY() {
      return this.deltaY;
   }

   public Double getDeltaZ() {
      return this.deltaZ;
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }
}
