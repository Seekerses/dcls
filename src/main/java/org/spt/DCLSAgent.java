package org.spt;

import lombok.extern.slf4j.Slf4j;
import org.spt.controller.AgentController;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

@Slf4j
public class DCLSAgent {

    public static void premain(String args, Instrumentation instrumentation) {
            new Thread(() -> {
                try {
                    new AgentController(args, instrumentation).start();
                } catch (InterruptedException e) {
                    log.error("Java Agent was interrupted. DCLS stopped...");
                } catch (IOException e) {
                    log.error("DCLS failed to start. Reason : ", e.getCause());
                }
            }).start();
    }
}