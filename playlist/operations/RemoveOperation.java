package playlist.operations;

import playlist.Playlist;

public class RemoveOperation extends Operation {
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
