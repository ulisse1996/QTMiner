package it.uniba.data;

import it.uniba.database.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/** 
 * <p>Classe che modella un insieme di tuple </p>
 */
public class Data {

	/** <p>Lista di oggetti di tipo Example che modella le transazioni</p> */
	private List<Example> data = new ArrayList<>();
	/** <p>Cardinalita' dell'insieme di transazioni (numero di righe in data)</p> */
	private int numberOfExamples = 0;
	/** <p>Lista degli attributi in ciascuna tupla (schema della tabella di dati)</p> */
	private List<Attribute> explanatorySet;	
	
	
	/**
	* <p> Inizializza data e l'explanatorySet con la tabella il cui nome e' passato in input</p>
	* @param table nome tabella 
	* @throws EmptyDatasetException 		{@link EmptyDatasetException}
	* @throws SQLException 					{@link SQLException}
	* @throws NoValueException 				{@link NoValueException}
	* @throws EmptySetException 			{@link EmptySetException}
	* @throws DatabaseConnectionException 	{@link DatabaseConnectionException}
	*/
	public Data(String table) throws EmptyDatasetException, SQLException, NoValueException, EmptySetException, DatabaseConnectionException {
		explanatorySet = new ArrayList<>();
		try (DbAccess access = DbAccess.getInstance()) {
			TableData td = new TableData(access);
			TableSchema tSchema = new TableSchema(access, table);
			for (int i = 0; i < tSchema.getNumberOfAttributes(); i++) {
				TableSchema.Column currentColumn = tSchema.getColumn(i);
				if (currentColumn.isNumber()) {
					explanatorySet.add(
							new ContinuousAttribute(
									currentColumn.getColumnName(), i,
									(float) td.getAggregateColumnValue(table, currentColumn, QUERY_TYPE.MIN),
									(float) td.getAggregateColumnValue(table, currentColumn, QUERY_TYPE.MAX)));
				} else {
					Set<Object> colVal = td.getDistinctColumnValues(table, currentColumn);    // Valori assunti da un attributo nella tabella
					String[] values = new String[colVal.size()];
					int index = 0;
					for (Object o : colVal) {
						values[index++] = (String) o;
					}
					explanatorySet.add(new DiscreteAttribute(
							currentColumn.getColumnName(), i,
							values
					));
				}
			}

			for (Example e: td.getDistinctTransazioni(table)) {
				data.add(e);
				numberOfExamples++;
			}
		}

		if(getNumberOfExamples()==0){
			throw new EmptyDatasetException();
		} 
	}
	/**
	 * <p> Restituisce numberOfExamples </p> 
	 * @return cardinalit� dell'insieme di transazioni
	 */
	public int getNumberOfExamples(){
		return numberOfExamples;
	}
	/**
	 * <p> Restituisce la dimensione di explanatorySet</p>
	 * @return cardinalit� dell'insieme degli attributi
	 */
	public int getNumberOfExplanatoryAttributes(){
		return explanatorySet.size();
	}
	
	/**
	 * <p> Restituisce il valore di un attributo di una certa tupla  </p>
	 * @param exampleIndex rappresenta la tupla da cui prendere il valore dell'attributo
	 * @param attributeIndex rappresenta l'attributo
	 * @return Object restituisce il valore di un attributo di una certa tupla
	 */
	public Object getAttributeValue(int exampleIndex, int attributeIndex){
		return data.get(exampleIndex).get(attributeIndex);
	}


	/**
	 * <p> Restituisce l'explanatoryset </p> 
	 * @return restituisce il riferimento a explanatorySet
	 */
  	public List<Attribute> getAttributeSchema(){
		return explanatorySet;
	}
	
	/**
	 *<p>Popola un {@link Tuple} a partire dagli elementi presenti nell'explanatorySet </p>
	 * @param index intero ricevuto in input rappresentate l'indice di riga
	 * @return  {@link Tuple} 
	 */
	public Tuple getItemSet(int index) {
		Tuple tuple = new Tuple(explanatorySet.size()); 
		int i = 0;
		for (Attribute a:explanatorySet) {
			if (a instanceof DiscreteAttribute) 
				tuple.add(new DiscreteItem((DiscreteAttribute)a, (String)data.get(index).get(i)), i);
			else 
				tuple.add(new ContinuousItem((ContinuousAttribute)a, (double)data.get(index).get(i)), i);
			i++;
		}
		return tuple;
	}
	
	/**
	 * <p>Stampa tutti gli attribute dell'explanatorySet </p>
	 */
	@Override
	public String toString(){	
		StringBuilder s = new StringBuilder();

		for (Attribute a:explanatorySet) {
			s.append(" ").append(a.getName());
		}
		
		s.append('\n');
		
		for (int i = 0; i < numberOfExamples; i++) {
			s.append(i + 1);
			for (int j = 0; j < explanatorySet.size(); j++) {
				s.append(" ").append(data.get(i).get(j));
			}
			s.append('\n');
		}
		return s.toString();
	}
}
