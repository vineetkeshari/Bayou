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

class StateMessage extends BayouMessage {
    final VectorClock vectorClock;
    final long CSN;
    public StateMessage (ProcessId src, VectorClock vectorClock, long CSN) {
        super(src);
        this.vectorClock = vectorClock;
        this.CSN = CSN;
    }
    public String toString() {
        return "\t[MESSAGE]\tSTATE\t" + String.valueOf(src) + "\t" + CSN + "\t" + vectorClock; 
    }
}

class AntiEntropyMessage extends BayouMessage {
    final ProcessId srcNode;
    
    public AntiEntropyMessage (ProcessId src, ProcessId srcNode) {
        super(src);
        this.srcNode = srcNode;
    }
}

class ActionUpdateMessage extends AntiEntropyMessage {
    final Update update;
    public ActionUpdateMessage (ProcessId src, ProcessId srcNode, Update update) {
        super(src, srcNode);
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tACTIONUPDATE\t" + String.valueOf(src) + "\t" + update; 
    }
}

class DBUpdateMessage extends AntiEntropyMessage {
    final Playlist db;
    final VectorClock omitVC;
    final long OSN;
    public DBUpdateMessage (ProcessId src, ProcessId srcNode, Playlist db, VectorClock omitVC, long OSN) {
        super(src, srcNode);
        this.db = db;
        this.omitVC = omitVC;
        this.OSN = OSN;
    }
    public String toString() {
        return "\t[MESSAGE]\tDBUPDATE\t" + String.valueOf(src) + "\t" + OSN + omitVC + "\n" + db; 
    }
}

class GetStateMessage extends AntiEntropyMessage {
    public GetStateMessage (ProcessId src, ProcessId srcNode) {
        super(src, srcNode);
    }
    public String toString() {
        return "\t[MESSAGE]\tGETSTATE\t" + String.valueOf(src); 
    }
}

class CommitMessage extends AntiEntropyMessage {
    final Update update;
    public CommitMessage (ProcessId src, ProcessId srcNode, Update update) {
        super(src, srcNode);
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tCOMMIT\t" + String.valueOf(src) + "\t" + update; 
    }
}

