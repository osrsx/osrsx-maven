package io.osrsx.api

/**
 * Shared banking capability, published once to the [ServiceRegistry] so every plugin banks the same way
 * — walk to the nearest reachable bank, deposit, withdraw a loadout — instead of each re-implementing it.
 * Consume it with `services.get<BankingService>()`; a plugin can publish its own to override the default
 * (the user-swappable seam the registry exists for).
 *
 * Items are specified as [ItemRef] / [ItemSpec], not bare names, so callers inherit the same
 * name/id/alternates/category resolution the ref-aware [Container] surfaces use — the low-level [Bank]
 * name/id ops underneath are an implementation detail this layer resolves down to.
 *
 * All methods run on the caller's plugin loop and are loop-driven: call [openNearest] each loop until it
 * returns true, then deposit/withdraw.
 */
interface BankingService {
    /** Walk to and open the nearest reachable bank. Loop-driven — call each loop until it returns true. */
    fun openNearest(): Boolean

    /** Whether a bank is currently open. */
    fun isOpen(): Boolean

    /** Deposit the entire inventory. Assumes a bank is open. */
    fun depositAll(): Boolean

    /** Deposit everything in the inventory except items matching [keep]. Assumes a bank is open. */
    fun depositAllExcept(vararg keep: ItemRef): Boolean

    /** Deposit all of each held item matching [items], keeping everything else (e.g. a gatherer's tool). */
    fun deposit(vararg items: ItemRef): Boolean

    /** Withdraw a loadout — each [ItemSpec] is a ref plus quantity (`qty <= 0` = all). Assumes a bank is open. */
    fun withdraw(vararg loadout: ItemSpec): Boolean

    /** Close the bank. */
    fun close(): Boolean
}
