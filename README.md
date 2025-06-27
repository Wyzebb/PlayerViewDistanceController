![Banner](https://i.ibb.co/p1fVk8s/Banner-min.webp)
<p>
    <a style="padding-right: 0.5rem" href="https://docs.wyzebb.dev/docs/pvdc/intro"><img alt="gitbook" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/gitbook_vector.svg" /></a>
    <a style="padding-right: 0.5rem" href="https://discord.gg/akbd8EPSgr"><img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg" /></a>
    <a style="padding-right: 0.5rem" href="https://modrinth.com/plugin/pvdc"><img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg" /></a>
    <a style="padding-right: 0.5rem" href="https://github.com/sponsors/Wyzebb"><img alt="ghsponsors-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/ghsponsors-singular_64h.png" /></a>
    <a style="padding-right: 0.5rem" href="https://ko-fi.com/wyzebb"><img alt="kofi-singular-alt" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular-alt_vector.svg" /></a>
</p>

**Player View Distance Controller (PVDC)** is a lightweight yet powerful Paper plugin that gives full control over view distance. It boosts performance and offers extensive customisation, including permissions, automatic performance modes, AFK handling, and more. With support for LuckPerms, GeyserMC, and PlaceholderAPI, it integrates smoothly into any server.

Every feature is customisable through config and permissions. View this intro page with more information **[in the docs](https://docs.wyzebb.dev/docs/pvdc/intro)**.

## **Key Features**
- **Per-player control**: Each player has their own max view distance, which can be set and saved individually by themselves or by another player.
- **Global control**: Easily set the max view distance of all online players.
- **MSPT‑based dynamic mode**: Automatically reduce view distance when MSPT increases, helping to stabilise performance.
- **Ping‑based adjustments**: Automatically reduce view distance for players with high ping to improve client performance.
- **AFK view restriction**: AFK players can have their view distances restricted while AFK to reduce unnecessary chunk loading and ticking.
- **LuckPerms integration**: There are many permissions available and max view distances can be set for groups or individual players via Luckperms (LuckPerms is not required).
- **Saves data persistently**: Automatically sets max view distances when players connect to the server.
- **Bedrock support**: Players connecting via GeyserMC are fully supported and bedrock players can have custom default values.
- **Very lightweight**: The plugin is very light and can even greatly improve performance as the server may have to send less chunk data to players! Entities, blocks and fluids outside view distance are also not ticked, improving performance.
- **Multi-language support**: All messages are customisable and you can even add your own languages.
- **PlaceHolderAPI support**: The plugin contains an internal expansion with some useful placeholders.
- **Console support**: Most commands can be executed from the console.