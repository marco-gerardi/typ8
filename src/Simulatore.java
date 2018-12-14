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
    double valoreCentrale;
    double sommaOss;
	double mediaCampionaria;
	double sommaMedia;
	double sommaVarianza;
	double e_n;
	double s2_n;
	private int p_run = 50; //variabile che indica il numero di run da effettuare per la stabilizz.
	
	
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
			System.out.println("*********************************************** Ciclo: "+i+" ****************");
			Throughtput= scheduler.run(i);
			
			//System.out.println("lunghezza della lista Throughtput: "+ Throughtput.size());
			// TODO: calcola media campionaria di ogni run
			sommaOss = 0.0;
			mediaCampionaria = 0.0;
			for (int j = 0; j < Throughtput.size(); j++) { 
				//System.out.println("Run numero " + j);
				System.out.println("Valore Throughtput: "+ Throughtput.get(j));
				sommaOss += Throughtput.get(j);
			}
			System.out.println("sommaOss " + sommaOss);
			System.out.println("Throughtput.size() " + Throughtput.size());
			
			mediaCampionaria = sommaOss/Throughtput.size(); // calcolo la media campionaria
			arrayCampionaria.add(mediaCampionaria); // memorizzo media campionaria del run in arrayCampionaria
			
			if(arrayCampionaria.size() <= p_run) {
				sommaMedia = 0.0;
				sommaVarianza = 0.0;
				
				for (int j = 0; j < arrayCampionaria.size(); j++) { 
					sommaMedia += arrayCampionaria.get(j);
				}
	
				e_n = sommaMedia/arrayCampionaria.size(); // tramite lo stimatore di Gordon per la media calcolo e(n)
				
				//aggiunge la media del run attuale in un vettore
				array_Stimatore_GordonEn.add(e_n);
				
				for (int z = 0; z < arrayCampionaria.size(); z++) {
					sommaVarianza += Math.pow(arrayCampionaria.get(z) - array_Stimatore_GordonEn.get(z), 2);
				}
				
				s2_n = Math.sqrt(sommaVarianza / array_Stimatore_GordonEn.size());
				//aggiunge la varianza del run attuale in un vettore
				array_Stimatore_GordonS2n.add(s2_n);
	
				System.out.println("Media campionaria: " + mediaCampionaria);
				System.out.println("Media: " + e_n);
				System.out.println("Varianza: " + s2_n);
			}
		
		}
		//stampa delle curve di Gordon della media campionaria, della media e della varianza
		System.out.println("\nCurva di Gordon della media campionaria:");
		for (int i = 0; i < arrayCampionaria.size(); i++)
		{
			System.out.print(arrayCampionaria.get(i) + "\n");
		}
		System.out.println("\n\nCurva di Gordon della media:");
		for (int i = 0; i < array_Stimatore_GordonEn.size(); i++)
		{
			System.out.print(array_Stimatore_GordonEn.get(i) + "\n");
		}
		System.out.println("\n\nCurva di Gordon della varianza:");
		for (int i = 0; i < array_Stimatore_GordonS2n.size(); i++)
		{
			System.out.print(array_Stimatore_GordonS2n.get(i) + "\n");
		}
		
		// FASE STATISTICA
		System.out.println("\n\n\n*** FASE STATISTICA ***\n");
		ArrayList<Double> campioni_media = new ArrayList<Double>();
		ArrayList<Double> medie = new ArrayList<Double>();
		ArrayList<Double> varianze = new ArrayList<Double>();
		ArrayList<Double> muMINs = new ArrayList<Double>();
		ArrayList<Double> muMAXs = new ArrayList<Double>();
		double intervallo_confidenza = 1.685; 
		//esecuzione di p run	
		for (int i = 1; i <= n0; i++)
		{
			Throughtput.clear();
			valoreCentrale = 0;
			for (int j = 1; j <= p_run; j++)
			{
				Throughtput= scheduler.run(j);
			}
			
			for (int z = 0; z < Throughtput.size(); z++) { 
				System.out.println("Run numero "+z+": " + Throughtput.get(z));
			}
			valoreCentrale = Throughtput.get(Throughtput.size()/2);
			array_batch.add(valoreCentrale);
			//batchArr.addAll(Throughtput);
			System.out.println("stat"+i+": "+Throughtput.size());
			System.out.println("posizione centrale "+Throughtput.size()/2);
			System.out.println("value posizione centrale "+valoreCentrale);
		}
		
		for (int z = 0; z < p_run; z++)
		{
			//System.out.println("valoreCentrale array Batch "+z+": "+array_batch.get(z));
			//Calcoliamo la media campionaria
			
			double media_campionaria1 = 0;
			
			for (int u = 0; u < array_batch.size(); u++)
			{
				media_campionaria1 += array_batch.get(u);
			}
			
			media_campionaria1 /= array_batch.size();
			campioni_media.add(media_campionaria1);
			
			// Calcoliamo la media
			
			double media1 = 0;
			
			for (int c = 0; c < campioni_media.size(); c++)
			{
				media1 += campioni_media.get(c);
			}
			media1 /= campioni_media.size();
			medie.add(media1);
			
			// Calcoliamo la varianza
			
			double varianza1 = 0;
			
			for (int a = 0; a < campioni_media.size(); a++)
			{
				varianza1 += Math.pow(campioni_media.get(a) - medie.get(a), 2);
			}
			
			varianza1 = Math.sqrt(varianza1 / medie.size());
			varianze.add(varianza1);
			
			
			// Calcoliamo l'intervallo di confidenza
			
			System.out.println("Intervallo di confidenza: ");
		     
			double d = Math.sqrt(Math.pow(varianza1, 2) / array_batch.size());
			double muMin = media1 - (d * intervallo_confidenza);
			double muMax = media1 + (d * intervallo_confidenza);
			
			muMINs.add(muMin);
			muMAXs.add(muMax);
			System.out.println("muMIN: " + muMin);
			System.out.println("muMAX: " + muMax);
			
		}
		
		
	     //print.stampa("MU MIN: "+muMin, name);
     //c4.append("MU MIN: "+muMin+" \n");
     
     //print.stampa("MU MIN: "+muMax, name);
     //c4.append("MU MIN: "+muMax+" \n");
		
		
		/*if(FASE_SIMULAZIONE == "stabilizzazione"){ // in caso di stabilizzazione
			if (Throughtput.size() == n0) { // verifico se sono state generate tutte le n osservazioni
				for ( i = 1; i <= Throughtput.size(); i++) {
					System.out.println("Run numero " + i);
					sommaOss = 0.0;
					mediaCampionaria = 0.0;
			
					for (int i = 0; i < n0; i++) {
						sommaOss += Throughtput.get(i);
						//System.out.println("sommaOss: "+ mediaCampionaria);
					}
					
					mediaCampionaria = sommaOss/n0; // calcolo la media campionaria
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
		}*/
	
		
		//TODO: stampa delle curve di Gordon della media campionaria, della media e della varianza
		
		
		// TODO: FASE STATISTICA
		p_batch=50;
		for (int i = 0; i < p_batch; i++) {}
		
		
		//TODO: stampa intervalli di confidenza
		

	}
	

	

}