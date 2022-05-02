package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

@ClientJarOnly
public class JigsawBlockEditScreen extends Screen {
   private final JigsawBlockEntity jigsawEntity;
   private EditBox attachementTypeEdit;
   private EditBox targetPoolEdit;
   private EditBox finalStateEdit;
   private Button doneButton;

   public JigsawBlockEditScreen(JigsawBlockEntity jigsawEntity) {
      super(NarratorChatListener.NO_TITLE);
      this.jigsawEntity = jigsawEntity;
   }

   public void tick() {
      this.attachementTypeEdit.tick();
      this.targetPoolEdit.tick();
      this.finalStateEdit.tick();
   }

   private void onDone() {
      this.sendToServer();
      this.minecraft.setScreen((Screen)null);
   }

   private void onCancel() {
      this.minecraft.setScreen((Screen)null);
   }

   private void sendToServer() {
      this.minecraft.getConnection().send((Packet)(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.attachementTypeEdit.getValue()), new ResourceLocation(this.targetPoolEdit.getValue()), this.finalStateEdit.getValue())));
   }

   public void onClose() {
      this.onCancel();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         this.onDone();
      }));
      this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.onCancel();
      }));
      this.targetPoolEdit = new EditBox(this.font, this.width / 2 - 152, 40, 300, 20, I18n.get("jigsaw_block.target_pool", new Object[0]));
      this.targetPoolEdit.setMaxLength(128);
      this.targetPoolEdit.setValue(this.jigsawEntity.getTargetPool().toString());
      this.targetPoolEdit.setResponder((string) -> {
         this.updateValidity();
      });
      this.children.add(this.targetPoolEdit);
      this.attachementTypeEdit = new EditBox(this.font, this.width / 2 - 152, 80, 300, 20, I18n.get("jigsaw_block.attachement_type", new Object[0]));
      this.attachementTypeEdit.setMaxLength(128);
      this.attachementTypeEdit.setValue(this.jigsawEntity.getAttachementType().toString());
      this.attachementTypeEdit.setResponder((string) -> {
         this.updateValidity();
      });
      this.children.add(this.attachementTypeEdit);
      this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 120, 300, 20, I18n.get("jigsaw_block.final_state", new Object[0]));
      this.finalStateEdit.setMaxLength(256);
      this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
      this.children.add(this.finalStateEdit);
      this.setInitialFocus(this.targetPoolEdit);
      this.updateValidity();
   }

   protected void updateValidity() {
      this.doneButton.active = ResourceLocation.isValidResourceLocation(this.attachementTypeEdit.getValue()) & ResourceLocation.isValidResourceLocation(this.targetPoolEdit.getValue());
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      String var4 = this.attachementTypeEdit.getValue();
      String var5 = this.targetPoolEdit.getValue();
      String var6 = this.finalStateEdit.getValue();
      this.init(minecraft, var2, var3);
      this.attachementTypeEdit.setValue(var4);
      this.targetPoolEdit.setValue(var5);
      this.finalStateEdit.setValue(var6);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(super.keyPressed(var1, var2, var3)) {
         return true;
      } else if(!this.doneButton.active || var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onDone();
         return true;
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawString(this.font, I18n.get("jigsaw_block.target_pool", new Object[0]), this.width / 2 - 153, 30, 10526880);
      this.targetPoolEdit.render(var1, var2, var3);
      this.drawString(this.font, I18n.get("jigsaw_block.attachement_type", new Object[0]), this.width / 2 - 153, 70, 10526880);
      this.attachementTypeEdit.render(var1, var2, var3);
      this.drawString(this.font, I18n.get("jigsaw_block.final_state", new Object[0]), this.width / 2 - 153, 110, 10526880);
      this.finalStateEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }
}
