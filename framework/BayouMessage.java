package framework;

import playlist.Playlist;

public class BayouMessage {
    final ProcessId src;
    
    public BayouMessage(ProcessId src) {
        this.src = src;
    }
}

class RetireMessage extends BayouMessage {
    public RetireMessage (ProcessId src) {
        super(src);
    }
    public String toString() {
        return "\t[MESSAGE]\tRETIRE\t" + String.valueOf(src); 
    }
}

class ActionMessage extends BayouMessage {
    final Update update;
    public ActionMessage (ProcessId src, Update update) {
        super(src);
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tACTION\t" + String.valueOf(src) + "\t" + update; 
    }
}

class ActionUpdateMessage extends BayouMessage {
    final ProcessId srcNode;
    final Update update;
    public ActionUpdateMessage (ProcessId src, ProcessId srcNode, Update update) {
        super(src);
        this.srcNode = srcNode;
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tACTIONUPDATE\t" + String.valueOf(src) + "\t" + srcNode + "\t" + update; 
    }
}

class DBUpdateMessage extends BayouMessage {
    final ProcessId srcNode;
    final Playlist db;
    public DBUpdateMessage (ProcessId src, ProcessId srcNode, Playlist db) {
        super(src);
        this.srcNode = srcNode;
        this.db = db;
    }
    public String toString() {
        return "\t[MESSAGE]\tDBUPDATE\t" + String.valueOf(src) + "\t" + srcNode + "\t" + db; 
    }
}

class GetStateMessage extends BayouMessage {
    public GetStateMessage (ProcessId src) {
        super(src);
    }
    public String toString() {
        return "\t[MESSAGE]\tGETSTATE\t" + String.valueOf(src); 
    }
}

class StateMessage extends BayouMessage {
    final VectorClock vectorClock;
    final long CSN;
    public StateMessage (ProcessId src, VectorClock vectorClock, long CSN) {
        super(src);
        this.vectorClock = vectorClock;
        this.CSN = CSN;
    }
    public String toString() {
        return "\t[MESSAGE]\tSTATE\t" + String.valueOf(src) + "\t" + vectorClock; 
    }
}

class CommitMessage extends BayouMessage {
    final Update update;
    public CommitMessage (ProcessId src, Update update) {
        super(src);
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tCOMMIT\t" + String.valueOf(src) + "\t" + update; 
    }
}

