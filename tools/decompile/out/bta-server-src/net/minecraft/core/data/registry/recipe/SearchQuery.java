package net.minecraft.core.data.registry.recipe;

import java.util.Objects;
import net.minecraft.core.util.collection.Pair;

public class SearchQuery {
   public String rawQuery;
   public SearchQuery.SearchMode mode;
   public Pair<SearchQuery.SearchScope, String> scope;
   public Pair<SearchQuery.QueryType, String> query;
   public boolean strict;

   public SearchQuery(
      String rawQuery, SearchQuery.SearchMode mode, Pair<SearchQuery.SearchScope, String> scope, Pair<SearchQuery.QueryType, String> query, boolean strict
   ) {
      this.rawQuery = rawQuery;
      this.mode = mode;
      this.scope = scope;
      this.query = query;
      this.strict = strict;
   }

   public static SearchQuery resolve(String rawQuery) {
      SearchQuery.SearchMode mode;
      if (rawQuery.startsWith("r:") || rawQuery.startsWith("R:")) {
         mode = SearchQuery.SearchMode.RECIPE;
      } else if (!rawQuery.startsWith("u:") && !rawQuery.startsWith("U:")) {
         mode = SearchQuery.SearchMode.ALL;
      } else {
         mode = SearchQuery.SearchMode.USAGE;
      }

      String modeRemoved = rawQuery.replace("r:", "").replace("u:", "").replace("R:", "").replace("U:", "");
      Pair<SearchQuery.SearchScope, String> scope;
      if (modeRemoved.startsWith("@") && modeRemoved.split(" ").length > 1) {
         String scopePart = modeRemoved.split(" ")[0];
         String searchPart = modeRemoved.split(" ")[1];
         if (scopePart.contains(":")) {
            scope = Pair.of(SearchQuery.SearchScope.NAMESPACE_GROUP, scopePart.replace("@", ""));
         } else {
            scope = Pair.of(SearchQuery.SearchScope.NAMESPACE, scopePart.replace("@", ""));
         }
      } else if (modeRemoved.startsWith("@")) {
         String scopePart = modeRemoved.trim();
         if (scopePart.contains(":")) {
            scope = Pair.of(SearchQuery.SearchScope.NAMESPACE_GROUP, scopePart.replace("@", ""));
         } else {
            scope = Pair.of(SearchQuery.SearchScope.NAMESPACE, scopePart.replace("@", ""));
         }
      } else {
         scope = Pair.of(SearchQuery.SearchScope.NONE, "");
      }

      String scopeRemoved = modeRemoved.replace("@" + scope.getRight(), "").trim();
      boolean strict = rawQuery.endsWith("!");
      scopeRemoved = scopeRemoved.replace("!", "");
      Pair<SearchQuery.QueryType, String> query;
      if (scopeRemoved.startsWith("#")) {
         query = Pair.of(SearchQuery.QueryType.GROUP, scopeRemoved.replace("#", ""));
      } else {
         query = Pair.of(SearchQuery.QueryType.NAME, scopeRemoved);
      }

      return new SearchQuery(rawQuery, mode, scope, query, strict);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SearchQuery that = (SearchQuery)o;
         if (this.strict != that.strict) {
            return false;
         } else if (!Objects.equals(this.rawQuery, that.rawQuery)) {
            return false;
         } else if (this.mode != that.mode) {
            return false;
         } else {
            return !Objects.equals(this.scope, that.scope) ? false : Objects.equals(this.query, that.query);
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "SearchQuery{rawQuery='"
         + this.rawQuery
         + '\''
         + ", mode="
         + this.mode
         + ", scope="
         + this.scope
         + ", query="
         + this.query
         + ", strict="
         + this.strict
         + '}';
   }

   public static enum QueryType {
      NAME,
      GROUP;
   }

   public static enum SearchMode {
      ALL,
      RECIPE,
      USAGE;
   }

   public static enum SearchScope {
      NONE,
      NAMESPACE,
      NAMESPACE_GROUP;
   }
}
