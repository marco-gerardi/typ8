import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulatore {
	
	/* DICHIARAZIONE VARIABILI E STRUTTURE DATI */
	
	private Scheduler scheduler;	 //scheduler per circuito come da prova in aula

	 
	private double mu1, mu2, m3, m4; // parametri dei centri di servizio
	private int n0; //variabile numero osservazioni da effettuare nei run di stabilizzazione
	private int n_osservazione; //num di osservazioni crescenti per run stab che vanno da 1 a n0
	//private int p_run; //variabile che indica il numero di run da effettuare per la stabilizz.
	private int p_batch; //variabile che indica il numero di run da effettuare per run statistico
	//private int NJobOut; // num di job che escono lungo la linea GammaOut
	
	//private ArrayList<Double> array_oss = new ArrayList<Double>();
	private ArrayList<Double> array_batch = new ArrayList<Double>();
	//private ArrayList<Double> mediaC = new ArrayList<Double>(); // Gordon
	private ArrayList<Double> media = new ArrayList<Double>();// Gordon
	private ArrayList<Double> varianza = new ArrayList<Double>();// Gordon
	ArrayList<Double> Throughtput = new ArrayList<Double>();
	private int i,j; // variabili per i contatori
	public String FASE_SIMULAZIONE = "stabilizzazione";
	private ArrayList<Double> arrayCampionaria = new ArrayList<Double>();
    private ArrayList<Double> array_Stimatore_GordonEn = new ArrayList<Double>();
    private ArrayList<Double> array_Stimatore_GordonS2n = new ArrayList<Double>();
    double sommaOss;
	double mediaCampionaria;
	double sommaMedia;
	double sommaVarianza;
	double e_n;
	double s2_n;
	private int p_run = 5; //variabile che indica il numero di run da effettuare per la stabilizz.
	
	
	public static void main(String[] args)
	{
		new Simulatore();
	}
	
	// INIZIALIZZAZIONE

	public Simulatore() {
		System.out.println("Costruttore del simulatore");
		
		scheduler = new Scheduler(); // istanzio lo scheduler
		n0=5;
		for ( i = 1; i <= n0; i++) { 
			//System.out.println("**** Ciclo: "+i+" ****************");
			Throughtput= scheduler.run(i);
			//System.out.println("Valore Throughtput: "+ Throughtput.get(i));
			//System.out.println("lunghezza della lista Throughtput: "+ Throughtput.size());
			// TODO: calcola media campionaria di ogni run
			
			
		}
		for (int j = 0; j < Throughtput.size(); j++) { 
			System.out.println("Valore Throughtput: "+ Throughtput.get(j));
		}
		if(FASE_SIMULAZIONE == "stabilizzazione"){ // in caso di stabilizzazione
			if (Throughtput.size() == n0) { // verifico se sono state generate tutte le n osservazioni
				for ( i = 1; i <= Throughtput.size(); i++) {
					System.out.println("Run numero " + i);
					sommaOss = 0.0;
					mediaCampionaria = 0.0;
			
					for (int i = 0; i < n0; i++) {
						mediaCampionaria += Throughtput.get(i);
						//System.out.println("sommaOss: "+ mediaCampionaria);
					}
					
					mediaCampionaria /= n0; // calcolo la media campionaria
					arrayCampionaria.add(mediaCampionaria); // memorizzo media campionaria del run in arrayCampionaria
					
					System.out.println("mediaCampionaria: "+ mediaCampionaria);
					//System.out.println("arrayCampionaria.size(): "+ arrayCampionaria.size());
					//Throughtput.clear();// azzero le osservazioni del run corrente array_oss
					// reimposto lo stato iniziale
					if(arrayCampionaria.size() == p_run) {
						for (int z = 1; z <= arrayCampionaria.size(); z++) { 
							sommaMedia = 0.0;
							sommaVarianza = 0.0;
							
							for (int j = 0; j < p_run; j++) {
								sommaMedia += arrayCampionaria.get(j);
							}
							
							e_n = sommaMedia/p_run; // tramite lo stimatore di Gordon per la media calcolo e(n)
							System.out.println("e_n: "+ e_n);
							for (int j = 0; j < p_run; j++) {
								sommaVarianza += Math.pow(arrayCampionaria.get(j) - e_n, 2);
							}
		
							s2_n = (sommaVarianza/(p_run-1));
							System.out.println("s2_n: "+ s2_n);
							array_Stimatore_GordonEn.add(e_n);
							array_Stimatore_GordonS2n.add(s2_n);
						}
						// memorizzo e(n) e s2(n) in array_Stimatore_Gordon[] (credo di creare un array bidimensionale)
					}
					
					
				}
			}
		}
	
		
		//TODO: stampa delle curve di Gordon della media campionaria, della media e della varianza
		
		
		// TODO: FASE STATISTICA
		p_batch=50;
		for (int i = 0; i < p_batch; i++) {}
		
		
		//TODO: stampa intervalli di confidenza
		

	}
	

	

}