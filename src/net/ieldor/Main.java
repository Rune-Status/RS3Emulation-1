/*
 * This file is part of RS3Emulation.
 *
 * RS3Emulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RS3Emulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RS3Emulation.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ieldor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ieldor.cache.Cache;
import net.ieldor.cache.Container;
import net.ieldor.cache.FileStore;
import net.ieldor.game.World;
import net.ieldor.game.model.player.Player;
import net.ieldor.game.service.ServiceManager;
import net.ieldor.modules.login.LoginManager;
import net.ieldor.modules.scripts.ScriptManager;
import net.ieldor.modules.worldlist.WorldList;
import net.ieldor.network.ChannelChildHandler;
import net.ieldor.ondemand.UpdateService;
import net.ieldor.utility.CharacterRepository;

/**
 * RS3Emulation
 * Main.java
 * Mar 11, 2014
 * @author Im Frizzy : Kyle Friz : <skype:kfriz1998>
 */
public class Main {

	/**
	 * The {@link ServerBootstrap} that is used to handle game networking.
	 */
	private ServerBootstrap bootstrap;

	/**
	 * The cache instance for the server.
	 */
	private Cache cache;

	/**
	 * The default logging instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * The update service for incoming requests.
	 */
	private final UpdateService updateService = new UpdateService();

	/**
	 * The executor for the update service
	 */
	private final Executor serviceExecutor = Executors.newCachedThreadPool();

	/**
	 * The checksum table instance.
	 */
	private ByteBuffer checksumTable;
	
	/**
	 * The name management service, used for managing player display names
	 */
	private static LoginManager loginServer;

	/**
	 * The {@link CharacterRepository} for storing players.
	 */
	private static CharacterRepository<Player> players;
	
	/**
	 * An {@link ArrayList} of all worlds hosted on this server.
	 */
	private static ArrayList<World> worlds;

	/**
	 * The service manager.
	 */
	private final ServiceManager serviceManager;
	
	/**
	 * The service manager.
	 */
	private final ScriptManager scriptManager = new ScriptManager();
	
	/**
	 * Constructs a new {@code Main} instance.
	 * @throws Exception 
	 */
	public Main() throws Exception {
		logger.info("Launching...");
		Main.players = new CharacterRepository<Player>(2500);
		serviceManager = new ServiceManager();
		worlds = new ArrayList<World>();
	}

	/**
	 * The main method of the Ieldor server, this is where everything get
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String... args) {
		Main mainContext = null;
		try {
			mainContext = new Main();
			mainContext.initate();
			mainContext.loadGameContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Complete!");
	}

	/**
	 * Initates a new gaming enviroment, here we are going to setup everything
	 * needed for the server to be able to bind.
	 */
	private void initate() {
		bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 100);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new LoggingHandler(LogLevel.INFO));
		bootstrap.childHandler(new ChannelChildHandler(this));
		try {
			bootstrap.localAddress(43594).bind().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Here we are going to load any context that is needed for the game to be
	 * successfully prepared for clients to connect. For example, here we are
	 * going to load item definitions, and the client cache for the client to
	 * store in the local cache storage directory.
	 * 
	 * @throws IOException
	 */
	private void loadGameContext() throws IOException {
		this.cache = new Cache(FileStore.open(new File("./data/cache/")));
		serviceExecutor.execute(updateService);

		Container container = new Container(Container.COMPRESSION_NONE, cache.createChecksumTable().encode(true, Constants.JS5ModulusKey, Constants.JS5PrivateKey));
		checksumTable = container.encode();
		serviceManager.startAll();
		
		//scriptManager.load("./data/scripts/");
		loginServer = new LoginManager();
		loginServer.init();
		logger.info("Initialised login server.");
		WorldList.init();
		
	    worlds.add(World.getLobby());
	    worlds.add(World.getDefaultWorld());
	}
	

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}


	/**
	 * Gets the players.
	 * @return the players
	 */
	public static CharacterRepository<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Gets the worlds currently hosted on this server
	 * @return	An ArrayList of worlds
	 */
	public static ArrayList<World> getWorlds () {
		return worlds;
	}
	
	/**
	 * Gets the cache instance.
	 * 
	 * @return the cache
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Gets the update service.
	 * 
	 * @return the update service
	 */
	public UpdateService getUpdateService() {
		return updateService;
	}

	/**
	 * Gets the {@link ByteBuffer} (checksum table) for the cache.
	 * 
	 * @return the checksum table
	 */
	public ByteBuffer getChecksumTable() {
		return checksumTable;
	}
	
	/**
	 * Gets the name manager.
	 * 
	 * @return the name manager
	 */
	public static LoginManager getloginServer () {
		return loginServer;
	}
}
