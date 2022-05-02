package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;

@ClientJarOnly
public class MinecartCommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final BaseCommandBlock commandBlock;

   public MinecartCommandBlockEditScreen(BaseCommandBlock commandBlock) {
      this.commandBlock = commandBlock;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   int getPreviousY() {
      return 150;
   }

   protected void init() {
      super.init();
      this.trackOutput = this.getCommandBlock().isTrackOutput();
      this.updateCommandOutput();
      this.commandEdit.setValue(this.getCommandBlock().getCommand());
   }

   protected void populateAndSendPacket(BaseCommandBlock baseCommandBlock) {
      if(baseCommandBlock instanceof MinecartCommandBlock.MinecartCommandBase) {
         MinecartCommandBlock.MinecartCommandBase var2 = (MinecartCommandBlock.MinecartCommandBase)baseCommandBlock;
         this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandMinecartPacket(var2.getMinecart().getId(), this.commandEdit.getValue(), baseCommandBlock.isTrackOutput())));
      }

   }
}
