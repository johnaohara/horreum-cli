package io.hyperfoil.tools.horreum.cli.cmd;

import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.ApiUtil;
import io.hyperfoil.tools.horreum.api.SortDirection;
import io.hyperfoil.tools.horreum.api.data.Access;
import io.hyperfoil.tools.horreum.api.data.Test;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;

@CommandLine.Command(name = "test", description = "List all tests", subcommands = {
        TestInfoCommand.class,
        TestListCommand.class,
        TestNewCommand.class
})
public class Tests {
}

@CommandLine.Command(name = "info", description = "Print detail of a test")
class TestInfoCommand extends AbstractCommand {
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

@CommandLine.Command(name = "new", description = "Create new test")
class TestNewCommand extends AbstractCommand {
    @Inject
    HorreumClient client;


    @CommandLine.Option(names = {"-n", "--name"}, description = "Test name", required = true)
    public String name;
    @CommandLine.Option(names = {"-o", "--owner"}, description = "Test owner", required = true)
    public String owner;
    @CommandLine.Option(names = {"-a", "--access"}, description = "Test access level", required = true)
    public String access;

    @CommandLine.Option(names = {"-f", "--folder"}, description = "Test folder in remote Horreum instance", required = false)
    public String folder;
    @CommandLine.Option(names = {"-d", "--description"}, description = "Test description", required = false)
    public String description;


    @Override
    public void runCmd() {
        if ( client != null ) {
            Test newTest = new Test();
            newTest.owner = owner;
            newTest.access = Access.fromString(access);
            newTest.name = name;
            if ( description != null)
                newTest.description = description;
            if ( folder != null)
                newTest.folder = folder;

            Test uploadedtest = client.testService.add(newTest);

            System.out.println("New Test created: ".concat(String.valueOf(uploadedtest.id)).concat(" - ").concat(uploadedtest.name));
        }
    }
}


