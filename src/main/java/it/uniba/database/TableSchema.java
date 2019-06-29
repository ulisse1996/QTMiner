package it.uniba.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Classe che costruisce lo schema della tabella nel database</p>
 */
public class TableSchema {

	/**
	 * <p> Oggetto della classe DbAccess</p>
	 */
	private DbAccess db;
	
	/**
	 * <p>Inner class che rappresenta una colonna della tabella</p>
	 */
	public class Column{
		
		/**
		 * <p> Nome della colonna</p>
		 */
		private String name;
		
		/**
		 * <p> Tipo contenuto della colonna </p>
		 */
		private String type;
		
		/**
		 * <p> Costruttore che inizializza i valori della classe </p>
		 * @param name nome colonna  
		 * @param type Tipo nella colonna
		 */
		private Column(String name,String type){
			this.name=name;
			this.type=type;
		}
		
		/**
		 * <p>Metodo get per il nome della colonna</p>
		 * @return Stringa che contiene il nome della colonna
		 */
		public String getColumnName(){
			return name;
		}
		
		/**
		 * <p> verifica se e' contenuto un numero</p>
		 * @return boolean che indica se il tipo della colonna e' un numero
		 */
		public boolean isNumber(){
			return type.equals("number");
		}
		
		@Override
		public String toString(){
			return name+":"+type;
		}
	}
	
	/**
	 * <p>Lista di colonne</p>
	 */
	private List<Column> tableSchema = new ArrayList<>();
	
	/**
	 * <p> Insieme usato per associare i tipi di mysql ai tipi java</p>
	 * @param db  Connessione al database tramite la classe {@link DbAccess}
	 * @param tableName Nome della tabella
	 * @throws SQLException {@link SQLException}
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException{
		this.db=db;
		HashMap<String,String> mapSQL_JAVATypes = new HashMap<>();

		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");

		 Connection con = this.db.getConnection();
		 DatabaseMetaData meta = con.getMetaData();
	     try (ResultSet res = meta.getColumns(null, null, tableName, null)) {

			 while (res.next()) {

				 if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
					 tableSchema.add(new Column(
							 res.getString("COLUMN_NAME"),
							 mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
					 );
			 }
		 }
	      
	    }
	  
		/**
		 * <p>Numero di colonne</p>
		 * @return Quantita' di attributi
		 */
		public int getNumberOfAttributes(){
			return tableSchema.size();
		}
		
		/**
		 * <p>Cerca una colonna nello schema della tabella</p>
		 * @param index Indice di colonna
		 * @return colonna in una determinata posizione
		 */
		public Column getColumn(int index){
			return tableSchema.get(index);
		}
		
}


