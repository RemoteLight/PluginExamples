# Simple Plugin usign Maven
Example project of a plugin for RemoteLight using Maven.

## Build
- clone project: `git clone https://github.com/RemoteLight/PluginExamples.git`
- navigate into the MavenExample directory: `cd ./PluginExamples/MavenExample`
- init maven project: `mvn install`

## Run plugin
- follow the steps above
- build jar file: `mvn clean install`
- copy `maven-example-0.1.jar` from `/MavenExample/target` to `%homepath%/.RemoteLight/plugins`
- run RemoteLight

## What does this plugin do?
It adds a simple animation with the name 'SimpleAnimation'.