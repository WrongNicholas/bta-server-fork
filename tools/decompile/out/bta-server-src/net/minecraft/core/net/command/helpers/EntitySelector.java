package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntitySelector {
   public static final String ENTITY_PREFIX = "entity.";
   private final int maxResults;
   private final boolean includesEntities;
   private final BiConsumer<Entity, List<? extends Entity>> order;
   @Nullable
   private final Class<? extends Entity> limitToType;
   private final boolean typeInverse;
   private final boolean currentEntity;
   private final Predicate<Entity> predicate;
   @Nullable
   private final String entityId;
   @Nullable
   private final String playerName;
   private final MinMaxBounds.Doubles distance;
   private final Function<Vec3, Vec3> position;
   @Nullable
   private final AABB aABB;

   public EntitySelector(
      int maxResults,
      boolean includesEntities,
      BiConsumer<Entity, List<? extends Entity>> order,
      @Nullable Class<? extends Entity> limitToType,
      boolean typeInverse,
      boolean currentEntity,
      Predicate<Entity> predicate,
      @Nullable String entityId,
      @Nullable String playerName,
      MinMaxBounds.Doubles distance,
      Function<Vec3, Vec3> position,
      @Nullable AABB aABB
   ) {
      this.maxResults = maxResults;
      this.includesEntities = includesEntities;
      this.order = order;
      this.limitToType = limitToType;
      this.typeInverse = typeInverse;
      this.currentEntity = currentEntity;
      this.predicate = predicate;
      this.entityId = entityId;
      this.playerName = playerName;
      this.distance = distance;
      this.position = position;
      this.aABB = aABB;
   }

   public List<? extends Entity> get(CommandSource source) throws CommandSyntaxException {
      if (this.entityId != null) {
         List<Entity> entities = new ArrayList<>();

         for (Entity entity : source.getWorld().loadedEntityList) {
            if (("entity." + entity.hashCode()).equals(this.entityId)) {
               entities.add(entity);
            }
         }

         List<Entity> list = entities.subList(0, Math.min(entities.size(), this.maxResults));
         if (list.isEmpty()) {
            throw CommandExceptions.emptySelector().create();
         } else {
            return list;
         }
      } else if (this.playerName != null) {
         List<Player> players = new ArrayList<>();

         for (Player player : source.getWorld().players) {
            if (player.username.equals(this.playerName) || player.nickname.equals(this.playerName)) {
               players.add(player);
            }
         }

         List<Player> list = players.subList(0, Math.min(players.size(), this.maxResults));
         if (list.isEmpty()) {
            throw CommandExceptions.emptySelector().create();
         } else {
            return list;
         }
      } else {
         List<? extends Entity> entities;
         if (this.currentEntity) {
            if (source.getSender() == null) {
               throw CommandExceptions.notInWorld().create();
            }

            entities = Collections.singletonList(source.getSender());
         } else if (this.includesEntities) {
            entities = source.getWorld().loadedEntityList;
         } else {
            entities = source.getWorld().players;
         }

         Vec3 sourceCoordinates = source.getCoordinates(true);
         Vec3 position;
         if (sourceCoordinates != null) {
            position = this.position.apply(sourceCoordinates);
            if (this.aABB != null) {
               this.aABB.minX = this.aABB.minX + position.x;
               this.aABB.maxX = this.aABB.maxX + position.x;
               this.aABB.minY = this.aABB.minY + position.y;
               this.aABB.maxY = this.aABB.maxY + position.y;
               this.aABB.minZ = this.aABB.minZ + position.z;
               this.aABB.maxZ = this.aABB.maxZ + position.z;
            }
         } else {
            position = this.position.apply(Vec3.getTempVec3(0.0, 0.0, 0.0));
         }

         List<? extends Entity> temp = new ArrayList<>(entities);

         for (Entity entityx : entities) {
            if (this.limitToType != null && this.limitToType.isInstance(entityx) == this.typeInverse
               || !this.predicate.test(entityx)
               || !this.distanceContains(entityx, position.x, position.y, position.z)
               || !aABBIntersectsWithAABB(this.aABB, entityx.bb)) {
               temp.remove(entityx);
            }
         }

         this.order.accept(source.getSender(), temp);
         List<Entity> listAfterPredicate = new ArrayList<>();

         for (Entity entityxx : temp) {
            if (this.predicate.test(entityxx)) {
               listAfterPredicate.add(entityxx);
            }
         }

         listAfterPredicate = listAfterPredicate.subList(0, Math.min(listAfterPredicate.size(), this.maxResults));
         if (listAfterPredicate.isEmpty()) {
            throw CommandExceptions.emptySelector().create();
         } else {
            return listAfterPredicate;
         }
      }
   }

   private boolean distanceContains(Entity entity, double x, double y, double z) {
      return this.distance.isAny() ? true : this.distance.contains(entity.distanceTo(x, y, z));
   }

   private static boolean aABBIntersectsWithAABB(AABB aABB1, AABB aABB2) {
      return aABB1 == null ? true : aABB2 != null && aABB1.intersects(aABB2);
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isCurrentEntity() {
      return this.currentEntity;
   }
}
