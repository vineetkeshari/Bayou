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
    
    Map<Integer, Node> nodes = new HashMap<Integer, Node>();
    BufferedReader  reader;
    int connected;
    Map<Integer, Set<Integer>> ignore = new HashMap<Integer, Set<Integer>>();
    
    public Env () {
        reader = new BufferedReader (new InputStreamReader (System.in));
    }
    
    public synchronized void sendMessage (int dst, BayouMessage m) {
        if (nodes.containsKey(dst) && !nodes.get(dst).ignoring && !ignore.get(dst).contains(m.src)) {
            nodes.get(dst).deliver(m);
        }
    }
    
    public synchronized void addNode (int pID) {
        if (pID == 0 || nodes.containsKey(pID)) {
            print(pID + " already exists!");
            return;
        }
        Node newNode = new Node(pID, this);
        nodes.put(newNode.pID, newNode);
        ignore.put(newNode.pID, new HashSet<Integer>());
        print("Added node:\t" + newNode.pID);
        newNode.start();
    }
    
    public synchronized void retire (int pID) {
        if (nodes.containsKey(pID)) {
            sendMessage (pID, new RetireMessage(0));
            nodes.remove(pID);
            print("Removed node:\t" + pID);
        } else
            print (pID + " does not exist!");
    }
    
    public synchronized void isolate (int pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = true;
    }
    
    public synchronized void reconnect (int pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).ignoring = false;
    }
    
    public synchronized void pause () {
        for (int pID : nodes.keySet()) {
            nodes.get(pID).paused = true;
        }
    }
    
    public synchronized void unpause () {
        for (int pID : nodes.keySet()) {
            nodes.get(pID).paused = false;
        }
    }
    
    public synchronized void breakConnection (int p1, int p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            ignore.get(p1).add(p2);
            ignore.get(p2).add(p1);
        }
    }
    
    public synchronized void recoverConnection (int p1, int p2) {
        if (nodes.containsKey(p1) && nodes.containsKey(p2)) {
            ignore.get(p1).remove(p2);
            ignore.get(p2).remove(p1);
        }
    }
    
    public synchronized void printAllLogs () {
        for (int pID : nodes.keySet()) {
            nodes.get(pID).printLog();
        }
    }
    
    public synchronized void printLog(int pID) {
        if (nodes.containsKey(pID))
            nodes.get(pID).printLog();
    }
    
    public synchronized void connect (int pID) {
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
