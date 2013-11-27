package framework;

import playlist.Operation;

public class BayouMessage {
    int src;
    
    public BayouMessage(int src) {
        this.src = src;
    }
}

class RetireMessage extends BayouMessage {
    public RetireMessage (int src) {
        super(src);
    }
}

class ActionMessage extends BayouMessage {
    Operation op;
    public ActionMessage (int src, Operation op) {
        super(src);
        this.op = op;
    }
}

