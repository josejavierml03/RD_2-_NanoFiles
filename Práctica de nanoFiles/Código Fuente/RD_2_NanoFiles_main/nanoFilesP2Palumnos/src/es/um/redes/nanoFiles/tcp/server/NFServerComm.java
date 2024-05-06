package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	private boolean finL = false;
	private final static String MODE_READ = "r";
	
	/*
	 * DONE: Crear dis/dos a partir del socket
	 */
	/*
	 * DONE: Mientras el cliente esté conectado, leer mensajes de socket,
	 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
	 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
	 */
	/*
	 * DONE: Para servir un fichero, hay que localizarlo a partir de su hash (o
	 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
	 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
	 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
	 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
	 * devuelve la ruta al fichero a partir de su hash completo.
	 */
	
	public void serveFilesToClient(Socket socket) {
		
		DataInputStream dis;
		DataOutputStream dos;
		
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			while (!finL) {
				try {
					PeerMessage mensage = PeerMessage.readMessageFromInputStream(dis);
					PeerMessage respuesta = getRespuesta(mensage);
					respuesta.writeMessageToOutputStream(dos);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private PeerMessage getRespuesta(PeerMessage message) {
		PeerMessage response = null;
		switch (message.getOpcode()) {
		case PeerMessageOps.OPCODE_DOWNLOAD:{
			String hash = new String(message.gethash());
			int hashLength = message.getLength();
			byte[] data = new byte[hashLength];
			FileInfo[] matchingFile = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), hash );
			String filePath = matchingFile[0].filePath;
			try {
				RandomAccessFile fich = new RandomAccessFile(filePath, MODE_READ);
				fich.seek(0);
				fich.readFully(data);
				response = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_OK, hash, hashLength, data);
				fich.close();
				finL = true;
				return response;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;
		}
		case PeerMessageOps.OPCODE_TAMANO:{
			try {
				String hash = message.gethash();
				FileInfo[] fichs = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), hash);
				if(fichs.length == 1) {
					int tamano =(int) fichs[0].getFileSize();
					response = PeerMessage.messageTamOk(tamano);
					return response;
				}
			} catch (Exception e) {
				System.err.println("Existen 2 o más ficheros con el mismo hash. Solución: Introduzca más valores");
			}
			break;
		}
		default:
			System.err.println("Error en NFServerComm");
			break;
		}
		return response;
	}
}
