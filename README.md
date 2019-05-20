# spc-plugin

This is the Eclipse plugin for the spoken language programming environment
in Benjamin M. Gordon's PhD dissertation.  See the
[spc-compiler](https://github.com/yetamrra/spc-compiler) repository on
github for the matching compiler.

## Prerequisites

*  [spc-compiler](https://github.com/yetamrra/spc-compiler), the compiler
   backend for this plugin.
*  ANTLR 3.5.2 from [antlr3.org](https://www.antlr3.org/download.html).
*  A recent
   [Eclipse](https://www.eclipse.org/downloads/packages/release/2019-03/r/eclipse-ide-java-developers)
   IDE with the egit, Java, and PDE plugins.  Fedora's 2018-12 and the upstream
   2019-03 are known to work.  The "Eclipse IDE for Java Developers" edition
   includes most of what you need, but you will need to add the
   "Eclipse plug-in development environment" plugin through the software updater.
*  [CMU Sphinx 4](https://cmusphinx.github.io).  This has been tested with the
   prebuilt [1.0 beta5](https://sourceforge.net/projects/cmusphinx/files/sphinx4/). 

## Building

First, follow the build instructions for the
[spc-compiler](https://github.com/yetamrra/spc-compiler) repository
from github and make sure you can run all the tests. 

Clone this project somewhere, e.g. `~/projects/spc-plugin`.  Import
`~/projects/spc-plugin` into the same workspace as the `spc-compiler` project
with the `File -> Import -> Git` wizard.  Be sure to choose the
"Import existing Eclipse projects" option at the appropriate step so that it
reads the existing metadata.  This should create a project called
`EclipsePlugin`.

Copy `antlr-3.5.2-complete.jar` from `spc-compiler/lib` into `lib`.  Then
right-click `/spc-compiler/spc.jardesc` and choose "Create JAR".  This will
create `/EclipsePlugin/lib/spc.jar`.  You will need to redo this step
whenever you change the compiler backend.

Download [Sphinx 4](https://sourceforge.net/projects/cmusphinx/files/sphinx4/)
from Sourceforge and unzip and build it somewhere, e.g. `~/projects/sphinx4`.
In a terminal:

```
$ cd ~/projects/sphinx4
$ cd lib
$ bash jsapi.sh
$ cd ..
$ ant
```

When you run the `jsapi.sh` command inside `~/projects/sphinx4/lib`, you will
have to agree to the license and then it generates a working `jsapi.jar` file.

After building Sphinx, you will also need to copy these files from
`~/projects/sphinx4/lib` into `lib` inside the `EclipsePlugin` project:

* sphinx4.jar
* jsapi.jar
* jsapi-1.0-base.jar
* WSJ\_8gau\_13dCep\_16k\_40mel\_130Hz\_6800Hz.jar

Make sure your copy of the WSJ jar has the `/dict/fillerdict` file in it.
This file was present in Sphinx beta5, but may no longer be there if you build
Sphinx beta6.

Now leave your terminal and head back to Eclipse.  Since this is an Eclipse
plugin, it builds in Eclipse.  You need to set the `SPHINX_HOME` classpath
variable to wherever you put Sphinx:

1.  `Window -> Preferences -> Java -> Build Path -> Classpath Variables`
1.  Click the `New...` button.
1.  Call your new variable `SPHINX_HOME` and point it to `~/projects/sphinx4`
    or wherever you unpacked Sphinx earlier.  Hit OK.
1.  Rebuild your whole project after hitting `Apply and Close`.

Once you have all this in place, Eclipse should build the plugin without any
ongoing tweaking.

## Running

To run the project, switch to the "Plug-In Development" perspective.  You
may have to do `Window -> Open Perspective -> Other...` if you haven't
used it before.

Double-click the `META-INF/MANIFEST.MF` entry in the Package Explorer, then
click the green Run arrow in the plugin editor that opens.  This will launch
a new copy of Eclipse with your plugin loaded.  Once you've done this, a
normal run configuration is created that will run your plugin without having
the `MANIFEST.MF` file open.

The plugin adds a new "Spoken Programming" menu and several corresponding
toolbar buttons.  Before you can begin dictation, you need to add the
"Spoken Programming" view by going to `Window -> Show View -> Other...`
and finding it in the list.  Then create a file with a .spk extension and
click the "start dictation" button while that file is open in an editor.  To
stop dictation, say "stop listening" or click the "stop dictation" button.

The language syntax matches the syntax accepted by the compiler, with a few
additional editing commands.  Read the .gram files in the lib directory to
see the exact syntax.

## License

Some portions of this code came from demo code in the CMU Sphinx project and
are covered by their license.  Everything else is released under the GPL 3.
