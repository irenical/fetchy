package org.irenical.fetchy;

public class Node {

    private String id;
    private String address;
    private Integer port;

    public Node(String address) {
        this.address = address;
    }

    public Node(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public Node(String id, String address, Integer port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (id != null ? !id.equals(node.id) : node.id != null) return false;
        if (!address.equals(node.address)) return false;
        return port != null ? port.equals(node.port) : node.port == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + address.hashCode();
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }
}
