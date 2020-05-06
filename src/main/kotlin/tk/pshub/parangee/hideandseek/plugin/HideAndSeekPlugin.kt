package tk.pshub.parangee.hideandseek.plugin

import org.bukkit.plugin.java.JavaPlugin
import com.github.noonmaru.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import tk.pshub.parangee.hideandseek.Store
import tk.pshub.parangee.hideandseek.controllers.PlayerRandomSelector
import kotlin.random.Random

class HideAndSeekPlugin:JavaPlugin() {
    val store:Store = Store()
    val plugin:HideAndSeekPlugin = this

    override fun onEnable() {
        registerCommand()
        this.saveDefaultConfig()
    }
    private fun registerCommand() {
        kommand {
            register("술래잡기") {
                then("start") {
                    executes {
                        store.finder = PlayerRandomSelector().selectPlayer(store, plugin)
                        repeat(30) {
                            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                                if (it != 29) {
                                    var rand:Player = (Bukkit.getOnlinePlayers() as List<Player>)[Random.nextInt(Bukkit.getOnlinePlayers().size)]
                                    Bukkit.getOnlinePlayers().forEach{
                                        it.sendTitle("술래 정하기", "${ChatColor.getByChar(Integer.toHexString(Random.nextInt(16)))}${rand.displayName}", 0, 40, 0)
                                        it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 1f)
                                    }
                                } else {
                                    Bukkit.getOnlinePlayers().forEach{
                                        it.sendTitle("술래가 선택되었습니다.", "${ChatColor.RED}${store.finder.displayName}", 0, 60, 0)
                                        it.playSound(it.location, Sound.BLOCK_ANVIL_PLACE, 1f, 1f)
                                    }
                                }
                            }, it.toLong())
                        }
                        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                            Bukkit.getOnlinePlayers().forEach{
                                if (it == store.finder) {
                                    it.teleport(store.seekerStartLocation)
                                } else {
                                    it.teleport(store.startLocation)
                                }
                            }
                        }, 100)
                    }
                }
                then("stop") {
                }
                then("reset") {
                    executes {
                        store.finder = Bukkit.getPlayer("") as Player
                        store.start = false
                        store.time = 300
                        it.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&ai&f] &a시스템 초기화가 완료되었습니다."))
                    }
                }
                then("location") {
                    then("seeker") {
                        executes {
                            store.seekerStartLocation = (it.sender as Player).location
                            it.sender.sendMessage("술래 시작 위치가 설정되었습니다.")
                        }
                    }
                    then("start"){
                        executes {
                            store.startLocation = (it.sender as Player).location
                            it.sender.sendMessage("시작 위치가 설정되었습니다.")
                        }
                    }
                }
            }
        }
    }
}