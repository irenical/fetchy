package org.irenical.fetchy.discoverer.consul;

import com.ecwid.consul.ConsulException;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Self;
import com.ecwid.consul.v1.health.model.HealthService;
import org.irenical.fetchy.Node;
import org.irenical.fetchy.discoverer.DiscoverException;
import org.irenical.fetchy.discoverer.Discoverer;
import org.irenical.jindy.Config;
import org.irenical.jindy.ConfigFactory;
import org.irenical.lifecycle.LifeCycle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConsulDiscoverer implements Discoverer, LifeCycle {

    private static final String DEFAULT_CONSUL_HOST_PROPERTY = "consul.host";
    private static final String DEFAULT_CONSUL_PORT_PROPERTY = "consul.port";
    private static final int DEFAULT_CONSUL_PORT = 8500;


    private final Config config = ConfigFactory.getConfig();

    private final String consulHostPropertyKey;
    private final String consulPortPropertyKey;

    private ConsulClient consulClient;


    public ConsulDiscoverer() {
        this(DEFAULT_CONSUL_HOST_PROPERTY, DEFAULT_CONSUL_PORT_PROPERTY);
    }

    public ConsulDiscoverer(String consulHostPropertyKey) {
        this(consulHostPropertyKey, DEFAULT_CONSUL_PORT_PROPERTY);
    }

    public ConsulDiscoverer(String consulHostPropertyKey, String consulPortPropertyKey) {
        if (consulHostPropertyKey == null || consulHostPropertyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("consul host property key cannot be null or empty");
        }
        if (consulPortPropertyKey == null || consulPortPropertyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("consul port property key cannot be null or empty");
        }

        this.consulHostPropertyKey = consulHostPropertyKey;
        this.consulPortPropertyKey = consulPortPropertyKey;
    }

    @Override
    public void start() throws Exception {
        consulClient = new ConsulClient(config.getMandatoryString(consulHostPropertyKey),
                config.getInt(consulPortPropertyKey, DEFAULT_CONSUL_PORT));
    }

    @Override
    public void stop() throws Exception {
        consulClient = null;
    }

    @Override
    public boolean isRunning() throws Exception {
        if (consulClient == null) {
            return false;
        }
        Response<Self> agentSelf = consulClient.getAgentSelf();
        return agentSelf != null && agentSelf.getValue() != null;
    }

    @Override
    public List<Node> discover(String serviceId) throws DiscoverException {
        try {
            Response<List<HealthService>> serviceResponse = consulClient.getHealthServices(serviceId, true, null);
            if (serviceResponse == null) {
                return Collections.emptyList();
            }

            List<HealthService> responseValue = serviceResponse.getValue();
            if (responseValue == null) {
                return Collections.emptyList();
            }

            return responseValue.stream()
                    .map(this::fromHealthService)
                    .collect(Collectors.toList());
        } catch (ConsulException e) {
            throw new DiscoverException(e.getLocalizedMessage(), e);
        }
    }

    private Node fromHealthService(HealthService healthService) {
        HealthService.Service consulService = healthService.getService();
        HealthService.Node consulNode = healthService.getNode();

        Integer port = consulService.getPort();

        return new Node(
                consulNode.getNode(),
                coalesce(consulService.getAddress(), consulNode.getAddress()),
                (port == null || port == 0 ? null : port)
        );
    }

    private static String coalesce(String... parameters) {
        for (String parameter : parameters) {
            if (parameter != null && !parameter.trim().isEmpty()) {
                return parameter;
            }
        }
        return null;
    }
}
