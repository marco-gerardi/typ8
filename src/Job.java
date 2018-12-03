public class Job {

	//dichiarazione variabile tempo di servizio e variabile indicante il momento della creazione del job
	private int id; 

	private double processingTime; 
	private double startServiceTime; 

	//Costruttori della classe
	public Job(int id, double processingTime, double startServiceTime)
	{
		this.id=id;
		this.processingTime = processingTime;
		this.startServiceTime = startServiceTime;
	}

	public Job(int id, double processingTime)
	{
		this(id,processingTime, 0);
	}

	public Job(int id)
	{
		this(id,0, 0);
	}

	//metodi get che restituiscono i valori delle variabili della classe
	public double getProcessingTime() { return processingTime; }
	public double getStartServiceTime() { return startServiceTime; }
	public int getId() { return id; }



	//metodi set che impostano i valori delle variabili della classe
	public void setProcessingTime(double processingTime) {this.processingTime = processingTime;}
	public void setStartServiceTime(double startServiceTime) {this.startServiceTime = startServiceTime;}
	public void setId(int id) { this.id = id; }
}
