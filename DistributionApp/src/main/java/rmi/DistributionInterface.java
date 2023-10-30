package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DistributionInterface extends Remote {

    public void addingMaterial(String company, String order) throws RemoteException;
}
