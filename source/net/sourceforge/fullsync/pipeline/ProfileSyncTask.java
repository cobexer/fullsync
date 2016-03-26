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
package net.sourceforge.fullsync.pipeline;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BackgroundTask;
import net.sourceforge.fullsync.BackgroundTaskState;
import net.sourceforge.fullsync.FileFilterChain;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.impl.BackupActionDecider;
import net.sourceforge.fullsync.impl.ExactCopyActionDecider;
import net.sourceforge.fullsync.impl.PublishActionDecider;
import net.sourceforge.fullsync.impl.PublishOverwriteActionDecider;
import net.sourceforge.fullsync.impl.StateDecider;
import net.sourceforge.fullsync.impl.TwoWaySyncActionDecider;
import net.sourceforge.fullsync.util.DebouncedHysteresisEmitter;
import net.sourceforge.fullsync.util.HysteresisReceiver;
import net.sourceforge.fullsync.util.SmartQueue;

public class ProfileSyncTask implements BackgroundTask, HysteresisReceiver, TaskletWorkNotificationTarget {
	private BackgroundTaskState state;
	private final Profile profile;
	private final boolean interactive;
	private final AtomicInteger workingTasks;
	private final ArrayList<SyncTasklet<? extends Object, ? extends Object>> subTasks;
	private final DebouncedHysteresisEmitter debouncer;
	private SmartQueue<Runnable> cleanupTasks;

	public ProfileSyncTask(final Profile _profile, final boolean _interactive) {
		state = BackgroundTaskState.Initializing;
		profile = _profile;
		interactive = _interactive;
		workingTasks = new AtomicInteger();
		subTasks = new ArrayList<SyncTasklet<? extends Object, ? extends Object>>(5);
		debouncer = new DebouncedHysteresisEmitter(this, 0, 300);
		restart();
	}

	@Override
	public BackgroundTaskState getState() {
		return state;
	}

	@Override
	public synchronized void pause() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.pause();
		}
	}

	@Override
	public synchronized void cancel() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.cancel();
		}
		debouncer.cancel();
	}

	@Override
	public synchronized void resume() {
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			task.resume();
		}
	}

	@Override
	public synchronized void restart() {
		cancel();
		state = BackgroundTaskState.Running;
		subTasks.clear();
		FileFilterChain filterChain = WeightedFilterChain.fromProfile(profile);
		ListFilesystemTasklet src = new ListFilesystemTasklet(this, profile.getSource(), filterChain);
		ListFilesystemTasklet dst = new ListFilesystemTasklet(this, profile.getDestination(), filterChain);
		subTasks.add(src);
		subTasks.add(dst);
		//TODO: decide if src and target use the same location and thus should avoid multiple connections
		DebugPrintQueue<File> srcDebugPrinter = new DebugPrintQueue<File>(this, src.getOutput(), "SRC");
		DebugPrintQueue<File> dstDebugPrinter = new DebugPrintQueue<File>(this, dst.getOutput(), "DST");
		subTasks.add(srcDebugPrinter);
		subTasks.add(dstDebugPrinter);
		StateDecider stateDecider = new StateDecider(profile.getRuleSet().createRuleSet());
		ActionDecider actionDecider;
		if (profile.getSynchronizationType().equals("Publish/Update")) {
			actionDecider = new PublishActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Publish/Update Overwrite")) {
			actionDecider = new PublishOverwriteActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Backup Copy")) {
			actionDecider = new BackupActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Exact Copy")) {
			actionDecider = new ExactCopyActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Two Way Sync")) {
			actionDecider = new TwoWaySyncActionDecider();
		}
		else {
			throw new IllegalArgumentException("Profile has unknown synchronization type.");
		}
		SyncActionGenerator actionGenerator = new SyncActionGenerator(this, srcDebugPrinter.getOutput(), dstDebugPrinter.getOutput(),
				stateDecider, actionDecider);
		subTasks.add(actionGenerator.getSourceTask());
		subTasks.add(actionGenerator.getDestinationTask());
		DebugPrintQueue<Task> taskDebugPrinter = new DebugPrintQueue<Task>(this, actionGenerator.getOutput(), "Task");
		subTasks.add(taskDebugPrinter);
		if (interactive) {
			//TODO: Queue for the GUI
		}
		else {
			//TODO: Queue for execution
		}
		for (SyncTasklet<? extends Object, ? extends Object> task : subTasks) {
			FullSync.submit(task);
		}
	}

	@Override
	public void showUI() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startWork() {
		int working = workingTasks.incrementAndGet();
		if (1 == working) {
			debouncer.up();
		}
	}

	@Override
	public void endWork() {
		int working = workingTasks.decrementAndGet();
		if (0 == working) {
			debouncer.down();
		}
	}

	@Override
	public synchronized void up() {
		FullSync.publish(this); // FIXME: publish BackgroundTaskWorking() or something
	}

	@Override
	public synchronized void down() {
		FullSync.publish(this); // FIXME: publish BackgroundTaskIdle() or something
	}

	@Override
	public void queueCleanupTask(Runnable task) {
		cleanupTasks.offer(task);
	}

	@Override
	public synchronized void syncEnded() {
		Runnable r;
		cleanupTasks.shutdown();
		for (r = null; null != r; r = cleanupTasks.take()) {
			try {
				r.run();
			}
			catch (Exception ex) {
				//TODO: remember / log
			}
		}
	}

}

class SyncEnded extends SyncTasklet<Task, Object> {
	private TaskletWorkNotificationTarget workNotificationTarget;

	public SyncEnded(TaskletWorkNotificationTarget _workNotificationTarget, SmartQueue<Task> _inputQueue) {
		super(_workNotificationTarget, _inputQueue);
		workNotificationTarget = _workNotificationTarget;
	}

	@Override
	protected void processItem(Task item) throws Exception {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void cancel() {
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		workNotificationTarget.syncEnded();
	}
}
