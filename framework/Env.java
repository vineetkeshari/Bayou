package framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Env {
    public static void main (String[] args) {
        Env env = new Env();
        env.run(args);
    }
    
    private static final ProcessId pID = new ProcessId("ENV");
    
    Map<ProcessId, Node> nodes = new HashMap<ProcessId, Node>();
    BufferedReader  reader;
    ProcessId connected;
    boolean paused = false;
    Map<ProcessId, Set<ProcessId>> ignore = new HashMap<ProcessId, Set<ProcessId>>();
    
    public Env () {
        reader = new BufferedReader (new InputStreamReader (System.in));
    }
    
    public synchronized void sendMessage (ProcessId dst, BayouMessage m) {
        if (nodes.containsKey(dst) && !nodes.get(dst).ignoring && !ignore.get(dst).contains(m.src)) {
            nodes.get(dst).deliver(m);
        }
    }
    
    public synchronized void addNode (ProcessId pID) {
        if (nodes.containsKey(pID)) {
            print(pID + " already exists!");
            return;
        }
        Node newNode = new Node(pID, this);
        nodes.put(pID, newNode);
        ignore.put(pID, new HashSet<ProcessId>());
        print("Added node:\t" + pID);
        newNode.start();
    }
    
    public synchronized void retire (ProcessId pID) {
        if (nodes.containsKey(pID)) {
            sendMessage (pID, new RetireMessage(this.pID));
            nodes.remove(pID);
            print("Removed node:\t" + pID);
        } else
            print (pID + " does not exist!");
    }
    
    public synchronized void isolate (ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = true;
    }
    
    public synchronized void reconnect (ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = false;
    }
    
    public synchronized void pause () {
        paused = true;
        for (ProcessId pID : nodes.keySet()) {
            nodes.get(pID).paused = true;
        }
        print ("Environment Paused");
    }
    
    public synchronized void unpause () {
        paused = false;
        for (ProcessId pID : nodes.keySet()) {
            nodes.get(pID).paused = false;
        }
        print ("Environment Unpaused");
    }
    
    public synchronized void breakConnection (ProcessId p1, ProcessId p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            ignore.get(p1).add(p2);
            ignore.get(p2).add(p1);
        }
    }
    
    public synchronized void recoverConnection (ProcessId p1, ProcessId p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            ignore.get(p1).remove(p2);
            ignore.get(p2).remove(p1);
        }
    }
    
    public synchronized void printAllLogs () {
        for (ProcessId pID : nodes.keySet()) {
            nodes.get(pID).printLog();
        }
    }
    
    public synchronized void printLog(ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).printLog();
    }
    
    public synchronized void connect (ProcessId pID) {
        if (nodes.containsKey(pID)) {
            connected = pID;
            print ("Connected to " + pID);
        } else {
            print (pID + " does not exist!");
        }
    }
    
    private String getNextInput() {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            System.out.println("Error reading input!");
            line = null;
        }
        return line;
    }
    
    public void run (String[] args) {
        while (true) {
            String input = getNextInput();
            if (!InputParser.parseInput(input, this)) {
                print("Goodbye!");
                die();
            }
        }
    }
    
    private void print (String s) {
        System.out.println("[ENV]\t" + s);
    }
    
    private void die() {
        try {
            reader.close();
        } catch (IOException e) {
            print ("IOException is close!");
        } finally {
            System.exit(1);
        }
    }
    
}
