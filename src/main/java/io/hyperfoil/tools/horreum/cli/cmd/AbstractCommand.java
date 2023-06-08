package io.hyperfoil.tools.horreum.cli.cmd;

import io.hyperfoil.tools.horreum.cli.srv.RunSvc;
import io.quarkus.logging.Log;
import org.apache.http.conn.HttpHostConnectException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
            //TODO:: this error should be logged and not displayed ot user
            // we should have already displayed an error message
            System.err.println("Failed: ".concat(e.getMessage()));
        }
    }

}
