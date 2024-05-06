package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class PeerMessage {

	private byte opcode;

	/*
	 * DONE: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */
	
    private String hash;
    private int length;
    private byte[] data;

	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
	public PeerMessage(byte opcode, String hash, int length) {
        this.opcode = opcode;
        this.hash = hash;
        this.length = length;
    }

    public PeerMessage(byte opcode, String hash,int length, byte[] data) {
        this.opcode = opcode;
        this.hash = hash;
        this.length = length;
        this.data = data;
    }
    
    public static PeerMessage messageTam(String hash) {
    	PeerMessage pm = new PeerMessage(PeerMessageOps.OPCODE_TAMANO);
    	pm.setHash(hash);
    	return pm;
    }
    
    public static PeerMessage messageTamOk(int length) {
    	PeerMessage pm = new PeerMessage(PeerMessageOps.OPCODE_TAMANO_OK);
    	pm.setLength(length);
    	return pm;
	}

	/*
	 * DONE: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	
	
	public byte getOpcode() {
		return opcode;
	}

	public void setOpcode(byte opcode) {
		this.opcode = opcode;
	}

	public String gethash() {
		return this.hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getLength() {
	    if (opcode == PeerMessageOps.OPCODE_DOWNLOAD || opcode == PeerMessageOps.OPCODE_DOWNLOAD_OK || opcode == PeerMessageOps.OPCODE_TAMANO_OK) {
	    	return length;
	    }else {
	    throw new IllegalStateException("Error en Peermessage: Longitud no definida");
	    }
	}

	public void setLength(int length) {
		 if (opcode == PeerMessageOps.OPCODE_DOWNLOAD || opcode == PeerMessageOps.OPCODE_DOWNLOAD_OK || opcode == PeerMessageOps.OPCODE_TAMANO_OK) {
	        this.length = length;
	    }else {
		 throw new IllegalStateException("Error en Peermessage: No se ha podido establecer la longitud");
	    }
	}

	public byte[] getData() {
	    if (opcode == PeerMessageOps.OPCODE_DOWNLOAD_OK) {
	    	return data;
	    }else {
	    throw new IllegalStateException("Error en Peermessage: Datos no definidos");
	    }
	}

	public void setData(byte[] data) {
		if (opcode == PeerMessageOps.OPCODE_DOWNLOAD_OK) {
	        this.data = data;
	    }else {
		throw new IllegalStateException("Error en Peermessage:  No se han podido establecer los datos");
	    }
	}


	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * DONE: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		try {
			message.opcode = dis.readByte();
			switch (message.opcode) {
			
			case PeerMessageOps.OPCODE_DOWNLOAD:
				message.setHash(dis.readUTF());
				message.setLength(dis.readInt());
	            break;
	        case PeerMessageOps.OPCODE_DOWNLOAD_OK:
	        	message.setHash(dis.readUTF());
	        	message.setLength(dis.readInt());
	        	message.setData(new byte[message.getLength()]);
	        	dis.readFully(message.data);
	            break;
	        case PeerMessageOps.OPCODE_TAMANO:
	        	message.setHash(dis.readUTF());
	        	break;
	        case PeerMessageOps.OPCODE_TAMANO_OK:
	        	message.setLength(dis.readInt());
	        	break;

			default:
				System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
						+ PeerMessageOps.opcodeToOperation(message.opcode));
				System.exit(-1);
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;	
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * DONE: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {

		case PeerMessageOps.OPCODE_DOWNLOAD:
			dos.writeUTF(hash);
			dos.writeInt(length);
            break;
        case PeerMessageOps.OPCODE_DOWNLOAD_OK:
        	dos.writeUTF(hash);
            dos.writeInt(length);
            dos.write(data, 0, length);
            break;
        case PeerMessageOps.OPCODE_TAMANO:
        	dos.writeUTF(hash);
        	break;
        case PeerMessageOps.OPCODE_TAMANO_OK:
        	dos.writeInt(length);
        	break;
		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}

}