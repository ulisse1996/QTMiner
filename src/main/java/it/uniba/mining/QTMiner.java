package it.uniba.mining;

import it.uniba.data.Data;
import it.uniba.data.SaveFile;
import it.uniba.data.Tuple;
import it.uniba.database.DatabaseConnectionException;
import it.uniba.database.DbAccess;
import it.uniba.database.TableData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

/**
 * <p>Classe che include l'implementazione dell'algoritmo QT</p>
 */
public class QTMiner {
	
	/**
	 * <p> Elemento clusterSet di tipo ClusterSet </p>
	 */
	private ClusterSet clusterSet;
	
	/**
	 * <p> Rappresenta il raggio dei cluster </p>
	 */
	private double radius;
	
	/**
	 * <p>Crea l'oggetto ClusterSet riferito da clusterSet e inizializza radius con il parametro passato come input</p>
	 * @param radius rappresenta il  raggio dei cluster di tipo double
	 */
	public QTMiner(double radius) {
		clusterSet = new ClusterSet();
		this.radius = radius;
	}

	/**
	 * Crea una nuova istanza utilizando un {@link ClusterSet} recuperato da un salvataggio
	 * @param file da cui leggere il {@link ClusterSet}
	 * @throws IOException nel caso di errori durante la deserializzazione del {@link ClusterSet}
	 */
	public QTMiner(SaveFile file) throws IOException {
		try (ObjectInputStream ois = new ObjectInputStream(file.getData().getBinaryStream())){
			clusterSet = (ClusterSet) ois.readObject();
		} catch (ClassNotFoundException | SQLException ex) {
			// Never Happen
		}
	}

	/**
	 * <p>Restituisce il {@link ClusterSet}</p>
	 * @return clusterSet
	 */
	public ClusterSet getClusterSet() {
		return clusterSet;
	}
	/**
	 * <p>Esegue il seguente algoritmo </p>
	 * <p>1. Costruisce un cluster per ciascuna tupla non ancora clusterizzata, includendo nel cluster i punti (non ancora clusterizzati in alcun altro cluster) che ricadano nel vicinato sferico della tuple avente raggio radius</p>
	 * <p>2. Salva il candidato cluster piu' popoloso e rimuove tutti punti di tale cluster dall'elenco delle tuple ancora da clusterizzare</p>
	 * <p>3. Ritorna al passo 1 finche' ci sono ancora tuple da assegnare ad un cluster</p>
	 * @param data Insieme di tuple
	 * @throws ClusteringRadiusException {@link ClusteringRadiusException}
	 */
	public void compute(Data data) throws ClusteringRadiusException {
		int numClusters = 0;
		boolean[] isClustered = new boolean[data.getNumberOfExamples()];	// IsClustered e' un vettore grande quanto il numero di tuple in data
		for (int i = 0; i < isClustered.length; i++) 	// Vengono settati a falso
			isClustered[i] = false;
		int countClustered = 0; 
		while(countClustered != data.getNumberOfExamples()) {
			// Ricerca cluster piu' popoloso
			Cluster c = buildCandidateCluster(data, isClustered);
			clusterSet.add(c);
			numClusters++;
			
			// Rimuove Tuple clusterizzate da dataset
			for (Integer i:c) {
				isClustered[i] = true;
			}
			countClustered += c.getSize();
		}
		if (numClusters==1)
			throw new ClusteringRadiusException("Only 1 cluster");
	}
	
	/**
	 * <p>Costruisce un cluster per ciascuna tupla di data non ancora clusterizzata in un cluster di clusterSet e restituisce il cluster candidato piu' popoloso</p>
	 * @param data insiene di tuple da raggruppare in cluster
	 * @param isClustered informazione booleana sullo stato di clusterizzazione di una tupla (per esempio isClustered[i]=false se la tupla i-esima di data non e' ancora assegnata ad alcun cluster di clusterSet, true altrimenti)
	 * @return Cluster piu' popoloso
	 */
	private Cluster buildCandidateCluster(Data data, boolean[] isClustered) {
		Cluster candidate = null;
		for (int i = 0; i < data.getNumberOfExamples(); i++) {
			if (isClustered[i]) continue;				
			Tuple t = data.getItemSet(i);
			Cluster c = new Cluster(t);
			for (int j = 0; j < data.getNumberOfExamples(); j++) {
				if (isClustered[j]) 
					continue;
				Tuple t2 = data.getItemSet(j);
				if (t.getDistance(t2) <= radius) {
					c.addData(j);
				}
			}
			if (candidate == null)	candidate = c;
			else if (c.getSize() > candidate.getSize()) candidate = c;
		}
		return candidate;
	}

	/**
	 * <p>Chiama il toString di {@link ClusterSet} per clusterSet</p>
	 * @return Cluster 
	 */	@Override
	public String toString(){
		return clusterSet.toString();
	}

	/**
	 * Salva la computazione in un file nella table "Save" del Database
	 * @param file istanza del salvataggio da salvare
	 * @throws IOException nel caso di errore durante la serializzazione
	 * @throws SQLException nel caso di errori SQL
	 * @throws DatabaseConnectionException nel caso di errori durante la connessione al Database
	 */
	public void salva(SaveFile file) throws IOException, SQLException, DatabaseConnectionException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 ObjectOutputStream stream = new ObjectOutputStream(bos);
			 DbAccess access = DbAccess.getInstance()){
			stream.writeObject(clusterSet);
			stream.flush();
			new TableData(access).insertSave(file, bos.toByteArray());
		}
	}
}
