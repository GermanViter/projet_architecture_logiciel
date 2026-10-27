package com.rpg;
import java.net.URI;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import restalo.config.ApplicationContext;

/**
 * DÉMARRAGE — point d'entrée du programme.
 *
 * <p>Son seul rôle : démarrer le serveur HTTP. Il ne contient aucune logique d'affaires et ne
 * crée lui-même aucun objet de l'application : il délègue ce travail à {@link ApplicationContext}.
 *
 * <p>Ordre au démarrage :
 *
 * <ol>
 *   <li>Main calcule l'adresse du serveur (port 8080 par défaut, ou la variable d'environnement
 *       PORT).
 *   <li>Main demande à ApplicationContext de construire et d'assembler tous les objets.
 *   <li>Le serveur Grizzly démarre et attend des requêtes HTTP.
 * </ol>
 */
public class Main {

    private static final String DEFAULT_PORT = "8080";

    public static void main(String[] args) {
        URI baseUri = URI.create("http://0.0.0.0:" + port() + "/");
        GrizzlyHttpServerFactory.createHttpServer(baseUri, ApplicationContext.createResourceConfig());
        System.out.printf("Restalo started at %s%n", baseUri);
    }

    private static String port() {
        String port = System.getenv("PORT");
        return port == null || port.isBlank() ? DEFAULT_PORT : port;
    }
}
