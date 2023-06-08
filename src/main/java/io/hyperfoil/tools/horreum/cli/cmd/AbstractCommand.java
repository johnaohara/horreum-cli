package io.hyperfoil.tools.horreum.cli.cmd;

import org.apache.http.conn.HttpHostConnectException;

import javax.ws.rs.ProcessingException;

public abstract class AbstractCommand implements Runnable  {

    public abstract void runCmd();

    @Override
    public void run() {
        try {
            runCmd();
        } catch (ProcessingException processingException) {
            if (processingException.getCause() instanceof HttpHostConnectException) {
                System.err.println("Could not connect to Horreum server, please verify configuration and try again");
            } else {
                System.err.println("Failed: ".concat(processingException.getMessage()));
                processingException.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Failed: ".concat(e.getMessage()));
            e.printStackTrace();
        }
    }

}
