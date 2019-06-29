package it.uniba.data;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.Iterator;

/** 
 *<p>Rappresenta un attributo discreto Categorico</p>
*/
public class DiscreteAttribute extends Attribute implements Iterable<String>{
	
	/** <p> TreeSet di oggetti String, uno per ciascun valore del dominio discreto.</p>
	 * <p> I valori del dominio sono memorizzati in values seguendo un ordine lessicografico.</p>
	 */
	private TreeSet<String> values;
	
	/**
	 * <p> Iteratore per ciclare sulle stringhe contenute in values. </p>
	 */
	public Iterator<String> iterator(){
		return values.iterator();
	}
	
	
	/**<p>Il metodo Discrete Attribute Invoca il costruttore della classe madre e </p>
	 * <p>inizializza il membro values con il parametro in input  </p> 
	 * @param name  e' il nome dell'attributo di tipo stringa
	 * @param index  e' un intero che rappresenta l'identificativo numerico dell'attributo
	 * @param values e' un TreeSet di stringhe rappresentante il dominio dell'attributo
	 * */
	DiscreteAttribute(String name, int index, String[] values) {
		super(name, index);
		this.values = new TreeSet<>();
		this.values.addAll(Arrays.asList(values));
	}
	
}
