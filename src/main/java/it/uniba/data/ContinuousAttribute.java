package it.uniba.data;
/**
 * <p>Modella un attributo continuo.</p>
 * <p> Essa include i metodi per la normalizzazione del dominio dell'attributo nell'intervallo [0,1] al fine da rendere confrontabili attributi aventi domini diversi </p>
 * */
class ContinuousAttribute extends Attribute {
	
	/** <p>rappresenta il secondo estremo dell'intervallo </p>*/
	private double max;
	/** <p>rappresenta il primo  dell'intervallo </p>*/
	private double min; 
	
	/** 
	 * <p> Setta gli intervalli del dominio  avendo precedentemente invocato il costruttore della superclasse</p>
	 * @param name  rappresenta il nome dell'attributo ricevuto in input di tipo stringa
	 * @param index  rappresenta l' identificativo numerico ricevuto in input di tipo intero
	 * @param min  rappresenta il primo  estremo dell'intervallo ricevuto in input di tipo double
	 * @param max rappresenta il secondo estremo dell'intervallo ricevuto in input di tipo double
	 */
	ContinuousAttribute(String name, int index, double min, double max) {
		super(name, index); 
		this.max = max;
		this.min = min;
	}
	/**
	 * <p> Calcola e restituisce il valore normalizzato del parametro passato in input. La normalizzazione ha come codominio l'intervallo [0,1].</p>
	 * <p>la normalizzazione avviene usando questa formula : <b> v'=(v-min)/(max-min) </b> </p>
	 * @param v e' il valore dell'attributo da normalizzare
	 * @return il valore normalizzato
	 */
	double getScaledValue(double v) {
		return (v-min)/(max-min);
	}
	
}
