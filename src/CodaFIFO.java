
/**
 * La classe implementa una coda FIFO
 */

public class CodaFIFO extends Coda {

	public CodaFIFO(String nameCoda) {
		super(nameCoda);
	}
	
	/*Ritorna l'ultimo job entrato nella coda */
	@Override
	public Job extract() {
		Job pop = coda.remove(0);
		return pop;
	}
}