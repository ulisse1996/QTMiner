package it.uniba.database;

import java.util.ArrayList;
import java.util.List;

 /**
  * <p>Classe modellata per contenere momentaneamente una tupla</p>
  */
 public class Example implements Comparable<Example>{
	
	 /**
	 * <p> Lista di Object che rappresenta la tupla </p>
	 */
	private List<Object> example=new ArrayList<>();
	
	/**
	 * <p>aggiunge un oggetto alla tupla</p>
	 * @param o Object da aggiungere alla tupla
	 */
	void add(Object o){
		example.add(o);
	}
	
	/** 
	 * Getter per l'i-esimo valore della tupla
	 * @param i Intero rappresentante una posizione della lista
	 * @return Object contenuto in posizione i all'interno della lista
	 */
	public Object get(int i){
		return example.get(i);
	}
	

	/**
	 * <p>Confronta due tuple e ritorna un intero a seconda dell'esito del confronto</p>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compareTo(Example ex) {
		
		int i = 0;
		for(Object o : ex.example){
			if(!o.equals(this.example.get(i)))
				return ((Comparable)o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}
	
	/**
	 * <p>Stampa tutti gli elementi presenti in example  che e' una lista di Object</p>
	 * @return Stringa  rappresentante lo stato di ogni elemento in example
	 */
	@Override
	public String toString(){
		StringBuilder str= new StringBuilder();
		for(Object o:example)
			str.append(o.toString()).append(" ");
		return str.toString();
	}
	
}
