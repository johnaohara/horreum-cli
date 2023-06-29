package io.hyperfoil.tools.horreum.cli.srv;

import com.fasterxml.jackson.databind.JsonNode;
import io.hyperfoil.tools.HorreumClient;
import io.hyperfoil.tools.horreum.api.ApiUtil;
import io.hyperfoil.tools.horreum.api.SortDirection;
import io.hyperfoil.tools.horreum.api.data.Access;
import io.hyperfoil.tools.horreum.api.data.DataSet;
import io.hyperfoil.tools.horreum.api.services.ExperimentService;
import io.hyperfoil.tools.horreum.api.services.RunService;
import io.hyperfoil.tools.horreum.api.services.RunService.RunsSummary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.specimpl.AbstractBuiltResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;


@ApplicationScoped
public class RunSvc {

    @Inject
    HorreumClient client;

    @ConfigProperty(name = "horreum.uri")
    String horreumUri;

    public RunsSummary list(int testID) {
        return client.runService.listTestRuns(
                testID,
                false,
                null,
                null,
                "name",
                SortDirection.Ascending
        );
    }

    public DataSet dataSet(int datasetID) {
        return client.datasetService.getDataSet(datasetID);
    }

    public RunService.RunSummary runSummary(int runID) {
        return client.runService.getRunSummary(runID, null);
    }

    public Object runData(int runID) {
        return client.runService.getData(runID, null, null);
    }

    public List<ExperimentService.ExperimentResult> executeExperiment(int datasetID) {
        return client.experimentService.runExperiments(datasetID);
    }

    public String upload(String jsonFile, String start, String stop, String test, String owner, String s, String access, String schema) {
        JsonNode data = loadFile(Path.of(jsonFile));
        if (data == null) {
            return null; //error has occurred
        }
        Access mappedAccess = Access.fromString(access);

        Response response = null;
        try {
            response = client.runService.addRunFromData(start, stop, test, owner, mappedAccess, null, schema, null, data);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        if (response != null && response.getStatus() >= 300) {
            String reponseBody = "";
            if ( response.getEntity() != null && response.getEntity() instanceof FilterInputStream){
                try {
                    reponseBody = new String(((FilterInputStream)response.getEntity()).readAllBytes(), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
            System.err.println(
                    "ERROR: ("
                    .concat(response.getStatusInfo().toString()).concat("): ")
                    .concat(reponseBody)
            );
        } else {
            String location = response.getHeaderString("Location");
            String newRunUrl = horreumUri.concat(response.getHeaderString("Location")).concat("#run");
            System.out.println("new run uploaded: ".concat(newRunUrl));
            return location;
        }
        return null; //error has occurred

    }


    private JsonNode loadFile(Path uploadFile) {
        try {
            return ApiUtil.OBJECT_MAPPER.readTree(uploadFile.toFile());
        } catch (IOException e) {
            System.err.println("File for upload cannot be read: " + uploadFile + " - " + e.getMessage());
            return null;
        }
    }

}
