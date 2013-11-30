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
    long CSN = 0, committedSN = 0;
    
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
    
    protected boolean canAccept (BayouMessage msg) {
        if (msg.src.isAE)
            return (!ignoring && !microIgnore.contains(env.AEs.get(msg.src).parent.pID));
        else
            return (!ignoring && !microIgnore.contains(msg.src)) || msg.src.equals(env.envPID);
    }
    
    protected void handle (BayouMessage msg) {
        if (msg instanceof RetireMessage) {
            handleRetire((RetireMessage)msg);
        } else if (msg instanceof GetStateMessage) {
            handleGetState((GetStateMessage)msg);
        } else if (msg instanceof ActionMessage) {
            handleAction((ActionMessage)msg);
        } else if (msg instanceof ActionUpdateMessage) {
            handleActionUpdate((ActionUpdateMessage)msg);
        } else if (msg instanceof CommitMessage) {
            handleCommit((CommitMessage)msg);
        }
        
        
        
    }
    
    protected void handleRetire (RetireMessage m) {
        print(m.toString());
        if (m.src.equals(env.envPID))
            retire();
    }
    
    protected void handleGetState (GetStateMessage m) {
        print(m.toString());
        sendMessage(m.src, new StateMessage(pID, vectorClock, CSN));
    }
    
    protected void handleAction (ActionMessage m) {
        print(m.toString());
        write (m.src, m.update);
        propagate ();
    }
    
    protected void handleActionUpdate (ActionUpdateMessage m) {
        print(m.toString());
        write (m.srcNode, m.update);
    }
    
    protected void handleCommit (CommitMessage m) {
        print(m.toString());
        
        if (log.contains(m.update))
            log.remove(m.update);
        log.add(m.update);
        
        boolean canCommit = false;
        for (Update u : log) {
            if (!canCommit && u.CSN > CSN)
                canCommit = true;
            if (canCommit)
                if (u.CSN == CSN+1) {
                    commit(u);
                    CSN = u.CSN;
                } else
                    break;
        }
    }
    
    protected void write (ProcessId updateSrc, Update update) {
        log.add(update);
        if (!vectorClock.containsKey(updateSrc) || update.created > vectorClock.get(updateSrc))
            vectorClock.put(updateSrc, update.created);
    }
    
    protected void propagate () {
        for (ProcessId p : env.nodes.keySet()) {
            if (!p.equals(pID)) {
                new AntiEntropy (this, p, env).start();
            }
        }
    }
    
    protected void commit (Update update) {
        update.operation.perform(db);
    }
    
    private void retire () {
        alive = false;
    }
    
    public void printLog () {
        print ("LOG:");
        for (Update u : log) {
            System.out.println("\t\t\t\t" + u);
        }
    }
    
    public void printDB () {
        print ("DB:");
        System.out.print(db);
    }
    
    protected BayouMessage getNextMessage() {
        return inbox.bdequeue();
    }
    
    protected void sendMessage(ProcessId dst, BayouMessage msg) {
        env.sendMessage(dst, msg);
        delay();
    }
    
    public void deliver (BayouMessage msg) {
        inbox.enqueue(msg);
    }
    
    protected void delay() {
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
    
    protected void print (String s) {
        System.out.println("[" + pID + "]\t" + System.currentTimeMillis() + "\t" + s);
    }
    
}
