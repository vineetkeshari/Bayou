package framework;

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
}

class ActionMessage extends BayouMessage {
    Operation op;
    public ActionMessage (ProcessId src, Operation op) {
        super(src);
        this.op = op;
    }
}

