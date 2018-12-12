import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	private Clock clock;
	private double T_oss; // intervallo di osservazione Delta T
	private double Dt = 2.8; //intervallo tra due osservazioni
	RandomGenerator rg;
	UniformGenerator RoutingM1Out, RoutingM2Out, RoutingM4Out; // Routing job uscenti dal centro 1 e dal centro 4
	ExponentialGenerator C1; // Esponenziale per il centro 1
	KErlangGenerator C4; // Generatore 2-Elang per il tempo di servizio del centro 4
	HyperExpGenerator C2, C3;

	private List<Job> CodaM1Fifo, CodaM2Lifo, CodaM3Lifo, CodaM4Sptf;
	private List<Event> calendar;
	private Machine M1, M2, M3, M4;
	private String FASE_SIMULAZIONE = "stabilizzazione";
	private int NJobOut; // num di job che escono lungo la linea GammaOut
    //boolean fineM = false;
    double NX;
    String NXMachine;
    int next; // contiene il prossimo evento prelevato dal calendario eventi
   // private Job job1,job2,job3,job4,job5, job6, job7, job8, job9, job10, job11, job12;
    
    double th = 0.0;
    private ArrayList<Job> job = new ArrayList<Job>();
    private ArrayList<Double> array_oss = new ArrayList<Double>();
    
	
	
	public Scheduler() {
		clock = new Clock();
		T_oss=Dt;
		InizializzaGeneratori(0.8, 0.8, 0.4, 0.7, 0.3); // mu1, mu2, mu3, m4, p (per Hyperexp)
		InizializzaCode();
		calendar = new ArrayList<Event>(); // istanzio il calendar
		// istranzio i job
		
		for(int i=1;i<=12;i++){
			job.add(new Job(i));
		}
		// istanzio i centri di servizio
		M1=new Machine("M1");
		M2=new Machine("M2");
		M3=new Machine("M3");
		M4=new Machine("M4");
		
		double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
		//System.out.println(TM1);
		//addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		//addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
		// metto i 12 job in coda M1
		
		// aggiungo i 12 job alla coda M1 - fase iniziale
		for(int i=0;i<12;i++){
			CodaM1Fifo.add(job.get(i)); 
		}
		
		addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
		addEvent(new Event(Event.Fine_M2, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M3, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M4, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.FINESIM, Event.INFINITY )); // imposto Finesim a evento non prevedibile
	}
	
	public ArrayList<Double> run(int n) { // simulo un run dello scheduler di lunghezza n passato come parametro
		//vettore osservazione Throughtput nel sistema
		ArrayList<Double> Throughtput = new ArrayList<Double>();

		
        while (Throughtput.size()<n) {
        	//System.out.println(Throughtput.get(n));
        	// guardo il calendario e vedo qual'è il primo evento
        	next = calendar.get(0).getE_id();
        	clock.setSimTime(calendar.get(0).getE_time()); // aggiorno il clock
        	System.out.println("$$$$$$$$$$ CLOCK: "+clock.getSimTime());
        	
        	switch (next) {
        	case Event.Fine_M1:
        		simFineM1();
        		break;
        	case Event.Fine_M2:
        		simFineM2(); 
        		break;
        	case Event.Fine_M3:
        		simFineM3();
        		break;
        	case Event.Fine_M4:
        		simFineM4();
        		break;
        	case Event.OSSERVAZIONE:
        		Throughtput = Osservazione();
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
	

	private void simFineM4(){
		System.out.println("***** Eseguo Routine Fine M4");
		//clock.setSimTime(calendar.get(0).getE_time());
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM4Out.getNextNumber();
		System.out.println("Routing: "+NX);
		if(NX < 0.1) {// il job va verso il centro M3
			if (M3.statoLibero()) {
				//System.out.println("ID JOB FINE M4: "+M4.getJob().getId());
				M3.setJob(M4.getJob()); // occupo M3 col job che prima era in M4
				M4.rimuoviJob();
				double TM3=C3.getNextHyperExp();// prevedo tempo di servizio  Tm3(j) 
				addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3
				System.out.println("Messo iljob da fine M4 in M3");
			}
			else {
				CodaM3Lifo.add(M4.getJob()); // inserisco job in coda M3 con disciplina lifo
				M4.rimuoviJob();
				System.out.println("Il job è stato inserito in codaM3");
			}
			 
         }else  {// il job va verso il centro M1
        	NJobOut++; // incremento la variabile NJobOUT
        	System.out.println("NJobOut: "+NJobOut);
 			if (M1.statoLibero()) {
 				//System.out.println("ID JOB FINE M4: "+M4.getJob().getId());
				M1.setJob(M4.getJob()); // occupo M1 col job che prima era in M4
				M4.rimuoviJob();
				double TM1=C1.getNextExp(); // prevedo tempo di servizio  Tm1(j) 
				addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
				System.out.println("Messo iljob da fine M4 in M1");
			}
			else { // lo metto in coda M1 con disciplina fifo
				//System.out.println("ID JOB FINE M4: "+M4.getJob().getId());
				//int idTmpM4 = M4.getJob().getId();
				CodaM1Fifo.add(M4.getJob()); // inserisco job in coda M1
				M4.rimuoviJob();
				System.out.println("Il job è stato inserito in codaM1");
			}
         }
		System.out.println("Mi guardo alle spalle e verifico se la coda M4 è vuota");
		if (CodaM4Sptf.isEmpty()) {
			addEvent(new Event(Event.Fine_M4, Event.INFINITY )); // imposto M4 a evento non prevedibile
			System.out.println("la coda M4 è vuota e imposto a infinito il tempo di servizio di M4");
		}
		else {
			System.out.println("la coda M4 NON è vuota, prelevo un job dalla coda SPTF");
			Job tmp=new Job();
			tmp=CodaM4Sptf.get(CodaM4Sptf.size() - 1);
			M4.setJob(tmp); // rimuovo job dalla coda M4 e lo metto dentro M4
			double TM4 =tmp.getProcessingTime() ; // prelevo il tempo di servizio dal job rimosso dalla coda
			addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
			System.out.println("Il tempo di servizio del job prelevato dalla coda SPTF: "+TM4);
		}
		System.out.println("***** FINE Routine Fine M4");
		 //System.out.println("Route " + NXMachine); 
	} // fine evento FineM4
	
	
	
	
	
	private void simFineM3() {
		System.out.println("***** Eseguo Routine Fine M3");
		//clock.setSimTime(calendar.get(0).getE_time());
		calendar.remove(0); // rimuovo l'evento dal calendario
		NXMachine = "M3";
		if (M4.statoLibero()) {
			M4.setJob(M3.getJob()); // occupo M4 col job che prima era in M3
			M3.rimuoviJob();
			double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j) 
			addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
			System.out.println("Il job è stato inserito in M4");
		}
		else { // metto in coda con disciplina SPTF
			double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j)
			Job tmp=new Job();
			tmp.setProcessingTime(TM4);
			addJobToSPTF(tmp); // inserisco job in coda M4 e lo ordino
			M3.rimuoviJob(); // rimuovo il job da M3
			System.out.println("Il job è stato inserito in codaM4 SPTF");
			System.out.println("Il job è stato inserito in codaM4 SPTF, il tempo di servizio è: " +TM4);
		}
		System.out.println("Mi guardo alle spalle e verifico se la coda M3 è vuota");
		if (CodaM3Lifo.isEmpty()) {
			//M3.setJob(null);
			addEvent(new Event(Event.Fine_M3, Event.INFINITY )); // imposto M3 a evento non prevedibile
			System.out.println("la coda M3 è vuota e imposto a infinito il tempo di servizio di M3");
		}
		else {
			M3.setJob(CodaM3Lifo.remove(CodaM3Lifo.size()-1)); // rimuovo job dalla coda M3 e lo metto dentro M3
			double TM3 = C3.getNextHyperExp(); // genero il tempo di servizio del centro M3
			addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3	
			System.out.println("la coda M3 NON è vuota, prelevo un job dalla coda");
		}
		System.out.println("***** FINE Routine Fine M3");

    } // fine evento FineM3 

	
	
	private void simFineM2() {
		System.out.println("***** Eseguo Routine Fine M2");
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM2Out.getNextNumber();
		if(NX >= 0.5) {// il job va verso il centro M1
			if (M1.statoLibero()) {
				//System.out.println("ID JOB FINE M2: "+M2.getJob().getId());
				M1.setJob(M2.getJob()); // occupo M1 col job che prima era in M2
				M2.rimuoviJob();
				double TM1=C1.getNextExp();// prevedo tempo di servizio  Tm1(j) 
				addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
				System.out.println("Il job è stato prelevato da M2 e inserito in M1");
			}
			else {
				CodaM1Fifo.add(M2.getJob()); // inserisco job in coda M1 con disciplina FiFo
				M2.rimuoviJob();
				System.out.println("Il job è stato inserito in codaM1");
			}
         } else { // il job va verso M4
        	 
  			if (M4.statoLibero()) {
  				//System.out.println("ID JOB FINE M2: "+M2.getJob().getId());
 				M4.setJob(M2.getJob()); // occupo M4 col job che prima era in M2
 				M2.rimuoviJob();
 				double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j) 
 				addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
 				System.out.println("Il job è stato inserito in M4");
 			}
 			else { // M4 è occupato
 				double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j)
 				Job tmp=new Job();
 				tmp.setProcessingTime(TM4);
 				addJobToSPTF(tmp); // inserisco job in coda M4 e lo ordino
 				M2.rimuoviJob(); // rimuovo il job da M2
 				System.out.println("Il job è stato inserito in codaM4 SPTF");
 				System.out.println("Il job è stato inserito in codaM4 SPTF, il tempo di servizio è: " +TM4);
 			}
       	 
  			//System.out.println("fine M2");
          }
			System.out.println("Mi guardo alle spalle e verifico se la coda M2 è vuota");
			if (CodaM2Lifo.isEmpty()) {
				
				addEvent(new Event(Event.Fine_M2, Event.INFINITY )); // imposto M2 a evento non prevedibile
				System.out.println("la coda M2 è vuota e imposto a infinito il tempo di servizio di M2");
			}
			else {
				M2.setJob(CodaM2Lifo.remove(CodaM2Lifo.size()-1)); // rimuovo job dalla coda M2 e lo metto dentro M2
				double TM2 = C2.getNextHyperExp(); // genero il tempo di servizio del centro M2
				addEvent(new Event(Event.Fine_M2, clock.getSimTime() + TM2)); // prevedo il prox evento di fine M2		
				System.out.println("la coda M2 NON è vuota, prelevo un job dalla coda M2");
			}
			System.out.println("***** FINE Routine Fine M2");  
        	 
         } // fine evento FineM2 
		
	private void simFineM1(){
		System.out.println("***** Eseguo Routine Fine M1");
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM1Out.getNextNumber();
		//System.out.println(NX);
		if(NX >= 0.3) {// il job va verso il centro M2
			if (M2.statoLibero()) {
				M2.setJob(M1.getJob()); // occupo M2 col job che prima era in M1
				M1.rimuoviJob();
				double TM2=C2.getNextHyperExp();// prevedo tempo di servizio  Tm2(j) 
				addEvent(new Event(Event.Fine_M2, clock.getSimTime() + TM2)); // prevedo il prox evento di fine M2
				System.out.println("Il job è stato prelevato da M1 e inserito in M2");
			}
			else {
				//System.out.println("ID JOB FINE M1: "+M1.getJob().getId());
				//int idTmpM1 = M1.getJob().getId();
				CodaM2Lifo.add(M1.getJob()); // inserisco job in coda M2
				M1.rimuoviJob();
				//job.add(new Job(idTmpM1));  
				//CodaM1Fifo.add(job.get(idTmpM1));
				System.out.println("Il job è stato inserito in codaM2");
			}
			 NXMachine = " vado verso Fine_M2";
         }else  {// il job va verso il centro M3
 			if (M3.statoLibero()) {
 				//System.out.println("ID JOB FINE M1: "+M1.getJob().getId());
				M3.setJob(M1.getJob()); // occupo M3 col job che prima era in M1
				M1.rimuoviJob();
				double TM3=C3.getNextHyperExp();// prevedo tempo di servizio  Tm3(j) 
				addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3
				System.out.println("Il job è stato inserito in M3");
			}
			else { // M3 è occupato e quindi lo metto in coda M3
				//System.out.println("ID JOB FINE M1: "+M1.getJob().getId());
				//int idTmpM1 = M1.getJob().getId();
				CodaM3Lifo.add(M1.getJob()); // inserisco job in coda M3
				M1.rimuoviJob();
				//job.add(new Job(idTmpM1));  
				//CodaM1Fifo.add(job.get(idTmpM1)); 
				System.out.println("Il job è stato inserito in codaM3");
			}
      	 
        	 //NXMachine = "vado verso Fine_M3";
         }
		System.out.println("Mi guardo alle spalle e verifico se la coda M1 è vuota");
		if (CodaM1Fifo.isEmpty()) {
			addEvent(new Event(Event.Fine_M1, Event.INFINITY )); // imposto M1 a evento non prevedibile
			System.out.println("la coda M1 è vuota e imposto il tempo di servizio di M1 a infinito ");
		}
		else {
			M1.setJob(CodaM1Fifo.remove(0)); // rimuovo job dalla coda M1 e lo metto dentro M1
			double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
			addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1	
			System.out.println("la coda M1 NON è vuota, prelevo un job dalla coda M1");
		}
		System.out.println("***** FINE Routine Fine M1"); 
	} // fine evento FineM1
	
	private ArrayList<Double> Osservazione(){
		System.out.println("---------- INIZIO Osservazione");
		System.out.println("Clock Osservazione: "+calendar.get(0).getE_time());
		// genero prossimo osservazione
	    //T_oss = T_oss + Dt;
		//System.out.println("Prossima Osservazione: "+T_oss);
		calendar.remove(0);
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione

		th = (NJobOut/T_oss); // calcolo throughput
		NJobOut = 0;
		System.out.println("TH= "+th);
		//Throughtput.add(th);
		array_oss.add(th); // memorizzo th nell'array
		
		return array_oss;
		
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
	
	public List<Job> pop(List<Job> lista) {
		if (lista != null && !lista.isEmpty())  lista.remove(lista.size()-1);
		return lista;
	}
	
	private void InizializzaGeneratori(double mu1, double mu2, double mu3, double mu4, double p) {
		rg= new RandomGenerator();
		RoutingM1Out=new UniformGenerator(5, 0, 1);// seme=5
		RoutingM2Out=new UniformGenerator(5, 0, 1);// seme=11
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

}