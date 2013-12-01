package framework;

public class AntiEntropy extends Thread {
    private final long created = System.currentTimeMillis();
    ProcessId pID, dest;
    Node parent;
    Env env;
    Queue<BayouMessage> inbox = new Queue<BayouMessage>();
    
    boolean alive = true;
    
    public AntiEntropy(Node parent, ProcessId dest, Env env) {
        this.pID = new ProcessId("AE " + parent.pID + " " + dest, true);
        this.parent = parent;
        this.dest = dest;
        this.env = env;
    }
    
    @Override
    public void run() {
        env.AEs.put(pID, this);
        sendMessage(dest, new GetStateMessage(pID, parent.pID));
        body();
        retire();
    }
    
    private void body() {
        while (alive) {
            BayouMessage msg = getNextMessage();
            if (msg != null) {
                handle(msg);
            }
        }
    }
    
    private void handle (BayouMessage msg) {
        if (msg instanceof StateMessage) {
            StateMessage m = (StateMessage)msg;
            print(m.toString());
            if (msg.src.equals(dest)) {
                long destCSN = m.CSN;
                VectorClock destVC = m.vectorClock;
                print("Parent OSN: " + parent.OSN);
                if (parent.OSN > destCSN) {
                    sendMessage (dest, new DBUpdateMessage (pID, parent.pID, parent.omitDB, parent.omitVC, parent.OSN));
                }
                
                if (destCSN < parent.CSN) {
                    boolean canSend = false;
                    for (Update u : parent.log) {
                        if (!canSend && u.CSN > destCSN)
                            canSend = true;
                        if (canSend) {
                            if (destVC.containsKey(parent.pID) && u.created <= destVC.get(parent.pID))
                                sendMessage (dest, new CommitMessage(pID, parent.pID, u));
                            else
                                sendMessage(dest, new ActionUpdateMessage(pID, parent.pID, u));
                        }
                    }
                }
                
                for (Update u : parent.log) {
                    if (!destVC.containsKey(parent.pID) || destVC.get(parent.pID) < u.created) {
                        sendMessage(dest, new ActionUpdateMessage(pID, parent.pID, u));
                    }
                }
            }
        }
        
        
        
    }
    
    private void retire () {
        try {
            Thread.sleep(env.DELAY*10);
        } catch (InterruptedException e) {
            print ("InterruptedException in sleep!");
        }
        alive = false;
        env.AEs.remove(pID);
    }
    
    private BayouMessage getNextMessage() {
        return inbox.bdequeue();
    }
    
    private void sendMessage(ProcessId dst, BayouMessage msg) {
        env.sendMessage(dst, msg);
        delay();
    }
    
    public void deliver (BayouMessage msg) {
        inbox.enqueue(msg);
    }
    
    private void delay() {
        try {
            Thread.sleep(env.DELAY);
        } catch (InterruptedException e) {
            print("InterruptedException is sleep!");
        }
    }
    
    public String toString () {
        return String.valueOf(pID);
    }
    
    @Override
    public boolean equals (Object other) {
        if (!(other instanceof AntiEntropy))
            return false;
        else {
            AntiEntropy o = (AntiEntropy)other;
            return this.pID.equals(o.pID) && this.parent.equals(o.parent) && this.created == o.created;
        }
    }
    
    @Override
    public int hashCode () {
        return pID.hashCode() + parent.hashCode() + Long.valueOf(created).hashCode();
    }
    
    private void print (String s) {
        System.out.println("[" + pID + "]\t" + System.currentTimeMillis() + "\t" + s);
    }
    
}
