public class ArrayDeque<Item> {
    private int size;
    private Item[] items;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
	int size = 0;
	items = (Item[]) new Object[8];
	int nextFirst = 0;
	int nextLast = size;
    }

    /** sizeUp determines whether the "items" array
     *  needs to double in size, delegates
     *  resizing to resize if necessary.  */
    private void sizeUp(Item[] items) {
	size = size + 1;
	if (size == items.length) {
	    String operation = "Up";
	    items = resize(items, operation);
	}
    }

    /** sizeDown determines whether the "items"
     *  array is using at least 25% of the allocated
     *  memory for real objects, and if not
     *  will half the size of the array. We don't
     *  mind storing an empty array of size 2. */
    private void sizeDown(Item[] items) {
	size = size - 1;
	if (size <= items.length/4) {
	    if (size <= 2) {
		return;
	    }
	    String operation = "Down";
	    items = resize(items, operation);
	}
    }


    /** resize is responsible for ALL resizing
     *  of the "items" array. Only doubles or
     *  halves the size of "items," contingent
     *  upon the operation string that gets fed
     *  at function call.  */
    private Item[] resize(Item[] items, String operation) {
	if (operation == "Up") {
	    Item[] newitems = (Item[]) new Object[2*items.length];
	    System.arraycopy(items,0,newitems,0,items.length);
	    return newitems;
	}
	if (operation == "Down") {
	    Item[] newitems = (Item[]) new Object[items.length/2];
	    System.arraycopy(items,0,newitems,0,items.length);
	    return newitems;
	}
	return null;
    }

    public void addFirst(Item x) {
	sizeUp(items);
	items[nextFirst] = x;
	if (nextFirst == 0) {
	    nextFirst = items.length;
	}
	else {
	nextFirst = nextFirst - 1;
	}
    }

    public void addLast(Item x) {
        sizeUp(items);
	items[nextLast] = x;
	if (nextLast == items.length) {
	    nextLast = 0;
	}
	else {
	    nextLast = nextLast + 1;
	}
    }

    public Item removeFirst() {
	sizeDown(items);
	if (nextFirst == items.length) {
	    nextFirst = 0;
	}
	else {
	    nextFirst = nextFirst + 1;
	}
	Item oldFirstItem = items[nextFirst];
	items[nextFirst] = null;
	return oldFirstItem;
    }

    public Item removeLast() {
	sizeDown(items);
	if (nextLast == 0) {
	    nextLast = items.length;
	}
	else {
	    nextLast = nextLast - 1;
	}
	Item oldLastItem = items[nextLast];
	items[nextLast] = null;
	return oldLastItem;
    }

    public boolean isEmpty() {
	if (size == 0) {
	    return true;
	}
	return false;
    }

    public int size() {
	return size;
    }

    public void printDeque() {
	int index;
	if (nextFirst == items.length) {
	    index = 0;
	}
	else {
	    for (index = nextFirst + 1;
		 index <= nextLast - 1;
		 index = index + 1) {
		System.out.print(items[index]);
		System.out.print(" ");
	    }
	}
    }

    public Item get(int index) {
	if (items[index] == null) {
	    System.out.println("Invalid index.");
	    return null;
	}
	else {
	return items[index];
	}
    }
	    
	    
}
