package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * 
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */

	private LinkedList<Integer> puertos;


	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * DONE: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		this.socket = new DatagramSocket(DIRECTORY_PORT);
		
		/*
		 * DONE: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */
		this.nicks = new HashMap<String, Integer>();
		this.sessionKeys = new HashMap<Integer, String>();
		this.puertos = new LinkedList<Integer>();

		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = null;
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		/*
		 * DONE: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */
		receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
		DatagramPacket packetFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);


		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio

			// DONE: (Boletín UDP) Recibimos a través del socket un datagrama
			
			socket.receive(packetFromClient);
			
			// DONE: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido
			
			dataLength = packetFromClient.getLength();
			
			// DONE: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido
			
			clientAddr = (InetSocketAddress) packetFromClient.getSocketAddress();

			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {
				String messageFromClient = null;
				/*
				 * DONE: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */
				messageFromClient = new String(receptionBuffer, 0, packetFromClient.getLength());

				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * DONE: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */
					
					if(messageFromClient.contains("login")) {
						String messageToClient = new String("loginok");
						byte[] dataToClient = messageToClient.getBytes();
						DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
						socket.send(packetToClient);
					}

				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}
					DirMessage mensaje = DirMessage.fromString(messageFromClient);
					
					/*
					 * DONE: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */
					
					mensaje = buildResponseFromRequest(mensaje, clientAddr);
					
					/*
					 * DONE: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */
					
					byte[] dataToClient = mensaje.toString().getBytes();
					DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
					socket.send(packetToClient);
					
					/*
					 * DONE: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */

				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}
		}
	}

	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * DONE: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */
		
		/*
		 * DONE: Comprobamos si tenemos dicho usuario registrado (atributo "nicks"). Si
		 * no está, generamos su sessionKey (número aleatorio entre 0 y 1000) y añadimos
		 * el nick y su sessionKey asociada. NOTA: Puedes usar random.nextInt(10000)
		 * para generar la session key
		 */

		/*
		 * DONE: Construimos un mensaje de respuesta que indique el éxito/fracaso del
		 * login y contenga la sessionKey en caso de éxito, y lo devolvemos como
		 * resultado del método.
		 */

		/*
		 * DONE: Imprimimos por pantalla el resultado de procesar la petición recibida
		 * (éxito o fracaso) con los datos relevantes, a modo de depuración en el
		 * servidor
		 */
		String operation = msg.getOperation();

		DirMessage response = null;

		switch (operation) {
		
		case DirMessageOps.OPERATION_LOGIN: {
			String username = msg.getNickname();

			if (!nicks.containsKey(username)) {
				int numeroal = random.nextInt(10000);
				nicks.putIfAbsent(username, numeroal);
				sessionKeys.putIfAbsent(numeroal, username);
				response = new DirMessage(DirMessageOps.OPERATION_LOGIN_OK, Integer.toString(numeroal));
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_LOGIN_NOTOK);
			}

			break;
		}

		case DirMessageOps.OPERATION_LOGOUT: {
			String value = msg.getNickname();
			int sessionkey = Integer.parseInt(value);
			if (sessionKeys.containsKey(sessionkey)) {
				String nick = sessionKeys.get(sessionkey);
				sessionKeys.remove(sessionkey);
				nicks.remove(nick);
				response = new DirMessage(DirMessageOps.OPERATION_LOGOUT_OK, value);
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_LOGOUT_NOTOK);
			}
			break;
		}

		case DirMessageOps.OPERATION_USERLIST: {
			try {
				String nicknames = nicks.keySet().toString();
				response = new DirMessage(DirMessageOps.OPERATION_USERLIST_OK, nicknames);
			} catch (Exception e) {
				response = new DirMessage(DirMessageOps.OPERATION_USERLIST_NOTOK);

			}
			break;
		}

		case DirMessageOps.OPERATION_BGSERVER: {
			String value = msg.getNickname();
			int puerto = Integer.parseInt(value);
			if (!puertos.contains(puerto)) {
				puertos.add(puerto);
				response = new DirMessage(DirMessageOps.OPERATION_BGSERVER_OK, value);
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_BGSERVER_NOTOK);
			}
			break;
		}

		case DirMessageOps.OPERATION_DESBGSERVER: {
			String value = msg.getNickname();
			int puerto = Integer.parseInt(value);
			if (puertos.contains(puerto)) {
				puertos.removeFirstOccurrence(puerto);
				response = new DirMessage(DirMessageOps.OPERATION_DESBGSERVER_OK, value);
			} else {
				response = new DirMessage(DirMessageOps.OPERATION_DESBGSERVER_NOTOK);
			}
			break;
		}

		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		return response;

	}
}
