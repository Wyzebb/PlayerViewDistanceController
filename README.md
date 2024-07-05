![Banner](https://i.ibb.co/p1fVk8s/Banner-min.webp)
`DISCLAIMER: PAPER IS REQUIRED`
<p>
    <a href="https://www.spigotmc.org/resources/player-view-distance-controller.117627/"><img alt="spigot" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg"></a>
    <a href="https://modrinth.com/plugin/player-view-distance-controller"><img alt="modrinth" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
    <a href="https://www.curseforge.com/minecraft/bukkit-plugins/pvdc"><img alt="curseforge" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg">
    <a href="https://hangar.papermc.io/Wyzebb/PlayerViewDistanceController"><img alt="hangar" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg"></a>
    <br />
    <a href="https://discord.gg/akbd8EPSgr"><img alt="discord-plural" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
    <a href="https://www.patreon.com/Wyzebb"><img alt="patreon-singular" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/patreon-singular_vector.svg"></a>
</p>
<br/>
This plugin that makes the maximum view distance controllable per player. Those with the permission can change their own view distance or the view distance of other players.

<br/>

## **Features**
- **Saves across restarts**: Each player's view distance saves across restarts.
- **Join and leave handling**: Automatically restores player view distances when they connect to the server.
- **Highly configurable**: Control various settings such as default, max, and min view distances, and custom messages.
- **Per-player control**: Each player has their own view distance, which can be set and saved individually.
- **Commands and permissions**: Easily change view distances via commands or the per-player config, with permissions in place.

<br/>

## **Permissions**
- Main permission: `viewdistance.command`
- Granted to ops by default

<br/>

## **Commands**
####  `/viewdistance`
- **Usage:** `/<command> <chunks> [player]`
- **Aliases:** `setchunks`, `vd`

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
