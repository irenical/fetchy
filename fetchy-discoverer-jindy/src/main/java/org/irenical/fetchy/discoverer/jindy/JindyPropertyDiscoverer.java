package org.irenical.fetchy.discoverer.jindy;

import org.irenical.fetchy.Node;
import org.irenical.fetchy.discoverer.DiscoverException;
import org.irenical.fetchy.discoverer.Discoverer;
import org.irenical.jindy.Config;
import org.irenical.jindy.ConfigFactory;

import java.util.Collections;
import java.util.List;

public class JindyPropertyDiscoverer implements Discoverer {

    private final Config config;
    private final String addressProperty;
    private final String portProperty;

    public JindyPropertyDiscoverer(String addressProperty) {
        this(ConfigFactory.getConfig(), addressProperty, null);
    }

    public JindyPropertyDiscoverer(String addressProperty, String portProperty) {
        this(ConfigFactory.getConfig(), addressProperty, portProperty);
    }

    public JindyPropertyDiscoverer(Config config, String addressProperty) {
        this(config, addressProperty, null);
    }

    public JindyPropertyDiscoverer(Config config, String addressProperty, String portProperty) {
        this.config = config;
        this.addressProperty = addressProperty;
        this.portProperty = portProperty;
    }

    @Override
    public List<Node> discover(String s) throws DiscoverException {
        String address = config.getString(addressProperty);

        if (address == null || address.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Node node = new Node(address);

        if (portProperty != null) {
            final int port = config.getInt(portProperty, -1);

            if (port > -1) {
                node.setPort(port);
            }
        }

        return Collections.singletonList(node);
    }

}
