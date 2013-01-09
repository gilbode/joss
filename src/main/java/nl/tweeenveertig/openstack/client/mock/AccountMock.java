package nl.tweeenveertig.openstack.client.mock;

import nl.tweeenveertig.openstack.model.Container;
import nl.tweeenveertig.openstack.client.core.AbstractAccount;
import nl.tweeenveertig.openstack.client.mock.scheduled.ObjectDeleter;
import nl.tweeenveertig.openstack.command.identity.access.AccessImpl;
import nl.tweeenveertig.openstack.exception.CommandException;
import nl.tweeenveertig.openstack.headers.account.AccountBytesUsed;
import nl.tweeenveertig.openstack.headers.account.AccountContainerCount;
import nl.tweeenveertig.openstack.headers.account.AccountObjectCount;
import nl.tweeenveertig.openstack.model.PaginationMap;

import java.util.*;

public class AccountMock extends AbstractAccount {

    private Map<String, Container> containers = new TreeMap<String, Container>();

    public AccessImpl authenticate() { return null; /* ignore */ }

    private ObjectDeleter objectDeleter;

    public AccountMock() {
        super(ALLOW_CACHING);
    }

    public AccountMock setOnFileObjectStore(String onFileObjectStore) {
        if (onFileObjectStore == null) {
            return this;
        }
        OnFileObjectStoreLoader loader = new OnFileObjectStoreLoader();
        try {
            loader.createContainers(this, onFileObjectStore);
        } catch (Exception err) {
            throw new CommandException("Unable to load the object store from file: "+err.getMessage(), err);
        }
        return this;
    }

    public AccountMock setObjectDeleter(ObjectDeleter objectDeleter) {
        this.objectDeleter = objectDeleter;
        return this;
    }

    public ObjectDeleter getObjectDeleter() {
        return this.objectDeleter;
    }

    @Override
    protected void saveMetadata() {} // no action necessary

    @Override
    protected void getInfo() {

        int containerCount= 0;
        int objectCount = 0;
        long bytesUsed = 0;
        for (Container container : containers.values()) {
            containerCount++;
            objectCount += container.getObjectCount();
            bytesUsed += container.getBytesUsed();
        }
        this.info.setContainerCount(new AccountContainerCount(Integer.toString(containerCount)));
        this.info.setObjectCount(new AccountObjectCount(Integer.toString(objectCount)));
        this.info.setBytesUsed(new AccountBytesUsed(Long.toString(bytesUsed)));
    }

    public Collection<Container> listContainers() {
        return containers.values();
    }

    @Override
    public Collection<Container> listContainers(String marker, int pageSize) {
        // TBD
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PaginationMap getPaginationMap(int pageSize) {
        // TBD
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Container getContainer(String name) {
        Container foundContainer = containers.get(name);
        return foundContainer == null ? new ContainerMock(this, name) : foundContainer;
    }

    public void createContainer(Container container) {
        this.containers.put(container.getName(), container);
    }

    public void deleteContainer(Container container) {
        this.containers.remove(container.getName());
    }

    public String getPublicURL() {
        return "";
    }

}
