package io.hyperfoil.tools.horreum.cli;

import io.hyperfoil.tools.horreum.cli.cmd.HorreumUriCommand;
import io.hyperfoil.tools.horreum.cli.cmd.Runs;
import io.hyperfoil.tools.horreum.cli.cmd.Tests;
import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {
        HorreumUriCommand.class,
        Runs.class,
        Tests.class
})
public class EntryCommand {
}

