package framework;

public class ProcessId implements Comparable {
	String name;
	boolean isAE;

	public ProcessId(String name, boolean isAE) {
	    this.name = name;
	    this.isAE = isAE;
	}

	public boolean equals(Object other){
		return name.equals(((ProcessId) other).name);
	}

	public int compareTo(Object other){
		return name.compareTo(((ProcessId) other).name);
	}
	
	public int hashCode() {
	    return name.hashCode();
	}

	public String toString(){ return name; }
}
