package io.hyperfoil.tools.horreum.cli.cdi;

import io.hyperfoil.tools.HorreumClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class HorreumClientProducer {
    @ConfigProperty(name = "horreum.uri", defaultValue = "")
    String horremUrl;

    @ConfigProperty(name ="horreum.user", defaultValue = "")
    String horremUserName;

    @ConfigProperty(name ="horreum.password", defaultValue = "")
    String horremPassword;


    @Produces
    @Singleton
    HorreumClient produce() {
        try {
            return new HorreumClient.Builder()
                    .horreumUrl(horremUrl)
                    .horreumUser(horremUserName)
                    .horreumPassword(horremPassword).build();
        } catch (Exception e){
            e.printStackTrace();
            System.err.println(String.format("Could not connect to Horreum (%s), please check configuration and try again", horremUrl ));
        }
        return null;
    }
}
