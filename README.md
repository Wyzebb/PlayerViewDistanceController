![Banner](https://i.ibb.co/p1fVk8s/Banner-min.webp)
<p>
  <a href="https://github.com/Wyzebb/PlayerViewDistanceController"><img alt="github" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
  <a href="https://ko-fi.com/wyzebb"><img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg"></a>
  <a href="https://discord.gg/akbd8EPSgr"><img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
</p>
This plugin that makes the maximum view distance controllable per player. Those with permissions can change their own max view distance, the max view distance of any other player or all online players.
<br/><br/>

## **Features**
- **Saves across restarts**: Each player's view distance saves across restarts.
- **Join and leave handling**: Automatically restores player view distances when they connect to the server.
- **Highly configurable**: Control various settings such as default, max, and min view distances, and custom messages.
- **Per-player control**: Each player has their own view distance, which can be set and saved individually.
- **Global control**: Easily set the max view distance of all online players.
- **Commands and permissions**: Easily change view distances via commands or the per-player config, with permissions in place.

<br/>

## **Permissions**
- Base command and set self and other player max view distance: `viewdistance.command`
- Global view distance permission: `viewdistance.setonline`
- Both are granted to ops by default

<br/>

## **Commands**
### Base Command: `/viewdistance`
- **Usage:** `/<command> <subcommand>`
- **Aliases:** `vd`, `setchunks`
  <br/><br/>
### Subcommands
#### - `set`
- **Usage:** `/<vd> <set> <chunks> [player]`
- Sets the maximum view distance of yourself or another online player

#### - `setonline`
- **Usage:** `/<vd> <setonline> <chunks>`
- Sets the maximum view distance of all online players
