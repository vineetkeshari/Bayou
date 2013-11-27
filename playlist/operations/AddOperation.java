package playlist.operations;

import playlist.Playlist;

public class AddOperation extends Operation {
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