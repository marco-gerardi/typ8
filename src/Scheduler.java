import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	private Clock clock;
	private double T_oss; // intervallo di osservazione Delta T
	RandomGenerator rg;
	UniformGenerator RoutingM1Out, RoutingM4Out; // Routing job uscenti dal centro 1 e dal centro 4
	ExponentialGenerator C1; // Esponenziale per il centro 1
	KErlangGenerator C4; // Generatore 2-Elang per il tempo di servizio del centro 4
	HyperExpGenerator C2, C3;
	private List<Job> CodaM1Fifo, CodaM2Lifo, CodaM3Lifo, CodaM4Sptf;
	private List<Event> calendar;
	private boolean occupatoC1;
	private boolean occupatoC2;
	private boolean occupatoC3;
	private boolean occupatoC4;
	
	public Scheduler() {
		clock = new Clock();
		T_oss=10;
		InizializzaGeneratori(0.8, 0.8, 0.4, 0.7, 0.3); // mu1, mu2, mu3, m4, p (per Hyperexp)
		InizializzaCode();
		calendar = new ArrayList<Event>(); // istanzio il calendar
		
		double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
		addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
	}
	
	public ArrayList<Integer> run() { // simulo un run dello scheduler
		ArrayList<Integer> Throughtput = new ArrayList<Integer>();
		
		System.out.println("Rnd: "+rg.getNextNumber());
		System.out.println("RoutingM1Out: "+RoutingM1Out.getNextNumber());
		System.out.println("RoutingM4Out: "+RoutingM4Out.getNextNumber());
		System.out.println("Exp: "+C1.getNextExp());
		System.out.println("HypC2: "+C2.getNextHyperExp());
		System.out.println("HypC3: "+C3.getNextHyperExp());
		System.out.println("K-Erl M4: "+C4.getNextNumber());
		System.out.println("------------------------------");	
		
				
		return Throughtput; 	
	}

	private void InizializzaGeneratori(double mu1, double mu2, double mu3, double mu4, double p) {
		rg= new RandomGenerator();
		RoutingM1Out=new UniformGenerator(5, 0, 1);// seme=5
		RoutingM4Out=new UniformGenerator(7, 0, 1);// seme=7
		C1=new ExponentialGenerator(5, mu1);// seme=5, mu=0.4
		C2= new HyperExpGenerator(5, 7, mu2, p );
		C3= new HyperExpGenerator(7, 5, mu3, p );
		C4=new KErlangGenerator(mu4);
	}
	
	private void InizializzaCode() {
		CodaM1Fifo = new ArrayList<Job>();
		CodaM2Lifo = new ArrayList<Job>();
		CodaM3Lifo = new ArrayList<Job>();
		CodaM4Sptf = new ArrayList<Job>();
	}

	
	// aggiungo un evento al calendario e lo ordino per tempo di clock
	private void addEvent(Event newEvent)
	{
		calendar.add(newEvent);

		if (calendar.size() > 1) {
			Collections.sort(calendar, new Comparator<Event>() {
				public int compare(Event event1, Event event2) {
					return Double.compare(event1.getE_time(), event2.getE_time());
				}
			});
		}
	}
	
	// aggiungo un job alla coda SPTF e li ordino in base al tempo di processamento
	private void addJobToSPTF(Job newJob)
	{
		CodaM4Sptf.add(newJob);

		if (CodaM4Sptf.size() > 1) {
			Collections.sort(CodaM4Sptf, new Comparator<Job>() {
				public int compare(Job job1, Job job2) {
					return Double.compare(job1.getProcessingTime(), job2.getProcessingTime());
				}
			});
		}
	}
	

}