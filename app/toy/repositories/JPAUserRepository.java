package toy.repositories;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import play.db.jpa.JPAApi;
import toy.entities.UserData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Singleton
public class JPAUserRepository implements UserRepository {

  private final JPAApi jpaApi;
  private final CircuitBreaker circuitBreaker = new CircuitBreaker().withFailureThreshold(1).withSuccessThreshold(3);

  @Inject
  public JPAUserRepository(JPAApi api) {
    this.jpaApi = api;
  }

  @Override
  public CompletionStage<Stream<UserData>> list() {
    return supplyAsync(() -> wrap(em -> select(em)));
  }

  @Override
  public CompletionStage<UserData> create(UserData UserData) {
    return supplyAsync(() -> wrap(em -> insert(em, UserData)));
  }

  @Override
  public CompletionStage<Optional<UserData>> get(Long id) {
    return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))));
  }

  @Override
  public CompletionStage<Stream<UserData>> find(String email) {
    return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> select(em, email))));
  }

  @Override
  public CompletionStage<Optional<UserData>> update(Long id, UserData UserData) {
    return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, UserData))));
  }

  @Override
  public CompletionStage<Optional<UserData>> delete(Long id) {
    return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> delete(em, id))));
  }

  private <T> T wrap(Function<EntityManager, T> function) {
    return jpaApi.withTransaction(function);
  }

  private Optional<UserData> lookup(EntityManager em, Long id) throws SQLException {
    throw new SQLException("Call this to cause the circuit breaker to trip");
    //return Optional.ofNullable(em.find(UserData.class, id));
  }

  private Stream<UserData> select(EntityManager em) {
    TypedQuery<UserData> query = em.createQuery("SELECT p FROM UserData p", UserData.class);
    return query.getResultList().stream();
  }

  private Stream<UserData> select(EntityManager em, String email) {
    TypedQuery<UserData> query = em.createQuery("SELECT p FROM UserData p where email = '" + email + "'", UserData.class);
    return query.getResultList().stream();
  }

  private Optional<UserData> modify(EntityManager em, Long id, UserData UserData) throws InterruptedException {
    final UserData data = em.find(UserData.class, id);
    if (data != null) {
      data.email = UserData.email;
      data.password = UserData.password;
    }
    return Optional.ofNullable(data);
  }

  private Optional<UserData> delete(EntityManager em, Long id) throws InterruptedException {
    final UserData data = em.find(UserData.class, id);
    em.remove(data);
    return Optional.ofNullable(data);
  }

  private UserData insert(EntityManager em, UserData UserData) {
    return em.merge(UserData);
  }
}
