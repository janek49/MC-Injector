package net.minecraft.client.gui.screens.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class JoinMultiplayerScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerStatusPinger pinger = new ServerStatusPinger();
   private final Screen lastScreen;
   protected ServerSelectionList serverSelectionList;
   private ServerList servers;
   private Button editButton;
   private Button selectButton;
   private Button deleteButton;
   private String toolTip;
   private ServerData editingServer;
   private LanServerDetection.LanServerList lanServerList;
   private LanServerDetection.LanServerDetector lanServerDetector;
   private boolean initedOnce;

   public JoinMultiplayerScreen(Screen lastScreen) {
      super(new TranslatableComponent("multiplayer.title", new Object[0]));
      this.lastScreen = lastScreen;
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      if(this.initedOnce) {
         this.serverSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
      } else {
         this.initedOnce = true;
         this.servers = new ServerList(this.minecraft);
         this.servers.load();
         this.lanServerList = new LanServerDetection.LanServerList();

         try {
            this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception var2) {
            LOGGER.warn("Unable to start LAN server detection: {}", var2.getMessage());
         }

         this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.children.add(this.serverSelectionList);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 52, 100, 20, I18n.get("selectServer.select", new Object[0]), (button) -> {
         this.joinSelectedServer();
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height - 52, 100, 20, I18n.get("selectServer.direct", new Object[0]), (button) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName", new Object[0]), "", false);
         this.minecraft.setScreen(new DirectJoinServerScreen(this::directJoinCallback, this.editingServer));
      }));
      this.addButton(new Button(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.get("selectServer.add", new Object[0]), (button) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName", new Object[0]), "", false);
         this.minecraft.setScreen(new EditServerScreen(this::addServerCallback, this.editingServer));
      }));
      this.editButton = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 28, 70, 20, I18n.get("selectServer.edit", new Object[0]), (button) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if(var2 instanceof ServerSelectionList.OnlineServerEntry) {
            ServerData var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData();
            this.editingServer = new ServerData(var3.name, var3.ip, false);
            this.editingServer.copyFrom(var3);
            this.minecraft.setScreen(new EditServerScreen(this::editServerCallback, this.editingServer));
         }

      }));
      this.deleteButton = (Button)this.addButton(new Button(this.width / 2 - 74, this.height - 28, 70, 20, I18n.get("selectServer.delete", new Object[0]), (button) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if(var2 instanceof ServerSelectionList.OnlineServerEntry) {
            String var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData().name;
            if(var3 != null) {
               Component var4 = new TranslatableComponent("selectServer.deleteQuestion", new Object[0]);
               Component var5 = new TranslatableComponent("selectServer.deleteWarning", new Object[]{var3});
               String var6 = I18n.get("selectServer.deleteButton", new Object[0]);
               String var7 = I18n.get("gui.cancel", new Object[0]);
               this.minecraft.setScreen(new ConfirmScreen(this::deleteCallback, var4, var5, var6, var7));
            }
         }

      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 28, 70, 20, I18n.get("selectServer.refresh", new Object[0]), (button) -> {
         this.refreshServerList();
      }));
      this.addButton(new Button(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.onSelectedChange();
   }

   public void tick() {
      super.tick();
      if(this.lanServerList.isDirty()) {
         List<LanServer> var1 = this.lanServerList.getServers();
         this.lanServerList.markClean();
         this.serverSelectionList.updateNetworkServers(var1);
      }

      this.pinger.tick();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      if(this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.pinger.removeAll();
   }

   private void refreshServerList() {
      this.minecraft.setScreen(new JoinMultiplayerScreen(this.lastScreen));
   }

   private void deleteCallback(boolean b) {
      ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if(b && var2 instanceof ServerSelectionList.OnlineServerEntry) {
         this.servers.remove(((ServerSelectionList.OnlineServerEntry)var2).getServerData());
         this.servers.save();
         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void editServerCallback(boolean b) {
      ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if(b && var2 instanceof ServerSelectionList.OnlineServerEntry) {
         ServerData var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData();
         var3.name = this.editingServer.name;
         var3.ip = this.editingServer.ip;
         var3.copyFrom(this.editingServer);
         this.servers.save();
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void addServerCallback(boolean b) {
      if(b) {
         this.servers.add(this.editingServer);
         this.servers.save();
         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void directJoinCallback(boolean b) {
      if(b) {
         this.join(this.editingServer);
      } else {
         this.minecraft.setScreen(this);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(super.keyPressed(var1, var2, var3)) {
         return true;
      } else if(var1 == 294) {
         this.refreshServerList();
         return true;
      } else if(this.serverSelectionList.getSelected() == null || var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.joinSelectedServer();
         return true;
      }
   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.renderBackground();
      this.serverSelectionList.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      super.render(var1, var2, var3);
      if(this.toolTip != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.toolTip)), var1, var2);
      }

   }

   public void joinSelectedServer() {
      ServerSelectionList.Entry var1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if(var1 instanceof ServerSelectionList.OnlineServerEntry) {
         this.join(((ServerSelectionList.OnlineServerEntry)var1).getServerData());
      } else if(var1 instanceof ServerSelectionList.NetworkServerEntry) {
         LanServer var2 = ((ServerSelectionList.NetworkServerEntry)var1).getServerData();
         this.join(new ServerData(var2.getMotd(), var2.getAddress(), true));
      }

   }

   private void join(ServerData serverData) {
      this.minecraft.setScreen(new ConnectScreen(this, this.minecraft, serverData));
   }

   public void setSelected(ServerSelectionList.Entry selected) {
      this.serverSelectionList.setSelected(selected);
      this.onSelectedChange();
   }

   protected void onSelectedChange() {
      this.selectButton.active = false;
      this.editButton.active = false;
      this.deleteButton.active = false;
      ServerSelectionList.Entry var1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if(var1 != null && !(var1 instanceof ServerSelectionList.LANHeader)) {
         this.selectButton.active = true;
         if(var1 instanceof ServerSelectionList.OnlineServerEntry) {
            this.editButton.active = true;
            this.deleteButton.active = true;
         }
      }

   }

   public ServerStatusPinger getPinger() {
      return this.pinger;
   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public ServerList getServers() {
      return this.servers;
   }
}
