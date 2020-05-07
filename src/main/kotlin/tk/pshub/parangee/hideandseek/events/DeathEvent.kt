package tk.pshub.parangee.hideandseek.events

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import tk.pshub.parangee.hideandseek.plugin.HideAndSeekPlugin

class DeathEvent:Listener {
    private val pl:HideAndSeekPlugin
    constructor(pl:HideAndSeekPlugin) {
        this.pl = pl
    }
    @EventHandler
    fun onDeath(e:EntityDeathEvent) {
        if (e.entityType == EntityType.PLAYER && pl.store.start && pl.store.time < 300 && e.entity.killer == pl.store.finder) {
            (e.entity as Player).gameMode = GameMode.SPECTATOR
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&ai&f] &b${(e.entity as Player).displayName}&a님이 잡혔습니다."))
        }
        if (pl.store.start) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onDamage(e:EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.PLAYER && e.entityType == EntityType.PLAYER && e.damager as Player != pl.store.finder && pl.store.start) {
            e.isCancelled = true
        }
        if (!pl.store.start) {
            e.isCancelled = true
        }
    }
    @EventHandler
    fun onJump(e:EntityChangeBlockEvent) {
        e.isCancelled = true
    }
    @EventHandler
    fun onJoin(e:PlayerJoinEvent) {
        pl.locationConfig.getString("repack")?.let { e.player.setResourcePack(it) }
    }
}