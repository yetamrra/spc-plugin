spc-plugin
============

This is the Eclipse plugin for the spoken language programming environment
in Benjamin M. Gordon's PhD dissertation.  

# Building

After cloning the project, you also need to download Sphinx 4 from 
http://cmusphinx.sourceforge.net/ and unzip and build it somewhere. Make
sure to run the jsapi.sh command inside lib to generate a working jsapi.jar
file.

Since this is an Eclipse plugin, it builds in Eclipse.  You need to set the
SPHINX_HOME classpath variable to wherever you put Sphinx.  You will also
need to copy these files from SPHINX_HOME/lib into lib:

* sphinx4.jar
* jsapi.jar
* WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar

You will also need a copy of the spc-compiler repository checked out as a
second project in the same workspace.

# Running

To run the project, switch to the "Plug-In Development" perspective.  You
may have to do `Window -> Open Perspective -> Other...` if you haven't
used it before.

Double-click the META-INF/MANIFEST.MF entry in the Package Explorer, then
click the green Run arrow in the plugin editor that opens.  This will launch
a new copy of Eclipse with your plugin loaded.

The plugin adds a new "Spoken Programming" menu and several corresponding
toolbar buttons.  To begin dictation, create a file with a .spk extension and
click the "start dictation" button while that file is open in an editor.  To
stop dictation, say "stop listening" or click the "stop dictation" button.

The language syntax matches the syntax accepted by the compiler, with a few
additional editing commands.  Read the .gram files in the lib directory to
see the exact syntax.

# License

Some portions of this code came from demo code in the CMU Sphinx project and
are covered by their license.  Everything else is released under the GPL 3.
