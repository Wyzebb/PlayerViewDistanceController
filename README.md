# PlayerViewDistanceController  
![Banner image](https://i.ibb.co/hdNnMNG/Banner.png)
**<p style="color:red">DISCLAIMER: PAPER IS REQUIRED</p>**

<a href="https://github.com/Wyzebb/PlayerViewDistanceController"><img src="https://i.ibb.co/whyhbcN/Bugs-and-Features-button-Photoroom-1.png" alt="Github" width="350"/></a>

<a href="https://discord.gg/akbd8EPSgr">![Discord](https://img.shields.io/discord/1254765564790837288?style=for-the-badge&logo=discord&label=Discord)</a>

Also see on: <a href="https://modrinth.com/plugin/player-view-distance-controller">Modrinth</a>,    <a href="https://hangar.papermc.io/Wyzebb/PlayerViewDistanceController">Paper Hangar</a>,    <a href="https://www.curseforge.com/minecraft/bukkit-plugins/player-view-distance-controller">Curseforge</a>,    <a href="https://www.spigotmc.org/resources/player-view-distance-controller.117627/">Spigot</a>


<br/>

### **What does this plugin do?**
This plugin that makes the maximum view distance controllable per player. Those with the permission can change their own view distance or the view distance of other players.

<br/>

### **Features**
- **Saves across restarts**: Each player's view distance saves across restarts.
- **Join and leave handling**: Automatically restores player view distances when they connect to the server.
- **Highly configurable**: Control various settings such as default, max, and min view distances, and custom messages.
- **Per-player control**: Each player has their own view distance, which can be set and saved individually.
- **Commands and permissions**: Easily change view distances via commands or the per-player config, with permissions in place.

<br/>

### **Commands**
####  `/viewdistance`
- **Usage:** `/<command> <chunks> [player]`
- **Aliases:** `setchunks`, `vd`

<br/>

**Permission:** `viewdistance.command`
- Operators have the permission granted by default

<br/>

### **Example Usage**
- To set your own view distance to 10 chunks:
  ```
  /viewdistance 10
  ```
- To set another player's view distance to 15 chunks:
  ```
  /viewdistance 15 <player>
  ```
