package org.bxg.spokencompiler.eclipse.handlers;

/*
 * Copyright 2013-2014 Benjamin M. Gordon
 * 
 * This file is part of the spoken compiler Eclipse plugin.
 *
 * The spoken compiler Eclipse plugin is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The spoken compiler Eclipse plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the spoken compiler Eclipse plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

import org.bxg.spokencompiler.eclipse.SpeechListener;
import org.bxg.spokencompiler.eclipse.SpeechManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class StartDictation extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		SpeechListener l = new SpeechListener( window, "spokenLang.config.xml" );
		Thread t = new Thread( l );
		SpeechManager.getManager().setListener( l, t );
		t.start();
		return null;
	}

}
