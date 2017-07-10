package toy.services;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class AtomicCounter implements Counter {

    private final AtomicInteger atomicCounter = new AtomicInteger();

    @Override
    public int nextCount() {
       return atomicCounter.getAndIncrement();
    }

}
