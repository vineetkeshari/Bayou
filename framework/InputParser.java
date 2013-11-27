package framework;

public class InputParser {
    public static boolean parseInput (String input, Env env) {
        if (input == null)
            return false;
        
        input = input.trim();
        String[] parts = input.split("\t");
        if (parts.length == 1) {
            if (parts[0].equals("END")) {
                return false;
            } else if (parts[0].equals("PRINTLOG")) {
                env.printAllLogs();
            } else if (parts[0].equals("PAUSE")) {
                env.pause();
            } else if (parts[0].equals("CONTINUE")) {
                env.unpause();
            }
        } else if (parts.length == 2) {
            if (parts[0].equals("JOIN")) {
                env.addNode(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("LEAVE")) {
                env.retire(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("ISOLATE")) {
                env.isolate(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("RECONNECT")) {
                env.reconnect(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("PRINTLOG")) {
                env.printLog(Integer.parseInt(parts[1]));
            } else if (parts[0].equals("CONNECT")) {
                env.connect(Integer.parseInt(parts[1]));
            }
        } else if (parts.length == 3) {
            if (parts[0].equals("BREAK")) {
                env.breakConnection (Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } else if (parts[0].equals("RECOVER")) {
                env.recoverConnection (Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            }
        } else if (parts.length == 4) {
            
        }
        
        return true;
    }

}
