package net.sourceforge.fullsync.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;
import net.sourceforge.fullsync.rules.Rule;





/**
 * Provides informations and rules about how to handle specific<br>
 * files and whether system files should be used or not.<br>
 * <br>TODO:
 * <ul>
 * <li>refine commands to a lot of SETs and add RESET commands
 * </ul>
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class AbstractRuleSet implements RuleSet, Cloneable
{
    String	name;
	boolean	usingRecursion;
	boolean	usingRecursionOnIgnore;
	int		usingSyncRulesFile;

	String	syncRulesFilename;
	
	int 	checkingBufferAlways;
	int		checkingBufferOnReplace;
	
	Vector	takeRules;
	Vector	ignoreRules;
	Vector	syncRules;
	
	boolean	applyingTakeRules;
	boolean	applyingIgnoreRules;
	boolean	applyingSyncRules;
	int	applyingDeletion;
	/**
	 * ignore all files by default; just Take rules will allow actions
	 */
	boolean	ignoreAll;
	
	/**
	 * Indicates allowed directions: <ul><li>0 - none (do nothing)<li>1 - to_local<li>2 - to_remote</ul>
	 */
	int	direction;
	
	String		ruleSet;
	private boolean	justLogging;
	

	public AbstractRuleSet()
	{
		reset();
		justLogging	= true;
	}
	/**
	 * Constructor AbstractRuleSet.
	 * @param buf
	 */
	public AbstractRuleSet( String name )
	{
	    this();
	    this.name = name;
	}
	
	protected void reset()
	{
		usingSyncRulesFile	= Location.Both;
		syncRulesFilename	= ".syncrules";
		//useSyncBufferFile	= RuleSet.NONE;
		//syncBufferFilename	= ".syncfiles";
		//useSyncCommandsFile	= RuleSet.NONE;
		//syncCommandsFilename= ".synccmd";
		
		checkingBufferAlways = Location.None;
		checkingBufferOnReplace = Location.None;
		
		if( takeRules == null )
			 takeRules = new Vector();
		else takeRules.clear();
		
		if( ignoreRules == null )
			 ignoreRules = new Vector();
		else ignoreRules.clear();
		
		if( syncRules == null )
			 syncRules = new Vector();
		else syncRules.clear();
		
		applyingTakeRules	= false;
		applyingIgnoreRules = false;
		applyingSyncRules	= false;
		applyingDeletion	= Location.None;
		
		ignoreAll			= false;
		
		direction			= 0;
		
		ruleSet				= null;
	}
	
	/**
	 * @return Returns true if the file should be taken, false if ignored
	 */
	public boolean isNodeIgnored( Node node )
	{
	    String filename = node.getName();
		boolean take = !ignoreAll;
		Enumeration e;
		
		if( take )
		for( e = ignoreRules.elements(); e.hasMoreElements(); )
		{
			Rule rule = (Rule)e.nextElement();
			if( rule.accepts( node ) )
			{
				take = false;
				break;
			}
		}
		
		if( !take )
		for( e = takeRules.elements(); e.hasMoreElements(); )
		{
			Rule rule = (Rule)e.nextElement();
			if( rule.accepts( node ) )
			{
				take = true;
				break;
			}
		}
		
		return !take;
	}


	protected long evalRealValue( File f, String exp ) throws DataParseException
	{
		if( exp.equalsIgnoreCase( "length" ) ) return f.getLength();
		else if( exp.equalsIgnoreCase( "date" ) ) return f.getLastModified();
		else throw new DataParseException( "Error while parsing SyncRule: '"+exp+"' is unknown", 0 );
	}

	protected int eval( long srcValue, String operator, long dstValue ) throws DataParseException
	{
		if( operator.equals( "!=" ) ) return (srcValue != dstValue)?-100:0;
		else if( operator.equals( "==" ) ) return (srcValue == dstValue)?0:-100;
		else if( operator.equals( ">" ) ) return (srcValue > dstValue)?-1:1;
		else if( operator.equals( "<" ) ) return (srcValue < dstValue)?1:-1;
		else throw new DataParseException( "Error while parsing SyncRule: '"+operator+"' is unknown operator", 0 );
	}

	public State compareFiles( File src, File dst ) throws DataParseException
	{
		for( Enumeration e = syncRules.elements(); e.hasMoreElements(); )
		{
			String rule = (String)e.nextElement();
			boolean isEqual = true;
			int val = 0, totalVal = 0;
			
			StringTokenizer t = new StringTokenizer( rule, " " );
			String srcValue = t.nextToken();
			String operator = t.nextToken();
			String dstValue = t.nextToken();
			val += eval( evalRealValue( src, srcValue ), operator, evalRealValue( dst, dstValue ) );
			if( val < -50 )
			{
			    val += 100;
			    isEqual = false;
			}

			for( ; t.hasMoreTokens(); )
			{
				String bind = t.nextToken();
				if( bind.equals( "," ) )
				{
				}
				
				srcValue = t.nextToken();
				operator = t.nextToken();
				dstValue = t.nextToken();
				val += eval( evalRealValue( src, srcValue ), operator, evalRealValue( dst, dstValue ) );
				if( val < -50 )
				{
				    val += 100;
				    isEqual = false;
				}
			}
			
			if( totalVal == 0 && isEqual )
			    return new State( State.NodeInSync, Location.Both );
			else if( totalVal > 0 ) {
			    return new State( State.FileChange, Location.Destination );
			} else if( totalVal < 0 ) {
				return new State( State.FileChange, Location.Source );
			} else {
				return new State( State.FileChange, Location.None );
			}
		}
		return new State( State.NodeInSync, Location.Both );
	}
	
	public RuleSet createChild( Directory src, Directory dst )
		throws DataParseException, FileSystemException
	{
	    AbstractRuleSet rules = this;
		if( rules.isUsingRulesFile( SyncTokenizer.LOCAL ) )
	        rules = rules.createChild( src );
		if( rules.isUsingRulesFile( SyncTokenizer.REMOTE ) )
		    rules = rules.createChild( dst );
		return rules;
	}
	
	public AbstractRuleSet createChild( Directory dir )
		throws DataParseException, FileSystemException
	{
	    // TODO really unbuffered ?
	    AbstractRuleSet rules = this;
	    Node node = ((Directory)dir.getUnbuffered()).getChild( syncRulesFilename );
	    if( node != null && !node.isDirectory() )
	    {
			try {
				InputStream in = ((File)node).getInputStream();
				rules = createChild( in, ((File)node).getPath() );
				in.close();
			} catch( FileNotFoundException fnfe ) {
			} catch( IOException ioe ) {
			    ioe.printStackTrace();
			}
	    }
	    return rules;
	}
	public abstract AbstractRuleSet createChild( InputStream in, String filename ) throws FileSystemException, DataParseException;
	/**
	 * 
	 * @return boolean true if rules should be processed, false if not; it does not depend on the active direction
	 */
	public boolean isUsingRulesFile( int where )
	{
		/*switch( useSyncRulesFile )
		{
		case SyncTokenizer.NONE:
			return false;
		case SyncTokenizer.LOCAL:
			return (where == SyncTokenizer.LOCAL);
		case SyncTokenizer.REMOTE:
			return (where == SyncTokenizer.REMOTE);
		case SyncTokenizer.BOTH:
			return true;
		}*/
		return (usingSyncRulesFile & where) > 0;
//		return false;
	}

	public boolean isUsingRecursion()
	{
		return usingRecursion;
	}

	public boolean isUsingRecursionOnIgnore()
	{
		return usingRecursionOnIgnore;
	}

	/**
	 * Returns the ruleSet.
	 * @return String
	 */
	public String getRuleSet()
	{
		return ruleSet;
	}

	/**
	 * Sets the ruleSet.
	 * @param ruleSet The ruleSet to set
	 */
	public void setRuleSet(String ruleSet)
	{
		this.ruleSet = ruleSet;
	}

	/**
	 * Returns the direction.
	 * @return int
	 */
	public int getDirection()
	{
		return direction;
	}

	/**
	 * Returns the justLogging.
	 * @return boolean
	 */
	public boolean isJustLogging()
	{
		return justLogging;
	}

	/**
	 * Sets the justLogging.
	 * @param justLogging The justLogging to set
	 */
	public void setJustLogging(boolean justLogging)
	{
		this.justLogging = justLogging;
	}

	/**
	 * Returns the applyDeletion.
	 * @return boolean
	 */
	public boolean isApplyingDeletion( int location )
	{
	    return (applyingDeletion & location) > 0;
	}

    public boolean isApplyingIgnoreRules()
    {
        return applyingIgnoreRules;
    }
    public void setApplyingIgnoreRules( boolean applyingIgnoreRules )
    {
        this.applyingIgnoreRules = applyingIgnoreRules;
    }
    public boolean isApplyingSyncRules()
    {
        return applyingSyncRules;
    }
    public void setApplyingSyncRules( boolean applyingSyncRules )
    {
        this.applyingSyncRules = applyingSyncRules;
    }
    public boolean isApplyingTakeRules()
    {
        return applyingTakeRules;
    }
    public void setApplyingTakeRules( boolean applyingTakeRules )
    {
        this.applyingTakeRules = applyingTakeRules;
    }
    public boolean isCheckingBufferAlways( int location )
    {
        return (checkingBufferAlways & location) > 0;
    }
    public boolean isCheckingBufferOnReplace( int location )
    {
        return (checkingBufferOnReplace & location) > 0;
    }
    public boolean isIgnoreAll()
    {
        return ignoreAll;
    }
    public void setIgnoreAll( boolean ignoreAll )
    {
        this.ignoreAll = ignoreAll;
    }
    public Vector getIgnoreRules()
    {
        return ignoreRules;
    }
    public void setIgnoreRules( Vector ignoreRules )
    {
        this.ignoreRules = ignoreRules;
    }
    public void addIgnoreRule( Rule rule )
    {
        this.ignoreRules.add( rule );
    }
    public Vector getSyncRules()
    {
        return syncRules;
    }
    public void setSyncRules( Vector syncRules )
    {
        this.syncRules = syncRules;
    }
    public String getSyncRulesFilename()
    {
        return syncRulesFilename;
    }
    public void setSyncRulesFilename( String syncRulesFilename )
    {
        this.syncRulesFilename = syncRulesFilename;
    }
    public Vector getTakeRules()
    {
        return takeRules;
    }
    public void setTakeRules( Vector takeRules )
    {
        this.takeRules = takeRules;
    }
    public void addTakeRule( Rule rule )
    {
        this.takeRules.add( rule );
    }
    public int getUsingSyncRulesFile()
    {
        return usingSyncRulesFile;
    }
    public void setUsingSyncRulesFile( int usingSyncRulesFile )
    {
        this.usingSyncRulesFile = usingSyncRulesFile;
    }
    public void setApplyingDeletion( int applyingDeletion )
    {
        this.applyingDeletion = applyingDeletion;
    }
    public void setDirection( int direction )
    {
        this.direction = direction;
    }
    public void setUsingRecursion( boolean usingRecursion )
    {
        this.usingRecursion = usingRecursion;
    }
    public void setUsingRecursionOnIgnore( boolean usingRecursionOnIgnore )
    {
        this.usingRecursionOnIgnore = usingRecursionOnIgnore;
    }
    
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
}

