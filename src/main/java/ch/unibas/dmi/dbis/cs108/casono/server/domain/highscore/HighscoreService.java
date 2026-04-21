package ch.unibas.dmi.dbis.cs108.casono.server.domain.highscore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Stores and reads persistent highscore entries in timestamp + winner format. */
public final class HighscoreService {

    private static final HighscoreService INSTANCE = new HighscoreService();
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    private static final int MAX_RETURNED_ENTRIES = 100;
    private static final String LINE_SEPARATOR = "\t";

    private final Path storagePath;

    private HighscoreService() {
        this.storagePath =
                Path.of(System.getProperty("user.home"), ".casono", "highscores", "winners.log");
    }

    public static HighscoreService getInstance() {
        return INSTANCE;
    }

    public synchronized void appendWinner(String winnerName) {
        if (winnerName == null) {
            return;
        }

        String sanitized = sanitizeName(winnerName);
        if (sanitized.isEmpty()) {
            return;
        }

        String line = Instant.now() + LINE_SEPARATOR + sanitized + System.lineSeparator();

        try {
            Files.createDirectories(storagePath.getParent());
            Files.writeString(
                    storagePath,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // Highscore persistence must never break the game state response path.
        }
    }

    public synchronized List<String> readFormattedEntries() {
        if (!Files.exists(storagePath)) {
            return List.of();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(storagePath, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            return List.of();
        }

        List<String> formatted = new ArrayList<>();

        for (String raw : lines) {
            if (raw == null || raw.isBlank()) {
                continue;
            }

            String[] parts = raw.split(LINE_SEPARATOR, 2);
            if (parts.length < 2) {
                continue;
            }

            try {
                Instant ts = Instant.parse(parts[0].trim());
                String display = DISPLAY_FORMATTER.format(ts) + " | " + parts[1].trim();
                formatted.add(display);
            } catch (Exception ignored) {
                // Skip malformed lines and keep all valid entries.
            }
        }

        int from = Math.max(0, formatted.size() - MAX_RETURNED_ENTRIES);
        return new ArrayList<>(formatted.subList(from, formatted.size()));
    }

    public synchronized void clearAll() {
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException ignored) {
            // Clearing highscores must not break request handling.
        }
    }

    private static String sanitizeName(String winnerName) {
        return winnerName.trim().replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }
}
