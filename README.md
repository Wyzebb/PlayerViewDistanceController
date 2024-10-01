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
- **Per-player control**: Each player has their own view distance, which can be set and saved individually.
- **Permission-based control**: Max view distance can be set for groups or individual players via Luckperms (LuckPerms is optional).
- **Global control**: Easily set the max view distance of all online players.
- **Saves data persistently**: Automatically restores player view distances when they connect to the server, even across restarts.
- **Highly configurable**: Control various settings such as default, max, and min view distances, and custom messages.
- **Name prefix control**: Allow bedrock players or players with specific prefixes on their names to have custom max view distances.

<br/>

## **Permissions**
- Set your own max view distance: `pvdc.set-self`
- Set the max view distance of other players: `pvdc.set-others`
- Permission-based max view distance via LuckPerms: `pvdc.maxdistance.<chunks>`
- Global view distance: `pvdc.setonline`
- Reload plugin config: `pvdc.reload`
- Get the max view distance of another player: `pvdc.get-others`
- Get your own max view distance: `pvdc.get-self`

- All permissions are granted to ops by default
- `pvdc.get-self` is granted to all players by default

<br/>

## **Commands**
### Base Command: `/viewdistance`
- **Usage:** `/<command> <subcommand>`
- **Aliases:** `vd`, `pvdc`
### Subcommands
#### - `set`
- **Usage:** `/<vd> <set> <chunks> [player]`
- Sets the maximum view distance of yourself or another online player

#### - `setonline`
- **Usage:** `/<vd> <setonline> <chunks>`
- Sets the maximum view distance of all online players

#### - `reload`
- **Usage:** `/<vd> <reload>`
- Reloads the plugin config files

#### - `get`
- **Usage:** `/<vd> <get>`
- Get your own max view distance or the max view distance of another player

#### - `help`
- **Usage:** `/<vd> <help>`
- Displays the plugin help message