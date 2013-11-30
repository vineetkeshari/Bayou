package framework;

import playlist.operations.*;

public class InputParser {
    private static final ProcessId pID = new ProcessId("ENV", false);
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
                env.addNode(generatePID(parts[1]));
            } else if (parts[0].equals("LEAVE")) {
                env.retire(generatePID(parts[1]));
            } else if (parts[0].equals("ISOLATE")) {
                env.isolate(generatePID(parts[1]));
            } else if (parts[0].equals("RECONNECT")) {
                env.reconnect(generatePID(parts[1]));
            } else if (parts[0].equals("PRINTLOG")) {
                env.printLog(generatePID(parts[1]));
            } else if (parts[0].equals("CONNECT")) {
                env.connect(generatePID(parts[1]));
            } else if (parts[0].equals("REMOVE")) {
                if (env.nodes.containsKey(env.connected)) {
                    env.sendMessage(env.connected, new ActionMessage (pID, new Update (new RemoveOperation (input, parts[1]))));
                } else {
                    print("No connected process!");
                }
            }
        } else if (parts.length == 3) {
            if (parts[0].equals("BREAK")) {
                env.breakConnection (generatePID(parts[1]), generatePID(parts[2]));
            } else if (parts[0].equals("RECOVER")) {
                env.recoverConnection (generatePID(parts[1]), generatePID(parts[2]));
            } else if (parts[0].equals("ADD")) {
                if (env.nodes.containsKey(env.connected)) {
                    env.sendMessage(env.connected, new ActionMessage (pID, new Update (new AddOperation (input, parts[1], parts[2]))));
                } else {
                    print("No connected process!");
                }
            }
        } else if (parts.length == 4) {
            if (parts[0].equals("EDIT")) {
                if (env.nodes.containsKey(env.connected)) {
                    env.sendMessage(env.connected, new ActionMessage (pID, new Update (new EditOperation (input, parts[1], parts[2], parts[3]))));
                } else {
                    print("No connected process!");
                }
            }
        }
        
        return true;
    }
    
    private static ProcessId generatePID (String pID) {
        return new ProcessId ("Process:" + pID, false);
    }
    
    private static void print (String s) {
        System.out.println ("[PARSER]\t" + System.currentTimeMillis() + "\t" + s);
    }

}