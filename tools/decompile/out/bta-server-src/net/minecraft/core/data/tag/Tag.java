package net.minecraft.core.data.tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Tag<E extends ITaggable<E>> {
   protected final String name;
   protected final Set<E> elements;

   protected Tag(String name, Set<E> elements) {
      this.name = name;
      this.elements = elements;
   }

   protected Tag(String name) {
      this(name, new HashSet<>());
   }

   public static <E extends ITaggable<E>> Tag<E> of(String name) {
      return new Tag<>(name);
   }

   public static <E extends ITaggable<E>> Tag<E> of(String name, Set<E> elements) {
      return new Tag<>(name, elements);
   }

   public String getName() {
      return this.name;
   }

   public void tag(E element) {
      this.elements.add(element);
   }

   public void tagAll(Collection<E> allElements) {
      this.elements.addAll(allElements);
   }

   public boolean appliesTo(E element) {
      return this.elements.contains(element);
   }
}
