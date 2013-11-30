package framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Env {
    public static final long DELAY = 0;
    static final ProcessId envPID = new ProcessId("ENV", false);
    
    public static void main (String[] args) {
        Env env = new Env();
        env.run(args);
    }
    
    Map<ProcessId, Node> nodes = new HashMap<ProcessId, Node>();
    Map<ProcessId, AntiEntropy> AEs = new HashMap<ProcessId, AntiEntropy>();
    BufferedReader  reader;
    ProcessId connected;
    final ProcessId primary;
    boolean paused = false;
    
    public Env () {
        reader = new BufferedReader (new InputStreamReader (System.in));
        
        primary = new ProcessId("Process:0", false);
        PrimaryNode primaryNode = new PrimaryNode(primary, this);
        nodes.put(primary, primaryNode);
        connected = primary;
        print("Added primary node:\t" + primary);
        primaryNode.start();
    }
    
    public synchronized void sendMessage (ProcessId dst, BayouMessage m) {
        if (m.src.equals(envPID) || AEs.containsKey(m.src) && !AEs.get(m.src).parent.ignoring || nodes.containsKey(m.src) && !nodes.get(m.src).ignoring) {
            if (AEs.containsKey(dst) && AEs.get(dst).alive) {
                AEs.get(dst).deliver(m);
            } else if (nodes.containsKey(dst) && nodes.get(dst).alive) {
                nodes.get(dst).deliver(m);
            }
        }
    }
    
    public synchronized void addNode (ProcessId pID) {
        if (nodes.containsKey(pID)) {
            print(pID + " already exists!");
            return;
        }
        Node newNode = new Node(pID, this);
        nodes.put(pID, newNode);
        print("Added node:\t" + pID);
        newNode.start();
    }
    
    public synchronized void retire (ProcessId pID) {
        if (nodes.containsKey(pID)) {
            sendMessage (pID, new RetireMessage(envPID));
            nodes.remove(pID);
            print("Removed node:\t" + pID);
        } else
            print (pID + " does not exist!");
    }
    
    public synchronized void isolate (ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = true;
        print("Isolated node:\t" + pID);
    }
    
    public synchronized void reconnect (ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = false;
        print("Reconnected node:\t" + pID);
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
            nodes.get(pID).notify();
        }
        print ("Environment Unpaused");
    }
    
    public synchronized void breakConnection (ProcessId p1, ProcessId p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            nodes.get(p1).microIgnore.add(p2);
            nodes.get(p2).microIgnore.add(p1);
            print("Broke connection:\t" + p1 + "\t" + p2);
        } else
            print("One of these processIDs doesn't exist!");
    }
    
    public synchronized void recoverConnection (ProcessId p1, ProcessId p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            nodes.get(p1).microIgnore.remove(p2);
            nodes.get(p2).microIgnore.remove(p1);
            print("Recovered connection:\t" + p1 + "\t" + p2);
        } else
            print("One of these processIDs doesn't exist!");
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
    
    public synchronized void printAllDBs () {
        for (ProcessId pID : nodes.keySet()) {
            nodes.get(pID).printDB();
        }
    }
    
    public synchronized void printDB(ProcessId pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).printDB();
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
        System.out.print("BAYOU$ ");
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
        System.out.println("[ENV]\t" + System.currentTimeMillis() + "\t" + s);
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
