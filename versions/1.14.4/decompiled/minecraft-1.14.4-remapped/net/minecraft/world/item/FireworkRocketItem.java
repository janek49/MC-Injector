package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireworkRocketItem extends Item {
   public FireworkRocketItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      if(!var2.isClientSide) {
         ItemStack var3 = useOnContext.getItemInHand();
         Vec3 var4 = useOnContext.getClickLocation();
         FireworkRocketEntity var5 = new FireworkRocketEntity(var2, var4.x, var4.y, var4.z, var3);
         var2.addFreshEntity(var5);
         var3.shrink(1);
      }

      return InteractionResult.SUCCESS;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      if(player.isFallFlying()) {
         ItemStack var4 = player.getItemInHand(interactionHand);
         if(!level.isClientSide) {
            level.addFreshEntity(new FireworkRocketEntity(level, var4, player));
            if(!player.abilities.instabuild) {
               var4.shrink(1);
            }
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, player.getItemInHand(interactionHand));
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, player.getItemInHand(interactionHand));
      }
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      CompoundTag var5 = itemStack.getTagElement("Fireworks");
      if(var5 != null) {
         if(var5.contains("Flight", 99)) {
            list.add((new TranslatableComponent("item.minecraft.firework_rocket.flight", new Object[0])).append(" ").append(String.valueOf(var5.getByte("Flight"))).withStyle(ChatFormatting.GRAY));
         }

         ListTag var6 = var5.getList("Explosions", 10);
         if(!var6.isEmpty()) {
            for(int var7 = 0; var7 < var6.size(); ++var7) {
               CompoundTag var8 = var6.getCompound(var7);
               List<Component> var9 = Lists.newArrayList();
               FireworkStarItem.appendHoverText(var8, var9);
               if(!var9.isEmpty()) {
                  for(int var10 = 1; var10 < ((List)var9).size(); ++var10) {
                     var9.set(var10, (new TextComponent("  ")).append((Component)var9.get(var10)).withStyle(ChatFormatting.GRAY));
                  }

                  list.addAll(var9);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] BY_ID = (FireworkRocketItem.Shape[])Arrays.stream(values()).sorted(Comparator.comparingInt((fireworkRocketItem$Shape) -> {
         return fireworkRocketItem$Shape.id;
      })).toArray((i) -> {
         return new FireworkRocketItem.Shape[i];
      });
      private final int id;
      private final String name;

      private Shape(int id, String name) {
         this.id = id;
         this.name = name;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public static FireworkRocketItem.Shape byId(int id) {
         return id >= 0 && id < BY_ID.length?BY_ID[id]:SMALL_BALL;
      }
   }
}
