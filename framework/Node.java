package framework;

import playlist.Playlist;

public class Node extends Thread {
    private static final long DELAY = 0;
    
    ProcessId pID;
    Env env;
    Queue<BayouMessage> inbox = new Queue<BayouMessage>();
    
    Playlist db = new Playlist();
    
    boolean ignoring = false;
    boolean paused = false;
    
    public Node(ProcessId pID, Env env) {
        this.pID = pID;
        this.env = env;
    }
    
    @Override
    public void run() {
        body();
        env.retire(pID);
    }
    
    private void body() {
        while (true) {
            while (paused) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    print("InterruptedException in pause!");
                }
            }
            BayouMessage msg = getNextMessage();
            if (msg != null) {
                handle(msg);
            }
        }
    }
    
    private void handle (BayouMessage msg) {
        if (msg instanceof RetireMessage) {
            RetireMessage m = (RetireMessage)msg;
            if (m.src.equals("ENV"))
                retire();
        } else if (msg instanceof ActionMessage) {
            ActionMessage m = (ActionMessage)msg;
        }
        
        
        
    }
    
    private void retire () {
        
    }
    
    public void printLog () {
        
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
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            print("InterruptedException is sleep!");
        }
    }
    
    public String toString () {
        return String.valueOf(pID);
    }
    
    @Override
    public boolean equals (Object o) {
        if (!(o instanceof Node))
            return false;
        return this.pID == ((Node)o).pID;
    }
    
    @Override
    public int hashCode () {
        return pID.hashCode();
    }
    
    private void print (String s) {
        System.out.println("[" + pID + "]\t" + s);
    }
    
}
