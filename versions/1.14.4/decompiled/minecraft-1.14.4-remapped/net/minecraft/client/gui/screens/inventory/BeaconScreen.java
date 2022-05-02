package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

@ClientJarOnly
public class BeaconScreen extends AbstractContainerScreen {
   private static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private BeaconScreen.BeaconConfirmButton confirmButton;
   private boolean initPowerButtons;
   private MobEffect primary;
   private MobEffect secondary;

   public BeaconScreen(final BeaconMenu beaconMenu, Inventory inventory, Component component) {
      super(beaconMenu, inventory, component);
      this.imageWidth = 230;
      this.imageHeight = 219;
      beaconMenu.addSlotListener(new ContainerListener() {
         public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList nonNullList) {
         }

         public void slotChanged(AbstractContainerMenu abstractContainerMenu, int var2, ItemStack itemStack) {
         }

         public void setContainerData(AbstractContainerMenu abstractContainerMenu, int var2, int var3) {
            BeaconScreen.this.primary = beaconMenu.getPrimaryEffect();
            BeaconScreen.this.secondary = beaconMenu.getSecondaryEffect();
            BeaconScreen.this.initPowerButtons = true;
         }
      });
   }

   protected void init() {
      super.init();
      this.confirmButton = (BeaconScreen.BeaconConfirmButton)this.addButton(new BeaconScreen.BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addButton(new BeaconScreen.BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
      this.initPowerButtons = true;
      this.confirmButton.active = false;
   }

   public void tick() {
      super.tick();
      int var1 = ((BeaconMenu)this.menu).getLevels();
      if(this.initPowerButtons && var1 >= 0) {
         this.initPowerButtons = false;

         for(int var2 = 0; var2 <= 2; ++var2) {
            int var3 = BeaconBlockEntity.BEACON_EFFECTS[var2].length;
            int var4 = var3 * 22 + (var3 - 1) * 2;

            for(int var5 = 0; var5 < var3; ++var5) {
               MobEffect var6 = BeaconBlockEntity.BEACON_EFFECTS[var2][var5];
               BeaconScreen.BeaconPowerButton var7 = new BeaconScreen.BeaconPowerButton(this.leftPos + 76 + var5 * 24 - var4 / 2, this.topPos + 22 + var2 * 25, var6, true);
               this.addButton(var7);
               if(var2 >= var1) {
                  var7.active = false;
               } else if(var6 == this.primary) {
                  var7.setSelected(true);
               }
            }
         }

         int var2 = 3;
         int var3 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
         int var4 = var3 * 22 + (var3 - 1) * 2;

         for(int var5 = 0; var5 < var3 - 1; ++var5) {
            MobEffect var6 = BeaconBlockEntity.BEACON_EFFECTS[3][var5];
            BeaconScreen.BeaconPowerButton var7 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + var5 * 24 - var4 / 2, this.topPos + 47, var6, false);
            this.addButton(var7);
            if(3 >= var1) {
               var7.active = false;
            } else if(var6 == this.secondary) {
               var7.setSelected(true);
            }
         }

         if(this.primary != null) {
            BeaconScreen.BeaconPowerButton var5 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + (var3 - 1) * 24 - var4 / 2, this.topPos + 47, this.primary, false);
            this.addButton(var5);
            if(3 >= var1) {
               var5.active = false;
            } else if(this.primary == this.secondary) {
               var5.setSelected(true);
            }
         }
      }

      this.confirmButton.active = ((BeaconMenu)this.menu).hasPayment() && this.primary != null;
   }

   protected void renderLabels(int var1, int var2) {
      Lighting.turnOff();
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.primary", new Object[0]), 62, 10, 14737632);
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.secondary", new Object[0]), 169, 10, 14737632);

      for(AbstractWidget var4 : this.buttons) {
         if(var4.isHovered()) {
            var4.renderToolTip(var1 - this.leftPos, var2 - this.topPos);
            break;
         }
      }

      Lighting.turnOnGui();
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BEACON_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      this.itemRenderer.blitOffset = 100.0F;
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), var4 + 42, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), var4 + 42 + 22, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), var4 + 42 + 44, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), var4 + 42 + 66, var5 + 109);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   @ClientJarOnly
   class BeaconCancelButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconCancelButton(int var2, int var3) {
         super(var2, var3, 112, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.connection.send((Packet)(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId)));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int var1, int var2) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.cancel", new Object[0]), var1, var2);
      }
   }

   @ClientJarOnly
   class BeaconConfirmButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconConfirmButton(int var2, int var3) {
         super(var2, var3, 90, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().send((Packet)(new ServerboundSetBeaconPacket(MobEffect.getId(BeaconScreen.this.primary), MobEffect.getId(BeaconScreen.this.secondary))));
         BeaconScreen.this.minecraft.player.connection.send((Packet)(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId)));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int var1, int var2) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.done", new Object[0]), var1, var2);
      }
   }

   @ClientJarOnly
   class BeaconPowerButton extends BeaconScreen.BeaconScreenButton {
      private final MobEffect effect;
      private final TextureAtlasSprite sprite;
      private final boolean isPrimary;

      public BeaconPowerButton(int var2, int var3, MobEffect effect, boolean isPrimary) {
         super(var2, var3);
         this.effect = effect;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
         this.isPrimary = isPrimary;
      }

      public void onPress() {
         if(!this.isSelected()) {
            if(this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
         }
      }

      public void renderToolTip(int var1, int var2) {
         String var3 = I18n.get(this.effect.getDescriptionId(), new Object[0]);
         if(!this.isPrimary && this.effect != MobEffects.REGENERATION) {
            var3 = var3 + " II";
         }

         BeaconScreen.this.renderTooltip(var3, var1, var2);
      }

      protected void renderIcon() {
         Minecraft.getInstance().getTextureManager().bind(TextureAtlas.LOCATION_MOB_EFFECTS);
         blit(this.x + 2, this.y + 2, this.blitOffset, 18, 18, this.sprite);
      }
   }

   @ClientJarOnly
   abstract static class BeaconScreenButton extends AbstractButton {
      private boolean selected;

      protected BeaconScreenButton(int var1, int var2) {
         super(var1, var2, 22, 22, "");
      }

      public void renderButton(int var1, int var2, float var3) {
         Minecraft.getInstance().getTextureManager().bind(BeaconScreen.BEACON_LOCATION);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var4 = 219;
         int var5 = 0;
         if(!this.active) {
            var5 += this.width * 2;
         } else if(this.selected) {
            var5 += this.width * 1;
         } else if(this.isHovered()) {
            var5 += this.width * 3;
         }

         this.blit(this.x, this.y, var5, 219, this.width, this.height);
         this.renderIcon();
      }

      protected abstract void renderIcon();

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }
   }

   @ClientJarOnly
   abstract static class BeaconSpriteScreenButton extends BeaconScreen.BeaconScreenButton {
      private final int iconX;
      private final int iconY;

      protected BeaconSpriteScreenButton(int var1, int var2, int iconX, int iconY) {
         super(var1, var2);
         this.iconX = iconX;
         this.iconY = iconY;
      }

      protected void renderIcon() {
         this.blit(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
      }
   }
}
