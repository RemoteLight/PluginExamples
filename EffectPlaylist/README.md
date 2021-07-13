# Effect Playlist Plugin
Create a playlist with Animations and MusicEffects to achieve an automatic effect sequence.

## Build
- clone project: `git clone https://github.com/RemoteLight/PluginExamples.git`
- navigate into the EffectPlaylist directory: `cd ./PluginExamples/EffectPlaylist`
- init maven project: `mvn install`

## Run plugin
If you only want to download the plugin: [download here](https://github.com/RemoteLight/PluginExamples/raw/master/EffectPlaylist/target/EffectPlaylist-1.2.jar) and put it in the `%homepath%/.RemoteLight/plugins` folder (or start RemoteLight, go to Tools > Plugins and click 'Open plugins folder')

... or build it yourself:
- follow the [steps above](#build)
- build jar file: `mvn clean package`
- copy `EffectPlaylist-1.0.jar` from `/EffectPlaylist/target` to `%homepath%/.RemoteLight/plugins`
- start RemoteLight

## What does this plugin do?
It adds the functionality to [RemoteLight](https://github.com/Drumber/RemoteLight) to create your own scheduled effect sequences.  

Install the plugin as described above and then go to the `Tools Menu Panel` > `Effect Playlist`. There you can create new playlists by clicking on `Create Playlist`.  
In a playlist you can add Animations and MusicEffects and set the duration how long the effect should run.  
To start a playlist go to the playlist panel and click on the start button at the desired playlist entry.

**Note:** You have to set up and activate an output first. In case you are using MusicEffects, the audio input should be configured in the MusicSync menu panel.

## Screenshot
![screenshot](https://user-images.githubusercontent.com/29163322/91655569-d7de4300-eab1-11ea-9a55-175a1455fb81.png)
