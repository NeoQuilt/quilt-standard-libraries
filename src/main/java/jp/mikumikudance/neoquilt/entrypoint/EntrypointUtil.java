package jp.mikumikudance.neoquilt.entrypoint;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.util.ExceptionUtil;

public class EntrypointUtil {

	public static <T> void invoke(String name, Class<T> type, BiConsumer<T, ModContainer> invoker) {
		invoke0(name, type, container -> invoker.accept(container.getEntrypoint(), container.getProvider()));
	}

	public static <T> void invoke(String name, Class<T> type, Consumer<? super T> invoker) {
		// TODO Auto-generated method stub
		invoke0(name, type, container -> invoker.accept(container.getEntrypoint()));
	}
	private static <T> void invoke0(String name, Class<T> type, Consumer<EntrypointContainer<T>> invoker) {
		FabricLoader loader = FabricLoader.getInstance();
		RuntimeException exception = null;
		Collection<EntrypointContainer<T>> entrypoints = loader.getEntrypointContainers(name, type);

		for (EntrypointContainer<T> container : entrypoints) {
			try {
				invoker.accept(container);
			} catch (Throwable t) {
				exception = ExceptionUtil.gatherExceptions(t,
						exception,
						exc -> new RuntimeException(String.format("Could not execute entrypoint stage '%s' due to errors, provided by '%s'!",
								name, container.getProvider().getMetadata().getId()),
								exc));
			}
		}

		if (exception != null) {
			throw exception;
		}
	}

}
