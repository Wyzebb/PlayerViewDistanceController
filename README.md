![Banner](https://i.ibb.co/p1fVk8s/Banner-min.webp)
<p>
  <a href="https://github.com/Wyzebb/PlayerViewDistanceController"><img alt="github" height="56"
        src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
  <a href="https://ko-fi.com/wyzebb"><img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg"></a>
  <a href="https://discord.gg/akbd8EPSgr"><img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg"></a>
</p>

This plugin makes the maximum view distance of each player highly customisable.
<br/><br/>
The customisability of the plugin can make it a little confusing, so please refer to the documentation and flow diagrams and join the Discord if you need help!
<br/><br/>

## **Features**
- **Per-player control**: Each player has their own max view distance, which can be set and saved individually by them or by another player (if they have the permissions to do so).
- **Global control**: Easily set the max view distance of all online players.
- **Saves data persistently**: Automatically restores player view distances when they connect to the server, even across restarts.
- **LuckPerms integration**: Max view distances can be set for groups or individual players via Luckperms (LuckPerms is not required).
- **AFK view restriction**: AFK players can have their views restricted until they next move to improve server performance. This can be completely customised or disabled in the config.
- **Highly configurable**: Control most features and messages in the plugin (default configs at the bottom of this page!)
- **Bedrock support**: Full bedrock support and bedrock players can have custom default values.
- **Very lightweight**: The plugin does not use much CPU or RAM at all and can even greatly improve performance as the server may have to send less chunk data to players!)
- **Update checker**: As of v2.0.0, an update checker is built-in.
- **Multi-language support**: All messages are customisable and you can even add your own language by creating a file in the 'lang' folder and referencing that file in config.yml. If you wish, you could make a pull request with any more languages so the plugin can ship with them in the future. The plugin currently ships with English, Russian and Chinese.
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
- You can do `/pvdc <chunks>` as a shortcut as of v2.1.0

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

## **Default Configs**

<details style="font-size:1rem">
<summary>config.yml</summary>

```yaml
language: en_US
colour: "§e§l(!) §e"
error-colour: "§c§l(!) §c"
success-colour: "§a§l(!) §a"

# Default view distance for anyone who joins the server (Must be between 2 and 32)
default-distance: 32
bedrock-default-distance: 32

# Maximum view distance for anyone (Cannot exceed 32)
max-distance: 32

# Minimum view distance for anyone (Cannot be less than 2)
min-distance: 2

# Display a message when a player joins telling them what their view distance is set to
display-msg-on-join: true

# Display a message when a player joins telling them what their view distance is set to, when it is the default or maximum
display-max-join-msg: false

# If chunks loaded will be limited for AFK players
afk-chunk-limiter: true

# After how many seconds the plugin will consider a player AFK
afkTime: 20

# The view distance of an AFK player
afkChunks: 2

# Whether players in spectator mode bypass AFK checks
spectators-can-afk: true

update-checker-enabled: true
```
</details>

<br />

<details style="font-size:1rem">
<summary>en_US.yml</summary>

```yaml
messages:
  join: "Your maximum view distance is currently set to {chunks} chunks"

  player-offline: "That player is not online!"
  no-permission: "You do not have permission to execute this command!"

  # The message displayed when there are not enough arguments or the arguments are invalid
  incorrect-args: "Incorrect arguments - Use /pvdc help for help"

  afk: "You're AFK, so your view distance has been restricted to {chunks} chunks!"
  afk-return: "Your view distance has returned to normal!"

  chunks-too-high: "You can only set your view distance to a value less than or equal to {chunks} chunks!"

  self-view-distance-change: "You changed your maximum view distance to {chunks} chunks"

  # The message displayed for the command sender when they change someone else's maximum view distance
  sender-view-distance-change: "{target-player}'s maximum view distance was changed to {chunks} chunks"

  # The message displayed for a player when their maximum view distance has been changed
  target-view-distance-change: "Your maximum view distance has been changed to {chunks} chunks"

  reset: "You reset {target-player}'s saved view distance!"
  self-reset: "You reset your saved view distance!"

  reload-config: "The PVDC config files have been successfully reloaded"

  all-online-change: "The maximum view distance of all online players has been set to {chunks} chunks"

  view-distance-get: "{target-player}'s maximum view distance is currently set to {chunks} chunks"
  self-view-distance-get: "Your maximum view distance is currently set to {chunks} chunks"


commands:
  get: "Displays a player's max view distance"
  help: "Displays the plugin's help message"
  reload: "Reload the plugin's config.yml"
  reset: "Resets a player's max view distance"
  set: "Set your own max view distance or the max view distance of another player"
  setonline: "Sets the max view distance of all online players"
```
</details>
