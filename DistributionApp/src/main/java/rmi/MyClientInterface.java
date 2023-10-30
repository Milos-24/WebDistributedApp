package rmi;

import java.rmi.RemoteException;

public interface MyClientInterface {
    void sendClientData(String data) throws RemoteException;
}
