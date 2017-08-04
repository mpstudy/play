package toy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import toy.actions.PostAction;
import toy.models.PostResource;
import toy.services.PostResourceHandler;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@With(PostAction.class)
public class PostController extends Controller {

    private HttpExecutionContext ec;
    private PostResourceHandler handler;

    @Inject
    public PostController(HttpExecutionContext ec, PostResourceHandler handler) {
        this.ec = ec;
        this.handler = handler;
    }

    public CompletionStage<Result> list() {
        return handler.find()
                .thenApplyAsync(posts -> ok(Json.toJson(posts.collect(Collectors.toList()))), ec.current());
    }

    public CompletionStage<Result> show(String id) {
        return handler.lookup(id)
                .thenApplyAsync(optionalResource -> optionalResource
                        .map(resource -> ok(Json.toJson(resource)))
                        .orElseGet(Results::notFound), ec.current());
    }

    public CompletionStage<Result> update(String id) {
        JsonNode json = request().body().asJson();
        PostResource resource = Json.fromJson(json, PostResource.class);
        return handler.update(id, resource)
                .thenApplyAsync(optionalResource -> optionalResource
                        .map(r -> ok(Json.toJson(r)))
                        .orElseGet(Results::notFound), ec.current());
    }

    public CompletionStage<Result> create() {
        JsonNode json = request().body().asJson();
        final PostResource resource = Json.fromJson(json, PostResource.class);
        return handler.create(resource)
                .thenApplyAsync(savedResource -> created(Json.toJson(savedResource)), ec.current());
    }
}
