package toy.repositories;

import toy.entities.UserData;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface UserRepository {

  CompletionStage<Stream<UserData>> list();

  CompletionStage<UserData> create(UserData userData);

  CompletionStage<Optional<UserData>> get(Long id);

  CompletionStage<Stream<UserData>> find(String email);

  CompletionStage<Optional<UserData>> update(Long id, UserData userData);

  CompletionStage<Optional<UserData>> delete(Long id);
}

