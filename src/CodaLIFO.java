/**
 * La classe implementa una coda LIFO
*/

public class CodaLIFO extends Coda {

	public CodaLIFO(String nameCoda) {
		super(nameCoda);
	}

	/*Ritorna l'ultimo job entrato nella coda */
	@Override
	public Job extract() {
		Job pop = coda.remove(coda.size()-1);
		return pop;
	}

}
