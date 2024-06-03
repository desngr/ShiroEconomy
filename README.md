
# ShiroEconomy [1.8-1.20+]

## What's ShiroEconomy?

ShiroEconomy is a modern economy plugin, which allows players to manage in-game currency

## Features
- **Transaction history:** view any player's transaction history up to 100 last records. There's also a different category of transactions where you can view *huge* transactions (settings are available in [config.yml](https://github.com/desngr/ShiroEconomy/blob/main/src/main/resources/config.yml))
- **Holograms**: setup a holographic display with player balance top list
- **Flexibility**: all messages used in the plugin are customizable, so you can easily change the look of your holographic display or add a new language
- **Performance**: with the use of async methods and connection pools, the performance of plugin increases

## Requirements

- **Server software:** ShiroEconomy requires Spigot, Bukkit or Paper to run. Some other server software may work, but these are not tested.
- **Java version:** Currently, plugin requires Java 8 or higher. It's recommended to use the latest Java version supported by your server software.
- **Minecraft version:** ShiroEconomy requires any version from 1.8 up to 1.20+
- **Libraries:** ShiroEconomy requires **Vault** to interact with other plugins. Optional libraries:
    - **PlaceholderAPI:** support for placeholders *%shiroeconomy_balance%* - for balance display
    - **ProtocolLib:** support for holographic displays
    - **LuckPerms:** support for managing groups in huge transactions (see [config.yml](https://github.com/desngr/ShiroEconomy/blob/main/src/main/resources/config.yml))

![bStats](https://bstats.org/signatures/bukkit/ShiroEconomy.svg)


