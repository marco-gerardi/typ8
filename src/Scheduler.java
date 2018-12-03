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
	private Machine M1, M2, M3, M4;
	private String FASE_SIMULAZIONE = "stabilizzazione";
    //boolean fineM = false;
    double NX;
    String NXMachine;
    int next; // contiene il prossimo evento prelevato dal calendario eventi
    private Job job1,job2,job3,job4,job5;
	
	public Scheduler() {
		clock = new Clock();
		T_oss=10;
		InizializzaGeneratori(0.8, 0.8, 0.4, 0.7, 0.3); // mu1, mu2, mu3, m4, p (per Hyperexp)
		InizializzaCode();
		calendar = new ArrayList<Event>(); // istanzio il calendar
		job1=new Job(1);// istanzio i job
		job2=new Job(2);
		job3=new Job(3);
		job4=new Job(4);
		job5=new Job(5);
		M1=new Machine("M1");
		M2=new Machine("M2");
		M3=new Machine("M3");
		M4=new Machine("M4");

		double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
		System.out.println(TM1);
		//addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		//addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
	}
	
	public ArrayList<Integer> run(int n) { // simulo un run dello scheduler
		//vettore osservazione Throughtput nel sistema
		ArrayList<Integer> Throughtput = new ArrayList<Integer>();
		// aggiungo i 5 job alla coda M1 - fase iniziale
		CodaM1Fifo.add(job1); 
		CodaM1Fifo.add(job2);
		CodaM1Fifo.add(job3);
		CodaM1Fifo.add(job4);
		CodaM1Fifo.add(job5);
		double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
		System.out.println(TM1);
		addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
		addEvent(new Event(Event.Fine_M2, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M3, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M4, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.FINESIM, Event.INFINITY )); // imposto Finesim a evento non prevedibile
		M1.setJob(CodaM1Fifo.remove(0)); // rimuovo job dalla coda M1 e lo metto dentro M1
		

        
        while (Throughtput.size()<n) {
        	// guardo il calendario e vedo qual'� il primo evento
        	next = calendar.get(0).getE_id();
        	clock.setSimTime(calendar.get(0).getE_time()); // aggiorno il clock
        	
        	
        	switch (next) {
        	case Event.Fine_M1:
        		simFineM1();
        		break;
        	case Event.Fine_M2:
        		// routine 
        		break;
        	case Event.Fine_M3:
        		// routine 
        		break;
        	case Event.Fine_M4:
        		// routine 
        		break;
        	case Event.OSSERVAZIONE:
        		Osservazione();
        		break;
        	case Event.FINESIM:
        		// routine 
        		break;

        	} 
        }
				
		return Throughtput; 	
//		System.out.println("Rnd: "+rg.getNextNumber());
//		System.out.println("RoutingM1Out: "+RoutingM1Out.getNextNumber());
//		System.out.println("RoutingM4Out: "+RoutingM4Out.getNextNumber());
//		System.out.println("Exp: "+C1.getNextExp());
//		System.out.println("HypC2: "+C2.getNextHyperExp());
//		System.out.println("HypC3: "+C3.getNextHyperExp());
//		System.out.println("K-Erl M4: "+C4.getNextNumber());
//		System.out.println("------------------------------");	
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

	private void simFineM1(){
		
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM1Out.getNextNumber();
		System.out.println(NX);
		if(NX >= 0.3) {// il job va verso il centro M2
			if (M2.statoLibero()) {
				M2.setJob(M1.getJob()); // occupo M2 col job che prima era in M1
				double TM2=C2.getNextHyperExp();// prevedo tempo di servizio  Tm2(j) 
				addEvent(new Event(Event.Fine_M2, clock.getSimTime() + TM2)); // prevedo il prox evento di fine M2
				System.out.println("TM2: "+TM2);
			}
			else {
				CodaM2Lifo.add(M1.getJob()); // inserisco job in coda M2
				System.out.println("Il job � stato inserito in codaM2");
			}
			 NXMachine = " vado verso Fine_M2";
         }else  {// il job va verso il centro M3
 			if (M3.statoLibero()) {
				M3.setJob(M1.getJob()); // occupo M3 col job che prima era in M1
				double TM3=C3.getNextHyperExp();// prevedo tempo di servizio  Tm3(j) 
				addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3
				System.out.println("TM3: "+TM3);
			}
			else {
				CodaM3Lifo.add(M1.getJob()); // inserisco job in coda M3
				System.out.println("Il job � stato inserito in codaM3");
			}
      	 
        	 NXMachine = "vado verso Fine_M3";
         }
		
		if (CodaM1Fifo.isEmpty()) {
			addEvent(new Event(Event.Fine_M1, Event.INFINITY )); // imposto M1 a evento non prevedibile
		}
		else {
			M1.setJob(CodaM1Fifo.remove(0)); // rimuovo job dalla coda M1 e lo metto dentro M1
			double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
			addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1			
		}
		 System.out.println("Route " + NXMachine); 
	} // fine evento FineM1
	
	private void Osservazione(){
		 calendar.remove(0);
		 //System.out.println("Osservazione");
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