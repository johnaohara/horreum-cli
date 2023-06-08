package io.hyperfoil.tools.horreum.cli.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.SortDirection;
import io.hyperfoil.tools.horreum.api.data.Test;
import org.apache.http.conn.HttpHostConnectException;
import picocli.CommandLine;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
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

@CommandLine.Command(name = "info", description = "Print detail of a test")
class TestDetailCommand extends AbstractCommand {
    @Inject
    HorreumClient client;

    @CommandLine.Option(names = {"-i", "--id"}, description = "Test ID", required = true)
    String testID;

    @Override
    public void runCmd() {
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

