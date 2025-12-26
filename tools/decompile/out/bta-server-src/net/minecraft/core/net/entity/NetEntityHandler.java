package net.minecraft.core.net.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.net.entity.entries.NetEntryAnimal;
import net.minecraft.core.net.entity.entries.NetEntryArrow;
import net.minecraft.core.net.entity.entries.NetEntryBoat;
import net.minecraft.core.net.entity.entries.NetEntryBobber;
import net.minecraft.core.net.entity.entries.NetEntryCannonball;
import net.minecraft.core.net.entity.entries.NetEntryEgg;
import net.minecraft.core.net.entity.entries.NetEntryFallingBlock;
import net.minecraft.core.net.entity.entries.NetEntryFireball;
import net.minecraft.core.net.entity.entries.NetEntryFirefly;
import net.minecraft.core.net.entity.entries.NetEntryItem;
import net.minecraft.core.net.entity.entries.NetEntryMinecart;
import net.minecraft.core.net.entity.entries.NetEntryPainting;
import net.minecraft.core.net.entity.entries.NetEntryPebble;
import net.minecraft.core.net.entity.entries.NetEntryPlayerServer;
import net.minecraft.core.net.entity.entries.NetEntryPrimedTNT;
import net.minecraft.core.net.entity.entries.NetEntrySnowball;
import net.minecraft.core.net.entity.entries.NetEntrySquid;
import net.minecraft.core.net.packet.Packet;
import org.jetbrains.annotations.NotNull;

public class NetEntityHandler {
   private static final Map<Class<?>, INetworkEntry<?>> entryClassMap = new LinkedHashMap<>();
   private static final List<INetworkEntry<?>> netEntries = new ArrayList<>();
   private static final List<IPacketEntry<?>> packetEntries = new ArrayList<>();
   private static final List<ITrackedEntry<?>> trackedEntries = new ArrayList<>();
   private static final Map<Integer, IVehicleEntry<?>> typeToProviderMap = new HashMap<>();
   private static final Map<IVehicleEntry<?>, Integer> providerToTypeMap = new HashMap<>();
   private static boolean isListDirty = false;

   public static <T> void registerNetworkEntry(INetworkEntry<T> networkEntity) {
      addEntryRaw(networkEntity, -1);
   }

   public static <T> void registerNetworkEntry(IVehicleEntry<T> typedEntity, int type) {
      addEntryRaw(typedEntity, type);
   }

   private static <T> void addEntryRaw(INetworkEntry<T> entry, int type) {
      Class<? extends T> entityClass = entry.getAppliedClass();
      if (entryClassMap.containsKey(entityClass)) {
         INetworkEntry<?> previousEntry = entryClassMap.get(entityClass);
         netEntries.remove(previousEntry);
         packetEntries.remove(previousEntry);
         trackedEntries.remove(previousEntry);
         entryClassMap.remove(entityClass);
         if (type >= 0 && typeToProviderMap.containsKey(type)) {
            providerToTypeMap.remove(typeToProviderMap.remove(type));
         }
      }

      entryClassMap.put(entityClass, entry);
      netEntries.add(entry);
      if (type >= 0 && entry instanceof IVehicleEntry) {
         typeToProviderMap.put(type, (IVehicleEntry<?>)entry);
         providerToTypeMap.put((IVehicleEntry<?>)entry, type);
      }

      if (entry instanceof IPacketEntry) {
         packetEntries.add((IPacketEntry<?>)entry);
      }

      if (entry instanceof ITrackedEntry) {
         trackedEntries.add((ITrackedEntry<?>)entry);
      }

      isListDirty = true;
   }

   public static Packet getSpawnPacket(@NotNull EntityTrackerEntry trackerEntry) {
      if (isListDirty) {
         sortEntries();
      }

      for (IPacketEntry netEnt : packetEntries) {
         if (netEnt.getAppliedClass().isAssignableFrom(trackerEntry.getTrackedEntity().getClass())) {
            Packet packet;
            if (netEnt instanceof IVehicleEntry) {
               packet = ((IVehicleEntry)netEnt).getSpawnPacket(trackerEntry, trackerEntry.getTrackedEntity()).setType(providerToTypeMap.get(netEnt));
            } else {
               packet = netEnt.getSpawnPacket(trackerEntry, trackerEntry.getTrackedEntity());
            }

            return packet;
         }
      }

      throw new IllegalArgumentException("Don't know how to add " + trackerEntry.getTrackedEntity().getClass() + "!");
   }

   public static <T> ITrackedEntry<T> getTrackedEntry(T entity) {
      if (isListDirty) {
         sortEntries();
      }

      for (ITrackedEntry tracked : trackedEntries) {
         if (tracked.getAppliedClass().isAssignableFrom(entity.getClass())) {
            return tracked;
         }
      }

      return null;
   }

   public static boolean hasType(int type) {
      return typeToProviderMap.containsKey(type);
   }

   public static IVehicleEntry<?> getTypeEntry(int type) {
      return typeToProviderMap.get(type);
   }

   private static void sortEntries() {
      Comparator<INetworkEntry<?>> networkEntryComparator = (o1, o2) -> {
         if (o1.getAppliedClass().isAssignableFrom(o2.getAppliedClass())) {
            return 1;
         } else {
            return o2.getAppliedClass().isAssignableFrom(o1.getAppliedClass())
               ? -1
               : o1.getAppliedClass().getSimpleName().compareTo(o2.getAppliedClass().getSimpleName());
         }
      };
      netEntries.sort(networkEntryComparator);
      packetEntries.sort(networkEntryComparator);
      trackedEntries.sort(networkEntryComparator);
      isListDirty = false;
   }

   static {
      registerNetworkEntry(new NetEntryAnimal());
      registerNetworkEntry(new NetEntrySquid());
      registerNetworkEntry(new NetEntryFirefly());
      registerNetworkEntry(new NetEntryItem());
      registerNetworkEntry(new NetEntryPainting());
      registerNetworkEntry(new NetEntryPlayerServer());
      registerNetworkEntry(new NetEntryArrow(), 1);
      registerNetworkEntry(new NetEntryCannonball(), 2);
      registerNetworkEntry(new NetEntryEgg(), 3);
      registerNetworkEntry(new NetEntryFireball(), 4);
      registerNetworkEntry(new NetEntryPebble(), 5);
      registerNetworkEntry(new NetEntrySnowball(), 6);
      registerNetworkEntry(new NetEntryBoat(), 10);
      registerNetworkEntry(new NetEntryMinecart(), 11);
      registerNetworkEntry(new NetEntryFallingBlock(), 20);
      registerNetworkEntry(new NetEntryPrimedTNT(), 21);
      registerNetworkEntry(new NetEntryBobber(), 30);
   }
}
