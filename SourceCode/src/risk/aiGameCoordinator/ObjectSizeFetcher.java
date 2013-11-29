package risk.aiGameCoordinator;

import java.lang.instrument.Instrumentation;

import risk.commonObjects.GameState;

public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
    
    public static void main(String[]args) {
    	System.out.println(ObjectSizeFetcher.getObjectSize(new GameState()));
    }
}