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
package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.events.TaskTreeStarted;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;

public abstract class AbstractTaskGenerator implements TaskGenerator {
	protected FileSystemManager fsm;

	public AbstractTaskGenerator() {
		this.fsm = new FileSystemManager();
	}

	@Override
	public TaskTree execute(Profile profile, boolean interactive)
			throws FileSystemException, URISyntaxException, DataParseException, IOException {
		Site d1 = null, d2 = null;

		RuleSet rules = profile.getRuleSet().createRuleSet();

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

		try {
			ConnectionDescription srcDesc = profile.getSource();
			ConnectionDescription dstDesc = profile.getDestination();
			if (interactive) {
				srcDesc.setParameter(ConnectionDescription.PARAMETER_INTERACTIVE, "true");
				dstDesc.setParameter(ConnectionDescription.PARAMETER_INTERACTIVE, "true");
			}
			else {
				srcDesc.clearParameter(ConnectionDescription.PARAMETER_INTERACTIVE);
				dstDesc.clearParameter(ConnectionDescription.PARAMETER_INTERACTIVE);
			}
			d1 = fsm.createConnection(srcDesc);
			d2 = fsm.createConnection(dstDesc);
			return execute(d1, d2, actionDecider, rules);
		}
		catch (FileSystemException ex) {
			if (d1 != null) {
				d1.close();
			}
			if (d2 != null) {
				d2.close();
			}
			throw ex;
		}
	}

	@Override
	public TaskTree execute(Site source, Site destination, ActionDecider actionDecider, RuleSet rules)
			throws DataParseException, FileSystemException, IOException {
		if (!source.isAvailable()) {
			throw new FileSystemException("source is unavailable");
		}
		if (!destination.isAvailable()) {
			throw new FileSystemException("destination is unavailable");
		}

		TaskTree tree = new TaskTree(source, destination);
		Task root = new Task(null, null, State.InSync, new Action[] { new Action(Action.Nothing,
				Location.None, BufferUpdate.None, "Root") });
		tree.setRoot(root);

		FullSync.publish(new TaskTreeStarted(tree));

		// TODO use syncnodes here [?]
		// TODO get traversal type and start correct traversal action
		synchronizeDirectories(source.getRoot(), destination.getRoot(), rules, root, actionDecider);

		// TODO this would be better, but we need the rules to sync Nodes :-/
		// synchronizeNodes( source.getRoot(), destination.getRoot(), rules, root );

		FullSync.publish(tree);
		return tree;
	}

	public abstract void synchronizeNodes(File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider) throws DataParseException, IOException;

	public abstract void synchronizeDirectories(File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider) throws DataParseException, IOException;
}
