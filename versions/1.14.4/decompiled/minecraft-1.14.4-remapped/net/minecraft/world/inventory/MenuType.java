package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.inventory.StonecutterMenu;

public class MenuType {
   public static final MenuType GENERIC_9x1 = register("generic_9x1", ChestMenu::oneRow);
   public static final MenuType GENERIC_9x2 = register("generic_9x2", ChestMenu::twoRows);
   public static final MenuType GENERIC_9x3 = register("generic_9x3", ChestMenu::threeRows);
   public static final MenuType GENERIC_9x4 = register("generic_9x4", ChestMenu::fourRows);
   public static final MenuType GENERIC_9x5 = register("generic_9x5", ChestMenu::fiveRows);
   public static final MenuType GENERIC_9x6 = register("generic_9x6", ChestMenu::sixRows);
   public static final MenuType GENERIC_3x3 = register("generic_3x3", DispenserMenu::<init>);
   public static final MenuType ANVIL = register("anvil", AnvilMenu::<init>);
   public static final MenuType BEACON = register("beacon", BeaconMenu::<init>);
   public static final MenuType BLAST_FURNACE = register("blast_furnace", BlastFurnaceMenu::<init>);
   public static final MenuType BREWING_STAND = register("brewing_stand", BrewingStandMenu::<init>);
   public static final MenuType CRAFTING = register("crafting", CraftingMenu::<init>);
   public static final MenuType ENCHANTMENT = register("enchantment", EnchantmentMenu::<init>);
   public static final MenuType FURNACE = register("furnace", FurnaceMenu::<init>);
   public static final MenuType GRINDSTONE = register("grindstone", GrindstoneMenu::<init>);
   public static final MenuType HOPPER = register("hopper", HopperMenu::<init>);
   public static final MenuType LECTERN = register("lectern", (var0, inventory) -> {
      return new LecternMenu(var0);
   });
   public static final MenuType LOOM = register("loom", LoomMenu::<init>);
   public static final MenuType MERCHANT = register("merchant", MerchantMenu::<init>);
   public static final MenuType SHULKER_BOX = register("shulker_box", ShulkerBoxMenu::<init>);
   public static final MenuType SMOKER = register("smoker", SmokerMenu::<init>);
   public static final MenuType CARTOGRAPHY = register("cartography", CartographyMenu::<init>);
   public static final MenuType STONECUTTER = register("stonecutter", StonecutterMenu::<init>);
   private final MenuType.MenuSupplier constructor;

   private static MenuType register(String string, MenuType.MenuSupplier menuType$MenuSupplier) {
      return (MenuType)Registry.register(Registry.MENU, (String)string, new MenuType(menuType$MenuSupplier));
   }

   private MenuType(MenuType.MenuSupplier constructor) {
      this.constructor = constructor;
   }

   public AbstractContainerMenu create(int var1, Inventory inventory) {
      return this.constructor.create(var1, inventory);
   }

   interface MenuSupplier {
      AbstractContainerMenu create(int var1, Inventory var2);
   }
}
