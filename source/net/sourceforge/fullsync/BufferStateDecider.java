package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferStateDecider
{
    public State getState( Node buffered );
}
