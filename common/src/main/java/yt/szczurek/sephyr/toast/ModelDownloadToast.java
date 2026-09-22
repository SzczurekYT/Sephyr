package yt.szczurek.sephyr.toast;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class ModelDownloadToast implements Toast {
    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/system");
    private static final Component TITLE = Component.literal("Sephyr: Downloading Speech Model");

    private volatile long downloaded;
    private volatile long total = -1;
    private volatile boolean finished;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;

    public void setProgress(long downloaded, long total) {
        this.downloaded = downloaded;
        if (total > 0) {
            this.total = total;
        }
        if (downloaded == total) {
            finished = true;
        }
    }

    @Override
    public Toast.@NonNull Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(@NonNull ToastManager manager, long fullyVisibleForMs) {
        if (finished) {
            visibility = Toast.Visibility.HIDE;
        }
    }

    @Override
    public int width() {
        return 200;
    }

    @Override
    public int height() {
        return 32;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, @NonNull Font font, long fullyVisibleForMs) {
        int height = this.height();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), height);
        graphics.text(font, TITLE, 18, 7, -256, false);
        graphics.text(font, progressText(), 18, 18, -1, false);

        int progressBarY = height - 4;
        int contentStart = 3;
        int contentEnd = this.width() - 3;
        graphics.fill(contentStart, progressBarY, contentEnd, progressBarY + 1, -1);
        int filled = contentStart + (int) (progressFraction() * (contentEnd - contentStart));
        graphics.fill(contentStart, progressBarY, filled, progressBarY + 1, -16755456);
    }

    private float progressFraction() {
        if (total <= 0) {
            return 0.0F;
        }
        return Mth.clamp((float) downloaded / total, 0.0F, 1.0F);
    }

    private String progressText() {
        long downloadedMb = downloaded / (1024 * 1024);
        if (total <= 0) {
            return downloadedMb + " MB";
        }
        long totalMb = Math.max(1, total / (1024 * 1024));
        int percent = (int) (100 * downloaded / total);
        return percent + "% (" + downloadedMb + " / " + totalMb + " MB)";
    }
}