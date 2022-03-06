package org.loveyoupeng.orderbook;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.agrona.*;
import org.agrona.DeadlineTimerWheel.*;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class OrderServiceTest {
  private static final Logger logger =
      LoggerFactory.getLogger(OrderServiceTest.class);

  private Clock clock;
  @Before
  public void before() {
    clock = mock(Clock.class);
  }

  @Test
  public void test_order_service_timmer() throws Exception {
    when(clock.now()).thenReturn(0L, 100L);
    final long now = clock.now();
    final DeadlineTimerWheel wheel =
        new DeadlineTimerWheel(TimeUnit.MILLISECONDS, now, 1, 2);
    final int size = 100_000;
    for (int i = 0; i < size; i++) {
      wheel.scheduleTimer(100);
    } 
    assertEquals("error scheduled", 0,
                 wheel.poll(now, this::onTimerExpiry, Integer.MAX_VALUE));
    assertEquals("error scheduled", size,
                 wheel.poll(clock.now(), this::onTimerExpiry, Integer.MAX_VALUE));

    final Disruptor<EventContainer> disruptor =
        new Disruptor<>(EventContainer::new, 16, DaemonThreadFactory.INSTANCE);
    disruptor.handleEventsWith(this::timestamp);
    disruptor.start();

    disruptor.shutdown();
  }

  private void timestamp(final EventContainer event, final long sequence,
                         final boolean endOfBatch) throws Exception {
    event.timestamp(clock.now());
  }

  private boolean onTimerExpiry(TimeUnit timeUnit, long now, long timerId) {
    return true;
  }
}

interface Clock {
  long now();
}
interface OMSEvent {
  interface OMSEventHandler {

  }

  void acccept(final OMSEventHandler handler);
}

final class EventContainer {
  private long timestamp;
  private OMSEvent event;

  public OMSEvent event() {
    return event;
  }

  public void event(final OMSEvent event) {
    this.event = event;
  }
  
  public void timestamp(final long timestmap) {
    this.timestamp = timestamp;
  }

  public long timestamp() {
    return timestamp;
  }
}
