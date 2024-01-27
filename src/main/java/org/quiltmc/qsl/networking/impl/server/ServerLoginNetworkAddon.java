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

package org.quiltmc.qsl.networking.impl.server;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.ServerLoginNetworking;
import org.quiltmc.qsl.networking.impl.AbstractNetworkAddon;

import net.fabricmc.fabric.mixin.networking.accessor.ServerLoginNetworkHandlerAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

@ApiStatus.Internal
public final class ServerLoginNetworkAddon extends AbstractNetworkAddon<ServerLoginNetworking.QueryResponseReceiver> implements PacketSender {
	private final ClientConnection connection;
	private final ServerLoginNetworkHandler handler;
	private final MinecraftServer server;
	private final QueryIdFactory queryIdFactory;
	private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();
	private final Map<Integer, Identifier> channels = new ConcurrentHashMap<>();
	private boolean firstQueryTick = true;
	net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon main ;
	
	public ServerLoginNetworkAddon(ServerLoginNetworkHandler handler) {
		super(ServerNetworkingImpl.LOGIN, "ServerLoginNetworkAddon for " + handler.getConnectionInfo());
		this.connection = ((ServerLoginNetworkHandlerAccessor) handler).getConnection();
		this.handler = handler;
		this.server = ((ServerLoginNetworkHandlerAccessor) handler).getServer();
		this.queryIdFactory = QueryIdFactory.create();

		ServerLoginConnectionEvents.INIT.invoker().onLoginInit(handler, this.server);
		this.receiver.startSession(this);
		this.main= new net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon(handler);
		
	}

	// return true if no longer ticks query
	public boolean queryTick() {
	
		return main.queryTick();
	}


	/**
	 * Handles an incoming query response during login.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet was handled
	 */
	public boolean handle(LoginQueryResponseC2SPacket packet) {
	return main.handle(packet);
	}


	@Override
	public Packet<?> createPacket(Identifier channelName, PacketByteBuf buf) {
return main.createPacket(channelName, buf);
	}

	@Override
	public void sendPacket(Packet<?> packet) {
 main.sendPacket(packet);
	}

	@Override
	public void sendPacket(Packet<?> packet, PacketSendListener listener) {
		this.main.sendPacket(packet, listener);
	}

	public void registerOutgoingPacket(LoginQueryRequestS2CPacket packet) {
 main.registerOutgoingPacket(packet);
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
	}

	@Override
	protected void invokeDisconnectEvent() {
		ServerLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.handler, this.server);
		this.receiver.endSession(this);
	}

	public void handlePlayTransition() {
		this.receiver.endSession(this);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return false;
	}

	@Override
	protected void invokeInitEvent() {
		// TODO Auto-generated method stub
		ServerLoginConnectionEvents.INIT.invoker().onLoginInit(handler, this.server);
	}
}
