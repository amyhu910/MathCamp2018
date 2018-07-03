package linkedQueue;

import java.util.concurrent.CyclicBarrier;

public class TestLinkedQueueMC {
	private static final CyclicBarrier barrier = new CyclicBarrier(2001);
	
	static class Producer implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		private int[] content;
		int sum = 0;
		public Producer(String name, LinkedQueue<Integer> queue, int[] content){
			this.name = name;
			this.queue = queue;
			this.content = content;
		}
		public String getName() {return new String(name);}
		public int getSum() {
			return sum;
		}
		public void run() {
			try {
				for(int i=0; i<content.length; i++){
					//System.out.println("Thread " + name + " putting " + i);
					Integer temp = content[i];
					int seed = (temp.hashCode());
					barrier.await();
					sum += seed;
					seed = xorShift(seed);
					queue.put(seed);
					barrier.await();
				}
				//System.out.println("\n Thread " + name + " finished\n");
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	static class Consumer implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		private int content;
		private int sum = 0;
		public Consumer(String name, LinkedQueue<Integer> queue, int content){
			this.name = name;
			this.queue = queue;
			this.content = content;
		}
		public int getSum() {
			return sum;
		}
		public String getName() {
			return name;
		}
		public void run() {
			try {
				for(int i=0; i<content; i++){
					barrier.await();
					sum += queue.take(i);
					barrier.await();
				}
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
		//int[] p2 = new int[total_items];
		
		while (index < total_items){
			p1[index] = index; 
			index++;
		}

		/*for (int i = 0; i < total_items;i++){
			p2[i] = i + index; 
		};*/
		
		Producer prod1 = new Producer("T1", queue, p1);
		//Producer prod2 = new Producer("T2", queue, p2);
		Consumer cons = new Consumer("C", queue, p1.length);
		Thread t1 = new Thread(prod1);
		//Thread t2 = new Thread(prod2);
		t1.setName(prod1.getName());
		//t2.setName(prod2.getName());
		t1.start();
		//t2.start();
		try {
			t1.join();
			//t2.join();
			if(cons.getSum() == prod1.getSum()) {
				System.out.println("implementation is correct");
			}
			else {
				System.out.println("error.");
			}
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}

}
