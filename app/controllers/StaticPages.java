package controllers;

import controllers.Utils.Constantes;
import controllers.Utils.JsonUtils;
import play.Routes;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import services.UtilisateurService;
import views.html.a_propos;
import views.html.auth;
import views.html.contact;
import views.html.index;

import java.util.UUID;

public class StaticPages extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result apropos() {
        return ok(a_propos.render());
    }

    public static Result contact() {
        return ok(contact.render());
    }

    public static Result authRequise() {
        return ok(auth.render());
    }

    public static Result logout() {
        Cache.remove(session().get(Constantes.SESSION_ID).concat(Constantes.SESSION_UTILISATEUR_SUFFIXE));
        session().clear();
        return redirect(routes.StaticPages.index());
    }

    public static Result login(String action, String username, String password) {
        if( action.equals(Constantes.JSON_AUTH_REGULIERE) ) {
            UtilisateurService.LoginResult loginResult = UtilisateurService.loginRegulier(username, password);
            if( loginResult.statut == UtilisateurService.Statut.VARIABLES_MANQUANTES ) {

                return badRequest(JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Variables manquantes"));

            } else if( loginResult.statut == UtilisateurService.Statut.OK ) {

                session().clear();
                String sessionId = UUID.randomUUID().toString();
                session(Constantes.SESSION_ID, sessionId);
                Cache.set(sessionId.concat(Constantes.SESSION_UTILISATEUR_SUFFIXE), loginResult.utilisateur);
                return ok( JsonUtils.genererReponseJson(JsonUtils.JsonStatut.OK, "Authentification réussie."));

            } else if( loginResult.statut == UtilisateurService.Statut.IDENTIFIANTS_INVALIDES ) {

                return ok( JsonUtils.genererReponseJson(JsonUtils.JsonStatut.ERREUR, "Identifiants non valides"));
            }
        }
        return badRequest();
    }

    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("jsRoutes",
                        routes.javascript.Etudiants.apiStages(),
                        routes.javascript.StaticPages.login()
                )
        );
    }
}
