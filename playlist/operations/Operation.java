package playlist.operations;

import playlist.Playlist;

public abstract class Operation {
    String text;
    
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
        return text.hashCode();
    }
}

