package es.um.redes.nanoFiles.tcp.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NFServerSimple {

	private static final int PORT = 10000;
	private ServerSocket serverSocket = null;
	private int port;

	public NFServerSimple() throws IOException {
		/*
		 * DONE: Crear una direción de socket a partir del puerto especificado
		 */
		/*
		 * DONE: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		
		
		this.port = PORT;
		boolean bucle = false;
		while(!bucle) {
			InetSocketAddress serverSocketAddress = new InetSocketAddress(port);
			try {
				serverSocket = new ServerSocket();
				serverSocket.bind(serverSocketAddress);
				bucle = true;
			} catch (Exception e) {
				port++;
			}

		}
		System.out.println("\nServer is listening on port " + port);

	}

	/**
	 * Metodo para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * 
	 */
	public void run() {
		
		/*
		 * DONE: Comprobar que el socket servidor está creado y ligado
		 */
		/*
		 * DONE: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		/*
		 * DONE: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
		
		if(serverSocket != null && serverSocket.isBound()) {
			try {
				while(true) {
					
						System.out.println("Servidor esperando...");
						Socket socket = serverSocket.accept();
						System.out.println("\nSe ha conectado un nuevo cliente: " + socket.getInetAddress().toString() + ":" + socket.getPort());
						NFServerComm serverComm = new NFServerComm();
	                    serverComm.serveFilesToClient(socket);
				}
			} catch (IOException e) {
	            System.out.println("Ha ocurrido un error durante la ejecución del servidor");
	        } finally {
	            try {
	                serverSocket.close();
	            } catch (IOException e) {
	                System.out.println("Error cerrando el serverSocket");
	            }
	        }
		}
		System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
	}
}
