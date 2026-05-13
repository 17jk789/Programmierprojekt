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
    private static final int MIN_MONEY_COLUMN_COUNT = 3;
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
        appendWinner(winnerName, 0);
    }

    /** Append a winner with an optional money value (money may be 0). */
    public synchronized void appendWinner(String winnerName, int money) {
        if (winnerName == null) {
            return;
        }

        String sanitized = sanitizeName(winnerName);
        if (sanitized.isEmpty()) {
            return;
        }

        String line;
        if (money > 0) {
            line =
                    Instant.now()
                            + LINE_SEPARATOR
                            + sanitized
                            + LINE_SEPARATOR
                            + money
                            + System.lineSeparator();
        } else {
            // Keep legacy two-column format for backward compatibility
            line = Instant.now() + LINE_SEPARATOR + sanitized + System.lineSeparator();
        }

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

    /** Append a preformatted highscore entry such as "Lars (1000), Jona (1000)". */
    public synchronized void appendFormattedEntry(String entryText) {
        if (entryText == null) {
            return;
        }

        String sanitized = sanitizeName(entryText);
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
            // Support both legacy format (TIMESTAMP \t NAME) and new format (TIMESTAMP \t
            // NAME \t MONEY)
            String[] parts = raw.split(LINE_SEPARATOR);
            if (parts.length < 2) {
                continue;
            }

            try {
                Instant ts = Instant.parse(parts[0].trim());
                String name = parts[1].trim();
                String display = DISPLAY_FORMATTER.format(ts) + " | " + name;
                if (parts.length >= MIN_MONEY_COLUMN_COUNT) {
                    try {
                        int money = Integer.parseInt(parts[2].trim());
                        display = display + " | $" + money;
                    } catch (NumberFormatException nfe) {
                        // ignore malformed money
                    }
                }
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
