package net.minecraft.server.level;

import java.util.Comparator;
import java.util.function.ToLongFunction;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;

public class TicketType {
   private final String name;
   private final Comparator comparator;
   private final long timeout;
   public static final TicketType START = create("start", (var0, var1) -> {
      return 0;
   });
   public static final TicketType DRAGON = create("dragon", (var0, var1) -> {
      return 0;
   });
   public static final TicketType PLAYER = create("player", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType FORCED = create("forced", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType LIGHT = create("light", Comparator.comparingLong(ChunkPos::toLong));
   public static final TicketType PORTAL = create("portal", Comparator.comparingLong(ColumnPos::toLong));
   public static final TicketType POST_TELEPORT = create("post_teleport", Integer::compareTo, 5);
   public static final TicketType UNKNOWN = create("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

   public static TicketType create(String string, Comparator comparator) {
      return new TicketType(string, comparator, 0L);
   }

   public static TicketType create(String string, Comparator comparator, int var2) {
      return new TicketType(string, comparator, (long)var2);
   }

   protected TicketType(String name, Comparator comparator, long timeout) {
      this.name = name;
      this.comparator = comparator;
      this.timeout = timeout;
   }

   public String toString() {
      return this.name;
   }

   public Comparator getComparator() {
      return this.comparator;
   }

   public long timeout() {
      return this.timeout;
   }
}
