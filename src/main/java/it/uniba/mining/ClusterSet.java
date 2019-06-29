package it.uniba.mining;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;

/**
 * <p>Rappresenta un insieme di cluster (determinati da QT)</p>
 */
public class ClusterSet implements Iterable<Cluster> , Serializable {

	private final static long serialVersionUID = 1L;
	
	/**
	 * <p> Set contenete elementi di tipo {@link Cluster}</p>
	 */
	private Set<Cluster> C = new TreeSet<>();

	/**
	 *<p> Aggiunge un cluster al clusterSet C </p>
	 * @param c Elemento di tipo Cluster da aggiungere al ClusterSet C
	 */
	void add(Cluster c) {

		Set<Cluster> tempC = new TreeSet<>(C);
		
		tempC.add(c); 
		C = tempC; 
	}
	
	/**
	 * <p> Restituisce iteratore per la classe ClusterSet </p>
	 */
	public Iterator<Cluster> iterator() {
		return C.iterator();
	}

	/** Rappresenta un ClusterSet sotto forma di stringa */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		int i = 1;
		for (Cluster cluster:C) {
			if (cluster != null)
				s.append(i).append("@#").append(cluster.getCentroid()).append("\n");
			i++;
		}
		return s.toString();
	}

}
