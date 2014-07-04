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
* jsapi-1.0-base.jar
* WSJ_8gau_13dCep_16k_40mel_130Hz_6800Hz.jar

Make sure your copy of the WSJ jar has the /dict/fillerdict file in it.  This
file was present in Sphinx beta5, but may no longer be there if you build Sphinx
beta6.

You will also need a copy of the spc-compiler repository checked out as the
grammar project in the same workspace.

# Running

To run the project, switch to the "Plug-In Development" perspective.  You
may have to do `Window -> Open Perspective -> Other...` if you haven't
used it before.

Double-click the META-INF/MANIFEST.MF entry in the Package Explorer, then
click the green Run arrow in the plugin editor that opens.  This will launch
a new copy of Eclipse with your plugin loaded.  Once you've done this, a
normal run configuration is created that will run your plugin without having
the MANIFEST.MF file open.

The plugin adds a new "Spoken Programming" menu and several corresponding
toolbar buttons.  Before you can begin dictation, you need to add the
"Spoken Programming" view by going to `Window -> Show View -> Other...`
and finding it in the list.  Then create a file with a .spk extension and
click the "start dictation" button while that file is open in an editor.  To
stop dictation, say "stop listening" or click the "stop dictation" button.

The language syntax matches the syntax accepted by the compiler, with a few
additional editing commands.  Read the .gram files in the lib directory to
see the exact syntax.

# License

Some portions of this code came from demo code in the CMU Sphinx project and
are covered by their license.  Everything else is released under the GPL 3.
