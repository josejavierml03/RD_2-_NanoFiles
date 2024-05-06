		package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataInputStream dis;
	private DataOutputStream dos;

	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		
		/*
		 * DONE Se crea el socket a partir de la dirección del servidor (IP, puerto). La
		 * creación exitosa del socket significa que la conexión TCP ha sido
		 * establecida.
		 */
		
		serverAddr = fserverAddr;
		try {
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());

		/*
		 * DONE Se crean los DataInputStream/DataOutputStream a partir de los streams de
		 * entrada/salida del socket creado. Se usarán para enviar (dos) y recibir (dis)
		 * datos del servidor.
		 */

		dos = new DataOutputStream(socket.getOutputStream());
		dis = new DataInputStream(socket.getInputStream());
		}catch (UnknownHostException e) {
			System.err.println("Error en NFConnector: Al crear la conexión");
		}
	}

	/**
	 * Método para descargar un fichero a través del socket mediante el que estamos
	 * conectados con un peer servidor.
	 * @param fserverAddr 
	 * 
	 * @param targetFileHashSubstr Subcadena del hash del fichero a descargar
	 * @param file                 El objeto File que referencia el nuevo fichero
	 *                             creado en el cual se escribirán los datos
	 *                             descargados del servidor
	 * @return Verdadero si la descarga se completa con éxito, falso en caso
	 *         contrario.
	 * @throws IOException Si se produce algún error al leer/escribir del socket.
	 */
	public boolean downloadFile( String targetFileHashSubstr, File file) throws IOException {
		
		 boolean downloaded = false;
		
		/*
		 * DONE: Construir objetos PeerMessage que modelen mensajes con los valores
		 * adecuados en sus campos (atributos), según el protocolo diseñado, y enviarlos
		 * al servidor a través del "dos" del socket mediante el método
		 * writeMessageToOutputStream.
		 */

		/*
		 * DONE: Recibir mensajes del servidor a través del "dis" del socket usando
		 * PeerMessage.readMessageFromInputStream, y actuar en función del tipo de
		 * mensaje recibido, extrayendo los valores necesarios de los atributos del
		 * objeto (valores de los campos del mensaje).
		 */
        
		PeerMessage tamano = PeerMessage.messageTam(targetFileHashSubstr);
        tamano.writeMessageToOutputStream(dos);
        PeerMessage msg = PeerMessage.readMessageFromInputStream(dis);
        int tam = msg.getLength();
        PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD,targetFileHashSubstr,tam);
        request.writeMessageToOutputStream(dos);
        PeerMessage responseMessage = PeerMessage.readMessageFromInputStream(dis);
        
		/*
		 * DONE: Para escribir datos de un fichero recibidos en un mensaje, se puede
		 * crear un FileOutputStream a partir del parámetro "file" para escribir cada
		 * fragmento recibido (array de bytes) en el fichero mediante el método "write".
		 * Cerrar el FileOutputStream una vez se han escrito todos los fragmentos.
		 */
		
        if (responseMessage.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_OK) {
            byte[] fileContent = responseMessage.getData();
            try (FileOutputStream fileO = new FileOutputStream(file)) {
            	fileO.write(fileContent);
            	fileO.close();
            }

            downloaded = true;
        }
        
        /*
		 * DONE: Finalmente, comprobar la integridad del fichero creado para comprobar
		 * que es idéntico al original, calculando el hash a partir de su contenido con
		 * FileDigest.computeFileChecksumString y comparándolo con el hash completo del
		 * fichero solicitado. Para ello, es necesario obtener del servidor el hash
		 * completo del fichero descargado, ya que quizás únicamente obtuvimos una
		 * subcadena del mismo como parámetro.
		 */
        
        if(downloaded) {
        	
        	String fileHash = FileDigest.computeFileChecksumString(file.getName());
        	String prueba = responseMessage.gethash();
        	if (fileHash.contains(prueba)) {
        		System.out.println("El fichero se ha descargado correctamente");
        	}
        	else {
        		System.out.println("El fichero no ha conseguido descargarse correctamente");
        	}
        }

        return downloaded;
    }

	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
