# TAB Parser

Client-side Minecraft Forge 1.20.1 mod that parses the current TAB player list on manual `.parse` command and saves usernames with detected TAB prefix ranks to a text file.

## How it works

When you type:

```text
.parse
```

the mod cancels sending that message to the server and reads the player list from:

```java
Minecraft.getInstance().getConnection().getOnlinePlayers()
```

For every `PlayerInfo`, TAB Parser:

- gets the real Minecraft username from `playerInfo.getProfile().getName()`;
- reads the TAB prefix from the player's scoreboard team prefix when available;
- falls back to the TAB display name if the team prefix is unavailable;
- removes Minecraft color and formatting codes, including `§` formatting;
- extracts a donate/rank name from the cleaned prefix;
- writes every username only once, preserving TAB order where possible.

The output file is created or fully overwritten at:

```text
.minecraft/tab_nicks.txt
```

Output format:

```text
Player123 | VIP
Player456 | PREMIUM
Player789 | DELUXE
Admin123 | ADMIN
Player999 | Нет доната
```

## Rank detection

Ranks are detected from the TAB prefix, not from username or UUID.

Examples:

```text
§6[VIP] §fPlayer123     -> Player123 | VIP
§b[PREMIUM] §fPlayer456 -> Player456 | PREMIUM
§c[ADMIN] §fAdmin123    -> Admin123 | ADMIN
Player999               -> Player999 | Нет доната
```

If no prefix is visible, the player is saved as:

```text
Player999 | Нет доната
```

If the prefix data exists but cannot be confidently separated from the displayed TAB text, the player is saved as:

```text
Player999 | Не определён
```

## Important limitation

A client-side mod can only parse data that the server sends to the Minecraft client. If a server plugin completely hides a player, prefix, or rank from TAB/team/display-name packets, TAB Parser cannot recover that hidden data.

TAB Parser does not use OCR, screenshots, external recognition tools, web requests, databases, Discord webhooks, or any server-side component.

## Manual only

TAB Parser never updates automatically.

It does not:

- scan TAB on a timer;
- update the file when players join or leave;
- parse automatically after joining a server;
- add a GUI, config file, or keybind.

The only way to update `tab_nicks.txt` is to type:

```text
.parse
```

## Install

1. Install Minecraft Forge 1.20.1.
2. Put the built jar into `.minecraft/mods`.
3. Start Minecraft.
4. Join the target server.
5. Wait until the TAB player list is loaded.
6. Type `.parse` in chat.
7. Open `.minecraft/tab_nicks.txt`.

The mod works without installing anything on the server.

## Build

Use Java 17 or newer:

```powershell
.\gradlew.bat build
```

The built jar will be in:

```text
build/libs
```
