/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

import java.util.ArrayList;

import org.apache.log4j.Logger;



/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SchedulerImpl implements Scheduler, Runnable
{
    private Logger logger = Logger.getLogger( Scheduler.class );
    private ScheduleTaskSource scheduleSource;
	private Thread worker;
	private boolean running;
	private boolean enabled;
	
	private ArrayList<SchedulerChangeListener> schedulerListeners;
	
	public SchedulerImpl()
	{
		this( null );
	}
	public SchedulerImpl( ScheduleTaskSource source )
	{
		scheduleSource = source;
		schedulerListeners = new ArrayList<SchedulerChangeListener>();
	}
	
	public void setSource( ScheduleTaskSource source )
	{
		scheduleSource = source;
	}
	public ScheduleTaskSource getSource()
    {
        return scheduleSource;
    }
	public void addSchedulerChangeListener(SchedulerChangeListener listener) 
	{
    	schedulerListeners.add(listener);
    }
    public void removeSchedulerChangeListener(SchedulerChangeListener listener) 
    {
    	schedulerListeners.remove(listener);
    }
    protected void fireSchedulerChangedEvent() 
    {
    	for (int i = 0; i < schedulerListeners.size(); i++) {
    		((SchedulerChangeListener)schedulerListeners.get(i)).schedulerStatusChanged(enabled);
    	}
    }
	public boolean isRunning()
    {
        return running;
    }
	public boolean isEnabled()
	{
	    return enabled;
	}

	public void start()
	{
	    if( enabled )
	        return;
	    
		enabled = true;
		if( worker == null || !worker.isAlive() )
		{
			worker = new Thread( this, "Scheduler" );
			worker.setDaemon( true );
			worker.start();
		}
		fireSchedulerChangedEvent();
	}
	
	public void stop()
	{
	    if( !enabled || worker == null )
	        return;
	    
		enabled = false;
		if( running )
		{
			worker.interrupt();
		}
		try {
            worker.join();
        } catch( InterruptedException e ) {
        } finally {
            worker = null;
        }
        fireSchedulerChangedEvent();
	}
	
	public void refresh()
    {
	    if( worker != null )
	        worker.interrupt();
    }
	
	public void run()
	{
		running = true;
		while( enabled )
		{
		    long now = System.currentTimeMillis();
		    if( logger.isDebugEnabled() )
		        logger.debug( "searching for next task after "+now );
			ScheduleTask task = scheduleSource.getNextScheduleTask();
			if( logger.isDebugEnabled() )
			    logger.debug( "found: "+task.toString()+" at "+task.getExecutionTime() );
			
			if( task == null )
			{
				// TODO log sth here ?
				break;
			}
			
			long nextTime = task.getExecutionTime();
			try {
				if( logger.isDebugEnabled() )
					logger.debug( "waiting for "+(nextTime-now)+" mseconds" );
				if( nextTime >= now )
					Thread.sleep( nextTime-now );
				if( logger.isDebugEnabled() )
				    logger.debug( "Running task "+task );
				task.run();
			} catch( InterruptedException ie ) {
			}
			
		}
		running = false;
		if( enabled )
		{
		    enabled = false;
		    fireSchedulerChangedEvent();
		}
	}
}
