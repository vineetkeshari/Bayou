package framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VectorClock {
    Map<ProcessId, Long> clock = new HashMap<ProcessId, Long>();
    
    public void put (ProcessId p, long l) {
        clock.put(p,l);
    }
    
    public long get (ProcessId p) {
        return clock.get(p);
    }
    
    public long remove (ProcessId p) {
        return clock.remove(p);
    }
    
    public Set<ProcessId> keySet() {
        return clock.keySet();
    }
    
    public boolean containsKey(ProcessId p) {
        return clock.containsKey(p);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (ProcessId p : clock.keySet()) {
            sb.append("\n\t\t\t" + p + "\t" + clock.get(p));
        }
        return new String(sb);
    }
    
    public VectorClock clone() {
        VectorClock newVC = new VectorClock();
        for (ProcessId p : clock.keySet()) {
            newVC.put(p, clock.get(p));
        }
        return newVC;
    }
}
