package tk.pshub.parangee.hideandseek.events

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import tk.pshub.parangee.hideandseek.items.ShootStick
import tk.pshub.parangee.hideandseek.plugin.HideAndSeekPlugin

class InteractEvent:Listener {
    private val pl: HideAndSeekPlugin
    constructor(pl: HideAndSeekPlugin) {
        this.pl = pl
    }
    @EventHandler
    fun handlePlayerUseShootStick(e:PlayerInteractEvent) {
        var item = e.item
        if (item != null) {
            if (item.itemMeta == ShootStick().itemMeta) {
                pl.store.finder.velocity = e.player.location.direction
                var i = item.clone()
                i.amount = 1
                e.player.inventory.removeItem(i)
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&ci&f] &b${e.player.displayName}&a님이 술래 날리기 막대기를 사용했습니다."))
            }
        }
    }
}