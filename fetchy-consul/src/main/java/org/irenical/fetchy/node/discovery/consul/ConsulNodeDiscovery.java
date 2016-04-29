package org.irenical.fetchy.node.discovery.consul;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.irenical.fetchy.node.ServiceNode;
import org.irenical.fetchy.node.discovery.ServiceNodeDiscovery;
import org.irenical.jindy.Config;
import org.irenical.jindy.ConfigFactory;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Self;
import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;

public class ConsulNodeDiscovery implements ServiceNodeDiscovery {

    private static final String DEFAULT_CONSUL_HOST_PROPERTY = "consul.host";
    private static final String DEFAULT_CONSUL_PORT_PROPERTY = "consul.port";
    private static final int DEFAULT_CONSUL_PORT = 8500;


    private final Config config = ConfigFactory.getConfig();

    private final String consulHostPropertyKey;
    private final String consulPortPropertyKey;

    private ConsulClient consulClient;


    public ConsulNodeDiscovery() {
        this( DEFAULT_CONSUL_HOST_PROPERTY, DEFAULT_CONSUL_PORT_PROPERTY );
    }

    public ConsulNodeDiscovery(String consulHostPropertyKey) {
        this( consulHostPropertyKey, DEFAULT_CONSUL_PORT_PROPERTY );
    }

    public ConsulNodeDiscovery(String consulHostPropertyKey, String consulPortPropertyKey) {
        if ( consulHostPropertyKey == null || consulHostPropertyKey.trim().isEmpty() ) {
            throw new IllegalArgumentException( "consul host property key cannot be null or empty" );
        }
        if ( consulPortPropertyKey == null || consulPortPropertyKey.trim().isEmpty() ) {
            throw new IllegalArgumentException( "consul port property key cannot be null or empty" );
        }

        this.consulHostPropertyKey = consulHostPropertyKey;
        this.consulPortPropertyKey = consulPortPropertyKey;
    }

    @Override
    public void start() throws Exception {
        consulClient = new ConsulClient( config.getMandatoryString( consulHostPropertyKey ),
                config.getInt( consulPortPropertyKey, DEFAULT_CONSUL_PORT ) );
    }

    @Override
    public void stop() throws Exception {
        consulClient = null;
    }

    @Override
    public boolean isRunning() throws Exception {
        if ( consulClient == null ) {
            return false;
        }
        Response<Self> agentSelf = consulClient.getAgentSelf();
        return agentSelf != null && agentSelf.getValue() != null;
    }

    @Override
    public List<ServiceNode> getServiceNodes(String serviceId, boolean onlyHealthy ) {
        Response<List<HealthService>> serviceResponse = consulClient.getHealthServices( serviceId, onlyHealthy, null );
        if ( serviceResponse == null ) {
            return new ArrayList<>( 0 );
        }

        List<HealthService> responseValue = serviceResponse.getValue();
        if ( responseValue == null ) {
            return new ArrayList<>( 0 );
        }

        return responseValue.stream()
                .map(this::fromHealthService)
                .collect(Collectors.toList());
    }

    private ServiceNode fromHealthService( HealthService healthService ) {
        HealthService.Service consulService = healthService.getService();
        HealthService.Node consulNode = healthService.getNode();

        Integer port = consulService.getPort();

        ServiceNode serviceNode = new ServiceNode();
        serviceNode.setAddress( coalesce( consulService.getAddress(), consulNode.getAddress() ) );
        serviceNode.setPort( (port == null || port == 0 ? null : port) );
        serviceNode.setNode( consulNode.getNode() );
        serviceNode.setStatus( getServiceStatus( healthService, consulService.getId() ) );

        return serviceNode;
    }

    private ServiceNode.ServiceStatus getServiceStatus(HealthService healthService, String serviceId ) {
        List<Check> checks = healthService.getChecks();
        return checks.stream()
                .filter( check -> ("service:" + serviceId).equals(check.getCheckId()) )
                .map( check -> {
                    switch (check.getStatus()) {
                        case CRITICAL:
                            return ServiceNode.ServiceStatus.CRITICAL;
                        case PASSING:
                            return ServiceNode.ServiceStatus.HEALTHY;
                        case WARNING:
                            return ServiceNode.ServiceStatus.WARNING;
                        default:
                            return ServiceNode.ServiceStatus.UNKNOWN;
                    }
                })
                .collect( Collectors.collectingAndThen(
                        Collectors.toList(),
                        new Function<List<ServiceNode.ServiceStatus>, ServiceNode.ServiceStatus>() {
                            @Override
                            public ServiceNode.ServiceStatus apply(List<ServiceNode.ServiceStatus> list) {
                                if ( list == null || list.isEmpty() ) {
                                    return ServiceNode.ServiceStatus.UNKNOWN;
                                }
                                return list.get( 0 );
                            }
                        })
                );
    }

    private static String coalesce( String ... parameters ) {
        for ( String parameter : parameters ) {
            if ( parameter != null && ! parameter.trim().isEmpty() ) {
                return parameter;
            }
        }
        return null;
    }

}
