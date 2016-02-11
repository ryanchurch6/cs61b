import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Random;

public class TestArrayDeque1B {

    @Test
    public void test50operations() {
	StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<Integer>();
	ArrayDequeSolution<Integer> sol1 = new ArrayDequeSolution<Integer>();
	int[] sequence = new int[50];
	Random rn = new Random();
	for (int n = 0; n < 50; n++) {
	    int val = rn.nextInt(6);
	    sequence[n] = val;
	}
	operationDriver(sequence, sad1, sol1);
	}

    
    public void operationDriver(int[] sequence,
				StudentArrayDeque<Integer> sad,
				ArrayDequeSolution<Integer> sol) {
	Random ran = new Random();
	String fs = "";
	for (int i = 0; i < sequence.length; i++) {
	    if (sequence[i] == 0) {
		int num = ran.nextInt(50) + 1;
		sad.addFirst(num);
		sol.addFirst(num);
		fs = fs + "addFirst(" + num + ")" + "\n";
	    }
	    if (sequence[i] == 1) {
		int num = ran.nextInt(50) + 1;
		sad.addLast(num);
		sol.addLast(num);
		fs = fs + "addLast(" + num + ")" + "\n";
	    }
	    if (sequence[i] == 2) {
		fs = fs + "isEmpty()" + "\n";
		boolean o = sol.isEmpty();
		boolean a = sad.isEmpty();
		assertEquals(fs, o, a);
	    }
	    if (sequence[i] == 3) {
		fs = fs + "size()" + "\n";
		Integer o = sol.size();
		Integer a = sad.size();
		assertEquals(fs, o, a);
	    }
	    if (sequence[i] == 4) {
		fs = fs + "removeFirst()" + "\n";
		Integer o = sol.removeFirst();
		Integer a = sad.removeFirst();
		assertEquals(fs, o, a);
	    }
	    if (sequence[i] == 5) {
		fs = fs + "removeLast()" + "\n";
		Integer o = sol.removeLast();
		Integer a = sad.removeLast();
		assertEquals(fs + "removeLast()", o, a);
	    }
	    if (sequence[i] == 6) {
		int num = ran.nextInt(sequence.length - 1);
		fs = fs + "get(" + num + ")" + "\n";
		Integer o = sol.get(num);
		Integer a = sad.get(num);
		assertEquals(fs, o, a);
	    }
	}
    }
    
    public static void main(String[] args) {
	jh61b.junit.TestRunner.runTests("failed", TestArrayDeque1B.class);
    }
}
