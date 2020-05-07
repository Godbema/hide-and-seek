package tk.pshub.parangee.hideandseek.plugin

import com.github.noonmaru.kommand.argument.player
import com.github.noonmaru.kommand.argument.string
import org.bukkit.plugin.java.JavaPlugin
import com.github.noonmaru.kommand.kommand
import org.bukkit.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import tk.pshub.parangee.hideandseek.Store
import tk.pshub.parangee.hideandseek.controllers.PlayerRandomSelector
import tk.pshub.parangee.hideandseek.events.DeathEvent
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import kotlin.random.Random

class HideAndSeekPlugin : JavaPlugin() {
    val store: Store = Store()
    val plugin: HideAndSeekPlugin = this
    lateinit var locationConfig: FileConfiguration
    lateinit var locationConfigFile: File

    override fun onEnable() {
        createLocationConfig()
        registerCommand()
        startTimer()
        server.pluginManager.registerEvents(DeathEvent(this), this)
    }

    private fun registerCommand() {
        kommand {
            register("술래잡기") {
                then("start") {
                    executes {
                        play(PlayerRandomSelector().selectPlayer(store, plugin))
                    }
                    then("player" to player()) {
                        executes {
                            Bukkit.getPlayer(it.getArgument("player"))?.let { it1 -> play(it1) }
                        }
                    }
                }
                then("reset") {
                    executes {
                        store.finder = Bukkit.getPlayer("") as Player
                        store.start = false
                        store.time = 360
                        store.seekerFree = false
                        Bukkit.getOnlinePlayers().forEach {
                            it.teleport(store.startLocation)
                            it.gameMode = GameMode.ADVENTURE
                            it.inventory.clear()
                            it.removePotionEffect(PotionEffectType.SPEED)
                            it.health = it.maxHealth
                        }
                        it.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&ai&f] &a시스템 초기화가 완료되었습니다."))
                    }
                }
                then("location") {
                    then("seeker") {
                        executes {
                            store.seekerStartLocation = (it.sender as Player).location
                            locationConfig.set("location.seeker.x", (it.sender as Player).location.x)
                            locationConfig.set("location.seeker.y", (it.sender as Player).location.y)
                            locationConfig.set("location.seeker.z", (it.sender as Player).location.z)
                            locationConfig.set("location.seeker.world", (it.sender as Player).location.world.name)
                            locationConfig.save(locationConfigFile)
                            it.sender.sendMessage("술래 시작 위치가 설정되었습니다.")
                        }
                    }
                    then("start") {
                        executes {
                            store.startLocation = (it.sender as Player).location
                            locationConfig.set("location.start.x", (it.sender as Player).location.x)
                            locationConfig.set("location.start.y", (it.sender as Player).location.y)
                            locationConfig.set("location.start.z", (it.sender as Player).location.z)
                            locationConfig.set("location.start.world", (it.sender as Player).location.world.name)
                            locationConfig.save(locationConfigFile)
                            it.sender.sendMessage("시작 위치가 설정되었습니다.")
                        }
                    }
                }
                then("resourcepack") {
                    then("url" to string()) {
                        executes {
                            val pack = it.getArgument("url")
                            locationConfig.set("repack", pack)
                            locationConfig.save(locationConfigFile)
                            Bukkit.getOnlinePlayers().forEach{
                                it.setResourcePack(pack)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun play(p:Player) {
        Bukkit.getOnlinePlayers().forEach {
            it.gameMode = GameMode.ADVENTURE
            it.inventory.clear()
            it.health = it.maxHealth
            it.removePotionEffect(PotionEffectType.SPEED)
        }
        store.finder = p
        repeat(30) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                if (it != 29) {
                    var rand: Player = (Bukkit.getOnlinePlayers() as List<Player>)[Random.nextInt(Bukkit.getOnlinePlayers().size)]
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTitle("술래 정하기", "${ChatColor.getByChar(Integer.toHexString(Random.nextInt(16)))}${rand.displayName}", 0, 40, 0)
                        it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_BIT, 2f, 1f)
                    }
                } else {
                    Bukkit.getOnlinePlayers().forEach {
                        it.sendTitle("술래가 선택되었습니다.", "${ChatColor.RED}${store.finder.displayName}", 0, 60, 0)
                        it.playSound(it.location, Sound.BLOCK_ANVIL_PLACE, 1f, 1f)
                    }
                }
            }, it.toLong())
        }
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            Bukkit.getOnlinePlayers().forEach {
                if (it == store.finder) {
                    it.teleport(store.seekerStartLocation)
                } else {
                    it.teleport(store.startLocation)
                }
            }
            store.start = true
        }, 100)
    }

    private fun startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            store.survivalCount = 0
            Bukkit.getOnlinePlayers().forEach {
                if (it.gameMode == GameMode.ADVENTURE && it != store.finder) {
                    store.survivalCount++
                }
            }
            var str = StringBuilder()
            if (store.time > 300) {
                str.append(ChatColor.translateAlternateColorCodes('&', "&c&l술래가 풀려나기까지 &a${store.time - 300}초 | 생존자 수: ${store.survivalCount}"))
            } else {
                str.append("남은 시간: ${store.time}초 | 생존자 수: ${store.survivalCount}")
            }
            if (store.start) {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendActionBar(str.toString())
                }
            }
            if (store.survivalCount == 0 && store.start) {
                server.dispatchCommand(Bukkit.getConsoleSender(), "술래잡기 reset")
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&c술래잡기&f] &a게임 끝!"))
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c술래 승리!"), "")
                }
            }
            if (store.survivalCount != 0 && store.start && store.time == -1) {
                server.dispatchCommand(Bukkit.getConsoleSender(), "술래잡기 reset")
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&c술래잡기&f] &a게임 끝!"))
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a생존자 승리!"), "")
                }
            }
        }, 0L, 1)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (store.start) {
                store.time--
            }
        }, 0, 20)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (store.time == 300 && !store.seekerFree) {
                store.seekerFree = true
                store.finder.teleport(store.startLocation)
                Bukkit.getOnlinePlayers().forEach {
                    it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&c술래잡기 시작!"), "")
                }
                store.finder.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 300, 1), true)
                var item = ItemStack(Material.DIAMOND_SWORD)
                var meta = item.itemMeta
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
                meta.isUnbreakable = true
                item.itemMeta = meta
                store.finder.inventory.addItem(item)
            }
        }, 0, 1)
    }

    private fun createLocationConfig() {
        locationConfigFile = File(dataFolder, "config.yml")
        if (!locationConfigFile.exists()) {
            locationConfigFile.mkdirs()
            saveResource("config.yml", false)
        }
        locationConfig = YamlConfiguration()
        try {
            locationConfig.load(locationConfigFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        store.seekerStartLocation = Location(
                locationConfig.getString("location.seeker.world")?.let { Bukkit.getWorld(it) },
                locationConfig.getDouble("location.seeker.x"),
                locationConfig.getDouble("location.seeker.y"),
                locationConfig.getDouble("location.seeker.z")
        )
        store.startLocation = Location(
                locationConfig.getString("location.start.world")?.let { Bukkit.getWorld(it) },
                locationConfig.getDouble("location.start.x"),
                locationConfig.getDouble("location.start.y"),
                locationConfig.getDouble("location.start.z")
        )
    }
}