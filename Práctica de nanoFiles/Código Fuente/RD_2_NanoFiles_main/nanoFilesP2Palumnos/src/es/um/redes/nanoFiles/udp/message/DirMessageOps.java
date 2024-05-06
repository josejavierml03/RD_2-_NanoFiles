package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * DONE: Añadir aquí todas las constantes que definen los diferentes tipos de
	 * mensajes del protocolo de comunicación con el directorio.
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	
	public static final String OPERATION_LOGIN = "login";
	public static final String OPERATION_LOGIN_OK = "loginok";
	public static final String OPERATION_LOGIN_NOTOK = "login_fail";
	
	public static final String OPERATION_USERLIST = "userlist";
	public static final String OPERATION_USERLIST_OK = "userlistok";
	public static final String OPERATION_USERLIST_NOTOK = "userlist_fail";
	
	public static final String OPERATION_LOGOUT = "logout";
	public static final String OPERATION_LOGOUT_OK = "logoutok";
	public static final String OPERATION_LOGOUT_NOTOK = "logout_fail";
	
	public static final String OPERATION_BGSERVER = "bgserver";
	public static final String OPERATION_BGSERVER_OK = "bgserverok";
	public static final String OPERATION_BGSERVER_NOTOK = "bgserver_fail";
	
	public static final String OPERATION_DESBGSERVER = "desbgserver";
	public static final String OPERATION_DESBGSERVER_OK = "desbgserverok";
	public static final String OPERATION_DESBGSERVER_NOTOK = "desbgserver_fail";

}
