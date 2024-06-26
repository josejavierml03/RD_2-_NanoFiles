package es.um.redes.nanoFiles.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link NFServerThread} cada vez que se conecte un cliente.
 */
public class NFServer implements Runnable {

	private ServerSocket serverSocket = null;
	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private Thread hilo;
	private int puerto;
	
	public NFServer() throws IOException {
		
		/*
		 * DONE: Crear un socket servidor y ligarlo a cualquier puerto disponible
		 */
		serverSocket = new ServerSocket(0);
		puerto = serverSocket.getLocalPort();	
		serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
		System.out.println("Servidor escuchando en el puerto " + puerto);
			
	}

	/**
	 * Método que crea un socket servidor y ejecuta el hilo principal del servidor,
	 * esperando conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * DONE: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		
		while (true) {
			
			Socket socket;
			try {
				socket = serverSocket.accept(); 
				NFServerThread hiloS = new NFServerThread(socket); 
				hiloS.start(); 
			} catch (SocketTimeoutException e) {
				if (NFServerThread.shouldStopServer()) {
					try {
						serverSocket.close(); 
					} catch (IOException e1) {} 
					return;
				}
			} catch (IOException e) {
				System.out.println("* Unable to accept server");
				return;
			}
		}
		
		
		/*
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
		/*
		 * TODO: (Opcional) Crear un hilo nuevo de la clase NFServerThread, que llevará
		 * a cabo la comunicación con el cliente que se acaba de conectar, mientras este
		 * hilo vuelve a quedar a la escucha de conexiones de nuevos clientes (para
		 * soportar múltiples clientes). Si este hilo es el que se encarga de atender al
		 * cliente conectado, no podremos tener más de un cliente conectado a este
		 * servidor.
		 */
		
		/**
		 * DONE: Añadir métodos a esta clase para: 1) Arrancar el servidor en un hilo
		 * nuevo que se ejecutará en segundo plano 2) Detener el servidor (stopserver)
		 * 3) Obtener el puerto de escucha del servidor etc.
		 */
		
	}
	public void startServer() {
		hilo = new Thread(this); 
		hilo.start();
	}
	
	public void stopServer() {
		NFServerThread.stopServer();
	}
	
	public int getPort() {
		return puerto;
	}

}
