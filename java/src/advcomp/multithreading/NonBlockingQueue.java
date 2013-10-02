package advcomp.multithreading;

import java.util.concurrent.atomic.AtomicReference;

public class NonBlockingQueue {

    public static void main(String[] args) {
    	LinkedQueue<Integer> queue = new LinkedQueue<Integer>();
    	queue.put(1);
    	queue.put(2);
    	queue.put(3);
    	
    	// AtomicReference<Node<Integer>> p = queue.head.get().next;
    	// while (p.get() != null) {
    	// 	Node<Integer> x = p.get();
    	// 	System.out.println(x.item);
    	// 	p = x.next;
    	// }
    	
    	System.out.println(queue.pop());
    	System.out.println(queue.pop());
    	System.out.println(queue.pop());
    }

}

/**
 * Forked and modified from:
 * http://www.ibm.com/developerworks/library/j-jtp04186/
 * 
 * @author jiangwei1221
 *
 * @param <E>
 */
class LinkedQueue <E> {

	static class Node <E> {
	    final E item;
	    final AtomicReference<Node<E>> next;

	    Node(E item, Node<E> next) {
	        this.item = item;
	        this.next = new AtomicReference<Node<E>>(next);
	    }
	}

	// Changed: head and tail should be two different references.
	private Node<E> pivot = new Node<E>(null, null);
    private AtomicReference<Node<E>> head = new AtomicReference<Node<E>>(pivot);
    private AtomicReference<Node<E>> tail = new AtomicReference<Node<E>>(pivot);

    public boolean put(E item) {
        Node<E> newNode = new Node<E>(item, null);
        Node<E> curTail, residue;
        while (true) {
            curTail = tail.get();
            residue = curTail.next.get();
            if (curTail == tail.get()) {
                if (residue == null) /* A */ {
                    if (curTail.next.compareAndSet(null, newNode)) /* C */ {
                        tail.compareAndSet(curTail, newNode) /* D */ ;
                        return true;
                    }
                } else {
                    tail.compareAndSet(curTail, residue) /* B */;
                }
            }
        }
    }
    
    // Added: return next item of head and move head.
    public E pop() {
    	Node<E> result, next;
    	while (true) {
    		result = head.get();
    		if (result == null) {
    			return null;
    		}
    		next = result.next.get();
    		if (head.compareAndSet(result, next)) {
    			return next.item;
    		}
    	}
    }
}

