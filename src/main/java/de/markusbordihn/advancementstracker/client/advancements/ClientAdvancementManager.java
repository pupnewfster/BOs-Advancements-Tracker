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

package de.markusbordihn.advancementstracker.client.advancements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.advancementstracker.Constants;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientAdvancementManager implements ClientAdvancements.Listener {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static short ticks = 0;
  private static final short ADD_LISTENER_TICK = 2;
  private static boolean hasListener = false;
  private static ClientAdvancementManager clientAdvancementManager;

  protected ClientAdvancementManager() {}

  @SubscribeEvent
  public static void handleWorldEventLoad(WorldEvent.Load event) {
    if (!event.getWorld().isClientSide()) {
      return;
    }
    reset();
  }

  @SubscribeEvent
  public static void handleClientTickEvent(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      ticks++;
      return;
    }

    if (ticks == ADD_LISTENER_TICK && !hasListener) {
      addListener();
      ticks = 0;
    }
  }

  public static void reset() {
    log.debug("Resetting Client Advancement Manager ...");
    clientAdvancementManager = new ClientAdvancementManager();
    hasListener = false;
    ticks = 0;
  }

  public static void addListener() {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft == null || minecraft.player == null || minecraft.player.connection == null
        || minecraft.player.connection.getAdvancements() == null || minecraft.player.connection
            .getAdvancements().getAdvancements().getAllAdvancements().isEmpty()) {
      return;
    }
    log.debug("Adding client advancement manager listener...");
    minecraft.player.connection.getAdvancements().setListener(clientAdvancementManager);
    hasListener = true;
  }

  public static boolean isValidAdvancement(Advancement advancement) {
    String advancementId = advancement.getId().toString();
    return advancement.getDisplay() != null && !advancementId.startsWith("minecraft:recipes/")
        && !advancementId.startsWith("smallships:recipes");
  }

  @Override
  public void onUpdateAdvancementProgress(Advancement advancement,
      AdvancementProgress advancementProgress) {
    if (isValidAdvancement(advancement)) {
      log.debug("[Update Advancement Progress] {} with {}", advancement, advancementProgress);
      AdvancementsManager.updateAdvancementProgress(advancement, advancementProgress);
    }
  }

  @Override
  public void onAddAdvancementRoot(Advancement advancement) {
    if (isValidAdvancement(advancement) && advancement.getParent() == null) {
      log.debug("[Add Advancement Root] {}", advancement);
      AdvancementsManager.addAdvancementRoot(advancement);
    }
  }

  @Override
  public void onRemoveAdvancementRoot(Advancement advancement) {
    log.debug("[Remove Advancement Root] {}", advancement);
  }

  @Override
  public void onAddAdvancementTask(Advancement advancement) {
    if (isValidAdvancement(advancement) && advancement.getParent() != null) {
      log.debug("[Add Advancement Task] {}", advancement);
      AdvancementsManager.addAdvancementTask(advancement);
    }
  }

  @Override
  public void onRemoveAdvancementTask(Advancement advancement) {
    log.debug("[Remove Advancement Task] {}", advancement);
  }

  @Override
  public void onAdvancementsCleared() {
    log.debug("[Advancements Cleared] ...");
  }

  @Override
  public void onSelectedTabChanged(Advancement advancement) {
    // Not used.
    log.debug("[Selected Tab Changed] {}", advancement);
  }

}