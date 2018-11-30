public class Clock {
	private double simTime;

	//costruttore
	public Clock()
	{
		setSimTime(0.0);
	}

	//metodo che restituisce il valore del tempo di simulazione
	public double getSimTime()
	{
		return this.simTime;
	}

	//metodo che imposta il valore del tempo di simulazione
	public void setSimTime(double simTime)
	{
		this.simTime = simTime;
	}
}
