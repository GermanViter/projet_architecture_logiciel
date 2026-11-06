package com.rpg.config;

import com.rpg.api.FallbackExceptionMapper;
import com.rpg.api.player.PlayerInstaller;
import com.rpg.api.session.SessionInstaller;
import com.rpg.infrastructure.player.PlayerRegistry;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

/**
 * DÉMARRAGE — "Composition root" (racine de composition).
 *
 * <p>Elle prépare la configuration commune du serveur, puis demande à chaque fonctionnalité de
 * s'installer. Chaque installer crée et branche ses propres objets, y compris la traduction de
 * ses erreurs en HTTP.
 *
 * <p>Partout ailleurs, les classes reçoivent leurs dépendances de l'extérieur (par leur
 * constructeur) au lieu de les créer elles-mêmes.
 */
public final class ApplicationContext {

    private ApplicationContext() {}

    public static ResourceConfig createResourceConfig() {
        // Configuration commune à toutes les fonctionnalités :
        // BV_SEND_ERROR_IN_RESPONSE : quand le schéma est invalide, Jersey indique quel champ
        // pose problème (sinon il répond 400 avec un corps vide).
        ResourceConfig resourceConfig =
                new ResourceConfig().property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        // Une installation par fonctionnalité. L'ORDRE compte : le module réservation a besoin de
        // l'API publique du module restaurant, qui doit donc exister avant.
        PlayerRegistry playerRegistry = new PlayerRegistry();
        new PlayerInstaller(playerRegistry).install(resourceConfig);
        new SessionInstaller(playerRegistry).install(resourceConfig);
        resourceConfig.register(new FallbackExceptionMapper());

        return resourceConfig;
    }
}