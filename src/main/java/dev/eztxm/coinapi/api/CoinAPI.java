package dev.eztxm.coinapi.api;

import dev.eztxm.coinapi.CoinPlugin;
import dev.eztxm.coinapi.sql.MariaDBConnection;
import dev.eztxm.coinapi.sql.SQLiteConnection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CoinAPI {
    private final File coinsConfigFile = CoinPlugin.getInstance().getCoinsConfigFile();
    private final FileConfiguration coinsConfig = CoinPlugin.getInstance().getCoinsConfig();
    private final SQLiteConnection sqLiteConnection = CoinPlugin.getInstance().getSqLiteConnection();
    private final MariaDBConnection mariaDBConnection = CoinPlugin.getInstance().getMariaDBConnection();

    public long getCoinsByUUID(UUID uuid) {
        String uuidStr = uuid.toString();
        if (coinsConfig != null) {
            return coinsConfig.getLong(uuidStr + ".Coins");
        }
        if (sqLiteConnection != null) {
            try (ResultSet resultSet = sqLiteConnection.query("SELECT `coins` FROM `coinapi` WHERE `uuid`=?", uuidStr)) {
                if (resultSet.next()) {
                    return resultSet.getLong("coins");
                }
                return 0;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return 0;
            }
        }
        if (mariaDBConnection != null) {
            try (ResultSet resultSet = mariaDBConnection.query("SELECT `coins` FROM `coinapi` WHERE `uuid`=?", uuidStr)) {
                if (resultSet.next()) {
                    return resultSet.getLong("coins");
                }
                return 0;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return 0;
            }
        }
        CoinPlugin.getInstance().getLogger().warning("All options are null");
        return 0;
    }

    public void setCoinsByUUID(UUID uuid, long value) {
        String uuidStr = uuid.toString();
        if (coinsConfig != null) {
            try {
                coinsConfig.set(uuidStr + ".Coins", value);
                coinsConfig.save(coinsConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (sqLiteConnection != null) {
            sqLiteConnection.update("UPDATE `coinapi` SET `coins`=? WHERE `uuid`=?", value, uuidStr);
        }
        if (mariaDBConnection != null) {
            mariaDBConnection.update("UPDATE `coinapi` SET `coins`=? WHERE `uuid`=?", value, uuidStr);
        }
        CoinPlugin.getInstance().getLogger().warning("All options are null");
    }

    public void addCoinsByUUID(UUID uuid, long value) {
        long currentCoins = getCoinsByUUID(uuid);
        long newCoins = currentCoins + value;
        setCoinsByUUID(uuid, newCoins);
    }

    public void removeCoinsByUUID(UUID uuid, long value) {
        long currentCoins = getCoinsByUUID(uuid);
        long newCoins;
        if (currentCoins >= value) {
            newCoins = currentCoins - value;
        } else {
            newCoins = 0;
        }
        setCoinsByUUID(uuid, newCoins);
    }
}
