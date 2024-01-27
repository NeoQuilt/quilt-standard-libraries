/*
 * Copyright 2022 The Quilt Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.registry.impl.sync.server;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.networking.impl.ChannelInfoHolder;
import org.quiltmc.qsl.registry.impl.sync.ClientPackets;
import org.quiltmc.qsl.registry.impl.sync.ProtocolVersions;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepConnectionAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.PongC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabOpenC2SPacket;
import net.minecraft.network.packet.c2s.play.BeaconUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.BlockNbtQueryC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatSessionUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ChunkBatchAcknowledgementC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandBlockMinecartUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandBlockUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandCompletionRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.DifficultyLockUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.DifficultyUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.EntityNbtQueryC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.HandledScreenCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.InventoryItemPickC2SPacket;
import net.minecraft.network.packet.c2s.play.ItemRenameC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGenerationC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.MerchantTradeSelectionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerAbilityUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractionWithBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractionWithEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractionWithItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ReconfigurationAcknowledgementC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectedSlotUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.SignUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.SlotClickC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportationC2SPacket;
import net.minecraft.network.packet.c2s.play.StructureBlockUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmationC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_zpmnryxk;

/**
 * All the magic happens here!
 * <p>
 * This is special PacketListener for handling registry sync.
 * Why does it exist? Wouldn't usage of login packets be better?
 * <p>
 * And well, yes it would, but sadly these can't be made compatible with proxy
 * software like Velocity (see Forge). Thankfully emulating them on PLAY
 * protocol isn't too hard and gives equal results. And doing them on PLAY
 * is needed for Fabric compatibility anyway.
 * It still doesn't work with Velocity out of the box (they don't care much about this
 * being valid), getting support is still simple.
 */
@ApiStatus.Internal
public final class ServerRegistrySyncNetworkHandler implements ServerPlayPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final int HELLO_PING = 0;
	private static final int GOODBYE_PING = 1;

	private final ClientConnection connection;
	private final ExtendedConnectionClient extendedConnection;
	private final ServerPlayerEntity player;
	private final Runnable continueLoginRunnable;

	private final List<CustomPayloadC2SPacket> delayedPackets = new ArrayList<>();
	private int syncVersion = ProtocolVersions.NO_PROTOCOL;

	public ServerRegistrySyncNetworkHandler(ServerPlayerEntity player, ClientConnection connection, Runnable continueLogin) {
		this.connection = connection;
		this.player = player;
		this.continueLoginRunnable = continueLogin;
		this.extendedConnection = (ExtendedConnectionClient) connection;

		((DelayedPacketsHolder) this.player).quilt$setPacketList(this.delayedPackets);

		ServerRegistrySync.sendHelloPacket(connection);
		connection.send(new PongC2SPacket(HELLO_PING));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPlayPong(PongC2SPacket packet) {
		switch (packet.getParameter()) {
			case HELLO_PING -> {
				if (ServerRegistrySync.SERVER_SUPPORTED_PROTOCOL.contains(this.syncVersion)) {
					this.extendedConnection.quilt$setUnderstandsOptional();
					ServerRegistrySync.sendSyncPackets(this.connection, this.player, this.syncVersion);
				} else if (ServerRegistrySync.SERVER_SUPPORTED_PROTOCOL.contains(ProtocolVersions.FAPI_PROTOCOL) && (ServerRegistrySync.forceFabricFallback || (ServerRegistrySync.supportFabric && ((ChannelInfoHolder) this.connection).getPendingChannelsNames().contains(ServerFabricRegistrySync.ID)))) {
					ServerFabricRegistrySync.sendSyncPackets(this.connection);
					this.syncVersion = ProtocolVersions.FAPI_PROTOCOL;
				}

				this.connection.send(new PongC2SPacket(GOODBYE_PING));
			}
			case GOODBYE_PING -> {
				if (this.syncVersion == ProtocolVersions.NO_PROTOCOL && ServerRegistrySync.requiresSync()) {
					this.disconnect(ServerRegistrySync.noRegistrySyncMessage);
				} else {
					this.continueLogin();
				}
			}
		}
	}

	private void continueLogin() {
		this.player.server.execute(this.continueLoginRunnable);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
		PacketByteBuf buff = PacketByteBufs.create();
		packet.payload().write(buff);
		if (packet.payload().id().equals(ClientPackets.HANDSHAKE)) {
			this.syncVersion = buff.readVarInt();
		} else if (packet.payload().id().equals(ClientPackets.SYNC_FAILED)) {
			LOGGER.info("Disconnecting {} due to sync failure of {} registry", this.player.getGameProfile().getName(), buff.readIdentifier());
		} else if (packet.payload().id().equals(ClientPackets.UNKNOWN_ENTRY)) {
			this.handleUnknownEntry(buff);
		} else if (packet.payload().id().equals(ClientPackets.MOD_PROTOCOL)) {
			this.handleModProtocol(buff);
		} else {
			this.delayedPackets.add(new CustomPayloadC2SPacket(new PacketByteBuf(buff.copy()).writeIdentifier(packet.payload().id())));
		}
	}

	private void handleModProtocol(PacketByteBuf data) {
		var count = data.readVarInt();
		while (count-- > 0) {
			var id = data.readString();
			var version = data.readVarInt();
			this.extendedConnection.quilt$setModProtocol(id, version);
		}
	}

	private void handleUnknownEntry(PacketByteBuf data) {
		var registry = Registries.REGISTRY.get(data.readIdentifier());
		var length = data.readVarInt();

		while (length-- > 0) {
			var object = registry.get(data.readVarInt());

			if (object != null) {
				this.extendedConnection.quilt$addUnknownEntry(registry, object);
			}
		}
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
		for (var packet : this.delayedPackets) {
			PacketByteBuf buff = PacketByteBufs.create();
			packet.payload().write(buff);
			if (buff.refCnt() != 0) {
				buff.release(buff.refCnt());
			}
		}
	}

	public void disconnect(Text reason) {
		try {
			for (var packet : this.delayedPackets) {
				PacketByteBuf buff = PacketByteBufs.create();
				packet.payload().write(buff);
				if (buff.refCnt() != 0) {
					buff.release(buff.refCnt());
				}
			}

			this.connection.send(new DisconnectS2CPacket(reason),
					PacketSendListener.alwaysRun(() -> this.connection.disconnect(reason))
			);
		} catch (Exception var3) {
			LOGGER.error("Error whilst disconnecting player", var3);
		}
	}

	@Override
	public boolean isConnected() {
		return this.connection.isOpen();
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket packet) {}

	@Override
	public void onChatMessage(ChatMessageC2SPacket packet) {}

	@Override
	public void onChatCommand(ChatCommandC2SPacket packet) {}

	@Override
	public void onMessageAcknowledgment(MessageAcknowledgmentC2SPacket packet) {}

	@Override
	public void onClientStatusUpdate(ClientStatusUpdateC2SPacket packet) {}


	@Override
	public void onButtonClick(ButtonClickC2SPacket packet) {}

	@Override
	public void onSlotClick(SlotClickC2SPacket packet) {}

	@Override
	public void onCraftRequest(CraftRequestC2SPacket packet) {}

	@Override
	public void onHandledScreenClose(HandledScreenCloseC2SPacket packet) {}

	@Override
	public void onPlayerInteractionWithEntity(PlayerInteractionWithEntityC2SPacket packet) {}

	@Override
	public void onKeepConnectionAlive(KeepConnectionAliveC2SPacket packet) {}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {}

	@Override
	public void onPlayerAbilityUpdate(PlayerAbilityUpdateC2SPacket packet) {}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {}

	@Override
	public void onClientCommand(ClientCommandC2SPacket packet) {}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket packet) {}

	@Override
	public void onSelectedSlotUpdate(SelectedSlotUpdateC2SPacket packet) {}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {}

	@Override
	public void onSignUpdate(SignUpdateC2SPacket packet) {}

	@Override
	public void onPlayerInteractionWithBlock(PlayerInteractionWithBlockC2SPacket packet) {}

	@Override
	public void onPlayerInteractionWithItem(PlayerInteractionWithItemC2SPacket packet) {}

	@Override
	public void onSpectatorTeleportation(SpectatorTeleportationC2SPacket packet) {}

	@Override
	public void onResourcePackStatusUpdate(ResourcePackStatusUpdateC2SPacket packet) {}

	@Override
	public void onBoatPaddleStateUpdate(BoatPaddleStateUpdateC2SPacket packet) {}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket packet) {}

	@Override
	public void onTeleportConfirmation(TeleportConfirmationC2SPacket packet) {}

	@Override
	public void onRecipeBookUpdate(RecipeBookUpdateC2SPacket packet) {}

	@Override
	public void onRecipeCategoryOptionUpdate(RecipeCategoryOptionUpdateC2SPacket packet) {}

	@Override
	public void onAdvancementTabOpen(AdvancementTabOpenC2SPacket packet) {}

	@Override
	public void onCommandCompletionRequest(CommandCompletionRequestC2SPacket packet) {}

	@Override
	public void onCommandBlockUpdate(CommandBlockUpdateC2SPacket packet) {}

	@Override
	public void onCommandBlockMinecartUpdate(CommandBlockMinecartUpdateC2SPacket packet) {}

	@Override
	public void onInventoryItemPick(InventoryItemPickC2SPacket packet) {}

	@Override
	public void onItemRename(ItemRenameC2SPacket packet) {}

	@Override
	public void onBeaconUpdate(BeaconUpdateC2SPacket packet) {}

	@Override
	public void onStructureBlockUpdate(StructureBlockUpdateC2SPacket packet) {}

	@Override
	public void onMerchantTradeSelection(MerchantTradeSelectionC2SPacket packet) {}

	@Override
	public void onBookUpdate(BookUpdateC2SPacket packet) {}

	@Override
	public void onEntityNbtQuery(EntityNbtQueryC2SPacket packet) {}

	@Override
	public void onBlockNbtQuery(BlockNbtQueryC2SPacket packet) {}

	@Override
	public void onJigsawUpdate(JigsawUpdateC2SPacket packet) {}

	@Override
	public void onJigsawGeneration(JigsawGenerationC2SPacket packet) {}

	@Override
	public void onDifficultyUpdate(DifficultyUpdateC2SPacket packet) {}

	@Override
	public void onDifficultyLockUpdate(DifficultyLockUpdateC2SPacket packet) {}

	@Override
	public void onChatSessionUpdate(ChatSessionUpdateC2SPacket packet) {}

	@Override
	public void onPing(QueryPingC2SPacket var1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void method_54436(C_zpmnryxk var1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReconfigurationAcknowledgement(ReconfigurationAcknowledgementC2SPacket var1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChunkBatchAcknowledgement(ChunkBatchAcknowledgementC2SPacket var1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientSettingsUpdate(ClientSettingsUpdateC2SPacket var1) {
		// TODO Auto-generated method stub
		
	}
}
