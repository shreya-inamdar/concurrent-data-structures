import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.management.*;

public class ConTest implements Runnable{
	static Tree T;
	public static Lock printLock = new ReentrantLock();
	public static volatile int I= 300;
	public static volatile int D = 100;
	public static volatile int S = 600;
	//number of threads
	public static int N = 1000;
	//time measurement
	public static long [] startTime = new long[N];
	public static long [] endTime = new long[N];	
	public static volatile long threadTime = 0;
	final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	
	public static void main(String[] args){
		T = new Tree();	
		long start = System.currentTimeMillis();
		for(int i=0;i<N;i++){
			
			startTime[i] = startTime[i] = System.currentTimeMillis();
			new Thread(new ConTest()).start();
			endTime[i] = System.currentTimeMillis() - startTime[i];
		}
		
		long maxEndTime = endTime[0];
		double avEndTime = 0;
		for(int i = 0 ; i < N; i++){
			if (endTime[i] > maxEndTime)
				maxEndTime = endTime[i];
			avEndTime += endTime[i];
		}
		long end = System.currentTimeMillis() - start;
		avEndTime /= N;
		System.out.println("total program running time: " + end);
		System.out.println("Av threadTime: " + threadTime/1000000 + " ms" );
		return;
	}
	
		public void run(){
			Random rnd = new Random();
			for(int loop = 0; loop < 1000; loop++){
				int seed = rnd.nextInt(5000);
				if(I >= 0){
						T.insert(seed);
						I--;						
				}
				if(D >=0 ){
						T.delete(seed);
						D--;
				}
				if(S >= 0){
					T.find(seed);
					S--;
				}	
			}
			threadTime += bean.getCurrentThreadCpuTime();
		}	
	}
