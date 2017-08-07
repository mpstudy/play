package toy.controllers;

import play.data.Form;
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
  private Form<UserResource> userForm;
  private UserResourceHandler handler;

  @Inject
  public HomeController(HttpExecutionContext ec, FormFactory formFactory, UserResourceHandler handler) {
    this.ec = ec;
    this.userForm = formFactory.form(UserResource.class);
    this.handler = handler;
  }

  public Result index() {
    String email = session("connected");
    if (email != null) {
      return ok(views.html.index.render(email));
    } else {
      return ok(views.html.sign.render());
    }
  }

  public CompletionStage<Result> signin() {
    UserResource requestResource = userForm.bindFromRequest().get();
    return handler.find(requestResource.getEmail()).thenApplyAsync(userResourceList -> userResourceList.findFirst()
        .filter(userResource -> userResource.getPassword().equals(requestResource.getPassword()))
        .map(matchedUser -> {
          session("connected", matchedUser.getEmail());
          return ok();
        })
        .orElseGet(() -> {
          return unauthorized();
        }), ec.current());
  }

  public CompletionStage<Result> signup() {
    UserResource requestResource = userForm.bindFromRequest().get();
    return handler.create(requestResource).thenApplyAsync(savedResource -> {
      session("connected", savedResource.getEmail());
      return ok();
    }, ec.current());
  }

  public Result signout() {
    session().remove("connected");
    return redirect("/");
  }
}
