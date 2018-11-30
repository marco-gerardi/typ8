import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduler {
	
	RandomGenerator rg;
	UniformGenerator RoutingM1Out, RoutingM4Out; // Routing job uscenti dal centro 1 e dal centro 4
	ExponentialGenerator C1; // Esponenziale per il centro 1
	KErlangGenerator C4; // Generatore 2-Elang per il tempo di servizio del centro 4
	HyperExpGenerator C2, C3;
	private List<Job> CodaM1Fifo, CodaM2Lifo, CodaM3Lifo, CodaM4Sptf;
	
	
	
	
	
	public Scheduler() {
		InizializzaGeneratori(0.8, 0.8, 0.4, 0.7, 0.3); // mu1, mu2, mu3, m4, p (per Hyperexp)
		InizializzaCode();
		

		
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


}