package data;

import java.net.InetAddress;
import java.util.Objects;

public final class AddressWithPort {
    public final InetAddress address;
    public final int port;

    public AddressWithPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddressWithPort that = (AddressWithPort) o;
        return port == that.port && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
