package linkedQueue;
//import java.util.concurrent.atomic.AtomicInteger;



//import java.util.concurrent.CyclicBarrier;

public class TestLinkedQueueMC {
	//private static final CyclicBarrier barrier = new CyclicBarrier(4);
	private static int putsum = 0;
	private static int takesum = 0;
	
	static class Producer implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		private int[] content;
		public Producer(String name, LinkedQueue<Integer> queue, int[] content){
			this.name = name;
			this.queue = queue;
			this.content = content;;
		}
		public String getName() {return new String(name);}
		public int getSum() {
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
					putsum += seed;
					System.out.println("Seed: " + seed);
					System.out.println("SUM: " + putsum);
					//barrier.await();
				}
				LinkedQueue.Node<Integer> travel = queue.getHead();
				while (travel.next.get() != null) {
					travel = travel.next.get();
					int element = travel.item;
					System.out.println("Put Queue: " + element);
				}
				System.out.println("\n Thread " + name + " finished\n");
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	static class Monitor implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		//private int content;
		public Monitor(String name, LinkedQueue<Integer> queue){
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
				LinkedQueue.Node<Integer> travel = queue.getHead();
				//int temp = 0;
				//for(int i=0; i<content; i++){
					//barrier.await();
					
					while (travel.next.get() != null) {
						travel = travel.next.get();
						int element = travel.item;
						System.out.println("Get Queue: " + element);
						takesum += element;
						System.out.println("SUM: " + takesum);
						//System.out.println("Consumer " + name + " getting " + i);
					}
					//barrier.await();
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
		
		LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
		
		// Generation of varying-size inputs
		int index = 0;
		int total_items = 2; // Total random numbers per thread
		int[] p1 = new int[total_items];
		int[] p2 = new int[total_items];
		
		while (index < total_items){
			p1[index] = index + 13;  
			System.out.println(p1[index]);
			index++;
		}

		for (int i = 0; i < total_items;i++){
			p2[i] = i + index; 
		}
		Monitor mon = new Monitor("M", queue);
		Producer prod1 = new Producer("T1", queue, p1);
		Producer prod2 = new Producer("T2", queue, p2);
		Thread t1 = new Thread(prod1);
		Thread t2 = new Thread(prod2);
		t1.setName(prod1.getName());
		t2.setName(prod2.getName());
		t1.start();
		t2.start();
		try {
			t1.join();
			System.out.println("Producer has stopped");
			t2.join();
			System.out.println("Producer has stopped");
			Thread M= new Thread(mon);
			M.setName(mon.getName());
			M.start();
			//System.out.println("Consumer is running");
			M.join();
			System.out.println("Consumer has stopped");
			System.out.println(prod1.getSum());
			System.out.println(mon.getSum());
			if(mon.getSum() == prod1.getSum()) {
				System.out.println("implementation is correct.");
			}
			else {
				System.out.println("error.");
			}
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}
