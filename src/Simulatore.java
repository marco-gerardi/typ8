import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulatore {
	
	/**********************************************
	* DICHIARAZIONE VARIABILI E STRUTTURE DATI
	**********************************************/
	private Scheduler scheduler;	 //scheduler per circuito come da prova in aula

	// parametri dei centri di servizio
	private double mu1 = (1/0.8);
	private double mu2 = (1/0.8);
	private double mu3 = (1/0.4);
	private double mu4 = (1/0.7);
	private double p = 0.3;
	
	private int n0 = 30; //variabile numero osservazioni da effettuare nei run di stabilizzazione
	private int n; //num di osservazioni crescenti per run stab che vanno da 1 a n0
	private int p_run = 30; //variabile che indica il numero di run da effettuare per la stabilizz.
	private int p_batch = 30; //variabile che indica il numero di run da effettuare per run statistico
	private double T_oss = 12.86; // intervallo di osservazione Delta T
	
	private ArrayList<Double> array_batch = new ArrayList<Double>();
	private ArrayList<Double> media = new ArrayList<Double>();// Gordon
	private ArrayList<Double> varianza = new ArrayList<Double>();// Gordon
	private ArrayList<Double> Throughtput = new ArrayList<Double>();
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
	
	public static void main(String[] args)
	{
		new Simulatore();
	}
	
	// STABILIZZAZIONE
	public void OsservazioneStabilizzazione(ArrayList<Double> Th, int n) {
		if(Th.size() == n){
			//calcola media campionaria di ogni run
			sommaOss = 0.0;
			mediaCampionaria = 0.0;
			for (i = 0; i < Th.size(); i++) { 
				sommaOss += Th.get(i);
			}
			
			mediaCampionaria = sommaOss/Th.size(); // calcolo la media campionaria
			arrayCampionaria.add(mediaCampionaria); // memorizzo media campionaria del run in arrayCampionaria

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
	
	// STATISTICA
	public void OsservazioneStatistica(ArrayList<Double> Th, int n) {
			Th.clear();
			valoreCentrale = 0;
			for (int j = 1; j <= p_batch; j++)
			{
				Th = scheduler.run(j);
			}
			
			for (int z = 0; z < Th.size(); z++) { 
				System.out.println("Run numero "+z+": " + Th.get(z));
			}
			valoreCentrale = Th.get(Th.size()/2);
			array_batch.add(valoreCentrale);
			//batchArr.addAll(Throughtput);
			//System.out.println("stat"+i+": "+Th.size());
			//System.out.println("posizione centrale "+Th.size()/2);
			//System.out.println("value posizione centrale "+valoreCentrale);	
	}
	
	public void ResultStatistica(ArrayList<Double> ArrayBatch, int p) {
		double intervallo_confidenza = 1.685; 
		if(ArrayBatch.size() == p){
			//calcola media campionaria di ogni run
			sommaOss = 0.0;
			mediaCampionaria = 0.0;
			for (i = 0; i < ArrayBatch.size(); i++) { 
				sommaOss += ArrayBatch.get(i);
			}
			
			mediaCampionaria = sommaOss/ArrayBatch.size(); // calcolo la media campionaria
			arrayCampionaria.add(mediaCampionaria); // memorizzo media campionaria del run in arrayCampionaria

			System.out.println("mediaCampionaria "+mediaCampionaria);
			
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
			//array_Stimatore_GordonS2n.add(s2_n);
			// Calcoliamo l'intervallo di confidenza
			
			System.out.println("Intervallo di confidenza: ");
			System.out.println("s2_n: "+s2_n);
			System.out.println("ArrayBatch.size(): "+ArrayBatch.size());
			double d = Math.sqrt(Math.pow(s2_n, 2) / ArrayBatch.size());
			double muMin = e_n - (d * intervallo_confidenza);
			double muMax = e_n + (d * intervallo_confidenza);
			
			System.out.println("range: "+d);
			
			// Stampa i risultati a schermo
			System.out.println("Intervallo di confidenza al 90%: "+e_n+" +/- "+d+"\n"); 
			
			System.out.println("MU MIN: "+muMin);
			System.out.println("MU MAX: "+muMax);
	     
		}
	}
	
	
	public Simulatore() {
		System.out.println("Costruttore del simulatore");
		
		scheduler = new Scheduler(mu1,mu3,mu3,mu4,p,T_oss); // istanzio lo scheduler
		
		for ( n = 1; n <= n0; n++) { 
			System.out.println("*********************************************** Ciclo: "+i+" ****************");
			Throughtput= scheduler.run(n);
			OsservazioneStabilizzazione(Throughtput,n);
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
		
		/*for ( n = 1; n <= n0; n++) { 
			System.out.println("*********************************************** Ciclo: "+i+" ****************");
			Throughtput= scheduler.run(n);
			OsservazioneStatistica(Throughtput,n);
		}
		ResultStatistica(array_batch,p_batch);*/
		/*for (int i = 0; i < arrayCampionaria.size(); i++)
		{
			System.out.print(arrayCampionaria.get(i) + "\n");
		}*/
	}
}