package playlist.operations;

import playlist.Playlist;

public class EditOperation extends Operation {
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
