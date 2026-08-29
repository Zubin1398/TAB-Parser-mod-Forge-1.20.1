# TAB Parser

Client-side Minecraft Forge 1.20.1 mod for exporting the current TAB player list to a local text file.

The mod is started manually with:

```text
.parse
```

No server-side installation is required.

## Features

- Reads online players from the Minecraft client connection.
- Uses the real username from each player's `GameProfile`.
- Detects rank/donate information from TAB prefixes.
- Removes Minecraft color and formatting codes.
- Deduplicates usernames while preserving the TAB order where possible.
- Creates one output file per server.
- Runs only when `.parse` is entered.

## Output

Files are written to:

```text
.minecraft/TabParser/
```

The file name is based on server-provided data:

1. server MOTD;
2. server brand;
3. server IP as a fallback.

Local names from the Minecraft multiplayer server list are not used.

If the same server is parsed again, its file is overwritten. Parsing another server creates a separate `.txt` file.

Output format:

```text
Username | Rank
```

Example:

```text
Player123 | VIP
Player456 | PREMIUM
Admin123 | ADMIN
Player999 | Нет доната
```

## Limitations

TAB Parser can only process data that the server sends to the client. Hidden players, hidden prefixes, or ranks not present in TAB/team/display-name data cannot be recovered.

The mod does not use OCR, screenshots, external services, webhooks, databases, automatic scans, GUI, config files, or keybinds.

## Installation

1. Install Forge for Minecraft 1.20.1.
2. Copy the built jar to `.minecraft/mods`.
3. Start Minecraft and join a server.
4. Wait until the TAB list is loaded.
5. Type `.parse` in chat.
6. Open `.minecraft/TabParser/<server>.txt`.

## Build

Requirements:

- Java 17 or newer
- Gradle wrapper included in the project

Build command:

```powershell
.\gradlew.bat build
```

The jar is generated in:

```text
build/libs
```
