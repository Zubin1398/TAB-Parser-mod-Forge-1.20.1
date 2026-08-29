package com.example.tabparser;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(TabParserMod.MODID)
public class TabParserMod {
    public static final String MODID = "tabparser";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String COMMAND = ".parse";
    private static final String NO_DONATE = "Нет доната";
    private static final String UNKNOWN_DONATE = "Не определён";
    private static final Pattern HEX_COLOR_CODE = Pattern.compile("(?i)§x(§[0-9a-f]){6}");
    private static final Pattern FORMAT_CODE = Pattern.compile("(?i)§[0-9a-fk-or]");
    private static final Pattern BRACKETED_RANK = Pattern.compile("[\\[\\(\\{<【](.*?)[\\]\\)\\}>】]");
    private static final Pattern INVALID_FILE_NAME_CHARS = Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}]+");

    public TabParserMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        if (!COMMAND.equals(event.getMessage().trim())) {
            return;
        }

        event.setCanceled(true);
        parseTab();
    }

    private static void parseTab() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            sendMessage(mc, "[TabParser] Игрок не загружен.");
            return;
        }

        ClientPacketListener connection = mc.getConnection();
        if (connection == null) {
            sendMessage(mc, "[TabParser] Нет подключения к серверу.");
            return;
        }

        Map<String, String> parsedPlayers = new LinkedHashMap<>();
        for (PlayerInfo playerInfo : connection.getOnlinePlayers()) {
            GameProfile profile = playerInfo.getProfile();
            if (profile == null || profile.getName() == null || profile.getName().isBlank()) {
                continue;
            }

            String username = profile.getName();
            parsedPlayers.putIfAbsent(username, determineDonate(playerInfo, username));
        }

        String serverFileName = getServerFileName(mc);
        Path outputDirectory = mc.gameDirectory.toPath().resolve("TabParser");
        Path outputPath = outputDirectory.resolve(serverFileName + ".txt");
        List<String> lines = new ArrayList<>(parsedPlayers.size());
        for (Map.Entry<String, String> entry : parsedPlayers.entrySet()) {
            lines.add(entry.getKey() + " | " + entry.getValue());
        }

        try {
            Files.createDirectories(outputDirectory);
            Files.write(outputPath, lines, StandardCharsets.UTF_8);
            sendMessage(mc, "[TabParser] Успешно обработано игроков: " + parsedPlayers.size());
            sendMessage(mc, "[TabParser] Файл сохранён: TabParser/" + serverFileName + ".txt");
        } catch (IOException exception) {
            LOGGER.error("Failed to write TAB parser output to {}", outputPath, exception);
            sendMessage(mc, "[TabParser] Ошибка записи файла: " + exception.getMessage());
        }
    }

    private static String getServerFileName(Minecraft mc) {
        ServerData serverData = mc.getCurrentServer();
        String serverName = "";
        if (serverData != null) {
            String serverMotd = serverData.motd == null ? "" : serverData.motd.getString();
            String serverBrand = mc.player == null ? "" : mc.player.getServerBrand();
            serverName = firstNotBlank(serverMotd, serverBrand, serverData.ip);
        } else if (mc.hasSingleplayerServer()) {
            serverName = "Singleplayer";
        }

        return sanitizeFileName(serverName.isBlank() ? "Unknown Server" : serverName);
    }

    private static String firstNotBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private static String sanitizeFileName(String value) {
        String cleaned = INVALID_FILE_NAME_CHARS.matcher(cleanFormatting(value)).replaceAll("_").trim();
        cleaned = cleaned.replaceAll("^[. ]+", "").replaceAll("[. ]+$", "");
        if (cleaned.isBlank()) {
            return "Unknown Server";
        }
        return cleaned.length() > 80 ? cleaned.substring(0, 80).trim() : cleaned;
    }

    private static String determineDonate(PlayerInfo playerInfo, String username) {
        PrefixResult prefixResult = getTabPrefix(playerInfo, username);
        if (prefixResult.status() == PrefixStatus.MISSING) {
            return NO_DONATE;
        }
        if (prefixResult.status() == PrefixStatus.UNKNOWN) {
            return UNKNOWN_DONATE;
        }

        String donate = extractDonateName(prefixResult.prefix());
        return donate.isBlank() ? UNKNOWN_DONATE : donate;
    }

    private static PrefixResult getTabPrefix(PlayerInfo playerInfo, String username) {
        PlayerTeam team = playerInfo.getTeam();
        if (team != null) {
            String teamPrefix = cleanFormatting(team.getPlayerPrefix().getString());
            if (!teamPrefix.isBlank()) {
                return new PrefixResult(PrefixStatus.FOUND, teamPrefix);
            }
        }

        Component displayName = playerInfo.getTabListDisplayName();
        if (displayName == null) {
            return new PrefixResult(PrefixStatus.MISSING, "");
        }

        String displayed = cleanFormatting(displayName.getString());
        if (displayed.isBlank() || displayed.equals(username)) {
            return new PrefixResult(PrefixStatus.MISSING, "");
        }

        int usernameStart = displayed.indexOf(username);
        if (usernameStart > 0) {
            String prefix = displayed.substring(0, usernameStart).trim();
            return prefix.isBlank()
                    ? new PrefixResult(PrefixStatus.MISSING, "")
                    : new PrefixResult(PrefixStatus.FOUND, prefix);
        }

        return new PrefixResult(PrefixStatus.UNKNOWN, displayed);
    }

    static String cleanFormatting(String value) {
        String withoutHex = HEX_COLOR_CODE.matcher(value).replaceAll("");
        return FORMAT_CODE.matcher(withoutHex).replaceAll("").trim();
    }

    static String extractDonateName(String prefix) {
        String cleaned = cleanFormatting(prefix).trim();
        Matcher matcher = BRACKETED_RANK.matcher(cleaned);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return cleaned
                .replaceAll("^[»>]+\\s*", "")
                .replaceAll("\\s*[«<]+$", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private static void sendMessage(Minecraft mc, String message) {
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(message), false);
        }
    }

    private enum PrefixStatus {
        FOUND,
        MISSING,
        UNKNOWN
    }

    private record PrefixResult(PrefixStatus status, String prefix) {
    }
}
