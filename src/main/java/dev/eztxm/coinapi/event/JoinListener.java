package dev.eztxm.coinapi.event;

import dev.eztxm.coinapi.CoinPlugin;
import dev.eztxm.sql.MariaDBConnection;
import dev.eztxm.sql.SQLiteConnection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JoinListener implements Listener {
    private final File coinsConfigFile = CoinPlugin.getInstance().getCoinsConfigFile();
    private final FileConfiguration coinsConfig = CoinPlugin.getInstance().getCoinsConfig();
    private final SQLiteConnection sqLiteConnection = CoinPlugin.getInstance().getSqLiteConnection();
    private final MariaDBConnection mariaDBConnection = CoinPlugin.getInstance().getMariaDBConnection();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!isUserExists(uuid)) {
            String uuidStr = uuid.toString();
            try {
                long startCoins = CoinPlugin.getInstance().getConfig().getLong("Start-Coins");
                if (coinsConfig != null) {
                    coinsConfig.set(uuidStr + ".Coins", startCoins);
                    coinsConfig.save(coinsConfigFile);
                    return;
                }
                if (sqLiteConnection != null) {
                    try {
                        sqLiteConnection.put("INSERT INTO `coinapi`(uuid,coins) VALUES (?,?)", uuidStr, startCoins);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
                if (mariaDBConnection != null) {
                    try {
                        mariaDBConnection.put("INSERT INTO `coinapi`(uuid,coins) VALUES (?,?)", uuidStr, startCoins);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
            } catch (NumberFormatException | IOException e) {
                System.out.println(e.getMessage());
                return;
            }
            CoinPlugin.getInstance().getLogger().warning("All options are null");
        }
    }

    private boolean isUserExists(UUID uuid) {
        String uuidStr = uuid.toString();
        if (coinsConfig != null) {
            return coinsConfig.getString(uuidStr) != null;
        }
        if (sqLiteConnection != null) {
            try (ResultSet resultSet = sqLiteConnection.query("SELECT `uuid` FROM `coinapi`WHERE `uuid`=?", uuidStr)) {
                if (resultSet.next()) {
                    return resultSet.getString("uuid") != null;
                }
                return false;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        if (mariaDBConnection != null) {
            try (ResultSet resultSet = mariaDBConnection.query("SELECT `uuid` FROM `coinapi`WHERE `uuid`=?", uuidStr)) {
                if (resultSet.next()) {
                    return resultSet.getString("uuid") != null;
                }
                return false;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }
}
