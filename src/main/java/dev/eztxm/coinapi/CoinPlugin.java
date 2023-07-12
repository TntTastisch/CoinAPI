package dev.eztxm.coinapi;

import dev.eztxm.coinapi.event.JoinListener;
import dev.eztxm.coinapi.sql.MariaDBConnection;
import dev.eztxm.coinapi.sql.SQLiteConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class CoinPlugin extends JavaPlugin {
    private static CoinPlugin instance;
    private SQLiteConnection sqLiteConnection;
    private MariaDBConnection mariaDBConnection;
    private FileConfiguration coinsConfig;
    private File coinsConfigFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupStorageType();
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void setupStorageType() {
        FileConfiguration cfg = getConfig();
        switch (cfg.getString("Storage-Type").toLowerCase()) {
            case "sqlite":
                getLogger().info("SQLite: Loading...");
                if (cfg.getString("SQLite.Path") == null || cfg.getString("SQLite.Path").equals("")) {
                    sqLiteConnection = new SQLiteConnection(
                            cfg.getString("SQLite.File").endsWith(".db") ? cfg.getString("SQLite.File").substring(0, cfg.getString("SQLite.File").length() - 3) :
                                    cfg.getString("SQLite.File")
                    );
                    getLogger().info("SQLite: Loaded.");
                    sqLiteConnection.update("CREATE TABLE IF NOT EXISTS `coinapi`(uuid VARCHAR(255), coins BIGINT(255))");
                    getLogger().info("SQLite: Creating 'coinapi' table");
                    return;
                }
                sqLiteConnection = new SQLiteConnection(
                        getDataFolder()
                                + "/" +
                                (cfg.getString("SQLite.Path").startsWith("/") ? cfg.getString("SQLite.Path").substring(1) :
                                        cfg.getString("SQLite.Path").startsWith("\\") ? cfg.getString("SQLite.Path").substring(2) :
                                                cfg.getString("SQLite.Path").endsWith("/") ? cfg.getString("SQLite.Path").substring(0, cfg.getString("SQLite.Path").length() - 1) :
                                                        cfg.getString("SQLite.Path").endsWith("\\") ? cfg.getString("SQLite.Path").substring(0, cfg.getString("SQLite.Path").length() - 2) :
                                                                cfg.getString("SQLite.Path")),
                        cfg.getString("SQLite.File").endsWith(".db") ? cfg.getString("SQLite.File").substring(0, cfg.getString("SQLite.File").length() - 3) :
                                cfg.getString("SQLite.File")
                );
                getLogger().info("SQLite: Loaded.");
                getLogger().info("SQLite: Creating 'coinapi' table");
                sqLiteConnection.update("CREATE TABLE IF NOT EXISTS `coinapi`(uuid VARCHAR(255), coins BIGINT(255))");
                getLogger().info("SQLite: 'coinapi' table created");
                break;
            case "mariadb":
                getLogger().info("MariaDB: Loading...");
                mariaDBConnection = new MariaDBConnection(
                        cfg.getString("MariaDB.Host"),
                        cfg.getInt("MariaDB.Port"),
                        cfg.getString("MariaDB.Database"),
                        cfg.getString("MariaDB.Username"),
                        cfg.getString("MariaDB.Password")
                );
                getLogger().info("MariaDB: Loaded.");
                getLogger().info("MariaDB: Creating 'coinapi' table");
                mariaDBConnection.update("CREATE TABLE IF NOT EXISTS `coinapi`(uuid VARCHAR(255), coins BIGINT(255))");
                getLogger().info("MariaDB: 'coinapi' table created");
                break;
            case "config-file":
                getLogger().info("Config-File: Loading...");
                coinsConfigFile = new File(
                        getDataFolder()
                                + "/" +
                                (cfg.getString("Config-File.Path").startsWith("/") ? cfg.getString("Config-File.Path").substring(1) :
                                        cfg.getString("Config-File.Path").startsWith("\\") ? cfg.getString("Config-File.Path").substring(2) :
                                                cfg.getString("Config-File.Path").endsWith("/") ? cfg.getString("Config-File.Path").substring(0, cfg.getString("Config-File.Path").length() - 1) :
                                                        cfg.getString("Config-File.Path").endsWith("\\") ? cfg.getString("Config-File.Path").substring(0, cfg.getString("Config-File.Path").length() - 2) :
                                                                cfg.getString("Config-File.Path")
                                ) + "/" + (
                                cfg.getString("Config-File.File").endsWith(".yml") ? cfg.getString("Config-File.File") : cfg.getString("Config-File.File") + ".yml"
                        )
                );
                coinsConfig = YamlConfiguration.loadConfiguration(coinsConfigFile);
                if (!coinsConfigFile.exists()) {
                    try {
                        getLogger().info("Config-File: Creating file.");
                        coinsConfigFile.createNewFile();
                        getLogger().info("Config-File: File created.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                getLogger().info("Config-File: Loaded.");
                break;
            default:
                getLogger().warning("Storage-Type not correct, please check it!");
                Bukkit.getPluginManager().disablePlugin(this);
                break;
        }
    }

    public File getCoinsConfigFile() {
        if (getConfig().getString("Storage-Type").equalsIgnoreCase("config-file")) {
            return coinsConfigFile;
        }
        return null;
    }

    public FileConfiguration getCoinsConfig() {
        if (getConfig().getString("Storage-Type").equalsIgnoreCase("config-file")) {
            return coinsConfig;
        }
        return null;
    }

    public MariaDBConnection getMariaDBConnection() {
        if (getConfig().getString("Storage-Type").equalsIgnoreCase("mariadb")) {
            return mariaDBConnection;
        }
        return null;
    }

    public SQLiteConnection getSqLiteConnection() {
        if (getConfig().getString("Storage-Type").equalsIgnoreCase("sqlite")) {
            return sqLiteConnection;
        }
        return null;
    }

    public static CoinPlugin getInstance() {
        return instance;
    }
}
