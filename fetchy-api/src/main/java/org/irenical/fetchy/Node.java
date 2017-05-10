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
}
