/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.server.CommuServer;
import java.io.IOException;
import java.net.BindException;

/**
 * 
 * @author hejj
 */
public class CommuServerThread extends Thread {
	public CommuServerThread() {
	}

	public void run() {
		CommuServer server = new CommuServer();
		try {
			server.startSocketListener();
		} catch (BindException e) {

		} catch (IOException e) {

		}

	}

}
