package tk.pshub.parangee.hideandseek

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.properties.Delegates

class Store {
    var time:Int = 360
    var finder:Player = Bukkit.getPlayer("") as Player
    var start:Boolean = false
    lateinit var startLocation:Location
    lateinit var seekerStartLocation: Location
    var seekerFree = false
    var survivalCount = 0
}