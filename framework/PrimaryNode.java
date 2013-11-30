package framework;

public class PrimaryNode extends Node {
    
    public PrimaryNode (ProcessId pID, Env env) {
        super(pID, env);
    }
    
    @Override
    public void run() {
        body();
        env.retire(pID);
    }
    
    @Override
    protected void body() {
        while (alive) {
            while (paused) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    print("InterruptedException in pause!");
                }
            }
            BayouMessage msg = getNextMessage();
            if (msg != null && canAccept(msg)) {
                handle(msg);
            }
        }
    }

    @Override
    protected void handle (BayouMessage msg) {
        if (msg instanceof RetireMessage) {
            handleRetire((RetireMessage)msg);
        } else if (msg instanceof GetStateMessage) {
            handleGetState((GetStateMessage)msg);
        } else if (msg instanceof ActionMessage) {
            handleAction((ActionMessage)msg);
        } else if (msg instanceof ActionUpdateMessage) {
            handleActionUpdate((ActionUpdateMessage)msg);
        }
        
        
        
    }

    @Override
    protected void handleAction (ActionMessage m) {
        print(m.toString());
        write (m.src, m.update);
        updateCommits();
        propagate();
    }

    @Override
    protected void handleActionUpdate (ActionUpdateMessage m) {
        print(m.toString());
        write (m.srcNode, m.update);
        updateCommits();
        propagate();
    }
    
    protected void updateCommits () {
        for (Update u : log) {
            if (u.CSN==Update.INFINITY) {
                commit (u);
                u.CSN = ++CSN;
            }
        }
    }
    
    

}
