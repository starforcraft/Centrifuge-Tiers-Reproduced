package com.ultramega.centrifugetiersreproduced.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.List;
import java.util.function.Predicate;

public class MultiBlockHelper {
    public static BoundingBox buildStructureBounds(BlockPos startPos, int width, int height, int depth, int hOffset, int vOffset, int dOffset, Direction direction) {
        int x = startPos.getX();
        int y = startPos.getY();
        int z = startPos.getZ();

        width -= 1;
        height -= 1;
        depth -= 1;

        int minX, maxX, minZ, maxZ;
        switch (direction) {
            case NORTH -> {
                minX = x + hOffset;
                maxX = x + width + hOffset;
                minZ = z - dOffset - depth;
                maxZ = z - dOffset;
            }
            case EAST -> {
                minX = x + dOffset;
                maxX = x + depth + dOffset;
                minZ = z + hOffset;
                maxZ = z + width + hOffset;
            }
            case SOUTH -> {
                minX = x - hOffset - width;
                maxX = x - hOffset;
                minZ = z + dOffset;
                maxZ = z + depth + dOffset;
            }
            default -> {
                minX = x - dOffset - depth;
                maxX = x - dOffset;
                minZ = z - hOffset - width;
                maxZ = z - hOffset;
            }
        }

        return new BoundingBox(
                Math.min(minX, maxX), y + vOffset, Math.min(minZ, maxZ),
                Math.max(minX, maxX), y + height + vOffset, Math.max(minZ, maxZ)
        );
    }


    public static void buildStructureList(BoundingBox box, List<BlockPos> list, Predicate<BlockPos> predicate, BlockPos validatorPosition) {
        list.clear();
        BlockPos.betweenClosedStream(box)
                .filter(blockPos -> !blockPos.equals(validatorPosition))
                .filter(predicate)
                .forEach(blockPos -> list.add(blockPos.immutable()));
    }

    public static boolean validateStructure(List<BlockPos> list, Predicate<BlockPos> predicate, int totalBlocks) {
        return list.size() == totalBlocks && list.stream().allMatch(predicate);
    }
}
