package server;

import remote.IChatUpdate;
import remote.IClientUpdate;
import remote.IDrawingUpdate;

import javax.crypto.SecretKey;

public class User
{
    private String username;
    private IChatUpdate IChatUpdate;
    private IDrawingUpdate IDrawingUpdate;
    private IClientUpdate IClientUpdate;
    private boolean isAdmin;

    public SecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }

    private SecretKey sharedSecretKey;

    public User(String username, IChatUpdate IChatUpdate, IClientUpdate IClientUpdate, IDrawingUpdate IDrawingUpdate, SecretKey sharedSecretKey)
    {
        this.username = username;
        this.IChatUpdate = IChatUpdate;
        this.IDrawingUpdate = IDrawingUpdate;
        this.IClientUpdate = IClientUpdate;
        this.sharedSecretKey = sharedSecretKey;
        this.isAdmin = false;
    }

    public String getUserName()
    {
        return this.username;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    public IChatUpdate getIChatUpdate()
    {
        return this.IChatUpdate;
    }

    public IDrawingUpdate getIDrawingUpdate() { return this.IDrawingUpdate; }

    public IClientUpdate getIClientUpdate() { return this.IClientUpdate; }
}
