package tk.pshub.parangee.hideandseek

import org.bukkit.Location
import org.bukkit.entity.Player

class Store {
    var time:Int = 300
    lateinit var finder:Player
    var start:Boolean = false
    lateinit var startLocation:Location
    lateinit var seekerStartLocation: Location
}