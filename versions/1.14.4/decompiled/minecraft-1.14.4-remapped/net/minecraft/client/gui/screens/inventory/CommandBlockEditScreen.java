package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

@ClientJarOnly
public class CommandBlockEditScreen extends AbstractCommandBlockEditScreen {
   private final CommandBlockEntity autoCommandBlock;
   private Button modeButton;
   private Button conditionalButton;
   private Button autoexecButton;
   private CommandBlockEntity.Mode mode = CommandBlockEntity.Mode.REDSTONE;
   private boolean conditional;
   private boolean autoexec;

   public CommandBlockEditScreen(CommandBlockEntity autoCommandBlock) {
      this.autoCommandBlock = autoCommandBlock;
   }

   BaseCommandBlock getCommandBlock() {
      return this.autoCommandBlock.getCommandBlock();
   }

   int getPreviousY() {
      return 135;
   }

   protected void init() {
      super.init();
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.get("advMode.mode.sequence", new Object[0]), (button) -> {
         this.nextMode();
         this.updateMode();
      }));
      this.conditionalButton = (Button)this.addButton(new Button(this.width / 2 - 50, 165, 100, 20, I18n.get("advMode.mode.unconditional", new Object[0]), (button) -> {
         this.conditional = !this.conditional;
         this.updateConditional();
      }));
      this.autoexecButton = (Button)this.addButton(new Button(this.width / 2 + 50 + 4, 165, 100, 20, I18n.get("advMode.mode.redstoneTriggered", new Object[0]), (button) -> {
         this.autoexec = !this.autoexec;
         this.updateAutoexec();
      }));
      this.doneButton.active = false;
      this.outputButton.active = false;
      this.modeButton.active = false;
      this.conditionalButton.active = false;
      this.autoexecButton.active = false;
   }

   public void updateGui() {
      BaseCommandBlock var1 = this.autoCommandBlock.getCommandBlock();
      this.commandEdit.setValue(var1.getCommand());
      this.trackOutput = var1.isTrackOutput();
      this.mode = this.autoCommandBlock.getMode();
      this.conditional = this.autoCommandBlock.isConditional();
      this.autoexec = this.autoCommandBlock.isAutomatic();
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      super.resize(minecraft, var2, var3);
      this.updateCommandOutput();
      this.updateMode();
      this.updateConditional();
      this.updateAutoexec();
      this.doneButton.active = true;
      this.outputButton.active = true;
      this.modeButton.active = true;
      this.conditionalButton.active = true;
      this.autoexecButton.active = true;
   }

   protected void populateAndSendPacket(BaseCommandBlock baseCommandBlock) {
      this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandBlockPacket(new BlockPos(baseCommandBlock.getPosition()), this.commandEdit.getValue(), this.mode, baseCommandBlock.isTrackOutput(), this.conditional, this.autoexec)));
   }

   private void updateMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.modeButton.setMessage(I18n.get("advMode.mode.sequence", new Object[0]));
         break;
      case AUTO:
         this.modeButton.setMessage(I18n.get("advMode.mode.auto", new Object[0]));
         break;
      case REDSTONE:
         this.modeButton.setMessage(I18n.get("advMode.mode.redstone", new Object[0]));
      }

   }

   private void nextMode() {
      switch(this.mode) {
      case SEQUENCE:
         this.mode = CommandBlockEntity.Mode.AUTO;
         break;
      case AUTO:
         this.mode = CommandBlockEntity.Mode.REDSTONE;
         break;
      case REDSTONE:
         this.mode = CommandBlockEntity.Mode.SEQUENCE;
      }

   }

   private void updateConditional() {
      if(this.conditional) {
         this.conditionalButton.setMessage(I18n.get("advMode.mode.conditional", new Object[0]));
      } else {
         this.conditionalButton.setMessage(I18n.get("advMode.mode.unconditional", new Object[0]));
      }

   }

   private void updateAutoexec() {
      if(this.autoexec) {
         this.autoexecButton.setMessage(I18n.get("advMode.mode.autoexec.bat", new Object[0]));
      } else {
         this.autoexecButton.setMessage(I18n.get("advMode.mode.redstoneTriggered", new Object[0]));
      }

   }
}
