package toy.services;

import com.palominolabs.http.url.UrlBuilder;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import toy.entities.UserData;
import toy.models.UserResource;
import toy.repositories.UserRepository;

import javax.inject.Inject;
import java.nio.charset.CharacterCodingException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Handles presentation of User resources, which map to JSON.
 */
public class UserResourceHandler {

    private final UserRepository repository;
    private final HttpExecutionContext ec;

    @Inject
    public UserResourceHandler(UserRepository repository, HttpExecutionContext ec) {
        this.repository = repository;
        this.ec = ec;
    }

    public CompletionStage<Stream<UserResource>> find() {
        return repository.list()
            .thenApplyAsync(userDataStream -> userDataStream.map(data -> new UserResource(data)), ec.current());
    }

    public CompletionStage<UserResource> create(UserResource resource) {
        final UserData data = new UserData(resource.getEmail(), resource.getPassword());
        return repository.create(data)
            .thenApplyAsync(savedData -> new UserResource(savedData), ec.current());
    }

    public CompletionStage<Optional<UserResource>> lookup(String id) {
        return repository.get(Long.parseLong(id))
            .thenApplyAsync(optionalData -> optionalData.map(data -> new UserResource(data)), ec.current());
    }

    public CompletionStage<Optional<UserResource>> update(String id, UserResource resource) {
        final UserData data = new UserData(resource.getEmail(), resource.getPassword());
        return repository.update(Long.parseLong(id), data)
            .thenApplyAsync(optionalData -> optionalData.map(op -> new UserResource(op)), ec.current());
    }
}
