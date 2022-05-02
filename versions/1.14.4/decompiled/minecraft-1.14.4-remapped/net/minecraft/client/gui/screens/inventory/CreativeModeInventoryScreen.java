package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@ClientJarOnly
public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static int selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   private List originalSlots;
   private Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private boolean hasClickedOutside;
   private final Map visibleTags = Maps.newTreeMap();

   public CreativeModeInventoryScreen(Player player) {
      super(new CreativeModeInventoryScreen.ItemPickerMenu(player), player.inventory, new TextComponent(""));
      player.containerMenu = this.menu;
      this.passEvents = true;
      this.imageHeight = 136;
      this.imageWidth = 195;
   }

   public void tick() {
      if(!this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      } else if(this.searchBox != null) {
         this.searchBox.tick();
      }

   }

   protected void slotClicked(@Nullable Slot slot, int var2, int var3, ClickType clickType) {
      if(this.isCreativeSlot(slot)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean var5 = clickType == ClickType.QUICK_MOVE;
      clickType = var2 == -999 && clickType == ClickType.PICKUP?ClickType.THROW:clickType;
      if(slot == null && selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && clickType != ClickType.QUICK_CRAFT) {
         Inventory var6 = this.minecraft.player.inventory;
         if(!var6.getCarried().isEmpty() && this.hasClickedOutside) {
            if(var3 == 0) {
               this.minecraft.player.drop(var6.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var6.getCarried());
               var6.setCarried(ItemStack.EMPTY);
            }

            if(var3 == 1) {
               ItemStack var7 = var6.getCarried().split(1);
               this.minecraft.player.drop(var7, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var7);
            }
         }
      } else {
         if(slot != null && !slot.mayPickup(this.minecraft.player)) {
            return;
         }

         if(slot == this.destroyItemSlot && var5) {
            for(int var6 = 0; var6 < this.minecraft.player.inventoryMenu.getItems().size(); ++var6) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, var6);
            }
         } else if(selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
            if(slot == this.destroyItemSlot) {
               this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
            } else if(clickType == ClickType.THROW && slot != null && slot.hasItem()) {
               ItemStack var6 = slot.remove(var3 == 0?1:slot.getItem().getMaxStackSize());
               ItemStack var7 = slot.getItem();
               this.minecraft.player.drop(var6, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var6);
               this.minecraft.gameMode.handleCreativeModeItemAdd(var7, ((CreativeModeInventoryScreen.SlotWrapper)slot).target.index);
            } else if(clickType == ClickType.THROW && !this.minecraft.player.inventory.getCarried().isEmpty()) {
               this.minecraft.player.drop(this.minecraft.player.inventory.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.minecraft.player.inventory.getCarried());
               this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
            } else {
               this.minecraft.player.inventoryMenu.clicked(slot == null?var2:((CreativeModeInventoryScreen.SlotWrapper)slot).target.index, var3, clickType, this.minecraft.player);
               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         } else if(clickType != ClickType.QUICK_CRAFT && slot.container == CONTAINER) {
            Inventory var6 = this.minecraft.player.inventory;
            ItemStack var7 = var6.getCarried();
            ItemStack var8 = slot.getItem();
            if(clickType == ClickType.SWAP) {
               if(!var8.isEmpty() && var3 >= 0 && var3 < 9) {
                  ItemStack var9 = var8.copy();
                  var9.setCount(var9.getMaxStackSize());
                  this.minecraft.player.inventory.setItem(var3, var9);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }

               return;
            }

            if(clickType == ClickType.CLONE) {
               if(var6.getCarried().isEmpty() && slot.hasItem()) {
                  ItemStack var9 = slot.getItem().copy();
                  var9.setCount(var9.getMaxStackSize());
                  var6.setCarried(var9);
               }

               return;
            }

            if(clickType == ClickType.THROW) {
               if(!var8.isEmpty()) {
                  ItemStack var9 = var8.copy();
                  var9.setCount(var3 == 0?1:var9.getMaxStackSize());
                  this.minecraft.player.drop(var9, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var9);
               }

               return;
            }

            if(!var7.isEmpty() && !var8.isEmpty() && var7.sameItem(var8) && ItemStack.tagMatches(var7, var8)) {
               if(var3 == 0) {
                  if(var5) {
                     var7.setCount(var7.getMaxStackSize());
                  } else if(var7.getCount() < var7.getMaxStackSize()) {
                     var7.grow(1);
                  }
               } else {
                  var7.shrink(1);
               }
            } else if(!var8.isEmpty() && var7.isEmpty()) {
               var6.setCarried(var8.copy());
               var7 = var6.getCarried();
               if(var5) {
                  var7.setCount(var7.getMaxStackSize());
               }
            } else if(var3 == 0) {
               var6.setCarried(ItemStack.EMPTY);
            } else {
               var6.getCarried().shrink(1);
            }
         } else if(this.menu != null) {
            ItemStack var6 = slot == null?ItemStack.EMPTY:((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).clicked(slot == null?var2:slot.index, var3, clickType, this.minecraft.player);
            if(AbstractContainerMenu.getQuickcraftHeader(var3) == 2) {
               for(int var7 = 0; var7 < 9; ++var7) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(45 + var7).getItem(), 36 + var7);
               }
            } else if(slot != null) {
               ItemStack var7 = ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(slot.index).getItem();
               this.minecraft.gameMode.handleCreativeModeItemAdd(var7, slot.index - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + 36);
               int var8 = 45 + var3;
               if(clickType == ClickType.SWAP) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(var6, var8 - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + 36);
               } else if(clickType == ClickType.THROW && !var6.isEmpty()) {
                  ItemStack var9 = var6.copy();
                  var9.setCount(var3 == 0?1:var9.getMaxStackSize());
                  this.minecraft.player.drop(var9, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var9);
               }

               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         }
      }

   }

   private boolean isCreativeSlot(@Nullable Slot slot) {
      return slot != null && slot.container == CONTAINER;
   }

   protected void checkEffectRendering() {
      int var1 = this.leftPos;
      super.checkEffectRendering();
      if(this.searchBox != null && this.leftPos != var1) {
         this.searchBox.setX(this.leftPos + 82);
      }

   }

   protected void init() {
      if(this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         Font var10003 = this.font;
         int var10004 = this.leftPos + 82;
         int var10005 = this.topPos + 6;
         this.font.getClass();
         this.searchBox = new EditBox(var10003, var10004, var10005, 80, 9, I18n.get("itemGroup.search", new Object[0]));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.children.add(this.searchBox);
         int var1 = selectedTab;
         selectedTab = -1;
         this.selectTab(CreativeModeTab.TABS[var1]);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      String var4 = this.searchBox.getValue();
      this.init(minecraft, var2, var3);
      this.searchBox.setValue(var4);
      if(!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

   }

   public void removed() {
      super.removed();
      if(this.minecraft.player != null && this.minecraft.player.inventory != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean charTyped(char var1, int var2) {
      if(this.ignoreTextInput) {
         return false;
      } else if(selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
         return false;
      } else {
         String var3 = this.searchBox.getValue();
         if(this.searchBox.charTyped(var1, var2)) {
            if(!Objects.equals(var3, this.searchBox.getValue())) {
               this.refreshSearchResults();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      if(selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
         if(this.minecraft.options.keyChat.matches(var1, var2)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTab.TAB_SEARCH);
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         boolean var4 = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot != null && this.hoveredSlot.hasItem();
         if(var4 && this.checkNumkeyPressed(var1, var2)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String var5 = this.searchBox.getValue();
            if(this.searchBox.keyPressed(var1, var2, var3)) {
               if(!Objects.equals(var5, this.searchBox.getValue())) {
                  this.refreshSearchResults();
               }

               return true;
            } else {
               return this.searchBox.isFocused() && this.searchBox.isVisible() && var1 != 256?true:super.keyPressed(var1, var2, var3);
            }
         }
      }
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      return super.keyReleased(var1, var2, var3);
   }

   private void refreshSearchResults() {
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.clear();
      this.visibleTags.clear();
      String var1 = this.searchBox.getValue();
      if(var1.isEmpty()) {
         for(Item var3 : Registry.ITEM) {
            var3.fillItemCategory(CreativeModeTab.TAB_SEARCH, ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items);
         }
      } else {
         SearchTree<ItemStack> var2;
         if(var1.startsWith("#")) {
            var1 = var1.substring(1);
            var2 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
            this.updateVisibleTags(var1);
         } else {
            var2 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
         }

         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(var2.search(var1.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   private void updateVisibleTags(String string) {
      int var2 = string.indexOf(58);
      Predicate<ResourceLocation> var3;
      if(var2 == -1) {
         var3 = (resourceLocation) -> {
            return resourceLocation.getPath().contains(string);
         };
      } else {
         String var4 = string.substring(0, var2).trim();
         String var5 = string.substring(var2 + 1).trim();
         var3 = (resourceLocation) -> {
            return resourceLocation.getNamespace().contains(var4) && resourceLocation.getPath().contains(var5);
         };
      }

      TagCollection<Item> var4 = ItemTags.getAllTags();
      var4.getAvailableTags().stream().filter(var3).forEach((resourceLocation) -> {
         Tag var10000 = (Tag)this.visibleTags.put(resourceLocation, var4.getTag(resourceLocation));
      });
   }

   protected void renderLabels(int var1, int var2) {
      CreativeModeTab var3 = CreativeModeTab.TABS[selectedTab];
      if(var3.showTitle()) {
         GlStateManager.disableBlend();
         this.font.draw(I18n.get(var3.getName(), new Object[0]), 8.0F, 6.0F, 4210752);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;

         for(CreativeModeTab var13 : CreativeModeTab.TABS) {
            if(this.checkTabClicked(var13, var6, var8)) {
               return true;
            }
         }

         if(selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && this.insideScrollbar(var1, var3)) {
            this.scrolling = this.canScroll();
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if(var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;
         this.scrolling = false;

         for(CreativeModeTab var13 : CreativeModeTab.TABS) {
            if(this.checkTabClicked(var13, var6, var8)) {
               this.selectTab(var13);
               return true;
            }
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private boolean canScroll() {
      return selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[selectedTab].canScroll() && ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).canScroll();
   }

   private void selectTab(CreativeModeTab creativeModeTab) {
      int var2 = selectedTab;
      selectedTab = creativeModeTab.getId();
      this.quickCraftSlots.clear();
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.clear();
      if(creativeModeTab == CreativeModeTab.TAB_HOTBAR) {
         HotbarManager var3 = this.minecraft.getHotbarManager();

         for(int var4 = 0; var4 < 9; ++var4) {
            Hotbar var5 = var3.get(var4);
            if(var5.isEmpty()) {
               for(int var6 = 0; var6 < 9; ++var6) {
                  if(var6 == var4) {
                     ItemStack var7 = new ItemStack(Items.PAPER);
                     var7.getOrCreateTagElement("CustomCreativeLock");
                     String var8 = this.minecraft.options.keyHotbarSlots[var4].getTranslatedKeyMessage();
                     String var9 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     var7.setHoverName(new TranslatableComponent("inventory.hotbarInfo", new Object[]{var9, var8}));
                     ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(var7);
                  } else {
                     ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(var5);
            }
         }
      } else if(creativeModeTab != CreativeModeTab.TAB_SEARCH) {
         creativeModeTab.fillItemList(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items);
      }

      if(creativeModeTab == CreativeModeTab.TAB_INVENTORY) {
         AbstractContainerMenu var3 = this.minecraft.player.inventoryMenu;
         if(this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots);
         }

         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();

         for(int var4 = 0; var4 < var3.slots.size(); ++var4) {
            Slot var5 = new CreativeModeInventoryScreen.SlotWrapper((Slot)var3.slots.get(var4), var4);
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(var5);
            if(var4 >= 5 && var4 < 9) {
               int var6 = var4 - 5;
               int var7 = var6 / 2;
               int var8 = var6 % 2;
               var5.x = 54 + var7 * 54;
               var5.y = 6 + var8 * 27;
            } else if(var4 >= 0 && var4 < 5) {
               var5.x = -2000;
               var5.y = -2000;
            } else if(var4 == 45) {
               var5.x = 35;
               var5.y = 20;
            } else if(var4 < var3.slots.size()) {
               int var6 = var4 - 9;
               int var7 = var6 % 9;
               int var8 = var6 / 9;
               var5.x = 9 + var7 * 18;
               if(var4 >= 36) {
                  var5.y = 112;
               } else {
                  var5.y = 54 + var8 * 18;
               }
            }
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
      } else if(var2 == CreativeModeTab.TAB_INVENTORY.getId()) {
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if(this.searchBox != null) {
         if(creativeModeTab == CreativeModeTab.TAB_SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocus(true);
            if(var2 != creativeModeTab.getId()) {
               this.searchBox.setValue("");
            }

            this.refreshSearchResults();
         } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocus(false);
            this.searchBox.setValue("");
         }
      }

      this.scrollOffs = 0.0F;
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if(!this.canScroll()) {
         return false;
      } else {
         int var7 = (((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.size() + 9 - 1) / 9 - 5;
         this.scrollOffs = (float)((double)this.scrollOffs - var5 / (double)var7);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      this.hasClickedOutside = var8 && !this.checkTabClicked(CreativeModeTab.TABS[selectedTab], var1, var3);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(double var1, double var3) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      int var7 = var5 + 175;
      int var8 = var6 + 18;
      int var9 = var7 + 14;
      int var10 = var8 + 112;
      return var1 >= (double)var7 && var3 >= (double)var8 && var1 < (double)var9 && var3 < (double)var10;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if(this.scrolling) {
         int var10 = this.topPos + 18;
         int var11 = var10 + 112;
         this.scrollOffs = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);

      for(CreativeModeTab var7 : CreativeModeTab.TABS) {
         if(this.checkTabHovering(var7, var1, var2)) {
            break;
         }
      }

      if(this.destroyItemSlot != null && selectedTab == CreativeModeTab.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)var1, (double)var2)) {
         this.renderTooltip(I18n.get("inventory.binSlot", new Object[0]), var1, var2);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      this.renderTooltip(var1, var2);
   }

   protected void renderTooltip(ItemStack itemStack, int var2, int var3) {
      if(selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
         List<Component> var4 = itemStack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips?TooltipFlag.Default.ADVANCED:TooltipFlag.Default.NORMAL);
         List<String> var5 = Lists.newArrayListWithCapacity(var4.size());

         for(Component var7 : var4) {
            var5.add(var7.getColoredString());
         }

         Item var6 = itemStack.getItem();
         CreativeModeTab var7 = var6.getItemCategory();
         if(var7 == null && var6 == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> var8 = EnchantmentHelper.getEnchantments(itemStack);
            if(var8.size() == 1) {
               Enchantment var9 = (Enchantment)var8.keySet().iterator().next();

               for(CreativeModeTab var13 : CreativeModeTab.TABS) {
                  if(var13.hasEnchantmentCategory(var9.category)) {
                     var7 = var13;
                     break;
                  }
               }
            }
         }

         this.visibleTags.forEach((resourceLocation, tag) -> {
            if(tag.contains(var14)) {
               var5.add(1, "" + ChatFormatting.BOLD + ChatFormatting.DARK_PURPLE + "#" + resourceLocation);
            }

         });
         if(var7 != null) {
            var5.add(1, "" + ChatFormatting.BOLD + ChatFormatting.BLUE + I18n.get(var7.getName(), new Object[0]));
         }

         for(int var8 = 0; var8 < ((List)var5).size(); ++var8) {
            if(var8 == 0) {
               var5.set(var8, itemStack.getRarity().color + (String)var5.get(var8));
            } else {
               var5.set(var8, ChatFormatting.GRAY + (String)var5.get(var8));
            }
         }

         this.renderTooltip(var5, var2, var3);
      } else {
         super.renderTooltip(itemStack, var2, var3);
      }

   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Lighting.turnOnGui();
      CreativeModeTab var4 = CreativeModeTab.TABS[selectedTab];

      for(CreativeModeTab var8 : CreativeModeTab.TABS) {
         this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
         if(var8.getId() != selectedTab) {
            this.renderTabButton(var8);
         }
      }

      this.minecraft.getTextureManager().bind(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var4.getBackgroundSuffix()));
      this.blit(this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(var2, var3, var1);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var5 = this.leftPos + 175;
      int var6 = this.topPos + 18;
      int var7 = var6 + 112;
      this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
      if(var4.canScroll()) {
         this.blit(var5, var6 + (int)((float)(var7 - var6 - 17) * this.scrollOffs), 232 + (this.canScroll()?0:12), 0, 12, 15);
      }

      this.renderTabButton(var4);
      if(var4 == CreativeModeTab.TAB_INVENTORY) {
         InventoryScreen.renderPlayerModel(this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - var2), (float)(this.topPos + 45 - 30 - var3), this.minecraft.player);
      }

   }

   protected boolean checkTabClicked(CreativeModeTab creativeModeTab, double var2, double var4) {
      int var6 = creativeModeTab.getColumn();
      int var7 = 28 * var6;
      int var8 = 0;
      if(creativeModeTab.isAlignedRight()) {
         var7 = this.imageWidth - 28 * (6 - var6) + 2;
      } else if(var6 > 0) {
         var7 += var6;
      }

      if(creativeModeTab.isTopRow()) {
         var8 = var8 - 32;
      } else {
         var8 = var8 + this.imageHeight;
      }

      return var2 >= (double)var7 && var2 <= (double)(var7 + 28) && var4 >= (double)var8 && var4 <= (double)(var8 + 32);
   }

   protected boolean checkTabHovering(CreativeModeTab creativeModeTab, int var2, int var3) {
      int var4 = creativeModeTab.getColumn();
      int var5 = 28 * var4;
      int var6 = 0;
      if(creativeModeTab.isAlignedRight()) {
         var5 = this.imageWidth - 28 * (6 - var4) + 2;
      } else if(var4 > 0) {
         var5 += var4;
      }

      if(creativeModeTab.isTopRow()) {
         var6 = var6 - 32;
      } else {
         var6 = var6 + this.imageHeight;
      }

      if(this.isHovering(var5 + 3, var6 + 3, 23, 27, (double)var2, (double)var3)) {
         this.renderTooltip(I18n.get(creativeModeTab.getName(), new Object[0]), var2, var3);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(CreativeModeTab creativeModeTab) {
      boolean var2 = creativeModeTab.getId() == selectedTab;
      boolean var3 = creativeModeTab.isTopRow();
      int var4 = creativeModeTab.getColumn();
      int var5 = var4 * 28;
      int var6 = 0;
      int var7 = this.leftPos + 28 * var4;
      int var8 = this.topPos;
      int var9 = 32;
      if(var2) {
         var6 += 32;
      }

      if(creativeModeTab.isAlignedRight()) {
         var7 = this.leftPos + this.imageWidth - 28 * (6 - var4);
      } else if(var4 > 0) {
         var7 += var4;
      }

      if(var3) {
         var8 = var8 - 28;
      } else {
         var6 += 64;
         var8 = var8 + (this.imageHeight - 4);
      }

      GlStateManager.disableLighting();
      this.blit(var7, var8, var5, var6, 28, 32);
      this.blitOffset = 100;
      this.itemRenderer.blitOffset = 100.0F;
      var7 = var7 + 6;
      var8 = var8 + 8 + (var3?1:-1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack var10 = creativeModeTab.getIconItem();
      this.itemRenderer.renderAndDecorateItem(var10, var7, var8);
      this.itemRenderer.renderGuiItemDecorations(this.font, var10, var7, var8);
      GlStateManager.disableLighting();
      this.itemRenderer.blitOffset = 0.0F;
      this.blitOffset = 0;
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   public static void handleHotbarLoadOrSave(Minecraft minecraft, int var1, boolean var2, boolean var3) {
      LocalPlayer var4 = minecraft.player;
      HotbarManager var5 = minecraft.getHotbarManager();
      Hotbar var6 = var5.get(var1);
      if(var2) {
         for(int var7 = 0; var7 < Inventory.getSelectionSize(); ++var7) {
            ItemStack var8 = ((ItemStack)var6.get(var7)).copy();
            var4.inventory.setItem(var7, var8);
            minecraft.gameMode.handleCreativeModeItemAdd(var8, 36 + var7);
         }

         var4.inventoryMenu.broadcastChanges();
      } else if(var3) {
         for(int var7 = 0; var7 < Inventory.getSelectionSize(); ++var7) {
            var6.set(var7, var4.inventory.getItem(var7).copy());
         }

         String var7 = minecraft.options.keyHotbarSlots[var1].getTranslatedKeyMessage();
         String var8 = minecraft.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         minecraft.gui.setOverlayMessage((Component)(new TranslatableComponent("inventory.hotbarSaved", new Object[]{var8, var7})), false);
         var5.save();
      }

   }

   @ClientJarOnly
   static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container container, int var2, int var3, int var4) {
         super(container, var2, var3, var4);
      }

      public boolean mayPickup(Player player) {
         return super.mayPickup(player) && this.hasItem()?this.getItem().getTagElement("CustomCreativeLock") == null:!this.hasItem();
      }
   }

   @ClientJarOnly
   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList items = NonNullList.create();

      public ItemPickerMenu(Player player) {
         super((MenuType)null, 0);
         Inventory var2 = player.inventory;

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.addSlot(new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
            }
         }

         for(int var3 = 0; var3 < 9; ++var3) {
            this.addSlot(new Slot(var2, var3, 9 + var3 * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean stillValid(Player player) {
         return true;
      }

      public void scrollTo(float f) {
         int var2 = (this.items.size() + 9 - 1) / 9 - 5;
         int var3 = (int)((double)(f * (float)var2) + 0.5D);
         if(var3 < 0) {
            var3 = 0;
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 9; ++var5) {
               int var6 = var5 + (var4 + var3) * 9;
               if(var6 >= 0 && var6 < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, (ItemStack)this.items.get(var6));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(Player player, int var2) {
         if(var2 >= this.slots.size() - 9 && var2 < this.slots.size()) {
            Slot var3 = (Slot)this.slots.get(var2);
            if(var3 != null && var3.hasItem()) {
               var3.set(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
         return slot.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public boolean canDragTo(Slot slot) {
         return slot.container != CreativeModeInventoryScreen.CONTAINER;
      }
   }

   @ClientJarOnly
   class SlotWrapper extends Slot {
      private final Slot target;

      public SlotWrapper(Slot target, int var3) {
         super(target.container, var3, 0, 0);
         this.target = target;
      }

      public ItemStack onTake(Player player, ItemStack var2) {
         this.target.onTake(player, var2);
         return var2;
      }

      public boolean mayPlace(ItemStack itemStack) {
         return this.target.mayPlace(itemStack);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void set(ItemStack itemStack) {
         this.target.set(itemStack);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack itemStack) {
         return this.target.getMaxStackSize(itemStack);
      }

      @Nullable
      public String getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public ItemStack remove(int i) {
         return this.target.remove(i);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(Player player) {
         return this.target.mayPickup(player);
      }
   }
}
