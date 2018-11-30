public class Job {

	//dichiarazione variabile tempo di servizio e variabile indicante il momento della creazione del job
	private double processingTime; 
	private double startServiceTime; 

	//Costruttori della classe
	public Job(double processingTime, double startServiceTime, double endServiceTime)
	{
		this.processingTime = processingTime;
		this.startServiceTime = startServiceTime;
	}

	public Job(double processingTime, double startServiceTime)
	{
		this(processingTime, startServiceTime, startServiceTime);
	}

	public Job()
	{
		this(0, 0, 0);
	}

	//metodi get che restituiscono i valori delle variabili della classe
	public double getProcessingTime() { return processingTime; }
	public double getStartServiceTime() { return startServiceTime; }

	//metodi set che impostano i valori delle variabili della classe
	public void setProcessingTime(double processingTime) {this.processingTime = processingTime;}
	public void setStartServiceTime(double startServiceTime) {this.startServiceTime = startServiceTime;}
}
