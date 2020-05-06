package tk.pshub.parangee.hideandseek.controllers

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import tk.pshub.parangee.hideandseek.Store
import tk.pshub.parangee.hideandseek.plugin.HideAndSeekPlugin
import java.util.*

class PlayerRandomSelector {
    fun selectPlayer(store:Store, pl:HideAndSeekPlugin): Player {
        return (Bukkit.getOnlinePlayers() as List<Player>)[Random().nextInt(Bukkit.getOnlinePlayers().size)]
    }
}