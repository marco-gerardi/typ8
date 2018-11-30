import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulatore {
	
	/* DICHIARAZIONE VARIABILI E STRUTTURE DATI */
	private Clock clock;
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
	private Job j1,j2,j3,j4,j5; 
	private double mu1, mu2, m3, m4; // parametri dei centri di servizio
	private int n0; //variabile numero osservazioni da effettuare nei run di stabilizzazione
	private int n_osservazione; 
	private int p_run; //variabile che indica il numero di run da effettuare per la stabilizz.
	private int p_batch; //variabile che indica il numero di run da effettuare per run statistico
	private int NJobOut; // num di job che escono lungo la linea GammaOut
	private double T_oss; // intervallo di osservazione Delta T
	
	
	public static void main(String[] args)
	{
		new Simulatore();
	}
	
	// INIZIALIZZAZIONE

	public Simulatore() {
		System.out.println("Costruttore del costruttore");
		InizializzaGeneratori(0.8, 0.8, 0.4, 0.7, 0.3); // mu1, mu2, mu3, m4, p (per Hyperexp)
		InizializzaCode();
		
		
		for(int i = 0; i<100; i++) {
			System.out.println("Rnd["+i+"]: "+rg.getNextNumber());
			System.out.println("RoutingM1Out["+i+"]: "+RoutingM1Out.getNextNumber());
			System.out.println("RoutingM4Out["+i+"]: "+RoutingM4Out.getNextNumber());
			System.out.println("Exp["+i+"]: "+C1.getNextExp());
			System.out.println("HypC2["+i+"]: "+C2.getNextHyperExp());
			System.out.println("HypC3["+i+"]: "+C3.getNextHyperExp());
			System.out.println("K-Erl M4["+i+"]: "+C4.getNextNumber());
			System.out.println("------------------------------");
		}
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
	
}