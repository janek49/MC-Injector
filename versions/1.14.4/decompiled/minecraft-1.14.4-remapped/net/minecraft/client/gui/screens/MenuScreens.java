package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class MenuScreens {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map SCREENS = Maps.newHashMap();

   public static void create(@Nullable MenuType menuType, Minecraft minecraft, int var2, Component component) {
      if(menuType == null) {
         LOGGER.warn("Trying to open invalid screen with name: {}", component.getString());
      } else {
         MenuScreens.ScreenConstructor<T, ?> var4 = getConstructor(menuType);
         if(var4 == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", Registry.MENU.getKey(menuType));
         } else {
            var4.fromPacket(component, menuType, minecraft, var2);
         }
      }
   }

   @Nullable
   private static MenuScreens.ScreenConstructor getConstructor(MenuType menuType) {
      return (MenuScreens.ScreenConstructor)SCREENS.get(menuType);
   }

   private static void register(MenuType menuType, MenuScreens.ScreenConstructor menuScreens$ScreenConstructor) {
      MenuScreens.ScreenConstructor<?, ?> menuScreens$ScreenConstructor = (MenuScreens.ScreenConstructor)SCREENS.put(menuType, menuScreens$ScreenConstructor);
      if(menuScreens$ScreenConstructor != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(menuType));
      }
   }

   public static boolean selfTest() {
      boolean var0 = false;

      for(MenuType<?> var2 : Registry.MENU) {
         if(!SCREENS.containsKey(var2)) {
            LOGGER.debug("Menu {} has no matching screen", Registry.MENU.getKey(var2));
            var0 = true;
         }
      }

      return var0;
   }

   static {
      register(MenuType.GENERIC_9x1, ContainerScreen::<init>);
      register(MenuType.GENERIC_9x2, ContainerScreen::<init>);
      register(MenuType.GENERIC_9x3, ContainerScreen::<init>);
      register(MenuType.GENERIC_9x4, ContainerScreen::<init>);
      register(MenuType.GENERIC_9x5, ContainerScreen::<init>);
      register(MenuType.GENERIC_9x6, ContainerScreen::<init>);
      register(MenuType.GENERIC_3x3, DispenserScreen::<init>);
      register(MenuType.ANVIL, AnvilScreen::<init>);
      register(MenuType.BEACON, BeaconScreen::<init>);
      register(MenuType.BLAST_FURNACE, BlastFurnaceScreen::<init>);
      register(MenuType.BREWING_STAND, BrewingStandScreen::<init>);
      register(MenuType.CRAFTING, CraftingScreen::<init>);
      register(MenuType.ENCHANTMENT, EnchantmentScreen::<init>);
      register(MenuType.FURNACE, FurnaceScreen::<init>);
      register(MenuType.GRINDSTONE, GrindstoneScreen::<init>);
      register(MenuType.HOPPER, HopperScreen::<init>);
      register(MenuType.LECTERN, LecternScreen::<init>);
      register(MenuType.LOOM, LoomScreen::<init>);
      register(MenuType.MERCHANT, MerchantScreen::<init>);
      register(MenuType.SHULKER_BOX, ShulkerBoxScreen::<init>);
      register(MenuType.SMOKER, SmokerScreen::<init>);
      register(MenuType.CARTOGRAPHY, CartographyScreen::<init>);
      register(MenuType.STONECUTTER, StonecutterScreen::<init>);
   }

   @ClientJarOnly
   interface ScreenConstructor {
      default void fromPacket(Component component, MenuType menuType, Minecraft minecraft, int var4) {
         U var5 = this.create(menuType.create(var4, minecraft.player.inventory), minecraft.player.inventory, component);
         minecraft.player.containerMenu = ((MenuAccess)var5).getMenu();
         minecraft.setScreen(var5);
      }

      Screen create(AbstractContainerMenu var1, Inventory var2, Component var3);
   }
}
