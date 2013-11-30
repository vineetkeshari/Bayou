package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import playlist.Playlist;

public class Node extends Thread {
    private final long created = System.currentTimeMillis();
    ProcessId pID;
    Env env;
    Queue<BayouMessage> inbox = new Queue<BayouMessage>();
    Set<ProcessId> microIgnore = new HashSet<ProcessId>();
    
    Playlist db = new Playlist();
    VectorClock vectorClock = new VectorClock();
    Set<Update> log = new TreeSet<Update>();
    Set<Update> commitedLog = new TreeSet<Update>();
    long CSN = 0;
    
    boolean ignoring = false;
    boolean paused = false;
    boolean alive = true;
    
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
    
    private boolean canAccept (BayouMessage msg) {
        if (msg.src.isAE)
            return (!ignoring && !microIgnore.contains(env.AEs.get(msg.src).parent.pID));
        else
            return (!ignoring && !microIgnore.contains(msg.src)) || msg.src.equals(env.envPID);
    }
    
    private void handle (BayouMessage msg) {
        if (msg instanceof RetireMessage) {
            RetireMessage m = (RetireMessage)msg;
            print(m.toString());
            if (m.src.equals(env.envPID))
                retire();
        } else if (msg instanceof GetStateMessage) {
            GetStateMessage m = (GetStateMessage)msg;
            print(m.toString());
            sendMessage(m.src, new StateMessage(pID, vectorClock, CSN));
        } else if (msg instanceof ActionMessage) {
            ActionMessage m = (ActionMessage)msg;
            print(m.toString());
            write (m.src, m.update);
            for (ProcessId p : env.nodes.keySet()) {
                if (!p.equals(pID)) {
                    new AntiEntropy (this, p, env).start();
                }
            }
        } else if (msg instanceof ActionUpdateMessage) {
            ActionUpdateMessage m = (ActionUpdateMessage)msg;
            print(m.toString());
            write (m.srcNode, m.update);
        }
        
        
        
    }
    
    private void write (ProcessId updateSrc, Update update) {
        if (log.add(update))
            update.operation.perform(db);
        vectorClock.put(updateSrc, update.created);
    }
    
    private void retire () {
        alive = false;
    }
    
    public void printLog () {
        print ("LOG:");
        for (Update u : log) {
            System.out.println("\t\t\t" + u.created + "\t" + u.operation);
        }
    }
    
    public void printDB () {
        print ("DB:");
        System.out.println(db);
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
        if (!(other instanceof Node))
            return false;
        else {
            Node o = (Node)other;
            return this.pID.equals(o.pID) && this.created == o.created;
        }
    }
    
    @Override
    public int hashCode () {
        return pID.hashCode() + Long.valueOf(created).hashCode();
    }
    
    private void print (String s) {
        System.out.println("[" + pID + "]\t" + System.currentTimeMillis() + "\t" + s);
    }
    
}
