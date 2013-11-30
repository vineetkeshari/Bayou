package framework;

import playlist.operations.Operation;

public class Update implements Comparable<Update> {
    final long created = System.currentTimeMillis();
    final Operation operation;
    final ProcessId server;
    long CSN = 999999999999999999L;
    
    public Update (Operation operation, ProcessId server) {
        this.operation = operation;
        this.server = server;
    }
    
    public String toString () {
        return String.valueOf(created) + " " + operation;
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof Update))
            return false;
        else {
            Update o = (Update)other;
            return this.created == o.created && this.operation.equals(o.operation) && this.CSN == o.CSN;
        }
    }
    
    public int compareTo (Update other) {
        return (int)(created - other.created);
    }

}
