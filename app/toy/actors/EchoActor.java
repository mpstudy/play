package toy.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Created by kimyongin on 2017-08-28.
 */
public class EchoActor extends AbstractActor {
    private final ActorRef out;

    public static Props props(ActorRef out) {
        return Props.create(EchoActor.class, out);
    }

    public EchoActor(ActorRef out) {
        this.out = out;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, message ->
                out.tell("I received your message: " + message, self())
        ).build();
    }
}