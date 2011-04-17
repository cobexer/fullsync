package net.full.fs.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import net.sourceforge.fullsync.ConnectionDescription;

public class LocationDescription
{
    private URI uri;
    private Properties properties;
    
    public LocationDescription( URI uri )
    {
        this.uri = uri;
        this.properties = new Properties();
    }
    public LocationDescription( ConnectionDescription conn ) throws URISyntaxException 
    {
        this( new URI( conn.getUri() ) );
        
        if( conn.getUsername() != null )
        {
            this.properties.setProperty( "username", conn.getUsername() );
            this.properties.setProperty( "password", conn.getPassword() );
        }
    }
    
    public void setUri( URI uri )
    {
        this.uri = uri;
    }
    
    public URI getUri()
    {
        return uri;
    }
    
    public String getProperty( String name )
    {
        return properties.getProperty( name );
    }

    public void setProperty( String name, String value )
    {
        properties.setProperty( name, value );
    }
    
    public ConnectionDescription toConnectionDescription()
    {
        ConnectionDescription desc;
        desc = new ConnectionDescription( uri.toString(), "" ); //$NON-NLS-1$
        
        if( getProperty("username") != null )
        {
            desc.setUsername( getProperty("username") );
            desc.setPassword( getProperty("password") );
        }
        return desc;
    }
}
