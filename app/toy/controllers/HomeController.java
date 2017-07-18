package toy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import toy.models.PostResource;
import toy.models.UserResource;

import javax.inject.Inject;
import java.util.Map;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private FormFactory formFactory;

    @Inject
    public HomeController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        String email = session("connected");
        if(email != null) {
            return ok(views.html.index.render(email));
        } else {
            return ok(views.html.login.render());
        }
    }

    public Result login() {
        Map<String, String[]> body = request().body().asFormUrlEncoded();
        session("connected", body.get("email")[0]);
        return redirect("/");
    }

    public Result logout() {
        session().remove("connected");
        return redirect("/");
    }


}
