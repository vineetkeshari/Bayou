package framework;

import playlist.operations.Operation;

public class Update implements Comparable<Update> {
    final long created = System.currentTimeMillis();
    Operation operation;
    
    public Update (Operation operation) {
        this.operation = operation;
    }
    
    public String toString () {
        return String.valueOf(created) + " " + operation;
    }
    
    public boolean equals (Object other) {
        if (!(other instanceof Update))
            return false;
        else {
            Update o = (Update)other;
            return this.created == o.created && this.operation.equals(o.operation);
        }
    }
    
    public int compareTo (Update other) {
        return (int)(created - other.created);
    }

}
