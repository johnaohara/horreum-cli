package io.hyperfoil.tools.horreum.cli.cmd;

import io.hyperfoil.tools.HorreumClient;
import picocli.CommandLine;

import javax.inject.Inject;

@CommandLine.Command(name = "version", description = "Version of remote Horreum server")
public class ServerVersion implements Runnable {

    @Inject
    HorreumClient client;

    @Override
    public void run() {
        //no-op missing config api from client atm
    }
}
