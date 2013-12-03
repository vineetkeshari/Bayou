package framework;

public class Propagator extends Thread {
    private static final long SLEEPTIME = 5000;
    boolean alive = true;
    ProcessId pID;
    Node server;
    public Propagator (ProcessId pID, Node server) {
        this.pID = pID;
        this.server = server;
    }
    
    @Override
    public void run () {
        while (alive) {
            try {
                sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                System.out.println("\t[Propagator " + pID + "]\tSleep Interrupted!");
            }
            while (server.getState() != Thread.State.WAITING);
            //System.out.println("\tPropogating from " + pID + "...");
            server.propagate();
        }
    }
}
