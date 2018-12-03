public class Machine {
	private String idMachine;
	private Job job;
	
	
	/***
	 * Costruttore oggetto Machine
	 * @param idMachine = identificativo della Machine
	 */
	public Machine (String idMachine){ this.idMachine = idMachine; }
	
	/***
	 * Controlla se la machine non contiene job al suo interno
	 * @return	true se è libero, false altrimenti
	 */
	public boolean statoLibero(){
		if(job != null){ return false; }
		return true;
	}
	
	//Rimuove il job dalla machine
	 
	public void rimuoviJob(){ job = null; }
	
	/***
	 * Assegna il job al processore
	 * @param job = job da assegnare
	 */
	public void setJob(Job job){ this.job=job; }
	public String getIdMachine() { return idMachine; }
	public void setIdProcessore(String idMachine) { 	this.idMachine = idMachine; }
	public Job getJob() { return job; 	}
}