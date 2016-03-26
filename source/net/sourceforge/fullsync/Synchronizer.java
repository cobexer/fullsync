/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync;

import java.io.IOException;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferTaskExecutor;
import net.sourceforge.fullsync.impl.TaskGeneratorImpl;
import net.sourceforge.fullsync.remote.RemoteManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class should provide wrappers for most common synchronization tasks
 * like synchronizing a profile or perfoming a task tree.
 */
public class Synchronizer {
	private RemoteManager remoteManager;

	private Logger logger = LoggerFactory.getLogger("FullSync");

	public Synchronizer() {
	}

	public synchronized TaskTree executeProfile(Profile profile, boolean interactive) {
		if (remoteManager != null) {
			try {
				return remoteManager.executeProfile(profile.getName());
			}
			catch (Exception e) {
				ExceptionHandler.reportException(e);
			}
		}
		else {
			try {
				TaskGenerator taskGenerator = new TaskGeneratorImpl();
				return taskGenerator.execute(profile, interactive);
			}
			catch (Exception e) {
				ExceptionHandler.reportException(e);
			}
		}
		return null;
	}

	/**
	 * @return Returns the ErrorLevel
	 */
	public int performActions(TaskTree taskTree) {
		return performActions(taskTree, null);
	}

	/**
	 * TODO if we add some listener/feedback receiver here we could
	 * easily use this for visual action performing as well.
	 * -- done ?
	 *
	 * now we still need the action count info before everything is performed
	 * and later we'll need to cancel/stop the whole process
	 * looks like we really need to single thread the whole class !
	 *
	 * @return Returns the ErrorLevel
	 */
	public int performActions(TaskTree taskTree, TaskFinishedListener listener) {
		if (remoteManager != null) {
			logger.info("Remote Synchronization started");
			try {
				remoteManager.performActions(taskTree, listener);
				logger.info("synchronization successful"); // TODO ...with x errors and y warnings
				logger.info("------------------------------------------------------------");
				return 0;
			}
			catch (RemoteException e) {
				ExceptionHandler.reportException(e);
				logger.error("An Exception occured while performing actions", e);
				logger.info("synchronization failed");
				logger.info("------------------------------------------------------------");
				return 1;
			}
		}
		else {
			try {
				logger.info("Synchronization started");
				logger.info("  source:      " + taskTree.getSource().getConnectionDescription().getDisplayPath());
				logger.info("  destination: " + taskTree.getDestination().getConnectionDescription().getDisplayPath());

				BlockBuffer buffer = new BlockBuffer(logger);
				TaskExecutor queue = new FillBufferTaskExecutor(buffer);

				if (listener != null) {
					queue.addTaskFinishedListener(listener);
				}

				buffer.load();
				queue.enqueue(taskTree);
				queue.flush();
				buffer.unload();

				taskTree.getSource().flush();
				taskTree.getDestination().flush();
				taskTree.getSource().close();
				taskTree.getDestination().close();
				logger.info("synchronization successful"); // TODO ...with x errors and y warnings
				logger.info("------------------------------------------------------------");
				return 0;
			}
			catch (IOException ioe) {
				logger.error("An Exception occured while performing actions", ioe);
				logger.info("synchronization failed");
				logger.info("------------------------------------------------------------");
				return 1;
			}
		}
	}

	public void setRemoteConnection(RemoteManager remoteManager) {
		this.remoteManager = remoteManager;
	}

	public void disconnectRemote() {
		remoteManager = null;
	}

	public IoStatistics getIoStatistics(TaskTree taskTree) {
		// HACK omg, that's not the way io stats are intended to be generated / used
		Logger logger = LoggerFactory.getLogger("FullSync");
		BlockBuffer buffer = new BlockBuffer(logger);
		TaskExecutor queue = new FillBufferTaskExecutor(buffer);
		return queue.createStatistics(taskTree);
	}
}
