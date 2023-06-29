package io.hyperfoil.tools.horreum.cli.cmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.hyperfoil.tools.horreum.api.ApiUtil;
import io.hyperfoil.tools.horreum.api.data.DataSet;
import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.hyperfoil.tools.horreum.api.services.RunService;
import io.hyperfoil.tools.horreum.cli.srv.RunSvc;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.hyperfoil.tools.horreum.cli.cmd.Runs.runExperiment;

@CommandLine.Command(name = "run", description = "manage runs  for a particular test", subcommands = {
        GetDataCommand.class,
        GetDatasetCommand.class,
        GetRunSummary.class,
        ListCommand.class,
        RunRegression.class,
        NewRun.class
//        UploadAndRegression.class
})
public class Runs {
    public static void runExperiment(RunSvc runSvc, String datasetId) {
        RunService.RunSummary summary = runSvc.runSummary(Integer.parseInt(datasetId));

        Integer[] datasets = summary.datasets;

        if (datasets.length == 0) {
            System.err.println("ERROR: No datasets found, please check the run had a valid schema");
        } else {
            System.out.println("Running regression tests!");

            Map<Integer, List<ExperimentService.ExperimentResult>> experimentResults = new HashMap<>();

            Arrays.stream(datasets).forEach(
                    dataSetID -> experimentResults.put(dataSetID, runSvc.executeExperiment(dataSetID))
            );

            experimentResults.forEach((dataSetID, results) -> {
                System.out.println("Dataset: ".concat(dataSetID.toString()));
                results.stream().map(result -> {
                    try {
                        return ApiUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result);
                    } catch (JsonProcessingException e) {
                        System.err.println("Error processing result: ".concat(e.getMessage()));
                        return "";
                    }
                }).forEach(System.out::println);
            });

        }
    }

}

@CommandLine.Command(name = "list", description = "List all runs")
class ListCommand implements Runnable {
    @Inject
    RunSvc runSvc;

    @CommandLine.Option(names = {"-i", "--id"}, description = "Test ID", required = true)
    String testID;

    @Override
    public void run() {
        try {
            RunService.RunsSummary runsSummary = runSvc.list(Integer.parseInt(testID));
            runsSummary.runs.stream().map(run -> Integer.toString(run.id).concat(": ").concat(Long.toString(run.start))).forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("Failed: ".concat(e.getMessage()));
        }
    }

}

@CommandLine.Command(name = "dataset", description = "Get dataset payload")
class GetDatasetCommand implements Runnable {
    @Inject
    RunSvc runSvc;
    @CommandLine.Option(names = {"-i", "--run-id"}, description = "Run ID", required = true)
    String datasetID;

    @Override
    public void run() {
        try {
            DataSet dataSet = runSvc.dataSet(Integer.parseInt(datasetID));

            System.out.println(ApiUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataSet));

        } catch (Exception e) {
            System.err.println("Failed: ".concat(e.getMessage()));
        }

    }
}

@CommandLine.Command(name = "summary", description = "List all datasets")
class GetRunSummary implements Runnable {
    @Inject
    RunSvc runSvc;
    @CommandLine.Option(names = {"-i", "--run-id"}, description = "Run ID", required = true)
    String runID;

    @Override
    public void run() {
        try {
            System.out.println(
                    ApiUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
                            runSvc.runSummary(Integer.parseInt(runID))
                    ));

        } catch (Exception e) {
            System.err.println("Failed: ".concat(e.getMessage()));
        }

    }
}

@CommandLine.Command(name = "data", description = "Get run data payload")
class GetDataCommand extends AbstractCommand {
    @Inject
    RunSvc runSvc;

    @CommandLine.Option(names = {"-i", "--run-id"}, description = "Run ID", required = true)
    String runID;

    @Override
    public void runCmd() {
        try {
            System.out.println(ApiUtil.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(runSvc.runData(Integer.parseInt(runID))));
        } catch (Exception e) {
            System.err.println("Unable to deserialize run data: ".concat(e.getMessage()));
        }
    }
}


@CommandLine.Command(name = "new", description = "Upload a new run")
class NewRun extends AbstractCommand {

    @Inject
    RunSvc runSvc;

    @CommandLine.Option(names = {"-f", "--file"}, description = "File path to upload", required = true)
    String jsonFile;

    @CommandLine.Option(names = {"--start"}, description = "Run start time", required = true)
    String start;

    @CommandLine.Option(names = {"--stop"}, description = "Run stop time", required = true)
    String stop;

    @CommandLine.Option(names = {"--test"}, description = "Test name", required = true)
    String test;

    @CommandLine.Option(names = {"--owner"}, description = "Run owner", required = true)
    String owner;

    @CommandLine.Option(names = {"--access"}, description = "Access: [PUBLIC, PROTECTED, PRIVATE]", required = true)
    String access;

    @CommandLine.Option(names = {"--schema"}, description = "schema URI")
    String schema;

    @Override
    public void runCmd() {
        if ( runSvc != null ) {
            runSvc.upload(jsonFile, start, stop, test, owner, owner, access, schema);
        } else {
            System.err.println("ERROR: Could not instantiate RunSvc client");

        }
    }

}

//@CommandLine.Command(name = "new-with-regression", description = "Upload a new run and test datasets for regression")
//class UploadAndRegression extends UploadRun {
//    @Override
//    public void run() {
//        String location = runSvc.upload(jsonFile, start, stop, test, owner, owner, access, schema);
//
//        String runID = location.split("/")[1]; //todo: make this less fragile
//
//        runExperiment(runSvc, runID);
//
//    }
//
//}

@CommandLine.Command(name = "regression", description = "Perform regression analysis on existing run")
class RunRegression implements Runnable {
    @Inject
    RunSvc runSvc;

    @CommandLine.Option(names = {"-i", "--dataset-id"}, description = "Dataset ID", required = true)
    String datasetId;

    @Override
    public void run() {

        runExperiment(runSvc, datasetId);

    }
}

