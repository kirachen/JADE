
GUIDE FOR DOWNLOADING, INSTALLING AND RUNNING JADE

*****

*Installing*

Download jadeAll.zip (3.7 is the latest version, July 2009) from http://jade.tilab.com (you will be required to create a user account) into a directory of your choice (e.g. jade-3.7).

Load terminal. Go to directory jade-3.7. Unzip JADE-all-3.7.zip using command
jar xvf JADE-all-3.7.zip

Subsequently, unzip the four zips just extracted using commands
jar xvf JADE-bin-3.7.zip
jar xvf JADE-doc-3.7.zip
jar xvf JADE-examples-3.7.zip
jar xvf JADE-src-3.7.zip

*****

*Testing the installation*

Load terminal. Go to directory jade-3.7/jade (extracted from the zip files during installation).

Try the following command (if using Linux):
java -classpath "lib/jade.jar:lib/jadeTools.jar:lib/http.jar:." jade.Boot -gui

Or, instead, use the runJade.sh script (remembering to change the classpaths according to where you installed jade), as follows:
sh runJade.sh

Alternatively, if using Windows, try the following command:
java -classpath .;lib\jade.jar;lib\jadeTools.jar;lib\http.jar jade.Boot -gui

The GUI of the remote management agent should appear.

Note: Remember to modify the classpaths if running command from a directory other than jade-3.7/jade.

*****

*Compiling source files*

Load terminal. Go to directory jade-3.7/jade.

Try the following command:
javac -classpath "lib/jade.jar:lib/jadeTools.jar:lib/http.jar:." <source files> <-d> <destination directory>
e.g.
javac -classpath "lib/jade.jar:lib/jadeTools.jar:lib/http.jar:." "src/examples/hello/HelloWorldAgent.java" -d "classes"

This will compile the HelloWorldAgent.java source file and store the class files in directory jade-3.7/jade/classes with file name examples/hello/HelloWorldAgent.class according to the package name specified in HelloWorldAgent.java.

Alternatively, use the compileJade.sh or compileJade2.sh script (remembering to change the classpaths according to where you installed jade), as follows:
sh compileJade.sh "src/examples/hello/HelloWorldAgent.java"
or
sh compileJade2.sh "src/examples/hello/HelloWorldAgent.java" "classes"

You should now be able to run the HelloWorldAgent application. Enter command:
java -classpath "lib/jade.jar:lib/jadeTools.jar:lib/http.jar:classes:." jade.Boot -gui "Peter:examples.hello.HelloWorldAgent"
Note that the directory 'classes' is included in the classpaths.

*****

*Sniffing agents/messages*

The Sniffer agent can be loaded up by means of the GUI or the command prompt, specified as follows:
"sniffagent:jade.tools.sniffer.Sniffer"
or
"sniffagent:jade.tools.sniffer.Sniffer(ams pete)"
The latter command loads the Sniffer agent sniffing the "ams" and "pete" agents. If you want to specify agents that are to be sniffed but which might not yet exist when the Sniffer is born, add a file 'sniffer.properties' in the current directory that contains the preload property indicating the name of the agents to be sniffed, e.g.
"preload=agent1;agent2;helloagent*"
See jade.tools.sniffer.Sniffer class javadoc for more details.

*****