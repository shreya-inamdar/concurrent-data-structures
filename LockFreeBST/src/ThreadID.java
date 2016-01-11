public class ThreadID {
    private static volatile int nextID = 0;
    private static class ThreadLocalID extends ThreadLocal<Integer> {
    	protected synchronized Integer initialValue() {
    		return nextID++;
    	}
    }

    private static ThreadLocalID threadID = new ThreadLocalID();
    public static int get() {
    	return threadID.get();
    }
    public static void set(int index) {
    	threadID.set(index);
    }
}