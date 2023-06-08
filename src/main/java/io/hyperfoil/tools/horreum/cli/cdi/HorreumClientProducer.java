package io.hyperfoil.tools.horreum.cli.cdi;

import io.hyperfoil.tools.HorreumClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class HorreumClientProducer {
    @ConfigProperty(name = "horreum.uri")
    String horremUrl;

    @ConfigProperty(name = "horreum.keycloak.baseUrl")
    String keyCloakBaseUrl;

    @ConfigProperty(name = "horreum.keycloak.realm")
    String keycloakRealm;

    @ConfigProperty(name = "horreum.keycloak.clientID")
    String keycloakClientID;

    @ConfigProperty(name ="horreum.user")
    String horremUserName;

    @ConfigProperty(name ="horreum.password")
    String horremPassword;


    @Produces
    @Singleton
    HorreumClient produce() {
        HorreumClient.Builder clientBuilder = new HorreumClient.Builder()
                .horreumUrl(horremUrl)
                .keycloakUrl(keyCloakBaseUrl)
                .keycloakRealm(keycloakRealm)
                .clientId(keycloakClientID)
                .horreumUser(horremUserName)
                .horreumPassword(horremPassword);
        return clientBuilder.build();

    }

}
