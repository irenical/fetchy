package org.irenical.fetchy.node;

public class ServiceNode {

    public enum ServiceStatus {
        HEALTHY, WARNING, CRITICAL, UNKNOWN
    }

    private String address;
    private Integer port;

    private String node;

    private ServiceStatus status;

    public ServiceNode() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }
}
