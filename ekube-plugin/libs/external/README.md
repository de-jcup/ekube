Why is this an empty folder?
============================
libs are copied by gradle at eclipse task


How can i update external libraries?
====================================
If this an update of libraries (currently kubernetes-java-client-api 3.0.0 is in use)
Before doing the maven command: clear complete folder except this README.md.
Do maven clean install
Now open .classpath file of plugin project. delete all entries with external libraries.
after this open project->properties and look at JavaBuildPath/Libraries.
Now call "add jars" and add all new entries from libs/external again