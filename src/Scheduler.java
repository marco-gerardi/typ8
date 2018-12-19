import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	
	/**********************************************
	* DICHIARAZIONE VARIABILI E STRUTTURE DATI
	**********************************************/
	private final static int STABILIZZAZIONE = 0; 
	
	RandomGenerator rg;
	UniformGenerator RoutingM1Out, RoutingM2Out, RoutingM4Out; // Routing job uscenti dal centro 1 e dal centro 4
	ExponentialGenerator C1; // Esponenziale per il centro 1
	KErlangGenerator C4; // Generatore 2-Elang per il tempo di servizio del centro 4
	HyperExpGenerator C2, C3;
	
	public List<Job> CodaM1Fifo, CodaM2Lifo, CodaM3Lifo, CodaM4Sptf;
	
	public ArrayList<Job> job = new ArrayList<Job>();
	public ArrayList<Double> array_oss = new ArrayList<Double>();
	
	private Clock clock;
	private double T_oss; // intervallo di osservazione Delta T

	public List<Event> calendar;
	public Machine M1, M2, M3, M4;
	public String FASE_SIMULAZIONE = "stabilizzazione";
	public int NJobOut; // num di job che escono lungo la linea GammaOut

	public double NX;
	public String NXMachine;
	public int next; // contiene il prossimo evento prelevato dal calendario eventi
  
	public double th = 0.0;
	
	public Scheduler(double mu1, double mu2, double mu3, double mu4, double p, double Toss) {
		Inizializzazione(mu1, mu2, mu3, mu4, p, Toss);
	}

	/**********************************************
	* INIZIALIZZAZIONE
	**********************************************/
	public void Inizializzazione(double mu1, double mu2, double mu3, double mu4, double p, double Toss) {
		
		T_oss = Toss;
		
		//Inizializzo i Generatori
		rg = new RandomGenerator();
		RoutingM1Out = new UniformGenerator(5, 0, 1);// seme=5
		RoutingM2Out = new UniformGenerator(5, 0, 1);// seme=11
		RoutingM4Out = new UniformGenerator(5, 0, 1);// seme=7
		C1 = new ExponentialGenerator(5, mu1);// seme=5, mu=0.4
		C2 = new HyperExpGenerator(5, 7, mu2, p );
		C3 = new HyperExpGenerator(5, 7, mu3, p );
		C4 = new KErlangGenerator(21,2,mu4);
		
		//Inizializzo le Code 
	    CodaM1Fifo = new ArrayList<Job>();
		CodaM2Lifo = new ArrayList<Job>();
		CodaM3Lifo = new ArrayList<Job>();
		CodaM4Sptf = new ArrayList<Job>();
		
		clock = new Clock();

		calendar = new ArrayList<Event>(); // istanzio il calendar
		
		// istranzio i job
		/*for(int i=1;i<=12;i++){
			job.add(new Job(i));
		}*/
		
		/*Job nuovo1 = new Job();
		
		CodaM1Fifo.add(nuovo1);*/
		
		Job nuovo1 = new Job();
		Job nuovo2 = new Job();
		Job nuovo3 = new Job();
		Job nuovo4 = new Job();
		Job nuovo5 = new Job();
		Job nuovo6 = new Job();
		Job nuovo7 = new Job();
		Job nuovo8 = new Job();
		Job nuovo9 = new Job();
		Job nuovo10 = new Job();
		Job nuovo11 = new Job();
		Job nuovo12 = new Job();
		
		CodaM1Fifo.add(nuovo1);
		CodaM1Fifo.add(nuovo2);
		CodaM1Fifo.add(nuovo3);
		CodaM1Fifo.add(nuovo4);
		CodaM1Fifo.add(nuovo5);
		CodaM1Fifo.add(nuovo6);
		CodaM1Fifo.add(nuovo7);
		CodaM1Fifo.add(nuovo8);
		CodaM1Fifo.add(nuovo9);
		CodaM1Fifo.add(nuovo10);
		CodaM1Fifo.add(nuovo11);
		CodaM1Fifo.add(nuovo12);
		
		// istanzio i centri di servizio
		M1=new Machine("M1");
		M2=new Machine("M2");
		M3=new Machine("M3");
		M4=new Machine("M4");
		
		double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
		
		// aggiungo i 12 job alla coda M1 - fase iniziale
		/*for(int i=0;i<12;i++){
			CodaM1Fifo.add(job.get(i)); 
		}*/
		
		addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
		addEvent(new Event(Event.Fine_M2, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M3, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.Fine_M4, Event.INFINITY )); // imposto M2 a evento non prevedibile
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione
		addEvent(new Event(Event.FINESIM, Event.INFINITY )); // imposto Finesim a evento non prevedibile
	}
	
	// simulo un run dello scheduler di lunghezza n passato come parametro
	public ArrayList<Double> run(int n) { 
		//vettore osservazione Throughtput nel sistema
		ArrayList<Double> Throughtput = new ArrayList<Double>();
		
        while (Throughtput.size()<n) {
        	// guardo il calendario e vedo qual'è il primo evento
        	next = calendar.get(0).getE_id();
        	clock.setSimTime(calendar.get(0).getE_time()); // aggiorno il clock

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
	}
	
	/**********************************************
	* ROUTINE
	**********************************************/
	//Routine Fine M1
	public void simFineM1(){
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM1Out.getNextNumber();
		if(NX >= 0.3) {// il job va verso il centro M2
			if (M2.statoLibero()) {
				M2.setJob(M1.getJob()); // occupo M2 col job che prima era in M1
				M1.rimuoviJob();
				double TM2 = C2.getNextHyperExp();// prevedo tempo di servizio  Tm2(j) 
				addEvent(new Event(Event.Fine_M2, clock.getSimTime() + TM2)); // prevedo il prox evento di fine M2
			}
			else {
				CodaM2Lifo.add(M1.getJob()); // inserisco job in coda M2
				M1.rimuoviJob();
			}
	     }else  {// il job va verso il centro M3
			if (M3.statoLibero()) {
				M3.setJob(M1.getJob()); // occupo M3 col job che prima era in M1
				M1.rimuoviJob();
				double TM3 = C3.getNextHyperExp();// prevedo tempo di servizio  Tm3(j) 
				addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3
			}
			else { // M3 è occupato e quindi lo metto in coda M3
				CodaM3Lifo.add(M1.getJob()); // inserisco job in coda M3
				M1.rimuoviJob();
			}
	    }
		if (CodaM1Fifo.isEmpty()) {
			addEvent(new Event(Event.Fine_M1, Event.INFINITY )); // imposto M1 a evento non prevedibile
		}
		else {
			M1.setJob(CodaM1Fifo.remove(0)); // rimuovo job dalla coda M1 e lo metto dentro M1
			double TM1 = C1.getNextExp(); // genero il tempo di servizio del centro1 M1
			addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1	
		}
	} // fine evento FineM1
	
	//Routine Fine M2
	public void simFineM2() {
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM2Out.getNextNumber();
		if (NX >= 0.5) {// il job va verso il centro M1
			if (M1.statoLibero()) {
				M1.setJob(M2.getJob()); // occupo M1 col job che prima era in M2
				M2.rimuoviJob();
				double TM1=C1.getNextExp();// prevedo tempo di servizio  Tm1(j) 
				addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
			}
			else {
				CodaM1Fifo.add(M2.getJob()); // inserisco job in coda M1 con disciplina FiFo
				M2.rimuoviJob();
			}
		} else { // il job va verso M4
			if (M4.statoLibero()) {
				M4.setJob(M2.getJob()); // occupo M4 col job che prima era in M2
				M2.rimuoviJob();
				double TM4 = C4.getNextNumber();// prevedo tempo di servizio  Tm4(j) 
				addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
			}
			else { // M4 è occupato
				double TM4 = C4.getNextNumber();// prevedo tempo di servizio  Tm4(j)
				Job tmp=new Job();
				tmp.setProcessingTime(TM4);
				addJobToSPTF(tmp); // inserisco job in coda M4 e lo ordino
				M2.rimuoviJob(); // rimuovo il job da M2
			}
		}
		if (CodaM2Lifo.isEmpty()) {
			addEvent(new Event(Event.Fine_M2, Event.INFINITY )); // imposto M2 a evento non prevedibile
		}
		else {
			M2.setJob(CodaM2Lifo.remove(CodaM2Lifo.size()-1)); // rimuovo job dalla coda M2 e lo metto dentro M2
			double TM2 = C2.getNextHyperExp(); // genero il tempo di servizio del centro M2
			addEvent(new Event(Event.Fine_M2, clock.getSimTime() + TM2)); // prevedo il prox evento di fine M2		
		}
	} // fine evento FineM2 
	
	//Routine Fine M3
	public void simFineM3() {
		calendar.remove(0); // rimuovo l'evento dal calendario
		NXMachine = "M3";
		if (M4.statoLibero()) {
			M4.setJob(M3.getJob()); // occupo M4 col job che prima era in M3
			M3.rimuoviJob();
			double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j) 
			addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
		}
		else { // metto in coda con disciplina SPTF
			double TM4=C4.getNextNumber();// prevedo tempo di servizio  Tm4(j)
			Job tmp=new Job();
			tmp.setProcessingTime(TM4);
			addJobToSPTF(tmp); // inserisco job in coda M4 e lo ordino
			M3.rimuoviJob(); // rimuovo il job da M3
		}
		if (CodaM3Lifo.isEmpty()) {
			addEvent(new Event(Event.Fine_M3, Event.INFINITY )); // imposto M3 a evento non prevedibile
		}
		else {
			M3.setJob(CodaM3Lifo.remove(CodaM3Lifo.size()-1)); // rimuovo job dalla coda M3 e lo metto dentro M3
			double TM3 = C3.getNextHyperExp(); // genero il tempo di servizio del centro M3
			addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3	
		}
    } // fine evento FineM3 
	
	//Routine Fine M4
	public void simFineM4(){
		calendar.remove(0); // rimuovo l'evento dal calendario
		NX = RoutingM4Out.getNextNumber();
		
		if(NX < 0.1) {// il job va verso il centro M3
			if (M3.statoLibero()) {
				M3.setJob(M4.getJob()); // occupo M3 col job che prima era in M4
				M4.rimuoviJob();
				double TM3=C3.getNextHyperExp(); // prevedo tempo di servizio  Tm3(j) 
				addEvent(new Event(Event.Fine_M3, clock.getSimTime() + TM3)); // prevedo il prox evento di fine M3
			}
			else {
				CodaM3Lifo.add(M4.getJob()); // inserisco job in coda M3 con disciplina lifo
				M4.rimuoviJob();
			} 
         }else  {// il job va verso il centro M1
        	NJobOut++; // incremento la variabile NJobOUT
 			if (M1.statoLibero()) {
				M1.setJob(M4.getJob()); // occupo M1 col job che prima era in M4
				M4.rimuoviJob();
				double TM1=C1.getNextExp(); // prevedo tempo di servizio  Tm1(j) 
				addEvent(new Event(Event.Fine_M1, clock.getSimTime() + TM1)); // prevedo il prox evento di fine M1
			}
			else { // lo metto in coda M1 con disciplina fifo
				CodaM1Fifo.add(M4.getJob()); // inserisco job in coda M1
				M4.rimuoviJob();
			}
         }
		if (CodaM4Sptf.isEmpty()) {
			addEvent(new Event(Event.Fine_M4, Event.INFINITY )); // imposto M4 a evento non prevedibile
		}
		else {
			Job tmp=new Job();
			tmp=CodaM4Sptf.get(CodaM4Sptf.size() - 1);
			M4.setJob(tmp); // rimuovo job dalla coda M4 e lo metto dentro M4
			double TM4 =tmp.getProcessingTime() ; // prelevo il tempo di servizio dal job rimosso dalla coda
			addEvent(new Event(Event.Fine_M4, clock.getSimTime() + TM4)); // prevedo il prox evento di fine M4
		}
	} // fine evento FineM4
		
	//Routine Osservazione
	private ArrayList<Double> Osservazione(){
		calendar.remove(0);
		addEvent(new Event(Event.OSSERVAZIONE, clock.getSimTime()+T_oss)); //prevedo il prossimo evento di osservazione

		th = (NJobOut/T_oss); // calcolo throughput
		NJobOut = 0;
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
}