package net.pl3x.bukkit.claims.event;

import org.bukkit.entity.Player;

public class AccrueClaimBlocksEvent extends CancellableEvent {
    private int accrualRate;
    private final boolean isIdle;

    public AccrueClaimBlocksEvent(Player player, int accrualRate, boolean isIdle) {
        super(player);
        this.accrualRate = accrualRate;
        this.isIdle = isIdle;
    }

    public int getAccrualRate() {
        return accrualRate;
    }

    public void setAccrualRate(int accrualRate) {
        this.accrualRate = accrualRate;
    }

    public int getActualAccruedBlocks() {
        return accrualRate / 6;
    }

    public boolean isIdle() {
        return isIdle;
    }
}
