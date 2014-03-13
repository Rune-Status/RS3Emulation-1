package net.ieldor.utility.flooder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.logging.InternalLoggerFactory;
import io.netty.logging.Slf4JLoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ieldor.cache.Cache;
import net.ieldor.cache.ChecksumTable;
import net.ieldor.cache.FileStore;

public final class Flooder {

	private static final Logger logger = Logger.getLogger(Flooder.class.getName());

	public static void main(String[] args) {
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
		try {
			Flooder flooder = new Flooder();
			FlooderUI.start(flooder);
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Error starting flooder:", t);
		}
	}

	private final Cache cache;
	private final Bootstrap bootstrap = new Bootstrap();

	public Flooder() throws IOException {
		cache = new Cache(FileStore.open("./data/cache/"));
		ChecksumTable table = cache.createChecksumTable();
		int[] crc = new int[28];
		for (int i = 0; i < crc.length; i++)
			crc[i] = table.getEntry(i).getCrc();

		bootstrap.remoteAddress(new InetSocketAddress(InetAddress.getLoopbackAddress(), 43594));
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.handler(new FlooderChannelInitializer(crc));
	}

	public void start() throws InterruptedException {
		bootstrap.connect();
	}

	public void stop() {
		bootstrap.shutdown();
	}
}
