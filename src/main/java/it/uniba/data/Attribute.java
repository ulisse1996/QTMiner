package it.uniba.data;

import java.io.Serializable;

/**<p> Modella l'entita' attributo</p> */
public abstract class Attribute implements Serializable {

	private final static long serialVersionUID = 1L;
	
	/** <p> nome simbolico dell'attributo </p> */
	private String name;
	/** <p> identificativo numerico dell'attributo </p>*/
	private int index;
	
	/**<p>Questo metodo e' il costruttore della classe astratta Attribute</p>
	 * <p>Inizializza  i valori  name e index</p>
	 * @param name rappresenta l'input
	 * @param index rappresenta l'input
	 */
	Attribute(String name, int index) {
		this.name = name; 		
		this.index = index; 	
	}
	
	/**
	 * <p> Restituisce il nome dell'attributo </p>
	 * @return name e' il nome dell'attributo
	 */
	String getName() {
		return name;
	}

	
	/**
	 * <p>Rappresenta lo stato dell'oggetto</p>
	 * @return String rappresentante il nome e l'indice dell'attribute
	 */
	@Override
	public String toString() {
		return ("Name: " + name + "\nIndex: "+ index);
	}	
}
