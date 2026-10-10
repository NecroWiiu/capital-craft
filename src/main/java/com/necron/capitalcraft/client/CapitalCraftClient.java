package com.necron.capitalcraft.client;

import com.necron.capitalcraft.CapitalCraft;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class CapitalCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.isOf(CapitalCraft.BROKER_TABLET)) {
                if (world.isClient) {
                    MinecraftClient.getInstance()
                        .setScreen(new CapitalScreen("market"));
                }
                return TypedActionResult.success(stack, world.isClient);
            }

            if (stack.isOf(CapitalCraft.BET_SLIP)) {
                if (world.isClient) {
                    MinecraftClient.getInstance()
                        .setScreen(new CapitalScreen("casino"));
                }
                return TypedActionResult.success(stack, world.isClient);
            }

            return TypedActionResult.pass(stack);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.getBlockState(hit.getBlockPos())
                    .isOf(CapitalCraft.GPU_RIG)) {
                if (world.isClient) {
                    MinecraftClient.getInstance()
                        .setScreen(new CapitalScreen("crypto"));
                }
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    public static void send(String action, int amount) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(action);
            buf.writeInt(amount);

            ClientPlayNetworking.send(CapitalCraft.ACTION, buf);
        }
    }
}
