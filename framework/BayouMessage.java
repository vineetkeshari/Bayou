package framework;

import java.util.Map;

import playlist.operations.Operation;

public class BayouMessage {
    ProcessId src;
    
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
    Update update;
    public ActionMessage (ProcessId src, Update update) {
        super(src);
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tACTION\t" + String.valueOf(src) + "\t" + update; 
    }
}

class ActionUpdateMessage extends BayouMessage {
    ProcessId srcNode;
    Update update;
    public ActionUpdateMessage (ProcessId src, ProcessId srcNode, Update update) {
        super(src);
        this.srcNode = srcNode;
        this.update = update;
    }
    public String toString() {
        return "\t[MESSAGE]\tACTIONUPDATE\t" + String.valueOf(src) + "\t" + srcNode + "\t" + update; 
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
    VectorClock vectorClock;
    public StateMessage (ProcessId src, VectorClock vectorClock) {
        super(src);
        this.vectorClock = vectorClock;
    }
    public String toString() {
        return "\t[MESSAGE]\tSTATE\t" + String.valueOf(src) + "\t" + vectorClock; 
    }
}

