package it.uniba.database;

import it.uniba.data.SaveFile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>Modella l'insieme di transazioni collezionate in una tabella</p>
 * <p>Ogni transazione e' modellata dalla classe {@link Example}</p>
*/
public class TableData {

	/**
	 * <p> Oggetto della classe DbAccess</p>
	 */
	private DbAccess db;

	/**
	 * <p>Inizializza il database attributo di classe con quello ricevuto in input</p>
	 * @param db database da cui recuperare le informazioni
	 */
	public TableData(DbAccess db) {
		this.db=db;
	}

	/**
	 * <p>Ricava lo schema della tabella con nome table</p>
	 * <p>Esegue un'interrogazione al database per estrarre le tuple distinte dalla tabella </p>
	 * <p>Per ogni tupla del resultset si crea un oggetto che e' istanza della classe  {@link Example}, </p>
	 * <p>cui riferimento va incluso nella lista da restituire. In particolare per la tupla corrente nel</p>
	 * <p>resultset si estraggonno e li si aggiungono all'oggetto istanza della classe  {@link Example} che si sta costruendo  </p>
	 * @param table Tabella da cui ricavare lo schema
	 * @return Lista di transazioni distinte memorizzate nella tabella
	 * @throws SQLException  {@link SQLException}
	 * @throws EmptySetException  {@link EmptySetException}
	 */
	public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException{
		List<Example> transSet = new ArrayList<>();
		TableSchema tSchema=new TableSchema(db,table);


		StringBuilder query = new StringBuilder("select distinct ");

		for(int i=0;i<tSchema.getNumberOfAttributes();i++){
			TableSchema.Column c=tSchema.getColumn(i);
			if(i>0)
				query.append(",");
			query.append(c.getColumnName());
		}
		if(tSchema.getNumberOfAttributes()==0)
			throw new SQLException();
		query.append(" FROM ").append(table);

		try (Statement statement = db.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query.toString())) {
			boolean empty = true;
			while (rs.next()) {
				empty=false;
				Example currentTuple=new Example();
				for(int i=0;i<tSchema.getNumberOfAttributes();i++)
					if(tSchema.getColumn(i).isNumber())
						currentTuple.add(rs.getDouble(i+1));
					else
						currentTuple.add(rs.getString(i+1));
				transSet.add(currentTuple);
			}
			if(empty) throw new EmptySetException();
		}

		return transSet;

	}
	/**
	 * <p>Formula ed esegue una interrogazione SQL per </p>
	 * <p>estrarre i valori distinti orinati del parametro column e popolare un insieme da restituire </p>
	 * @param table nome della tabella
	 * @param column nome della colonna
	 * @return insieme di valori distinti ordianati in modalita' ascendente che l'attributo identificato da nome column(parametro) assue nella tabella identificata dal nome table(parametro)
	 * @throws SQLException {@link Example}
	 */
	public Set<Object> getDistinctColumnValues(String table, TableSchema.Column column) throws SQLException{
		Set<Object> valueSet = new TreeSet<>();
		//TableSchema tSchema=new TableSchema(db,table);


		String query="select distinct ";
		query += column.getColumnName();
		query += (" FROM "+table);
		query += (" ORDER BY " +column.getColumnName());



		try (Statement statement = db.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				if (column.isNumber())
					valueSet.add(rs.getDouble(1));
				else
					valueSet.add(rs.getString(1));

			}
		}

		return valueSet;

	}
	/**
	 * <p>Formula ed esegue una interrogazione SQL per estrarre il valore aggregato (valore minimo o valore massimo) ercato nella colonna nome column della tabella di nome table</p>
	 * @param table Nome della tabella
	 * @param column nome della colonna
	 * @param aggregate operatore sql di aggregazione(min,max)
	 * @return Aggreato cercato  {@link QUERY_TYPE}
	 * @throws SQLException {@link SQLException}
	 * @throws NoValueException {@link NoValueException}
	 */
	public Object getAggregateColumnValue(String table, TableSchema.Column column, QUERY_TYPE aggregate) throws SQLException,NoValueException{
		Object value=null;
		String aggregateOp="";

		String query="select ";
		if(aggregate==QUERY_TYPE.MAX)
			aggregateOp+="max";
		else
			aggregateOp+="min";
		query+=aggregateOp+"("+column.getColumnName()+ ") FROM "+table;


		try (Statement statement = db.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query)) {
			if (rs.next()) {
				if (column.isNumber())
					value = rs.getFloat(1);
				else
					value = rs.getString(1);

			}
			if (value == null)
				throw new NoValueException("No " + aggregateOp + " on " + column.getColumnName());
		}
		return value;

	}

	/**
	 * <p> Metodo per ottenere i nomi delle tabelle dal database </p>
	 * @return Lista di nomi delle tabelle nel Database
	 * @throws SQLException 				{@link SQLException}
	 * @throws DatabaseConnectionException  {@link  DatabaseConnectionException}
	 */
	public List<String> tableNameList() throws SQLException {
		try (Statement statement = db.getConnection().createStatement();
		ResultSet result = statement.executeQuery("SHOW TABLES FROM " + DbAccess.DATABASE+";")) {
			List<String> tables = new ArrayList<>();
			while (result.next()) {
				tables.add(result.getString(1));
			}
			return tables;
		}
	}

	/**
	 * Recupera la lista dei salvataggi e crea la tabella se necessario
	 * @return una {@link List} contente i salvataggi
	 * @throws SQLException in caso di errori SQL
	 * @throws DatabaseConnectionException nel caso di errori durante l'inizializzazione della connessione
	 */
	public List<SaveFile> getAllSaves() throws SQLException, DatabaseConnectionException {
		if (tableNameList().contains("save")) {
			return readSaves();
		} else {
			String sql = "CREATE TABLE SAVE( ID INTEGER(9) AUTO_INCREMENT, FILE_NAME VARCHAR(30), DATA LONGBLOB, PRIMARY KEY(ID), UNIQUE(FILE_NAME))";
			try (PreparedStatement conn = DbAccess.getInstance().getConnection().prepareStatement(sql)) {
				conn.execute();
				return readSaves();
			}
		}
	}

	/**
	 * Legge i salvataggi presenti nel database
	 * @return una {@link List} contenente i salvataggi
	 */
	private List<SaveFile> readSaves() {
		String sql = "SELECT ID, FILE_NAME, DATA FROM SAVE";
		try (ResultSet rs =  db.getConnection().prepareStatement(sql).executeQuery()) {
			List<SaveFile> result = new ArrayList<>();
			while (rs.next()) {
				SaveFile file = new SaveFile();
				file.setId(rs.getBigDecimal(1));
				file.setFileName(rs.getString(2));
				file.setData(rs.getBlob(3));
				result.add(file);
			}

			return result;
		} catch (SQLException e) {
			return new ArrayList<>();
		}
	}

	/**
	 * Inserisce un nuovo salvataggio nel database
	 * @param file il salvataggio da salvare
	 * @param bytes l'oggetto da salvare
	 * @throws SQLException in caso di errore SQL
	 * @throws DatabaseConnectionException in caso di errori durante l'inizializzione della connessione
	 */
	public void insertSave(SaveFile file, byte[] bytes) throws SQLException, DatabaseConnectionException {
		String sql = "INSERT INTO SAVE (FILE_NAME,DATA) VALUES (?,?)";
		try (PreparedStatement pr = db.getConnection().prepareStatement(sql)) {
			pr.setString(1, file.getFileName());
			pr.setBytes(2, bytes);
			pr.execute();
		}
	}
}
