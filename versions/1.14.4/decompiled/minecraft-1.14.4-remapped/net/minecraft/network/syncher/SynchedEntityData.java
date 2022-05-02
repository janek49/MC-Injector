package net.minecraft.network.syncher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map ENTITY_ID_POOL = Maps.newHashMap();
   private final Entity entity;
   private final Map itemsById = Maps.newHashMap();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isEmpty = true;
   private boolean isDirty;

   public SynchedEntityData(Entity entity) {
      this.entity = entity;
   }

   public static EntityDataAccessor defineId(Class class, EntityDataSerializer entityDataSerializer) {
      if(LOGGER.isDebugEnabled()) {
         try {
            Class<?> class = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if(!class.equals(class)) {
               LOGGER.debug("defineId called for: {} from {}", class, class, new RuntimeException());
            }
         } catch (ClassNotFoundException var5) {
            ;
         }
      }

      int var2;
      if(ENTITY_ID_POOL.containsKey(class)) {
         var2 = ((Integer)ENTITY_ID_POOL.get(class)).intValue() + 1;
      } else {
         int var3 = 0;
         Class<?> var4 = class;

         while(var4 != Entity.class) {
            var4 = var4.getSuperclass();
            if(ENTITY_ID_POOL.containsKey(var4)) {
               var3 = ((Integer)ENTITY_ID_POOL.get(var4)).intValue() + 1;
               break;
            }
         }

         var2 = var3;
      }

      if(var2 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var2 + "! (Max is " + 254 + ")");
      } else {
         ENTITY_ID_POOL.put(class, Integer.valueOf(var2));
         return entityDataSerializer.createAccessor(var2);
      }
   }

   public void define(EntityDataAccessor entityDataAccessor, Object object) {
      int var3 = entityDataAccessor.getId();
      if(var3 > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + var3 + "! (Max is " + 254 + ")");
      } else if(this.itemsById.containsKey(Integer.valueOf(var3))) {
         throw new IllegalArgumentException("Duplicate id value for " + var3 + "!");
      } else if(EntityDataSerializers.getSerializedId(entityDataAccessor.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + entityDataAccessor.getSerializer() + " for " + var3 + "!");
      } else {
         this.createDataItem(entityDataAccessor, object);
      }
   }

   private void createDataItem(EntityDataAccessor entityDataAccessor, Object object) {
      SynchedEntityData.DataItem<T> var3 = new SynchedEntityData.DataItem(entityDataAccessor, object);
      this.lock.writeLock().lock();
      this.itemsById.put(Integer.valueOf(entityDataAccessor.getId()), var3);
      this.isEmpty = false;
      this.lock.writeLock().unlock();
   }

   private SynchedEntityData.DataItem getItem(EntityDataAccessor entityDataAccessor) {
      this.lock.readLock().lock();

      SynchedEntityData.DataItem<T> synchedEntityData$DataItem;
      try {
         synchedEntityData$DataItem = (SynchedEntityData.DataItem)this.itemsById.get(Integer.valueOf(entityDataAccessor.getId()));
      } catch (Throwable var9) {
         CrashReport var4 = CrashReport.forThrowable(var9, "Getting synched entity data");
         CrashReportCategory var5 = var4.addCategory("Synched entity data");
         var5.setDetail("Data ID", (Object)entityDataAccessor);
         throw new ReportedException(var4);
      } finally {
         this.lock.readLock().unlock();
      }

      return synchedEntityData$DataItem;
   }

   public Object get(EntityDataAccessor entityDataAccessor) {
      return this.getItem(entityDataAccessor).getValue();
   }

   public void set(EntityDataAccessor entityDataAccessor, Object object) {
      SynchedEntityData.DataItem<T> var3 = this.getItem(entityDataAccessor);
      if(ObjectUtils.notEqual(object, var3.getValue())) {
         var3.setValue(object);
         this.entity.onSyncedDataUpdated(entityDataAccessor);
         var3.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public static void pack(List list, FriendlyByteBuf friendlyByteBuf) throws IOException {
      if(list != null) {
         int var2 = 0;

         for(int var3 = list.size(); var2 < var3; ++var2) {
            writeDataItem(friendlyByteBuf, (SynchedEntityData.DataItem)list.get(var2));
         }
      }

      friendlyByteBuf.writeByte(255);
   }

   @Nullable
   public List packDirty() {
      List<SynchedEntityData.DataItem<?>> list = null;
      if(this.isDirty) {
         this.lock.readLock().lock();

         for(SynchedEntityData.DataItem<?> var3 : this.itemsById.values()) {
            if(var3.isDirty()) {
               var3.setDirty(false);
               if(list == null) {
                  list = Lists.newArrayList();
               }

               list.add(var3.copy());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return list;
   }

   public void packAll(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> var3 : this.itemsById.values()) {
         writeDataItem(friendlyByteBuf, var3);
      }

      this.lock.readLock().unlock();
      friendlyByteBuf.writeByte(255);
   }

   @Nullable
   public List getAll() {
      List<SynchedEntityData.DataItem<?>> list = null;
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> var3 : this.itemsById.values()) {
         if(list == null) {
            list = Lists.newArrayList();
         }

         list.add(var3.copy());
      }

      this.lock.readLock().unlock();
      return list;
   }

   private static void writeDataItem(FriendlyByteBuf friendlyByteBuf, SynchedEntityData.DataItem synchedEntityData$DataItem) throws IOException {
      EntityDataAccessor<T> var2 = synchedEntityData$DataItem.getAccessor();
      int var3 = EntityDataSerializers.getSerializedId(var2.getSerializer());
      if(var3 < 0) {
         throw new EncoderException("Unknown serializer type " + var2.getSerializer());
      } else {
         friendlyByteBuf.writeByte(var2.getId());
         friendlyByteBuf.writeVarInt(var3);
         var2.getSerializer().write(friendlyByteBuf, synchedEntityData$DataItem.getValue());
      }
   }

   @Nullable
   public static List unpack(FriendlyByteBuf friendlyByteBuf) throws IOException {
      List<SynchedEntityData.DataItem<?>> list = null;

      int var2;
      while((var2 = friendlyByteBuf.readUnsignedByte()) != 255) {
         if(list == null) {
            list = Lists.newArrayList();
         }

         int var3 = friendlyByteBuf.readVarInt();
         EntityDataSerializer<?> var4 = EntityDataSerializers.getSerializer(var3);
         if(var4 == null) {
            throw new DecoderException("Unknown serializer type " + var3);
         }

         list.add(genericHelper(friendlyByteBuf, var2, var4));
      }

      return list;
   }

   private static SynchedEntityData.DataItem genericHelper(FriendlyByteBuf friendlyByteBuf, int var1, EntityDataSerializer entityDataSerializer) {
      return new SynchedEntityData.DataItem(entityDataSerializer.createAccessor(var1), entityDataSerializer.read(friendlyByteBuf));
   }

   public void assignValues(List list) {
      this.lock.writeLock().lock();

      for(SynchedEntityData.DataItem<?> var3 : list) {
         SynchedEntityData.DataItem<?> var4 = (SynchedEntityData.DataItem)this.itemsById.get(Integer.valueOf(var3.getAccessor().getId()));
         if(var4 != null) {
            this.assignValue(var4, var3);
            this.entity.onSyncedDataUpdated(var3.getAccessor());
         }
      }

      this.lock.writeLock().unlock();
      this.isDirty = true;
   }

   private void assignValue(SynchedEntityData.DataItem var1, SynchedEntityData.DataItem var2) {
      if(!Objects.equals(var2.accessor.getSerializer(), var1.accessor.getSerializer())) {
         throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", new Object[]{Integer.valueOf(var1.accessor.getId()), this.entity, var1.value, var1.value.getClass(), var2.value, var2.value.getClass()}));
      } else {
         var1.setValue(var2.getValue());
      }
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public void clearDirty() {
      this.isDirty = false;
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> var2 : this.itemsById.values()) {
         var2.setDirty(false);
      }

      this.lock.readLock().unlock();
   }

   public static class DataItem {
      private final EntityDataAccessor accessor;
      private Object value;
      private boolean dirty;

      public DataItem(EntityDataAccessor accessor, Object value) {
         this.accessor = accessor;
         this.value = value;
         this.dirty = true;
      }

      public EntityDataAccessor getAccessor() {
         return this.accessor;
      }

      public void setValue(Object value) {
         this.value = value;
      }

      public Object getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean dirty) {
         this.dirty = dirty;
      }

      public SynchedEntityData.DataItem copy() {
         return new SynchedEntityData.DataItem(this.accessor, this.accessor.getSerializer().copy(this.value));
      }
   }
}
