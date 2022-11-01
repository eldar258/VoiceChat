package data;

import java.net.InetAddress;

public record AddressWithPort(InetAddress address, int port) {}
