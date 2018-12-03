import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulatore {
	
	/* DICHIARAZIONE VARIABILI E STRUTTURE DATI */
	
	private Scheduler scheduler;	 //scheduler per circuito come da prova in aula

	 
	private double mu1, mu2, m3, m4; // parametri dei centri di servizio
	private int n0; //variabile numero osservazioni da effettuare nei run di stabilizzazione
	private int n_osservazione; //num di osservazioni crescenti per run stab che vanno da 1 a n0
	private int p_run; //variabile che indica il numero di run da effettuare per la stabilizz.
	private int p_batch; //variabile che indica il numero di run da effettuare per run statistico
	private int NJobOut; // num di job che escono lungo la linea GammaOut
	
	private ArrayList<Double> array_oss = new ArrayList<Double>();
	private ArrayList<Double> array_batch = new ArrayList<Double>();
	private ArrayList<Double> mediaC = new ArrayList<Double>(); // Gordon
	private ArrayList<Double> media = new ArrayList<Double>();// Gordon
	private ArrayList<Double> varianza = new ArrayList<Double>();// Gordon
	ArrayList<Integer> Throughtput = new ArrayList<Integer>();
	private int i,j; // variabili per i contatori
	public String FASE_SIMULAZIONE;
	public static void main(String[] args)
	{
		new Simulatore();
	}
	
	// INIZIALIZZAZIONE

	public Simulatore() {
		System.out.println("Costruttore del simulatore");
		
		scheduler = new Scheduler(); // istanzio lo scheduler
		n0=200;
		for ( i = 1; i <= n0; i++) { 
			System.out.println("**** Ciclo: "+i+" ****************");
			Throughtput= scheduler.run(i);
			System.out.println("lunghezza della lista Throughtput: "+ Throughtput.size());
			// TODO: calcola media campionaria di ogni run
		}
		
		
		//TODO: stampa delle curve di Gordon della media campionaria, della media e della varianza
		
		
		// TODO: FASE STATISTICA
		p_batch=50;
		for (int i = 0; i < p_batch; i++) {}
		
		
		//TODO: stampa intervalli di confidenza
		

	}
	

	

}