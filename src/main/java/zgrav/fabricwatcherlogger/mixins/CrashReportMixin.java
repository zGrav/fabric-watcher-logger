package zgrav.fabricwatcherlogger.mixins;

import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

import static zgrav.fabricwatcherlogger.Utils.sendToDiscord;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "writeToFile", at = @At("RETURN"))
    // this mixin is purely optional and can be removed if you don't want it.
    private void interceptCrashReport(File file, CallbackInfoReturnable<Boolean> cir) {
        sendToDiscord("A crash has happened! - ```THIS IS OPTIONAL" + file.getName() + "```");
    }
}
