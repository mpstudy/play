package toy.controllers;

import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import toy.entities.UserData;
import toy.models.UserResource;
import toy.repositories.UserRepository;
import toy.services.UserResourceHandler;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private HttpExecutionContext ec;
    private FormFactory formFactory;
    private UserResourceHandler handler;
    private UserRepository repository;

    @Inject
    public HomeController(HttpExecutionContext ec, FormFactory formFactory, UserResourceHandler handler, UserRepository repository) {
        this.ec = ec;
        this.formFactory = formFactory;
        this.handler = handler;
        this.repository = repository;
    }

    public Result index() {
        String email = session("connected");
        if (email != null) {
            return ok(views.html.index.render(email));
        } else {
            return ok(views.html.sign.render());
        }
    }

    public CompletionStage<Result> sign() {
        Map<String, String[]> body = request().body().asFormUrlEncoded();
        String action = body.get("action")[0];
        String email = body.get("email")[0];
        String password = body.get("password")[0];
        UserResource userResource = new UserResource(email, password);
        if (action.equals("SignIn")) {
            return signin(userResource);
        } else {
            return signup(userResource);
        }
    }

    public CompletionStage<Result> signin(UserResource resource) {
        return repository.find(resource.getEmail()).thenApplyAsync(userDataList -> userDataList.findFirst()
                .filter(userData -> userData.password.equals(resource.getPassword()))
                .map(matchedUser -> {
                    session("connected", matchedUser.email);
                    return redirect("/");
                })
                .orElseGet(() -> {
                    return redirect("/");
                }), ec.current());
    }

    public CompletionStage<Result> signup(UserResource resource) {
        final UserData data = new UserData(resource.getEmail(), resource.getPassword());
        return repository.create(data).thenApplyAsync(savedData -> {
            session("connected", savedData.email);
            return redirect("/");
        }, ec.current());
    }

    public Result signout() {
        session().remove("connected");
        return redirect("/");
    }
}
