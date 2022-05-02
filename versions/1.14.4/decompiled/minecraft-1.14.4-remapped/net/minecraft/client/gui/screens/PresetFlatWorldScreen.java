package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

@ClientJarOnly
public class PresetFlatWorldScreen extends Screen {
   private static final List PRESETS = Lists.newArrayList();
   private final CreateFlatWorldScreen parent;
   private String shareText;
   private String listText;
   private PresetFlatWorldScreen.PresetsList list;
   private Button selectButton;
   private EditBox export;

   public PresetFlatWorldScreen(CreateFlatWorldScreen parent) {
      super(new TranslatableComponent("createWorld.customize.presets.title", new Object[0]));
      this.parent = parent;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.shareText = I18n.get("createWorld.customize.presets.share", new Object[0]);
      this.listText = I18n.get("createWorld.customize.presets.list", new Object[0]);
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      this.export.setValue(this.parent.saveLayerString());
      this.children.add(this.export);
      this.list = new PresetFlatWorldScreen.PresetsList();
      this.children.add(this.list);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("createWorld.customize.presets.select", new Object[0]), (button) -> {
         this.parent.loadLayers(this.export.getValue());
         this.minecraft.setScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.updateButtonValidity(this.list.getSelected() != null);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.list.mouseScrolled(var1, var3, var5);
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      String var4 = this.export.getValue();
      this.init(minecraft, var2, var3);
      this.export.setValue(var4);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      this.drawString(this.font, this.shareText, 50, 30, 10526880);
      this.drawString(this.font, this.listText, 50, 70, 10526880);
      this.export.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity(boolean b) {
      this.selectButton.active = b || this.export.getValue().length() > 1;
   }

   private static void preset(String string, ItemLike itemLike, Biome biome, List list, FlatLayerInfo... flatLayerInfos) {
      FlatLevelGeneratorSettings var5 = (FlatLevelGeneratorSettings)ChunkGeneratorType.FLAT.createSettings();

      for(int var6 = flatLayerInfos.length - 1; var6 >= 0; --var6) {
         var5.getLayersInfo().add(flatLayerInfos[var6]);
      }

      var5.setBiome(biome);
      var5.updateLayers();

      for(String var7 : list) {
         var5.getStructuresOptions().put(var7, Maps.newHashMap());
      }

      PRESETS.add(new PresetFlatWorldScreen.PresetInfo(itemLike.asItem(), string, var5.toString()));
   }

   static {
      preset(I18n.get("createWorld.customize.preset.classic_flat", new Object[0]), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList(new String[]{"village"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.tunnelers_dream", new Object[0]), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList(new String[]{"biome_1", "dungeon", "decoration", "stronghold", "mineshaft"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.water_world", new Object[0]), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList(new String[]{"biome_1", "oceanmonument"}), new FlatLayerInfo[]{new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.overworld", new Object[0]), Blocks.GRASS, Biomes.PLAINS, Arrays.asList(new String[]{"village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.snowy_kingdom", new Object[0]), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList(new String[]{"village", "biome_1"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.bottomless_pit", new Object[0]), Items.FEATHER, Biomes.PLAINS, Arrays.asList(new String[]{"village", "biome_1"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE)});
      preset(I18n.get("createWorld.customize.preset.desert", new Object[0]), Blocks.SAND, Biomes.DESERT, Arrays.asList(new String[]{"village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"}), new FlatLayerInfo[]{new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.redstone_ready", new Object[0]), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo[]{new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK)});
      preset(I18n.get("createWorld.customize.preset.the_void", new Object[0]), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList(new String[]{"decoration"}), new FlatLayerInfo[]{new FlatLayerInfo(1, Blocks.AIR)});
   }

   @ClientJarOnly
   static class PresetInfo {
      public final Item icon;
      public final String name;
      public final String value;

      public PresetInfo(Item icon, String name, String value) {
         this.icon = icon;
         this.name = name;
         this.value = value;
      }
   }

   @ClientJarOnly
   class PresetsList extends ObjectSelectionList {
      public PresetsList() {
         super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);

         for(int var2 = 0; var2 < PresetFlatWorldScreen.PRESETS.size(); ++var2) {
            this.addEntry(new PresetFlatWorldScreen.PresetsList.Entry());
         }

      }

      public void setSelected(@Nullable PresetFlatWorldScreen.PresetsList.Entry selected) {
         super.setSelected(selected);
         if(selected != null) {
            NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(this.children().indexOf(selected))).name})).getString());
         }

      }

      protected void moveSelection(int i) {
         super.moveSelection(i);
         PresetFlatWorldScreen.this.updateButtonValidity(true);
      }

      protected boolean isFocused() {
         return PresetFlatWorldScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if(super.keyPressed(var1, var2, var3)) {
            return true;
         } else {
            if((var1 == 257 || var1 == 335) && this.getSelected() != null) {
               ((PresetFlatWorldScreen.PresetsList.Entry)this.getSelected()).select();
            }

            return false;
         }
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
         this.setSelected((PresetFlatWorldScreen.PresetsList.Entry)var1);
      }

      @ClientJarOnly
      public class Entry extends ObjectSelectionList.Entry {
         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            PresetFlatWorldScreen.PresetInfo var10 = (PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(var1);
            this.blitSlot(var3, var2, var10.icon);
            PresetFlatWorldScreen.this.font.draw(var10.name, (float)(var3 + 18 + 5), (float)(var2 + 6), 16777215);
         }

         public boolean mouseClicked(double var1, double var3, int var5) {
            if(var5 == 0) {
               this.select();
            }

            return false;
         }

         private void select() {
            PresetsList.this.setSelected(this);
            PresetFlatWorldScreen.this.updateButtonValidity(true);
            PresetFlatWorldScreen.this.export.setValue(((PresetFlatWorldScreen.PresetInfo)PresetFlatWorldScreen.PRESETS.get(PresetsList.this.children().indexOf(this))).value);
            PresetFlatWorldScreen.this.export.moveCursorToStart();
         }

         private void blitSlot(int var1, int var2, Item item) {
            this.blitSlotBg(var1 + 1, var2 + 1);
            GlStateManager.enableRescaleNormal();
            Lighting.turnOnGui();
            PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(item), var1 + 2, var2 + 2);
            Lighting.turnOff();
            GlStateManager.disableRescaleNormal();
         }

         private void blitSlotBg(int var1, int var2) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            PresetsList.this.minecraft.getTextureManager().bind(GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(var1, var2, PresetFlatWorldScreen.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}
