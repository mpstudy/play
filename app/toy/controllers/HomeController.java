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
    private UserResourceHandler userResourceHandler;

    @Inject
    public HomeController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        String email = session("connected");
        if(email != null) {
            return ok(views.html.index.render(email));
        } else {
            return ok(views.html.sign.render());
        }
    }

    public Result sign() {
        Map<String, String[]> body = request().body().asFormUrlEncoded();
        String action = body.get("action")[0];
        String email = body.get("email")[0];
        String password = body.get("password")[0];
        if (action.equals("SignIn")) {
            signin(email, password);
        } else if (action.equals("SignUp")) {
            signup(email, password);
        }
        return redirect("/");
    }

    public void signin(String email, String password) {

    }

    public void signup(String email, String password) {

    }

    public Result signout() {
        session().remove("connected");
        return redirect("/");
    }
}
