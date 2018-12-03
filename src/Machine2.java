public class Machine2 extends Event{
	
	private double genHyper;
	private HyperExpGenerator hyperExpG;
	private CodaLIFO coda;
	private Job job;
	
	/* Costruttore della classe. Istanzia una macchina M2 */
	
	public Machine2(long seed1, long seed2, double mu2, double p) {
		super(Event.Fine_M2);
		setHyperGenG(new HyperExpGenerator(seed1,seed2,mu2,p));
		setCoda(new CodaLIFO("Coda " + super.getE_id()));
	}
	
	/* Ritorna il tempo di servizio per il Job corrente */
	public double getTimeServiceCentro(Job jobCurrent) {
		setJob(jobCurrent);
		genHyper = getHyperGenG().getNextHyperExp();
		return genHyper;
	}
	
	/* Indica se la coda per il centro M2 è vuota */
	public final boolean codaVuota() {
		return getCoda().isEmpty();
	}

	/* Estrae dalla coda un job */
	public final Job extract() {
		return getCoda().extract();
	}

	/* Inserisce un job nella coda */
	public final void push(Job job) {
		getCoda().push(job);
	}

	/* Reset stato del centro */
	public final void reset() {
		getCoda().resetCoda();
	}

	public HyperExpGenerator getHyperGenG() {
		return hyperExpG;
	}

	public void setHyperGenG(HyperExpGenerator hyperExpG) {
		this.hyperExpG = hyperExpG;
	}

	public CodaLIFO getCoda() {
		return coda;
	}

	public void setCoda(CodaLIFO coda) {
		this.coda = coda;
	}

	public double getGenHyper() {
		return genHyper;
	}

	public void setGenHyper(double genHyper) {
		this.genHyper = genHyper;
	}

	/* Restituisce il Job in esecuzione nel centro */
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}


}
