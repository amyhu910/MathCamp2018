package linkedQueue;

import java.util.concurrent.atomic.AtomicReference;
// java.util.Enumeration;
import java.util.Hashtable;

public class BugQueue <E> {
	StringBuffer trace = new StringBuffer();
	Hashtable<String, Integer> structMap = new Hashtable();
	
	private final Node<E> dummy = new Node<E>(null, null);
	private final AtomicReference<Node<E>> head = new AtomicReference<Node<E>>(dummy);
	private final AtomicReference<Node<E>> tail = new AtomicReference<Node<E>>(dummy);
	
	public static class Node <E> {
		final E item;
		public Node<E> next;
		
		public Node(E item, Node<E> next) {
			this.item = item;
			this.next = next;
		}
	}
	public Node<E> getHead(){
		return head.get();	
	}
	public void clearTrace(){
		trace.delete(0, trace.length());
	}
	public String getTrace(){
		return trace.toString();
	}
		
	public BugQueue(){
		structMap.put("Head", 1);
		structMap.put("Tail", 0);
		structMap.put("Dummy", 0);
	}
	public int length() {
		BugQueue.Node<E> travel = this.getHead();
		int count = 0;
		while (travel.next != null) {
			travel = travel.next;
			if (travel.item != null) {
				count++;
			}
		}	
		return count;
	}
	public void put(E item) {
		Node<E> newNode = new Node<E>(item, null);
		Node<E> travel = head.get();
		while (travel.next != null) {
			travel = travel.next;
		}
		travel.next = newNode;
		newNode.next = tail.get();
	}
}
