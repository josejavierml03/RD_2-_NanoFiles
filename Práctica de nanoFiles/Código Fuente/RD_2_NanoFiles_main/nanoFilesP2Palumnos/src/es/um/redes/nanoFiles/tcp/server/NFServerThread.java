package es.um.redes.nanoFiles.tcp.server;

import java.net.Socket;


public class NFServerThread extends Thread {
	

	/*
	 * DONE: Esta clase modela los hilos que son creados desde NFServer y cada uno
	 * de los cuales simplemente se encarga de invocar a
	 * NFServerComm.serveFilesToClient con el socket retornado por el método accept
	 * (un socket distinto para "conversar" con un cliente)
	 */
	protected static volatile boolean stop = false; 

	Socket socket = null; 

	public NFServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() { 
		NFServerComm serverComm = new NFServerComm();
        serverComm.serveFilesToClient(socket);

	}

	public static void stopServer() {
		stop = true; 
	}
	
	
	public static boolean shouldStopServer() {
		return stop; 
	}

}

