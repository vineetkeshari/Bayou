package playlist;

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

class AddOperation extends Operation {
    String song, url;
    
    public AddOperation (String text, String song, String url) {
        super (text);
        this.song = song;
        this.url = url;
    }
    
    @Override
    public void perform (Playlist p) {
        p.addSong(song, url);
    }
}

class RemoveOperation extends Operation {
    String song;
    
    public RemoveOperation (String text, String song) {
        super (text);
        this.song = song;
    }
    
    @Override
    public void perform (Playlist p) {
        p.removeSong(song);
    }
}

class EditOperation extends Operation {
    String song, newSong, url;
    
    public EditOperation (String text, String song, String newSong, String url) {
        super (text);
        this.song = song;
        this.newSong = newSong;
        this.url = url;
    }
    
    @Override
    public void perform (Playlist p) {
        p.editSong(song, newSong, url);
    }
}
