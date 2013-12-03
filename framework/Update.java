package framework;

import playlist.operations.Operation;

public class Update implements Comparable<Update> {
    static final long INFINITY = 999999999999999999L;
    final long created = System.currentTimeMillis();
    final Operation operation;
    final ProcessId server;
    long CSN = INFINITY;
    
    public Update (Operation operation, ProcessId server) {
        this.operation = operation;
        this.server = server;
    }
    
    public String toString () {
        return String.valueOf(created) + " " + server + " " + operation + ((CSN==INFINITY)? "":"\t[COMMITTED]" + " " + CSN);
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof Update))
            return false;
        else {
            Update o = (Update)other;
            return this.created == o.created && this.operation.equals(o.operation) && this.server.equals(o.server);
        }
    }
    
    public int hashCode () {
        return Long.valueOf(created).hashCode() + operation.hashCode() + server.hashCode();
    }
    
    public int compareTo (Update other) {
        if (CSN < other.CSN)
            return -1;
        if (CSN > other.CSN)
            return 1;
        if (CSN != INFINITY)
            return 0;
        if (created != other.created)
            return (int)(created - other.created);
        return (int)(server.compareTo(other.server));
            
    }

}
