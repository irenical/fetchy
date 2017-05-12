package org.irenical.fetchy.event;

import org.irenical.fetchy.Node;
import org.irenical.lifecycle.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EventEmitter implements LifeCycle {

    private static final Logger LOG = LoggerFactory.getLogger(EventEmitter.class);

    private final Map<String, Map<String, Consumer<FetchyEvent>>> listeners = new ConcurrentHashMap<>();

    private final AtomicInteger idGenerator = new AtomicInteger(0);

    private ExecutorService executorService;

    public EventEmitter() {
    }

    public EventEmitter(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void fire(String entity, String name, String serviceId, Node node, Object target, long elapsedMillis) {
        if (!listeners.containsKey(entity)) {
            LOG.debug("No listeners registered for entity {}", entity);
            return;
        }

        getExecutorService().execute(() -> {
            FetchyEvent event = new FetchyEvent<>(serviceId, name, node, elapsedMillis, target);

            listeners.get(entity).entrySet().forEach(entry -> {
                try {
                    Consumer consumer = (Consumer) ((Map.Entry) entry).getValue();
                    consumer.accept(event);
                } catch (RuntimeException ex) {
                    LOG.error("Error calling listener " + ((Map.Entry) entry).getKey() + "... ignoring", ex);
                }
            });
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public String addListener(String entity, Consumer listener) {
        LOG.debug("Registering {} listener {}", entity, listener);
        if (entity == null || entity.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity cannot be null or empty");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }

        final Map<String, Consumer<FetchyEvent>> entityListeners = listeners.computeIfAbsent(entity, s -> new ConcurrentHashMap<>());

        String id = buildListenerId(entity);
        entityListeners.put(id, listener);
        return id;
    }

    public void removeListener(String listenerId) {
        if (listenerId == null || listenerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Listener ID cannot be null or empty");
        }

        final String entity = getEntity(listenerId);

        if (listeners.containsKey(entity)) {
            listeners.get(entity).remove(listenerId);
        }
    }

    public void clear() {
        listeners.clear();
        idGenerator.set(0);
    }

    @Override
    public <ERROR extends Exception> void start() throws ERROR {

    }

    @Override
    public <ERROR extends Exception> void stop() throws ERROR {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    @Override
    public <ERROR extends Exception> boolean isRunning() throws ERROR {
        return true;
    }

    private String getEntity(String listenerId) {
        return listenerId.substring(0, listenerId.lastIndexOf('-'));
    }

    private String buildListenerId(String entity) {
        return String.format("%s-listener:%d", entity, idGenerator.incrementAndGet());
    }

    private synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }
}
