package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List serverList = Lists.newArrayList();

   public ServerList(Minecraft minecraft) {
      this.minecraft = minecraft;
      this.load();
   }

   public void load() {
      try {
         this.serverList.clear();
         CompoundTag var1 = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
         if(var1 == null) {
            return;
         }

         ListTag var2 = var1.getList("servers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.serverList.add(ServerData.read(var2.getCompound(var3)));
         }
      } catch (Exception var4) {
         LOGGER.error("Couldn\'t load server list", var4);
      }

   }

   public void save() {
      try {
         ListTag var1 = new ListTag();

         for(ServerData var3 : this.serverList) {
            var1.add(var3.write());
         }

         CompoundTag var2 = new CompoundTag();
         var2.put("servers", var1);
         NbtIo.safeWrite(var2, new File(this.minecraft.gameDirectory, "servers.dat"));
      } catch (Exception var4) {
         LOGGER.error("Couldn\'t save server list", var4);
      }

   }

   public ServerData get(int i) {
      return (ServerData)this.serverList.get(i);
   }

   public void remove(ServerData serverData) {
      this.serverList.remove(serverData);
   }

   public void add(ServerData serverData) {
      this.serverList.add(serverData);
   }

   public int size() {
      return this.serverList.size();
   }

   public void swap(int var1, int var2) {
      ServerData var3 = this.get(var1);
      this.serverList.set(var1, this.get(var2));
      this.serverList.set(var2, var3);
      this.save();
   }

   public void replace(int var1, ServerData serverData) {
      this.serverList.set(var1, serverData);
   }

   public static void saveSingleServer(ServerData serverData) {
      ServerList var1 = new ServerList(Minecraft.getInstance());
      var1.load();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ServerData var3 = var1.get(var2);
         if(var3.name.equals(serverData.name) && var3.ip.equals(serverData.ip)) {
            var1.replace(var2, serverData);
            break;
         }
      }

      var1.save();
   }
}
