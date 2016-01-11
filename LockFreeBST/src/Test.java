import java.util.concurrent.*;
import java.lang.management.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
public class Test implements Runnable{
	public static Lock printLock = new ReentrantLock();
	//number of threads doing Insert, delete and search respectively
	public static int I = 300;
	public static int D = 100;
	public static int S = 600;
	//number of threads
	public static int N = 1000;
	WaitFreeConstruct WFC = new WaitFreeConstruct();
	public static long [] startTime = new long[N];
	public static long [] endTime = new long[N];	
	public static long [] startUserTime = new long[N];	
	public static long [] endUserTime = new long[N];	
	public static void main(String [] args){
		
		long start = System.currentTimeMillis();
		for(int i = 0 ; i < N;i++){
			
			Thread t = new Thread(new Test());
			startTime[i] = System.currentTimeMillis();
			t.run();
			
			endTime[i] = System.currentTimeMillis() - startTime[i];
		}
		long end = System.currentTimeMillis() - start;
		System.out.println("ThreadID " + "Start Time " + "End Time:");
		
		long maxEndTime = endTime[0];
		double avEndTime = 0;
		for(int i = 0 ; i < N; i++){
			System.out.println(i + " " + startTime[i] + " " +endTime[i]);
			if (endTime[i] > maxEndTime)
				maxEndTime = endTime[i];
			avEndTime += endTime[i];
		}
		System.out.println("Total time taken: " + avEndTime);
		avEndTime /= N;
		System.out.println("Max:" + maxEndTime + " | Av:"+avEndTime);
		System.out.println(end);
		return;
	}
	
	public void run(){
		
		Random rnd = new Random();
		for(int  j = 0; j  < 1000; j++){
		int seed = rnd.nextInt();
		if (seed < 0 )
			seed = seed * -1;
		seed = seed%100 ;
		int opId = seed%3+1;
		Invoc invoc;
		if(opId == 1 && I >= 0){
			invoc = new Invoc(1,seed);
			I--;
			WFC.apply(invoc);
		}
		else if(opId == 2 && D >= 0){
			invoc = new Invoc(2,seed);
			D--;
			WFC.apply(invoc);
			
		}
		else if(S >= 0){
			invoc = new Invoc(3,seed);
			S--;
			WFC.apply(invoc);
		}
		}
	}

}
