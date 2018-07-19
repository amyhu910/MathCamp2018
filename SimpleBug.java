package linkedQueue;

//import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;
//import java.util.ArrayList;



public class SimpleBug {
	private static AtomicInteger putsum = new AtomicInteger(0);
	private static int takesum = 0;
	private static final SnapShotCheckSum[] array = new SnapShotCheckSum[6];
	//private static int index = 0;
	
	static class SnapShotCheckSum {
		private final long time;
		private final int monitorSum;
		private final AtomicInteger producerSum;
		public SnapShotCheckSum(long initTime, int initMonitorSum,
				AtomicInteger initProducerSum) {
			time = initTime;
			monitorSum = initMonitorSum;
			producerSum = initProducerSum;
		}
		public long getTime() {
			return time;
		}
		public int getMonitorSum() {
			return monitorSum;
		}
		public AtomicInteger getProducerSum() {
			return producerSum;
		}
		public String toString() {
			return "[" + time + ", " + monitorSum + ", " + producerSum + "]";
		}
	}
	
	static class Producer implements Runnable {
		private String name;
		private BugQueue<Integer> queue;
		private int[] content;
		public Producer(String name, BugQueue<Integer> queue, int[] content){
			this.name = name;
			this.queue = queue;
			this.content = content;
		}
		public String getName() {return new String(name);}
		public AtomicInteger getSum() {
			return putsum;
		}
		public void run() {
			try {
				for(int i=0; i<content.length; i++){
					System.out.println("Thread " + name + " putting " + i);
					Integer temp = content[i];
					int seed = temp.hashCode();
					//barrier.await();
					seed = xorShift(seed);
					queue.put(seed);
					putsum.getAndAdd(seed);
					System.out.println("\n Put Queue: " + seed);
					System.out.println("\n Putsum: " + putsum);
					//System.out.println("Monitor is running " + name);
				}
				//System.out.println("\n Thread " + name + " finished\n");
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	static class Monitor implements Runnable {
		private String name;
		private BugQueue<Integer> queue;
		private static int index = 0;
		//private int content;
		public Monitor(String name, BugQueue<Integer> queue){
			this.name = name;
			this.queue = queue;
			//this.content = content;
		}
		public int getSum() {
			return takesum;
		}
		public String getName() {
			return name;
		}
		public void run() {
			try {
				System.out.println("Monitor is running");
				takesum = 0;
				BugQueue.Node<Integer> travel = queue.getHead();
				//int temp = 0;
				//for(int i=0; i<content; i++){
					//barrier.await();
					
					while (travel.next != null) {
						travel = travel.next;
						int element = travel.item;
						System.out.println("\n Get Queue: " + element);
						takesum += element;
						System.out.println("\n Takesum: " + takesum);
						//System.out.println("Consumer " + name + " getting ");
					}
					array[index] = new SnapShotCheckSum(System.nanoTime(), takesum, putsum);
					index++;
					//array.add(new SnapShotCheckSum(System.nanoTime(), takesum, putsum));
					/*if(takesum.get() == putsum.get()) {
						System.out.println("implementation is correct.");
					}
					else {
						System.out.println("error.");
					}*/
				//}
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	public static int xorShift(int y){
		y ^= (y << 6);
		y ^= (y >>> 21);
		y ^= (y << 7);
		return y;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BugQueue<Integer> queue = new BugQueue<Integer>();
		
		// Generation of varying-size inputs
		int index1 = 0;
		int total_items = 2; // Total random numbers per thread
		int[] p1 = new int[total_items];
		int[] p2 = new int[total_items];
		
		while (index1 < total_items){
			p1[index1] = index1 + 13;  
			//System.out.println(p1[index]);
			index1++;
		}

		for (int i = 0; i < total_items;i++){
			p2[i] = i + index1; 
		}
		Monitor mon = new Monitor("M", queue);
		Producer prod1 = new Producer("T1", queue, p1);
		Producer prod2 = new Producer("T2", queue, p2);
		Thread t1 = new Thread(prod1);
		Thread t2 = new Thread(prod2);
		t1.setName(prod1.getName());
		t2.setName(prod2.getName());
		Thread M = new Thread(mon);
		M.setName(mon.getName());
		//M.start();
		t1.start();
		t2.start();
		int counter = -1;
		while (queue.length() < p1.length + p2.length) {
			if (queue.length() > counter) {
				System.out.println("Monitor will run");
				M.run();
				counter = queue.length();
			}	
			//System.out.println("Monitor finished running");
			//System.out.println("Monitor has stopped");
		}
		//prod1.M.start();
		try {
			t1.join();
			//System.out.println("Producer has stopped");
			t2.join();
			//System.out.println("Producer has stopped");
			M.run();
			//M.join();
			//System.out.println("Consumer is running");
			//prod1.M.join();
			//System.out.println("Consumer has stopped");
			//System.out.println("Putsum: " + putsum);
			//System.out.println("Takesum: " + takesum);
			if(takesum == putsum.get()) {
				System.out.println("implementation is correct.");
			}
			else {
				System.out.println("error.");
			}
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					System.out.println(array[i]);
				}
				else {
					break;
				}
			}
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}
}
