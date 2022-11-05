package ok.dht.test.galeev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AckBarrier {
    private static final Logger LOGGER = LoggerFactory.getLogger(AckBarrier.class);
    private final AtomicInteger successfulResponses;
    private final AtomicInteger unsuccessfulResponses;
    private final AtomicBoolean hasAnswered;
    private final int ack;
    private final int from;
    private volatile boolean isNeedToResponse;

    public boolean isNeedToResponse() {
        return isNeedToResponse && hasAnswered.compareAndSet(false, true);
    }

    public AckBarrier(int ack, int from) {
        this.ack = ack;
        this.from = from;
        this.successfulResponses = new AtomicInteger(0);
        this.unsuccessfulResponses = new AtomicInteger(0);
        this.hasAnswered = new AtomicBoolean(false);
    }

    public void success() {
        if (successfulResponses.incrementAndGet() >= ack) {
            isNeedToResponse = true;
        }
    }

    public void unSuccess() {
        if (unsuccessfulResponses.incrementAndGet() >= (from - ack + 1)) {
            isNeedToResponse = true;
        }
    }

    public boolean isAckAchieved() {
        return successfulResponses.get() >= ack;
    }
}
