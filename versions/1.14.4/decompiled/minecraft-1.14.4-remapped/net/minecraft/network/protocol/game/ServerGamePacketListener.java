package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;

public interface ServerGamePacketListener extends PacketListener {
   void handleAnimate(ServerboundSwingPacket var1);

   void handleChat(ServerboundChatPacket var1);

   void handleClientCommand(ServerboundClientCommandPacket var1);

   void handleClientInformation(ServerboundClientInformationPacket var1);

   void handleContainerAck(ServerboundContainerAckPacket var1);

   void handleContainerButtonClick(ServerboundContainerButtonClickPacket var1);

   void handleContainerClick(ServerboundContainerClickPacket var1);

   void handlePlaceRecipe(ServerboundPlaceRecipePacket var1);

   void handleContainerClose(ServerboundContainerClosePacket var1);

   void handleCustomPayload(ServerboundCustomPayloadPacket var1);

   void handleInteract(ServerboundInteractPacket var1);

   void handleKeepAlive(ServerboundKeepAlivePacket var1);

   void handleMovePlayer(ServerboundMovePlayerPacket var1);

   void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket var1);

   void handlePlayerAction(ServerboundPlayerActionPacket var1);

   void handlePlayerCommand(ServerboundPlayerCommandPacket var1);

   void handlePlayerInput(ServerboundPlayerInputPacket var1);

   void handleSetCarriedItem(ServerboundSetCarriedItemPacket var1);

   void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket var1);

   void handleSignUpdate(ServerboundSignUpdatePacket var1);

   void handleUseItemOn(ServerboundUseItemOnPacket var1);

   void handleUseItem(ServerboundUseItemPacket var1);

   void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket var1);

   void handleResourcePackResponse(ServerboundResourcePackPacket var1);

   void handlePaddleBoat(ServerboundPaddleBoatPacket var1);

   void handleMoveVehicle(ServerboundMoveVehiclePacket var1);

   void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket var1);

   void handleRecipeBookUpdatePacket(ServerboundRecipeBookUpdatePacket var1);

   void handleSeenAdvancements(ServerboundSeenAdvancementsPacket var1);

   void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket var1);

   void handleSetCommandBlock(ServerboundSetCommandBlockPacket var1);

   void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket var1);

   void handlePickItem(ServerboundPickItemPacket var1);

   void handleRenameItem(ServerboundRenameItemPacket var1);

   void handleSetBeaconPacket(ServerboundSetBeaconPacket var1);

   void handleSetStructureBlock(ServerboundSetStructureBlockPacket var1);

   void handleSelectTrade(ServerboundSelectTradePacket var1);

   void handleEditBook(ServerboundEditBookPacket var1);

   void handleEntityTagQuery(ServerboundEntityTagQuery var1);

   void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery var1);

   void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket var1);

   void handleChangeDifficulty(ServerboundChangeDifficultyPacket var1);

   void handleLockDifficulty(ServerboundLockDifficultyPacket var1);
}
