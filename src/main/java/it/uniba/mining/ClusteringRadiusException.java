package it.uniba.mining;

/**
 * <p> Classe che estende la classe exception per gestire l'eccezione in cui il raggio e' minore di zero </p>
 */

@SuppressWarnings("serial")
public class ClusteringRadiusException extends Exception{
	/** 
	 * Costruttore stringa di exception 
	 * @param s Messaggio dell'eccezione
	 * */
	ClusteringRadiusException(String s){super(s);}
}
