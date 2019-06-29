package it.uniba.data;
/**
 * <p> Modella una  coppia del tipo (Attributo discreto, valore discreto) </p>
 */
class DiscreteItem extends Item {

	/**
	 * <p> Invoca il costruttore della classe madre </p> 
	 * @param attribute ricevuto in input di tipo attributo
	 * @param value ricevuto in input di tipo  stringa 
	 */
	DiscreteItem(DiscreteAttribute attribute, String value) {
		super(attribute, value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	double distance(Object a) {
		if(getValue().equals(((DiscreteItem)a).getValue()))
			return 0.0;
		else 
			return 1.0;
	}
}
