package io.hyperfoil.tools.horreum.cli.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hyperfoil.tools.HorreumClient;
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
class TestListCommand implements Runnable {
    @Inject
    HorreumClient client;

    @Override
    public void run() {
        try {
            List<Test> tests =  client.testService.list(
                    null,
                    null,
                    null,
                    "name",
                    SortDirection.Ascending
            );

            System.out.println("Found Tests: ");
            tests.stream().map(t -> t.id.toString().concat(" - ") .concat(t.name)).forEach(System.out::println);

        }
        catch (Exception e){
            System.err.println("Failed: ".concat(e.getMessage()));
            e.printStackTrace();
        }
    }
}

@CommandLine.Command(name = "info", description = "Print detail of a test")
class TestDetailCommand implements Runnable{
    @Inject
    HorreumClient client;

    @CommandLine.Option(names = {"-i", "--id"}, description = "Test ID", required = true)
    String testID;

    @Override
    public void run() {

        Integer ItestId = Integer.parseInt(testID);
        try {

            Test test = client.testService.get(ItestId, null);

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        } catch (Exception e) {
            System.err.println("ERROR: ".concat(e.getMessage()));
        }
    }

}

