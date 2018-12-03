
/**
 * Classe astratta da cui vengono derivate le altre classi per le code dei centri.
 */

import java.util.ArrayList;

public abstract class Coda {
	
	public ArrayList<Job> coda = new ArrayList<Job>();
	public String nameCoda;
	
	public Coda(String nameCoda) {
		this.nameCoda = nameCoda;
	}
	
	/*Ritorna il nome della coda di un centro */	
	public String getName(){
		return nameCoda;
	}
	
	/*Ritorna la dimensione della coda di un centro */	
	public final int getSize() {
		return coda.size();
	}
	
	/*Ritorna true se la coda è vuota */	
	public final boolean isEmpty() {
		return coda.isEmpty();
	}
	
	/*Aggiunge un job nella coda */	
	public final void push(Job push){
		coda.add(push);
	}
	
	/*Rimuove tutti gli elementi dalla coda di un centro */	
	public final void resetCoda() {
		coda.clear();
	}
	
	/*Estrae un job dalla coda in base alla disciplina */	
	public abstract Job extract();

}
