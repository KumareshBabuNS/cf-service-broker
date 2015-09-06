/**
 * 
 */
package de.evoila.cf.broker.service.availability;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Johannes Hiemer.
 *
 */
public class ServicePortAvailabilityVerifier {

	private static final int SOCKET_TIMEOUT = 5000;
	
	private static Logger log = LoggerFactory.getLogger(ServicePortAvailabilityVerifier.class);

	public static boolean execute(String ip, int port) {
		boolean available = false;
		
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(ip, port), SOCKET_TIMEOUT);

			if (socket.isConnected())
				available = true;
		} catch (Exception e) {
			log.info("Service port could not be reached", e);
		} finally {
			if (socket != null && socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					log.info("Could not close port", e);
				}
			}
		}
		return available;
	}

}
