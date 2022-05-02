package net.minecraft.server.level;

import java.util.Objects;
import net.minecraft.server.level.TicketType;

public final class Ticket implements Comparable {
   private final TicketType type;
   private final int ticketLevel;
   private final Object key;
   private final long createdTick;

   protected Ticket(TicketType type, int ticketLevel, Object key, long createdTick) {
      this.type = type;
      this.ticketLevel = ticketLevel;
      this.key = key;
      this.createdTick = createdTick;
   }

   public int compareTo(Ticket ticket) {
      int var2 = Integer.compare(this.ticketLevel, ticket.ticketLevel);
      if(var2 != 0) {
         return var2;
      } else {
         int var3 = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(ticket.type));
         return var3 != 0?var3:this.type.getComparator().compare(this.key, ticket.key);
      }
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Ticket)) {
         return false;
      } else {
         Ticket<?> var2 = (Ticket)object;
         return this.ticketLevel == var2.ticketLevel && Objects.equals(this.type, var2.type) && Objects.equals(this.key, var2.key);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, Integer.valueOf(this.ticketLevel), this.key});
   }

   public String toString() {
      return "Ticket[" + this.type + " " + this.ticketLevel + " (" + this.key + ")] at " + this.createdTick;
   }

   public TicketType getType() {
      return this.type;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   public boolean timedOut(long l) {
      long var3 = this.type.timeout();
      return var3 != 0L && l - this.createdTick > var3;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Ticket)var1);
   }
}
