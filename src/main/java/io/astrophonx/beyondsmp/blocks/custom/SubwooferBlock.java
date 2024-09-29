package io.astrophonx.beyondsmp.blocks.custom;

import java.util.List;
import java.util.Random;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
public class SubwooferBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private boolean particleCooldown;
    private boolean coolDown;

    public SubwooferBlock(Properties settings) {
        super(settings);
        this.particleCooldown = true;
        this.coolDown = false;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    // This is where the block gets its state when placed by the player
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Get the direction the player is facing and apply it to the block's state
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        if (!world.isClientSide && world.hasNeighborSignal(pos) && !this.coolDown) {
            this.particleCooldown = false;
            this.coolDown = true;
            Direction direction = state.getValue(FACING);
            int power = world.getBestNeighborSignal(pos);

            double force = Math.pow(power, 1.2) / 10.0;
            Vec3 pushVector = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).scale(force);

            int yVelocity = 0;
            if (direction.getStepY() > 0) {
                yVelocity = 5;
            }

            AABB effectBox = getEffectBox(pos, direction);
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, effectBox, entity -> true);

            for (Entity entity : entities) {
                entity.setDeltaMovement(pushVector);
                entity.hurtMarked = true;  // This flag allows entities to move properly when hit
            }


            world.playSound(null, pos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.BLOCKS, 1.0F, 1.0F);
            summonShockwaveParticles(world, pos, direction);

            this.coolDown = false;
        }
    }

    private AABB getEffectBox(BlockPos pos, Direction direction) {
        switch (direction) {
            case NORTH:
                return new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 10, pos.getX() + 2, pos.getY() + 2, pos.getZ());
            case SOUTH:
                return new AABB(pos.getX() - 1, pos.getY(), pos.getZ() + 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 11);
            case WEST:
                return new AABB(pos.getX() - 10, pos.getY(), pos.getZ() - 1, pos.getX(), pos.getY() + 2, pos.getZ() + 2);
            case EAST:
                return new AABB(pos.getX() + 1, pos.getY(), pos.getZ() - 1, pos.getX() + 11, pos.getY() + 2, pos.getZ() + 2);
            case UP:
                return new AABB(pos.getX() - 1, pos.getY() + 1, pos.getZ() - 1, pos.getX() + 2, pos.getY() + 11, pos.getZ() + 2);
            case DOWN:
                return new AABB(pos.getX() - 1, pos.getY() - 10, pos.getZ() - 1, pos.getX() + 2, pos.getY(), pos.getZ() + 2);
            default:
                return new AABB(pos);
        }
    }

    private void summonShockwaveParticles(Level world, BlockPos pos, Direction direction) {
        Vec3 startPos = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        int particleCount = 10;
        double distance = 10.0D;
        double increment = distance / particleCount;

        for (int i = 1; i <= particleCount; i++) {
            Vec3 particlePos = startPos.add((direction.getStepX() * i) * increment, (direction.getStepY() * i) * increment, (direction.getStepZ() * i) * increment);
            world.addParticle(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 0.0D, 0.0D, 0.0D);
        }
    }

    // Override the animateTick method to add custom particles
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        if (!this.particleCooldown) {
            this.particleCooldown = true;
            Direction direction = state.getValue(FACING);
            summonShockwaveParticles(world, pos, direction);
        }
    }
}