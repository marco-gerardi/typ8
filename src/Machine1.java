public class Machine1 extends Event{
	
	private double genExp;
	private ExponentialGenerator genExpG;
	private Job job;
	
	/* Costruttore della classe. Istanzia una macchina M1 */
	
	public Machine1(double mu1, long n) {
		super(Event.Fine_M1);
		setGenExpG(new ExponentialGenerator(n, mu1));
		//setCoda(new CodaFIFO("Coda " + super.getName()));
	}
	
	/* Ritorna il tempo di servizio per il Job corrente */
	public double getTimeServiceCentro(Job jobCurrent) {
		setJob(jobCurrent);
		genExp = getGenExpG().getNextExp();
		return genExp;
	}
	
	/* Indica se la coda per il centro M1 è vuota */
	/*public final boolean codaVuota() {
		return getCoda().isEmpty();
	}*/

	/* Estrae dalla coda un job */
	/*public final Job extract() {
		return getCoda().extract();
	}*/

	/* Inserisce un job nella coda */
	/*public final void push(Job job) {
		getCoda().push(job);
	}*/

	/* Reset stato del centro */
	/*public final void reset() {
		getCoda().resetCoda();
	}*/

	public ExponentialGenerator getGenExpG() {
		return genExpG;
	}

	public void setGenExpG(ExponentialGenerator genExpG) {
		this.genExpG = genExpG;
	}

	/*public CodaFIFO getCoda() {
		return coda;
	}

	public void setCoda(CodaFIFO coda) {
		this.coda = coda;
	}*/

	public double getGenExp() {
		return genExp;
	}

	public void setGenExp(double genExp) {
		this.genExp = genExp;
	}

	/* Restituisce il Job in esecuzione nel centro */
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}


}
