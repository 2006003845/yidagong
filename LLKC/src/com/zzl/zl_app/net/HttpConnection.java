package com.zzl.zl_app.net;

import java.io.IOException;

public interface HttpConnection extends StreamConnection
{
    // TODO: add in the methods for HttpConnection!
    
    public static final String GET                      = "GET";
    public static final String POST                     = "POST";
    public static final String HEAD                     = "HEAD";
    
    public static final int HTTP_OK                     = 200;
    
    public static final int HTTP_ACCEPTED               = 202;
    public static final int HTTP_NOT_AUTHORITATIVE      = 203;
    public static final int HTTP_NO_CONTENT             = 204;
    public static final int HTTP_RESET                  = 205;
    
    public static final int HTTP_MOVED_PERM             = 301;
    public static final int HTTP_SEE_OTHER              = 303;
    public static final int HTTP_TEMP_REDIRECT          = 307;
    
    public static final int HTTP_UNAUTHORIZED           = 401;
    
    public static final int  HTTP_INTERNAL_ERROR        = 500;//add_wang
    
    int getResponseCode() throws IOException;
    
    String getResponseMessage() throws IOException;
    
    String getRequestProperty( String key );
    
    void setRequestProperty( String key, String value ) throws IOException;
    
    void setRequestMethod( String method ) throws IOException;
    
    String getHeaderField( String key );
}
