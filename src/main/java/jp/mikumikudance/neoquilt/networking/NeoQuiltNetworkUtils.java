package jp.mikumikudance.neoquilt.networking;

import java.lang.reflect.Field;

import net.minecraft.client.network.AbstractClientNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.AbstractServerPacketHandler;

public class NeoQuiltNetworkUtils {

	public static ClientConnection handlerToConnection(AbstractServerPacketHandler handler) {
		ClientConnection connect;
		try {
			Field con = AbstractServerPacketHandler.class.getField("field_45013");
			con.setAccessible(true);
			 connect = (ClientConnection)con.get(handler);
		return connect;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		return null;
		}
	}

	public static ClientConnection handlerToConnection(AbstractClientNetworkHandler handler) {
		ClientConnection connect;
		try {
			Field con = AbstractClientNetworkHandler.class.getField("field_45589");
			con.setAccessible(true);
			 connect = (ClientConnection)con.get(handler);
		return connect;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		return null;
		}
	}
	
	
}
