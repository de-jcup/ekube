Why is this an empty folder?
============================
libs are copied by gradle at eclipse task


How can i update external libraries?
====================================

- delete external libraries and fetch new ones by calling
```
./gradlew cleanLibraries provideLibraries
```
- then open `ekube-plugin/plugin.xml` **runtime** tab and
  * remove all external lib definitions there 
  * add all external libs being now in external folder
