package linkedQueue;

//import java.util.concurrent.CyclicBarrier;

public class TestLinkedQueueMC {
	//private static final CyclicBarrier barrier = new CyclicBarrier(2001);
	
	static class Producer implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		private int[] content;
		private int sum;
		public Producer(String name, LinkedQueue<Integer> queue, int[] content){
			this.name = name;
			this.queue = queue;
			this.content = content;
			this.sum = 0;
		}
		public String getName() {return new String(name);}
		public int getSum() {
			return sum;
		}
		public void run() {
			try {
				sum = 0;
				for(int i=0; i<content.length; i++){
					System.out.println("Thread " + name + " putting " + i);
					Integer temp = content[i];
					int seed = temp.hashCode();
					//barrier.await();
					seed = xorShift(seed);
					sum += seed;
					queue.put(seed);
					System.out.println(seed);
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
	
	static class Consumer implements Runnable {
		private String name;
		private LinkedQueue<Integer> queue;
		//private int content;
		private int sum;
		public Consumer(String name, LinkedQueue<Integer> queue){
			this.name = name;
			this.queue = queue;
			//this.content = content;
			this.sum = 0;
		}
		public int getSum() {
			return sum;
		}
		public String getName() {
			return name;
		}
		public void run() {
			try {
				sum = 0;
				LinkedQueue.Node<Integer> travel = queue.getHead();
				//int temp = 0;
				//for(int i=0; i<content; i++){
					//barrier.await();
					
					while (travel.next.get() != null) {
						travel = travel.next.get();
						int element = travel.item;
						System.out.println("Get Queue: " + element);
						sum += element;
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
		
		Producer prod1 = new Producer("T1", queue, p1);
		Producer prod2 = new Producer("T2", queue, p2);
		Consumer cons = new Consumer("C", queue);
		Thread t1 = new Thread(prod1);
		Thread t2 = new Thread(prod2);
		t1.setName(prod1.getName());
		t2.setName(prod2.getName());
		t1.start();
		//System.out.println("Producer is running");
		t2.start();
		try {
			t1.join();
			System.out.println("Producer has stopped");
			t2.join();
			System.out.println("Producer has stopped");
			Thread C= new Thread(cons);
			C.setName(cons.getName());
			C.start();
			//System.out.println("Consumer is running");
			C.join();
			System.out.println("Consumer has stopped");
			System.out.println(prod1.getSum());
			System.out.println(cons.getSum());
			if(cons.getSum() == prod1.getSum()) {
				System.out.println("implementation is correct.");
			}
			else {
				System.out.println("error.");
			}
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}
	}

}
