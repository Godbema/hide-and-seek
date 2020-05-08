package tk.pshub.parangee.hideandseek.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ShootStick:ItemStack(Material.STICK) {
    init {
        init(1)
    }
    private fun init(amount:Int) {
        var meta = this.itemMeta
        meta.setDisplayName("술래 날리기")
        var lore = ArrayList<String>()
        lore.add("우클릭하면 보고잇는 방향으로 술래를 날립니다.")
        meta.lore = lore
        this.itemMeta = meta
    }
}