package it.uniba.mining;

import it.uniba.data.Tuple;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>Classe che modella un cluster </p>
 */
public class Cluster implements Iterable<Integer>, Comparable<Cluster>, Serializable {

	private final static long serialVersionUID = 1L;
	
	/**
	 * <p> Tupla rappresentante il centroide del cluster </p>
	 */
	private Tuple centroid;
	
	/**
	 * <p> Insieme di id delle tuple presenti nel cluster </p>
	 */
	private Set<Integer> clusteredData;
	
	
	/**
	 * <p> Confronta due Cluster </p>
	 * @param o Cluster da confrontare
	 * @return valore negativo se o e' maggiore del cluster attuale, altrimenti 1.
	 */
	public int compareTo(Cluster o) {
		if (this.getSize() < o.getSize()) {
			return -1;
		} else return 1;
	}
	
	/**
	 * <p>Costruttore della classe Cluster che inizializza il centroide con l'elemento avuto in input e inizializza clusteredData</p>
	 * @param centroid elemento di tipo Tuple
	 */
	Cluster(Tuple centroid){
		this.centroid=centroid;
		clusteredData=new HashSet<>();
	}
	
	/**
	 * <p>Restituisce il centroide letto</p>
	 * @return elemento di tipo tupla rappresentate il centroide
	 */
	Tuple getCentroid(){
		return centroid;
	}
	
	/**
	 * <p>Restituisce vero se la tupla sta cambiando il cluster</p>
	 * @param id intero ricevuto in input che rappresenta l'id della tupla da aggiungere a clusterdData
	 */
	void addData(int id){
		clusteredData.add(id);
	}

	/**
	 * <p>Restituisce la dimensione del clusterData</p>
	 * @return intero che e' la dimensione del clusterData
	 */
	int getSize(){
		return clusteredData.size();
	}
	
	/**
	 * <p>Restituisce l'iterator sugli elelementi di clusterData </p>
	 * @return iterator di oggetti integer
	 */
	public Iterator<Integer> iterator(){
		return clusteredData.iterator();		
	}
	
	
	/** Rappresenta un cluster sotto forma di stringa */
	@Override
	public String toString(){
		StringBuilder str= new StringBuilder("Centroid=(");
		for(int i=0; i<centroid.getLength(); i++)
			str.append(centroid.get(i));
		str.append(")");
		return str.toString();
	}
}
