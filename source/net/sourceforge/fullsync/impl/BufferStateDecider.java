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

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

public class BufferStateDecider extends StateDecider implements net.sourceforge.fullsync.BufferStateDecider {
	public BufferStateDecider(FileComparer comparer) {
		super(comparer);
	}

	@Override
	public State getState(File buffered) throws DataParseException, IOException {
		if (!buffered.isBuffered()) {
			return State.InSync;
		}

		File source = buffered.getUnbuffered();
		BufferedFile destination = (BufferedFile) buffered;

		if (!source.exists()) {
			if (!destination.exists()) {
				return State.InSync;
			}
			else {
				return State.OrphanDestination;
			}
		}
		else if (!destination.exists()) {
			return State.OrphanSource;
		}

		if (source.isDirectory()) {
			if (destination.isDirectory()) {
				return State.InSync;
			}
			else {
				return State.DirSourceFileDestination;
			}
		}
		else if (destination.isDirectory()) {
			return State.FileSourceDirDestination;
		}

		return comparer.compareFiles(source, destination);
	}
}
