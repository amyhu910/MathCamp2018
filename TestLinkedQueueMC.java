package linkedQueue;

import java.util.concurrent.CyclicBarrier;

public class TestLinkedQueueMC {
	private final CyclicBarrier barrier = new CyclicBarrier(2001);
	private String name;
	private LinkedQueue<Integer> queue;
	private int[] content;
	
	class Producer implements Runnable {
		public Producer(String name, LinkedQueue<Integer> queue, int[] content){
			this.name = name;
			this.queue = queue;
			this.content = content;
		}
		public String getName() {return new String(name);}
		public void run() {
			try {
				int sum = 0;
				for(int i=0; i<content.length; i++){
					//System.out.println("Thread " + name + " putting " + i);
					queue.put(content[i]);
					int seed = (this.hashCode() ^ (int)System.nanoTime());
					barrier.await();
					sum += seed;
					seed = xorShift(seed);
					barrier.await();
				}
				//System.out.println("\n Thread " + name + " finished\n");
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
	class Consumer implements Runnable {
		public void run() {
			try {
				barrier.await();
				int sum = 0;
				for(int i=0; i<content.length; i++){
					sum++; 
				}
				barrier.await();
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
			p1[index] = index; 
			index++;
		}

		for (int i = 0; i < total_items;i++){
			p2[i] = i + index; 
		}
		
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
			t2.join();
			LinkedQueue.Node<Integer> travel = queue.getHead();
			String strsum = "";
			while (true) {
				strsum = strsum + travel;
				if (travel.next.get() != null) {
					travel = travel.next.get();
					System.out.println(travel.item);
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
