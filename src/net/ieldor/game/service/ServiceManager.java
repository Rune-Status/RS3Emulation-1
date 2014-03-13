package net.ieldor.game.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.ieldor.ServerContext;

/**
 * A class which manages {@link Service}s.
 * @author Graham
 */
public final class ServiceManager {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());

	/**
	 * The service map.
	 */
	private Map<Class<? extends Service>, Service> services = new HashMap<Class<? extends Service>, Service>();

	/**
	 * Creates and initializes the {@link ServiceManager}.
	 * @throws Exception if an error occurs.
	 */
	public ServiceManager() throws Exception {
		init();
	}

	/**
	 * Initializes this service manager.
	 * @throws Exception if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	private void init() throws Exception {
		logger.info("Registering services...");
		
		Class<? extends Service> clazz = (Class<? extends Service>) (Class<? extends Service>) Class.forName("net.ieldor.game.GamePulse");
		register((Class<Service>) clazz, clazz.newInstance());
	}

	/**
	 * Registers a service.
	 * @param <S> The type of service.
	 * @param clazz The service's class.
	 * @param service The service.
	 */
	private <S extends Service> void register(Class<S> clazz, S service) {
		logger.fine("Registering service: " + clazz + "...");
		services.put(clazz, service);
	}

	/**
	 * Gets a service.
	 * @param <S> The type of service.
	 * @param clazz The service class.
	 * @return The service.
	 */
	@SuppressWarnings("unchecked")
	public <S extends Service> S getService(Class<S> clazz) {
		return (S) services.get(clazz);
	}

	/**
	 * Starts all the services.
	 */
	public void startAll() {
		logger.info("Starting services...");
		for (Service service : services.values()) {
			logger.fine("Starting service: " + service.getClass().getName() + "...");
			service.start();
		}
	}

	/**
	 * Sets the context of all services.
	 * @param ctx The server context.
	 */
	public void setContext(ServerContext ctx) {
		for (Service s : services.values()) {
			s.setContext(ctx);
		}
	}

}
