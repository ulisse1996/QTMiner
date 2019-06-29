package it.uniba.data;
import java.io.Serializable;
import java.util.Set;
/**
 * <p>Modella una Tupla come un array di {@link Item}</p>
 */
public class Tuple implements Serializable {

	private final static long serialVersionUID = 1L;

	/**
	 * <p>Array contenente {@link Item}</p>
	 */
	private Item [] tuple;

	/**
	 * <p>Costruisce l'oggetto riferito da tuple<p>
	 * @param size numero di item che costituir� la tupla
	 */
	Tuple(int size) {
		tuple = new Item[size];
	}
	
	/**
	 * <p>Resituisce la lunghezza dell'array tuple </p>
	 * @return intero che rappresenta la lunghezza array : tuple
	 */
	public int getLength() {
		return tuple.length;
	}
	
	/**
	 * <p> Restituisce l'item in posizione i-esima </p>
	 * @param i  e' di tipo intero ricevuto in input per indicare la posizione nell'array : tuple
	 * @return elemento di tipo item in una posizione ricevutao in input di tipo intero
	 */
	public Item get(int i) {
		return tuple[i];
	}
	
	/**
	 * <p> Memorizza l'item "c" in posizione i dell'array tuple 
	 * @param c elemento di tipo item da memorizzare in tuple
	 * @param i di tipo intero che indica la posizione in cui memorizzare l'elemento in tuple
	 */
	void add(Item c, int i) {
		if (i >= 0 && i < getLength()) {//Verifica che la posizione sia valida
			tuple[i] = c;
		}
	}
	/**
	 * <p>Determina la distanza tra la tupla riferita da obj e la tupla corrente (riferita da this). La distanza � ottenuta come la somma delle distanze tra gli item in posizioni eguali nelle due tuple</p>
	 * @param obj elemento ricevuto in input di tipo Tuple
	 * @return totDistance distanza da una tupla ad un altra
	 */ 
	public double getDistance(Tuple obj) {
		double totDistance = 0.0;
		if (this.getLength() == obj.getLength()) {
			for (int i = 0; i < this.getLength(); i++) {
				totDistance += get(i).distance(obj.get(i));	// Somma le distanze 
			}
		}
		return totDistance;
	}
	/**
	 * <p> Calcola la media delle distanze tra la tupla corrente e quelle ottenibili dalle righe della matrice data</p>
	 * @param data elemento ricevuto in input di tipo Data 
	 * @param clusteredData array di interi 
	 * @return double rappresentante  la media delle distanze tra la tupla corrente e quelle ottenibili dalle righe della matrice in data aventi indice in clusteredData
	 */
	public double avgDistance(Data data, Set<Integer> clusteredData) {
		double p = 0.0, sumD = 0.0;
		for (Integer i:clusteredData) {
			double d = getDistance(data.getItemSet(i));
			sumD += d;
		}
		
		p = sumD / clusteredData.size();
		return p;
	}
	
	/**
	 * <p>Stampa tutte gli {@link Item} presenti nell'array</p>
	 */
	@Override
	public String toString() {
		String s = "";
		for (Item i:tuple) {
			s += i.toString() + " ";
		}
		return s;
	}
	
}
