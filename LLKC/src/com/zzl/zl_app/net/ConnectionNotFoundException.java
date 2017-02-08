package com.zzl.zl_app.net;

import java.io.IOException;

public class ConnectionNotFoundException extends IOException
{
    public ConnectionNotFoundException( String message )
    {
        super( message );
    }
}
