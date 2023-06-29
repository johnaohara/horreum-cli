package io.hyperfoil.tools.horreum.cli.cmd;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.hyperfoil.tools.HorreumClient;
import picocli.CommandLine;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@CommandLine.Command(name = "import", description = "import configuration")
public class Import  extends AbstractCommand {

    @Inject
    HorreumClient client;

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    ImportGroup group;


    static class ImportGroup {
        @CommandLine.Option(names = {"-f", "--file"}, description = "File containing configuration")
        File configFile;

        @CommandLine.Option(names = {"-s", "--spec"}, description = "import specification")
        String spec;
    }

    @Override
    public void runCmd() {

        if( group.spec == null && !group.configFile.exists()) {
            System.err.println("Configuration file not found: ".concat(group.configFile.getAbsolutePath()));
        } else {
            ObjectMapper yamlmapper = new ObjectMapper(new YAMLFactory()); //TODO:: push this into the Horreum API
            yamlmapper.findAndRegisterModules();

            try {
                JsonNode mappedJson = group.configFile != null ?  yamlmapper.readTree(group.configFile.toURI().toURL()) : yamlmapper.readTree(group.spec);;

                if( mappedJson.has("type") ){
                    String type = mappedJson.get("type").asText().toUpperCase();
                    JsonNode spec = mappedJson.get("spec");
                    switch (type) {
                        case "SCHEMA":
                            client.schemaService.importSchema(spec); //TODO:: handle errors from remote service
                            break;
                        case "TEST":
                            client.testService.importTest(spec); //TODO:: handle errors from remote service
                            break;
                        default:
                            System.err.println("Unknown type: ".concat(type)); //TODO:validation based on schema
                            return;
                    }
                    System.out.println(String.format("Successfully imported %s definition", type.toLowerCase() ));
                } else {
                    System.err.println("Malformed yaml, missing: type"); //TODO:validation based on schema
                }
                System.out.println(mappedJson.toPrettyString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
