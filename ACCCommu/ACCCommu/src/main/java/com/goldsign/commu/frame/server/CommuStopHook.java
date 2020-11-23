package com.goldsign.commu.frame.server;

import org.apache.log4j.Logger;

/**
 * 
 * @author hejj
 */
public class CommuStopHook extends Thread {
	private CommuServer server;

	public CommuStopHook(CommuServer server) {
		this.server = server;
	}

	private static Logger logger = Logger.getLogger(CommuStopHook.class
			.getName());

        @Override
	public void run() {
		logger.info("Catch communication server stop event!");
		server.stop();
	}

}
