package me.swirtzly.cubicnightmare.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityGraboid extends EntityMob {
	
	private static final DataParameter<Boolean> IS_DIVING = EntityDataManager.createKey(EntityGraboid.class, DataSerializers.BOOLEAN);
	
	public EntityGraboid(World worldIn) {
		super(worldIn);
		this.setSize(1F, 1F);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}
	
	public boolean isDiving() {
		return getDataManager().get(IS_DIVING);
	}
	
	public void setDiving(boolean diving) {
		getDataManager().set(IS_DIVING, diving);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getAttackTarget() != null && getAttackTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) getAttackTarget();
			if (player.isSprinting()) {
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.50D);
			} else {
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
			}
		}
		
		if (ticksExisted % 2300 == 0 && rand.nextBoolean() || hurtTime == 1) {
			setDiving(!isDiving());
		}
	}
	
	/**
	 * Returns the Y Offset of this entity.
	 */
	@Override
	public double getYOffset() {
		return 0.1D;
	}
	
	@Override
	public float getEyeHeight() {
		return 0.1F;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
	}
	
	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return super.getBlockPathWeight(pos);
	}
	
	/**
	 * Checks to make sure the light is not too bright where the mob is spawning
	 */
	@Override
	protected boolean isValidLightLevel() {
		return true;
	}
	
	@Override
	public void spawnRunningParticles() {
		if (isDiving()) {
			for (int k = 0; k < 20; ++k) {
				double d2 = this.rand.nextGaussian() * 0.02D;
				double d0 = this.rand.nextGaussian() * 0.02D;
				double d1 = this.rand.nextGaussian() * 0.02D;
				this.world.spawnParticle(EnumParticleTypes.SPIT, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
			}
		} else {
			super.spawnRunningParticles();
		}
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if(isDiving()) return;
		super.damageEntity(damageSrc, damageAmount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (isDiving()) return false;
		return super.attackEntityAsMob(entityIn);
	}
}
