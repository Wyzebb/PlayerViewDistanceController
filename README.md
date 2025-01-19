![Banner](https://i.ibb.co/p1fVk8s/Banner-min.webp)
<p>
  <a href="https://github.com/Wyzebb/PlayerViewDistanceController"><img alt="github" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
  <a href="https://ko-fi.com/wyzebb"><img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg"></a>
  <a href="https://discord.gg/akbd8EPSgr"><img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
</p>
This plugin makes the maximum view distance of each player highly customisable.
<br/><br/>The system of priority for calculating max view distances of players has completely changed as of v2.0.0. Scroll to the bottom to see graphics to explain.
<br/><br/>

## **Features**
- **Per-player control**: Each player has their own max view distance, which can be set and saved individually by them or by another player (if they have the permissions to do so).
- **Global control**: Easily set the max view distance of all online players.
- **Saves data persistently**: Automatically restores player view distances when they connect to the server, even across restarts.
- **LuckPerms integration**: Max view distances can be set for groups or individual players via Luckperms (LuckPerms is not required).
- **AFK view restriction**: AFK players can have their views restricted until they next move to improve server performance. This can be completely customised or disabled in the config.
- **Highly configurable**: Control most features and messages in the plugin
- **Bedrock support**: Full bedrock support and bedrock players can have custom default values.
- **Very lightweight**: The plugin does not use much CPU or RAM at all and can even greatly improve performance as the server may have to send less chunk data to players!)
- **Update checker**: As of v2.0.0, an update checker is built-in.
- **Multi-language support**: All messages are customisable and you can even add your own language by creating a file in the 'messages' folder and referencing that file in config.yml. If you wish, you could make a pull request with any more languages so the plugin can ship with them in the future.
- **PlaceHolderAPI support**: Placeholders are now built in to see if players are AFK and to see a player's current max view distance.

<br/>

## **AFK System**
- If a player goes AFK, after a certain amount of time, their view distance will be restricted to stop AFK players making the server lag.
- Restriction distance and the time it takes to consider a player AFK are customisable.
- There is a bypass permission to stop your view distance being restricted when AFK.
- Spectator mode players can bypass AFK by default
- All of the above is customisable in config.yml.

<br />

## Base Command: `/pvdc`
- **Aliases:** `vd`, `viewdistance`
### Subcommands
#### - `set`
- **Usage:** `/pvdc set <chunks> [player]`
- Sets the maximum view distance of yourself or another online player

#### - `setonline`
- **Usage:** `/pvdc setonline <chunks>`
- Sets the maximum view distance of all online players

#### - `get`
- **Usage:** `/pvdc get [player]`
- Get your own max view distance or the max view distance of another player

#### - `reset`
- **Usage:** `/pvdc reset [player]`
- Resets a player's max view distance to the max (or the max allowed by LuckPerms permissions if installed!)

#### - `reload`
- **Usage:** `/pvdc reload`
- Reloads config.yml (restart server to apply changes to custom messages and the AFK system!)

#### - `help`
- **Usage:** `/pvdc help`
- Displays the plugin help message

<br/>

## **Permissions**
- Set your own max view distance: `pvdc.set-self`
- Set the max view distance of other players: `pvdc.set-others`
- Permission-based max view distance via LuckPerms: `pvdc.maxdistance.<chunks>`
- Global view distance: `pvdc.setonline`
- Reload plugin config: `pvdc.reload`
- Get the max view distance of another player: `pvdc.get-others`
- Get your own max view distance: `pvdc.get-self`
- Reset your max view distance to the max (or the max allowed by LuckPerms permissions if installed!): `pvdc.reset-self`
- Reset a player's max view distance to the max (or the max allowed by LuckPerms permissions if installed!): `pvdc.reset-others`
- Bypass any maxdistance permissions assigned to you: `pvdc.bypass-maxdistance`
- Bypass afk checks: `pvdc.bypass-afk`

<br />

- All permissions are granted to ops by default
- `pvdc.get-self` is granted to all players by default

<br />

## **How max view distance is calculated**

![JOIN](https://i.ibb.co/hVx5hCk/Join-dec-tree.png)
![COMMAND](https://i.ibb.co/gw0LnYc/Main-dec-tree.png)
