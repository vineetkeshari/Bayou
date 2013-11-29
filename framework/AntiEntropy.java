package framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import playlist.Playlist;

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
        env.sendMessage(dest, new GetStateMessage(pID));
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
                VectorClock destState = m.vectorClock;
                for (Update u : parent.log) {
                    if (!destState.containsKey(parent.pID) || destState.get(parent.pID) < u.created) {
                        env.sendMessage(dest, new ActionUpdateMessage(pID, parent.pID, u));
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
