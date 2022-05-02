package com.mojang.realmsclient.gui;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsDataFetcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
   private volatile boolean stopped = true;
   private final RealmsDataFetcher.ServerListUpdateTask serverListUpdateTask = new RealmsDataFetcher.ServerListUpdateTask();
   private final RealmsDataFetcher.PendingInviteUpdateTask pendingInviteUpdateTask = new RealmsDataFetcher.PendingInviteUpdateTask();
   private final RealmsDataFetcher.TrialAvailabilityTask trialAvailabilityTask = new RealmsDataFetcher.TrialAvailabilityTask();
   private final RealmsDataFetcher.LiveStatsTask liveStatsTask = new RealmsDataFetcher.LiveStatsTask();
   private final RealmsDataFetcher.UnreadNewsTask unreadNewsTask = new RealmsDataFetcher.UnreadNewsTask();
   private final Set removedServers = Sets.newHashSet();
   private List servers = Lists.newArrayList();
   private RealmsServerPlayerLists livestats;
   private int pendingInvitesCount;
   private boolean trialAvailable;
   private boolean hasUnreadNews;
   private String newsLink;
   private ScheduledFuture serverListScheduledFuture;
   private ScheduledFuture pendingInviteScheduledFuture;
   private ScheduledFuture trialAvailableScheduledFuture;
   private ScheduledFuture liveStatsScheduledFuture;
   private ScheduledFuture unreadNewsScheduledFuture;
   private final Map fetchStatus = new ConcurrentHashMap(RealmsDataFetcher.Task.values().length);

   public boolean isStopped() {
      return this.stopped;
   }

   public synchronized void init() {
      if(this.stopped) {
         this.stopped = false;
         this.cancelTasks();
         this.scheduleTasks();
      }

   }

   public synchronized void initWithSpecificTaskList(List list) {
      if(this.stopped) {
         this.stopped = false;
         this.cancelTasks();

         for(RealmsDataFetcher.Task var3 : list) {
            this.fetchStatus.put(var3, Boolean.valueOf(false));
            switch(var3) {
            case SERVER_LIST:
               this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
               break;
            case PENDING_INVITE:
               this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
               break;
            case TRIAL_AVAILABLE:
               this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
               break;
            case LIVE_STATS:
               this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
               break;
            case UNREAD_NEWS:
               this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
            }
         }
      }

   }

   public boolean isFetchedSinceLastTry(RealmsDataFetcher.Task realmsDataFetcher$Task) {
      Boolean var2 = (Boolean)this.fetchStatus.get(realmsDataFetcher$Task);
      return var2 == null?false:var2.booleanValue();
   }

   public void markClean() {
      for(RealmsDataFetcher.Task var2 : this.fetchStatus.keySet()) {
         this.fetchStatus.put(var2, Boolean.valueOf(false));
      }

   }

   public synchronized void forceUpdate() {
      this.stop();
      this.init();
   }

   public synchronized List getServers() {
      return Lists.newArrayList(this.servers);
   }

   public synchronized int getPendingInvitesCount() {
      return this.pendingInvitesCount;
   }

   public synchronized boolean isTrialAvailable() {
      return this.trialAvailable;
   }

   public synchronized RealmsServerPlayerLists getLivestats() {
      return this.livestats;
   }

   public synchronized boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public synchronized String newsLink() {
      return this.newsLink;
   }

   public synchronized void stop() {
      this.stopped = true;
      this.cancelTasks();
   }

   private void scheduleTasks() {
      for(RealmsDataFetcher.Task var4 : RealmsDataFetcher.Task.values()) {
         this.fetchStatus.put(var4, Boolean.valueOf(false));
      }

      this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
      this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
      this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
      this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
      this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
   }

   private void cancelTasks() {
      try {
         if(this.serverListScheduledFuture != null) {
            this.serverListScheduledFuture.cancel(false);
         }

         if(this.pendingInviteScheduledFuture != null) {
            this.pendingInviteScheduledFuture.cancel(false);
         }

         if(this.trialAvailableScheduledFuture != null) {
            this.trialAvailableScheduledFuture.cancel(false);
         }

         if(this.liveStatsScheduledFuture != null) {
            this.liveStatsScheduledFuture.cancel(false);
         }

         if(this.unreadNewsScheduledFuture != null) {
            this.unreadNewsScheduledFuture.cancel(false);
         }
      } catch (Exception var2) {
         LOGGER.error("Failed to cancel Realms tasks", var2);
      }

   }

   private synchronized void setServers(List servers) {
      int var2 = 0;

      for(RealmsServer var4 : this.removedServers) {
         if(servers.remove(var4)) {
            ++var2;
         }
      }

      if(var2 == 0) {
         this.removedServers.clear();
      }

      this.servers = servers;
   }

   public synchronized void removeItem(RealmsServer realmsServer) {
      this.servers.remove(realmsServer);
      this.removedServers.add(realmsServer);
   }

   private void sort(List list) {
      Collections.sort(list, new RealmsServer.McoServerComparator(Realms.getName()));
   }

   private boolean isActive() {
      return !this.stopped;
   }

   @ClientJarOnly
   class LiveStatsTask implements Runnable {
      private LiveStatsTask() {
      }

      public void run() {
         if(RealmsDataFetcher.this.isActive()) {
            this.getLiveStats();
         }

      }

      private void getLiveStats() {
         try {
            RealmsClient var1 = RealmsClient.createRealmsClient();
            if(var1 != null) {
               RealmsDataFetcher.this.livestats = var1.getLiveStats();
               RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.LIVE_STATS, Boolean.valueOf(true));
            }
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn\'t get live stats", var2);
         }

      }
   }

   @ClientJarOnly
   class PendingInviteUpdateTask implements Runnable {
      private PendingInviteUpdateTask() {
      }

      public void run() {
         if(RealmsDataFetcher.this.isActive()) {
            this.updatePendingInvites();
         }

      }

      private void updatePendingInvites() {
         try {
            RealmsClient var1 = RealmsClient.createRealmsClient();
            if(var1 != null) {
               RealmsDataFetcher.this.pendingInvitesCount = var1.pendingInvitesCount();
               RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, Boolean.valueOf(true));
            }
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn\'t get pending invite count", var2);
         }

      }
   }

   @ClientJarOnly
   class ServerListUpdateTask implements Runnable {
      private ServerListUpdateTask() {
      }

      public void run() {
         if(RealmsDataFetcher.this.isActive()) {
            this.updateServersList();
         }

      }

      private void updateServersList() {
         try {
            RealmsClient var1 = RealmsClient.createRealmsClient();
            if(var1 != null) {
               List<RealmsServer> var2 = var1.listWorlds().servers;
               if(var2 != null) {
                  RealmsDataFetcher.this.sort(var2);
                  RealmsDataFetcher.this.setServers(var2);
                  RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, Boolean.valueOf(true));
               } else {
                  RealmsDataFetcher.LOGGER.warn("Realms server list was null or empty");
               }
            }
         } catch (Exception var3) {
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, Boolean.valueOf(true));
            RealmsDataFetcher.LOGGER.error("Couldn\'t get server list", var3);
         }

      }
   }

   @ClientJarOnly
   public static enum Task {
      SERVER_LIST,
      PENDING_INVITE,
      TRIAL_AVAILABLE,
      LIVE_STATS,
      UNREAD_NEWS;
   }

   @ClientJarOnly
   class TrialAvailabilityTask implements Runnable {
      private TrialAvailabilityTask() {
      }

      public void run() {
         if(RealmsDataFetcher.this.isActive()) {
            this.getTrialAvailable();
         }

      }

      private void getTrialAvailable() {
         try {
            RealmsClient var1 = RealmsClient.createRealmsClient();
            if(var1 != null) {
               RealmsDataFetcher.this.trialAvailable = var1.trialAvailable().booleanValue();
               RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, Boolean.valueOf(true));
            }
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn\'t get trial availability", var2);
         }

      }
   }

   @ClientJarOnly
   class UnreadNewsTask implements Runnable {
      private UnreadNewsTask() {
      }

      public void run() {
         if(RealmsDataFetcher.this.isActive()) {
            this.getUnreadNews();
         }

      }

      private void getUnreadNews() {
         try {
            RealmsClient var1 = RealmsClient.createRealmsClient();
            if(var1 != null) {
               RealmsNews var2 = null;

               try {
                  var2 = var1.getNews();
               } catch (Exception var5) {
                  ;
               }

               RealmsPersistence.RealmsPersistenceData var3 = RealmsPersistence.readFile();
               if(var2 != null) {
                  String var4 = var2.newsLink;
                  if(var4 != null && !var4.equals(var3.newsLink)) {
                     var3.hasUnreadNews = true;
                     var3.newsLink = var4;
                     RealmsPersistence.writeFile(var3);
                  }
               }

               RealmsDataFetcher.this.hasUnreadNews = var3.hasUnreadNews;
               RealmsDataFetcher.this.newsLink = var3.newsLink;
               RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, Boolean.valueOf(true));
            }
         } catch (Exception var6) {
            RealmsDataFetcher.LOGGER.error("Couldn\'t get unread news", var6);
         }

      }
   }
}
