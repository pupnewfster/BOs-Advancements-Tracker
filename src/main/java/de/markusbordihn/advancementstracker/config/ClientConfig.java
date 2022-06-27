/**
 * Copyright 2021 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.advancementstracker.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

import de.markusbordihn.advancementstracker.Constants;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ClientConfig() {}

  static final ForgeConfigSpec clientSpec;
  public static final Config CLIENT;
  static {
    com.electronwill.nightconfig.core.Config.setInsertionOrderPreserved(true);
    final Pair<Config, ForgeConfigSpec> specPair =
        new ForgeConfigSpec.Builder().configure(Config::new);
    clientSpec = specPair.getRight();
    CLIENT = specPair.getLeft();
    log.info("{} Client config ...", Constants.LOG_REGISTER_PREFIX);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
  }

  public static class Config {

    public final ForgeConfigSpec.BooleanValue overviewEnabled;

    public final ForgeConfigSpec.BooleanValue widgetEnabled;
    public final ForgeConfigSpec.BooleanValue widgetVisible;
    public final ForgeConfigSpec.IntValue widgetHeight;
    public final ForgeConfigSpec.IntValue widgetWidth;
    public final ForgeConfigSpec.IntValue widgetTop;
    public final ForgeConfigSpec.IntValue widgetLeft;

    public final ForgeConfigSpec.ConfigValue<String> logLevel;

    public final ForgeConfigSpec.ConfigValue<List<String>> trackedAdvancements;
    public final ForgeConfigSpec.ConfigValue<List<String>> trackedAdvancementsRemote;
    public final ForgeConfigSpec.ConfigValue<List<String>> trackedAdvancementsLocal;

    Config(ForgeConfigSpec.Builder builder) {
      builder.comment("Advancements Tracker (Client configuration)");

      builder.push("general");
      trackedAdvancements =
          builder.comment("List of default tracked advancements, mostly used by mod packs.")
              .define("trackedAdvancements", new ArrayList<String>(Arrays.asList("")));
      builder.pop();

      builder.push("gui");

      builder.push("overview");
      overviewEnabled = builder.comment("Enable/Disable the advancements overview screen.")
          .define("overviewEnabled", true);
      builder.pop();

      builder.push("widget");
      widgetEnabled = builder.comment("Enable/Disable the advancements tracker widget.")
          .define("widgetEnabled", true);
      widgetVisible = builder.comment(
          "Shows the widget automatically. If this is set to false the widget will be only visible after pressing the defined hot-keys.")
          .define("widgetVisible", true);
      widgetHeight = builder.comment(
          "Defines the max. height of the widget. Default is 0 which mean use the max. available height.")
          .defineInRange("widgetHeight", 0, 0, 600);
      widgetWidth = builder.comment("Defines the max.width of the widget.")
          .defineInRange("widgetWidth", 80, 40, 400);
      widgetTop = builder.defineInRange("widgetTop", 0, 0, 400);
      widgetLeft = builder.defineInRange("widgetLeft", 0, 0, 400);
      builder.pop();

      builder.pop();

      builder.push("Debug");
      logLevel = builder.comment("Changed the default log level to get more output.")
          .define("logLevel", "info");
      builder.pop();

      builder.push("cache");
      trackedAdvancementsRemote =
          builder.define("trackedAdvancementsRemote", new ArrayList<String>(Arrays.asList("")));
      trackedAdvancementsLocal =
          builder.define("trackedAdvancementsLocal", new ArrayList<String>(Arrays.asList("")));
      builder.pop();
    }
  }

}
