package io.quill.paper.util.bukkit;

import io.quill.paper.util.bukkit.task.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Titles {
    private Titles() { }

    public static void show(Player player, Component title, Component subtitle, double fadeIn, double stay, double fadeOut) {
        checkNotNull(player, "player");

        Title built = createTitle(title, subtitle, fadeIn, stay, fadeOut);
        player.showTitle(built);
    }

    public static void showWithCallback(Player player, Component title, Component subtitle, double fadeIn, double stay, double fadeOut, double progressPercent, Runnable callback) {
        checkNotNull(player, "player");
        checkNotNull(callback, "callback");

        Title built = createTitle(title, subtitle, fadeIn, stay, fadeOut);
        player.showTitle(built);

        long delayTicks = calculateCallbackDelay(fadeIn, stay, fadeOut, progressPercent);

        Tasks.later(delayTicks, callback);
    }

    private static Title createTitle(Component title, Component subtitle, double fadeIn, double stay, double fadeOut) {
        Duration fadeInDuration = Duration.ofMillis((long) (fadeIn * 1000));
        Duration stayDuration = Duration.ofMillis((long) (stay * 1000));
        Duration fadeOutDuration = Duration.ofMillis((long) (fadeOut * 1000));

        Component safeTitle = title != null ? title : Component.empty();
        Component safeSubtitle = subtitle != null ? subtitle : Component.empty();

        return Title.title(safeTitle, safeSubtitle, Title.Times.times(fadeInDuration, stayDuration, fadeOutDuration));
    }

    private static long calculateCallbackDelay(double fadeIn, double stay, double fadeOut, double progressPercent) {
        checkArgument(progressPercent >= 0.0 && progressPercent <= 1.0, "progressPercent must be between 0.0 and 1.0");

        double totalSeconds = fadeIn + stay + fadeOut;
        double targetSeconds = totalSeconds * progressPercent;

        return Ticks.sec2Ticks(targetSeconds);
    }
}