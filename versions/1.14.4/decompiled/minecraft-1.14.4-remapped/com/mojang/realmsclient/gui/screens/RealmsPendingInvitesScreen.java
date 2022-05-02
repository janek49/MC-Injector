package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RowButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsPendingInvitesScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private String toolTip;
   private boolean loaded;
   private RealmsPendingInvitesScreen.PendingInvitationSelectionList pendingInvitationSelectionList;
   private RealmsLabel titleLabel;
   private int selectedInvite = -1;
   private RealmsButton acceptButton;
   private RealmsButton rejectButton;

   public RealmsPendingInvitesScreen(RealmsScreen lastScreen) {
      this.lastScreen = lastScreen;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.pendingInvitationSelectionList = new RealmsPendingInvitesScreen.PendingInvitationSelectionList();
      (new Thread("Realms-pending-invitations-fetcher") {
         public void run() {
            RealmsClient var1 = RealmsClient.createRealmsClient();

            try {
               List<PendingInvite> var2 = var1.pendingInvites().pendingInvites;
               List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> var3 = (List)var2.stream().map((pendingInvite) -> {
                  return RealmsPendingInvitesScreen.this.new PendingInvitationSelectionListEntry(pendingInvite);
               }).collect(Collectors.toList());
               Realms.execute(() -> {
                  RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(var3);
               });
            } catch (RealmsServiceException var7) {
               RealmsPendingInvitesScreen.LOGGER.error("Couldn\'t list invites");
            } finally {
               RealmsPendingInvitesScreen.this.loaded = true;
            }

         }
      }).start();
      this.buttonsAdd(this.acceptButton = new RealmsButton(1, this.width() / 2 - 174, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.accept")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.accept(RealmsPendingInvitesScreen.this.selectedInvite);
            RealmsPendingInvitesScreen.this.selectedInvite = -1;
            RealmsPendingInvitesScreen.this.updateButtonStates();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, this.height() - 32, 100, 20, getLocalizedString("gui.done")) {
         public void onPress() {
            Realms.setScreen(new RealmsMainScreen(RealmsPendingInvitesScreen.this.lastScreen));
         }
      });
      this.buttonsAdd(this.rejectButton = new RealmsButton(2, this.width() / 2 + 74, this.height() - 32, 100, 20, getLocalizedString("mco.invites.button.reject")) {
         public void onPress() {
            RealmsPendingInvitesScreen.this.reject(RealmsPendingInvitesScreen.this.selectedInvite);
            RealmsPendingInvitesScreen.this.selectedInvite = -1;
            RealmsPendingInvitesScreen.this.updateButtonStates();
         }
      });
      this.titleLabel = new RealmsLabel(getLocalizedString("mco.invites.title"), this.width() / 2, 12, 16777215);
      this.addWidget(this.titleLabel);
      this.addWidget(this.pendingInvitationSelectionList);
      this.narrateLabels();
      this.updateButtonStates();
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         Realms.setScreen(new RealmsMainScreen(this.lastScreen));
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void updateList(int i) {
      this.pendingInvitationSelectionList.removeAtIndex(i);
   }

   private void reject(final int i) {
      if(i < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-reject-invitation") {
            public void run() {
               try {
                  RealmsClient var1 = RealmsClient.createRealmsClient();
                  var1.rejectInvitation(((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(i)).pendingInvite.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(i);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn\'t reject invite");
               }

            }
         }).start();
      }

   }

   private void accept(final int i) {
      if(i < this.pendingInvitationSelectionList.getItemCount()) {
         (new Thread("Realms-accept-invitation") {
            public void run() {
               try {
                  RealmsClient var1 = RealmsClient.createRealmsClient();
                  var1.acceptInvitation(((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children().get(i)).pendingInvite.invitationId);
                  Realms.execute(() -> {
                     RealmsPendingInvitesScreen.this.updateList(i);
                  });
               } catch (RealmsServiceException var2) {
                  RealmsPendingInvitesScreen.LOGGER.error("Couldn\'t accept invite");
               }

            }
         }).start();
      }

   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.renderBackground();
      this.pendingInvitationSelectionList.render(var1, var2, var3);
      this.titleLabel.render(this);
      if(this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, var1, var2);
      }

      if(this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
         this.drawCenteredString(getLocalizedString("mco.invites.nopending"), this.width() / 2, this.height() / 2 - 20, 16777215);
      }

      super.render(var1, var2, var3);
   }

   protected void renderMousehoverTooltip(String string, int var2, int var3) {
      if(string != null) {
         int var4 = var2 + 12;
         int var5 = var3 - 12;
         int var6 = this.fontWidth(string);
         this.fillGradient(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(string, var4, var5, 16777215);
      }
   }

   private void updateButtonStates() {
      this.acceptButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
      this.rejectButton.setVisible(this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite));
   }

   private boolean shouldAcceptAndRejectButtonBeVisible(int i) {
      return i != -1;
   }

   public static String getAge(PendingInvite pendingInvite) {
      return RealmsUtil.convertToAgePresentation(Long.valueOf(System.currentTimeMillis() - pendingInvite.date.getTime()));
   }

   @ClientJarOnly
   class PendingInvitationSelectionList extends RealmsObjectSelectionList {
      public PendingInvitationSelectionList() {
         super(RealmsPendingInvitesScreen.this.width(), RealmsPendingInvitesScreen.this.height(), 32, RealmsPendingInvitesScreen.this.height() - 40, 36);
      }

      public void removeAtIndex(int i) {
         this.remove(i);
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 260;
      }

      public boolean isFocused() {
         return RealmsPendingInvitesScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsPendingInvitesScreen.this.renderBackground();
      }

      public void selectItem(int selected) {
         this.setSelected(selected);
         if(selected != -1) {
            List<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> var2 = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
            PendingInvite var3 = ((RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry)var2.get(selected)).pendingInvite;
            String var4 = RealmsScreen.getLocalizedString("narrator.select.list.position", new Object[]{Integer.valueOf(selected + 1), Integer.valueOf(var2.size())});
            String var5 = Realms.joinNarrations(Arrays.asList(new String[]{var3.worldName, var3.worldOwnerName, RealmsPendingInvitesScreen.getAge(var3), var4}));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", new Object[]{var5}));
         }

         this.selectInviteListItem(selected);
      }

      public void selectInviteListItem(int i) {
         RealmsPendingInvitesScreen.this.selectedInvite = i;
         RealmsPendingInvitesScreen.this.updateButtonStates();
      }
   }

   @ClientJarOnly
   class PendingInvitationSelectionListEntry extends RealmListEntry {
      final PendingInvite pendingInvite;
      private final List rowButtons;

      PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
         this.pendingInvite = pendingInvite;
         this.rowButtons = Arrays.asList(new RowButton[]{new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.AcceptRowButton(), new RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry.RejectRowButton()});
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderPendingInvitationItem(this.pendingInvite, var3, var2, var6, var7);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, var5, var1, var3);
         return true;
      }

      private void renderPendingInvitationItem(PendingInvite pendingInvite, int var2, int var3, int var4, int var5) {
         RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldName, var2 + 38, var3 + 1, 16777215);
         RealmsPendingInvitesScreen.this.drawString(pendingInvite.worldOwnerName, var2 + 38, var3 + 12, 7105644);
         RealmsPendingInvitesScreen.this.drawString(RealmsPendingInvitesScreen.getAge(pendingInvite), var2 + 38, var3 + 24, 7105644);
         RowButton.drawButtonsInRow(this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, var2, var3, var4, var5);
         RealmsTextureManager.withBoundFace(pendingInvite.worldOwnerUuid, () -> {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(var2, var3, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            RealmsScreen.blit(var2, var3, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
         });
      }

      @ClientJarOnly
      class AcceptRowButton extends RowButton {
         AcceptRowButton() {
            super(15, 15, 215, 5);
         }

         protected void draw(int var1, int var2, boolean var3) {
            RealmsScreen.bind("realms:textures/gui/realms/accept_icon.png");
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(var1, var2, var3?19.0F:0.0F, 0.0F, 18, 18, 37, 18);
            GlStateManager.popMatrix();
            if(var3) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.accept");
            }

         }

         public void onClick(int i) {
            RealmsPendingInvitesScreen.this.accept(i);
         }
      }

      @ClientJarOnly
      class RejectRowButton extends RowButton {
         RejectRowButton() {
            super(15, 15, 235, 5);
         }

         protected void draw(int var1, int var2, boolean var3) {
            RealmsScreen.bind("realms:textures/gui/realms/reject_icon.png");
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            RealmsScreen.blit(var1, var2, var3?19.0F:0.0F, 0.0F, 18, 18, 37, 18);
            GlStateManager.popMatrix();
            if(var3) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.invites.button.reject");
            }

         }

         public void onClick(int i) {
            RealmsPendingInvitesScreen.this.reject(i);
         }
      }
   }
}
