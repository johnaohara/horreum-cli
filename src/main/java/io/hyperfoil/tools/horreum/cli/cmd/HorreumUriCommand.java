package io.hyperfoil.tools.horreum.cli.cmd;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import picocli.CommandLine;

@CommandLine.Command(name = "horreum-uri", description = "Show horreum current URI")
public class HorreumUriCommand implements Runnable{
    @ConfigProperty(name = "horreum.uri")
    String horreumUri;

    @Override
    public void run() {
        System.out.println(horreumUri);
    }
}
