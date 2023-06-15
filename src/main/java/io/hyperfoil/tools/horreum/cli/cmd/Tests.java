package io.hyperfoil.tools.horreum.cli.cmd;

import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.ApiUtil;
import io.hyperfoil.tools.horreum.api.SortDirection;
import io.hyperfoil.tools.horreum.api.data.Test;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;

@CommandLine.Command(name = "test", description = "List all tests", subcommands = {
        TestDetailCommand.class,
        TestListCommand.class
})
public class Tests {
}

@CommandLine.Command(name = "list", description = "List all tests")
class TestListCommand extends AbstractCommand {
    @Inject
    HorreumClient client;

    @Override
    public void runCmd() {
        if ( client != null ) {
            List<Test> tests = client.testService.list(
                    null,
                    null,
                    null,
                    "name",
                    SortDirection.Ascending
            );

            System.out.println("Found Tests: ");
            tests.stream().map(t -> t.id.toString().concat(" - ").concat(t.name)).forEach(System.out::println);
        }
    }
}

@CommandLine.Command(name = "info", description = "Print detail of a test")
class TestDetailCommand extends AbstractCommand {
    @Inject
    HorreumClient client;

    @CommandLine.Option(names = {"-i", "--id"}, description = "Test ID", required = true)
    String testID;

    @Override
    public void runCmd() {
        if ( client != null ) {
            try {

                Test test = client.testService.get(Integer.parseInt(testID), null);

                System.out.println(ApiUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(test));
            } catch (Exception e) {
                System.err.println("ERROR: ".concat(e.getMessage()));
            }
        }
    }


}

