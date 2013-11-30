package playlist.operations;

import playlist.Playlist;

public abstract class Operation {
    private final long created = System.currentTimeMillis();
    final String text;
    
    public Operation (String text) {
        this.text = text;
    }
    
    public abstract void perform (Playlist p);
    
    public String text() {
        return text;
    }
    
    public String toString() {
        return text;
    }
    
    public int hashCode() {
        return text.hashCode() + Long.valueOf(created).hashCode();
    }
    
    public boolean equals(Object other) {
        if (!(other instanceof Operation))
            return false;
        else {
            Operation o = (Operation)other;
            return text.equals(o.text) && created == o.created;
        }
    }
}

