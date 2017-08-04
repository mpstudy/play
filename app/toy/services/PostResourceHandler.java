package toy.services;

import com.palominolabs.http.url.UrlBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import toy.entities.PostData;
import toy.models.PostResource;
import toy.repositories.PostRepository;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Handles presentation of Post resources, which map to JSON.
 */
public class PostResourceHandler {

    private final PostRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public PostResourceHandler(PostRepository repository, HttpExecutionContext ec) {
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Stream<PostResource>> find() {
        return repository.list()
                .thenApplyAsync(postDataStream -> postDataStream.map(data -> new PostResource(data, link(data))), ec.current());
    }

    public CompletionStage<PostResource> create(PostResource resource) {
        final PostData data = new PostData(resource.getTitle(), resource.getBody());
        return repository.create(data)
                .thenApplyAsync(savedData -> new PostResource(savedData, link(savedData)), ec.current());
    }

    public CompletionStage<Optional<PostResource>> lookup(String id) {
        return repository.get(Long.parseLong(id))
                .thenApplyAsync(optionalData -> optionalData.map(data -> new PostResource(data, link(data))), ec.current());
    }

    public CompletionStage<Optional<PostResource>> update(String id, PostResource resource) {
        final PostData data = new PostData(resource.getTitle(), resource.getBody());
        return repository.update(Long.parseLong(id), data)
                .thenApplyAsync(optionalData -> optionalData.map(op -> new PostResource(op, link(op))), ec.current());
    }

    private String link(PostData data) {
        // Make a point of using request context here, even if it's a bit strange
        final Http.Request request = Http.Context.current().request();
        final String[] hostPort = request.host().split(":");
        String host = hostPort[0];
        int port = (hostPort.length == 2) ? Integer.parseInt(hostPort[1]) : -1;
        final String scheme = request.secure() ? "https" : "http";
        try {
            return UrlBuilder.forHost(scheme, host, port)
                    .pathSegments("v1", "posts", data.id.toString())
                    .toUrlString();
        } catch (CharacterCodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
