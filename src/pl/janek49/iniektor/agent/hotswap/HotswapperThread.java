package pl.janek49.iniektor.agent.hotswap;

import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.AsmUtil;
import pl.janek49.iniektor.agent.Logger;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

public class HotswapperThread extends Thread {
    public Instrumentation instrumentation;

    public HotswapperThread(Instrumentation inst) {
        this.instrumentation = inst;
    }

    @Override
    public void run() {
        Logger.log("HotSwapper Thread initialized");

        while (true) {
            Class reflector = AsmUtil.findClass("pl.janek49.iniektor.client.hook.Reflector");

            boolean trigger = false;

            try {
                Field triggerField = reflector.getDeclaredField("TRIGGER_HOTSWAP");
                trigger = triggerField.getBoolean(null);
                if (trigger)
                    triggerField.set(null, false);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (trigger) {
                trigger = false;
                Logger.log("HotSwapper Triggered");
                try {
                    HotSwapper.HotSwapIniektor(instrumentation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
