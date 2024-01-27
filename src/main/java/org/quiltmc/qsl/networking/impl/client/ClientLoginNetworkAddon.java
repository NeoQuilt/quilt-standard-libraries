/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package org.quiltmc.qsl.networking.impl.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.quiltmc.qsl.networking.api.client.ClientLoginConnectionEvents;
import org.quiltmc.qsl.networking.api.client.ClientLoginNetworking;
import org.quiltmc.qsl.networking.impl.AbstractNetworkAddon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

public final class ClientLoginNetworkAddon extends AbstractNetworkAddon<ClientLoginNetworking.QueryRequestReceiver> {
	private final ClientLoginNetworkHandler handler;
	private final MinecraftClient client;
	private boolean firstResponse = true;
public net.fabricmc.fabric.impl.networking.client.ClientLoginNetworkAddon main;
	
	public ClientLoginNetworkAddon(ClientLoginNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingImpl.LOGIN, "ClientLoginNetworkAddon for Client");
		this.handler = handler;
		this.client = client;
		this.main=new net.fabricmc.fabric.impl.networking.client.ClientLoginNetworkAddon(handler,client);
	}

	@Override
	protected void invokeInitEvent() {
		ClientLoginConnectionEvents.INIT.invoker().onLoginStart(this.handler, this.client);
	}

	public boolean handlePacket(LoginQueryRequestS2CPacket packet) {
		return main.handlePacket(packet);
	}

	private boolean handlePacket(int queryId, Identifier channelName, PacketByteBuf originalBuf) {
Class clazz = main.getClass();
try {
	Method method = clazz.getDeclaredMethod("handlePacket", Identifier.class,PacketByteBuf.class);
method.setAccessible(true);
return (boolean)method.invoke(main, channelName,originalBuf);
} catch (NoSuchMethodException | SecurityException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IllegalAccessException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IllegalArgumentException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (InvocationTargetException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		return false;
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
	}

	@Override
	protected void invokeDisconnectEvent() {
		ClientLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.handler, this.client);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return false;
	}
}
