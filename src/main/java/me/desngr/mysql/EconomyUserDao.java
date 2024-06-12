package me.desngr.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.MysqlDatabaseType;
import com.j256.ormlite.jdbc.db.SqliteDatabaseType;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import me.desngr.ShiroEconomy;
import me.desngr.api.BalanceUpdateEvent;
import me.desngr.api.PlayerTransactionEvent;
import me.desngr.util.Logger;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyUserDao {

    private final DataSourceConnectionSource source;

    public EconomyUserDao(MysqlDataSource.Type sourceType) throws SQLException {
        this.source = new DataSourceConnectionSource(MysqlDataSource.getDataSource(),
                sourceType == MysqlDataSource.Type.MYSQL ?
                        new MysqlDatabaseType() :
                        new SqliteDatabaseType());

        TableUtils.createTableIfNotExists(source, EconomyUser.class);
        TableUtils.createTableIfNotExists(source, Transaction.class);
    }

    public void withdrawUser(UUID uuid, double amount) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Dao<EconomyUser, Long> userDao
                        = DaoManager.createDao(source, EconomyUser.class);
                List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

                if (users.isEmpty()) {
                    Logger.debug("No user found (" + uuid + ")");

                    return -1d;
                }

                EconomyUser user = users.get(0);

                if (user.getBalance() <= amount)
                    user.setBalance(0d);
                else
                    user.setBalance(user.getBalance() - amount);

                userDao.update(user);

                Logger.debug("Withdrawed " + uuid + " for " + amount);

                return user.getBalance();
            } catch (SQLException e) {
                throw new RuntimeException("Error while withdrawing user");
            }
        }).thenAccept((balance) -> {
            if (balance != -1d)
                Bukkit.getScheduler().runTask(ShiroEconomy.getApi().getPlugin(), () -> {
                    Bukkit.getServer().getPluginManager()
                            .callEvent(new BalanceUpdateEvent(
                                    balance,
                                    Bukkit.getOfflinePlayer(uuid)
                            ));
                });
        });
    }

    public void depositUser(UUID uuid, double amount) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Dao<EconomyUser, Long> userDao
                        = DaoManager.createDao(source, EconomyUser.class);
                List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

                if (users.isEmpty()) {
                    Logger.debug("No user found (" + uuid + ")");

                    return -1d;
                }

                EconomyUser user = users.get(0);

                user.setBalance(user.getBalance() + amount);

                userDao.update(user);

                Logger.debug("Deposited " + uuid + " for " + amount);

                return user.getBalance();
            } catch (SQLException e) {
                throw new RuntimeException("Error while depositing user");
            }
        }).thenAccept((balance) -> {
            if (balance != -1d)
                Bukkit.getScheduler().runTask(ShiroEconomy.getApi().getPlugin(), () -> {
                    Bukkit.getServer().getPluginManager()
                            .callEvent(new BalanceUpdateEvent(
                                    balance,
                                    Bukkit.getOfflinePlayer(uuid)
                            ));
                });
        });
    }

    public void setBalance(UUID uuid, double amount) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Dao<EconomyUser, Long> userDao
                        = DaoManager.createDao(source, EconomyUser.class);
                List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

                if (users.isEmpty()) {
                    Logger.debug("No user found (" + uuid + ")");

                    return -1d;
                }

                EconomyUser user = users.get(0);

                user.setBalance(amount);

                userDao.update(user);

                Logger.debug("Updated balance " + uuid + " for " + amount);

                return user.getBalance();
            } catch (SQLException e) {
                throw new RuntimeException("Error while setting balance");
            }
        }).thenAccept((balance) -> {
            if (balance != -1d)
                Bukkit.getScheduler().runTask(ShiroEconomy.getApi().getPlugin(), () -> {
                    Bukkit.getServer().getPluginManager()
                            .callEvent(new BalanceUpdateEvent(
                                    balance,
                                    Bukkit.getOfflinePlayer(uuid)
                            ));
                });
        });
    }

    public void createTransaction(UUID from, UUID to, double amount) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Dao<EconomyUser, Long> userDao
                        = DaoManager.createDao(source, EconomyUser.class);
                Dao<Transaction, Long> transactionDao
                        = DaoManager.createDao(source, Transaction.class);
                List<EconomyUser> users = userDao.queryForEq("uuid", from);

                if (users.isEmpty()) {
                    Logger.debug("No user found (" + from + ")");

                    return -1d;
                }

                EconomyUser userFrom = users.get(0);
                users = userDao.queryForEq("uuid", to);

                if (users.isEmpty()) {
                    Logger.debug("No user found (" + to + ")");

                    return -1d;
                }

                EconomyUser userTo = users.get(0);
                List<Transaction> userFromTransactions = getUserTransactionsById(userFrom.getId());
                List<Transaction> userToTransactions = getUserTransactionsById(userTo.getId());

                if (userFromTransactions.size() >= 100) {
                    Transaction toDelete = userFromTransactions
                            .stream()
                            .min(Comparator.comparing(t -> t.getTimestamp().getTime()))
                            .get();

                    Logger.debug("Removing the oldest transaction (" + from + ")");

                    transactionDao.deleteById(toDelete.getId());
                }

                if (userToTransactions.size() >= 100) {
                    Transaction toDelete = userToTransactions
                            .stream()
                            .min(Comparator.comparing(t -> t.getTimestamp().getTime()))
                            .get();

                    Logger.debug("Removing the oldest transaction (" + to + ")");

                    transactionDao.deleteById(toDelete.getId());
                }

                Transaction transaction = new Transaction(
                        userFrom,
                        userTo,
                        amount
                );

                transactionDao.create(transaction);

                Logger.debug("Transacted " + to + " and " + from + " for " + amount);

                return amount;
            } catch (SQLException e) {
                throw new RuntimeException("Error while creating transaction");
            }
        }).thenAccept((finalAmount) -> {
            Logger.debug("Transaction stage ended (" + finalAmount + ")");

            if (finalAmount != -1d)
                Bukkit.getScheduler().runTask(ShiroEconomy.getApi().getPlugin(), () -> {
                    Bukkit.getServer().getPluginManager()
                            .callEvent(new PlayerTransactionEvent(
                                    Bukkit.getOfflinePlayer(to),
                                    Bukkit.getPlayer(from),
                                    finalAmount
                            ));
                });
        });
    }

    public void createAccount(UUID uuid) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Dao<EconomyUser, Long> userDao
                        = DaoManager.createDao(source, EconomyUser.class);
                EconomyUser user = new EconomyUser(uuid);

                userDao.createIfNotExists(user);
                userDao.refresh(user);

                Logger.debug("Created new account (" + uuid + ")");

                return user;
            } catch (SQLException e) {
                throw new RuntimeException("Error while creating account");
            }
        });
    }

    public double getBalance(UUID uuid) {
        try {
            Dao<EconomyUser, Long> userDao
                    = DaoManager.createDao(source, EconomyUser.class);
            List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

            if (users.isEmpty()) {
                Logger.debug("No user found. Can't return balance (" + uuid + ")");

                return 0d;
            }

            return users.get(0).getBalance();
        } catch (SQLException e) {
            throw new RuntimeException("Error while setting balance");
        }
    }

    public EconomyUser getUser(UUID uuid) {
        try {
            Dao<EconomyUser, Long> userDao
                    = DaoManager.createDao(source, EconomyUser.class);
            List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

            if (users.isEmpty()) {
                Logger.debug("No user found. Can't return user (" + uuid + ")");

                return null;
            }

            return users.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting user");
        }
    }

    public List<EconomyUser> getRichestUsers() {
        try {
            Dao<EconomyUser, Long> userDao
                    = DaoManager.createDao(source, EconomyUser.class);

            return userDao.query(userDao.queryBuilder()
                    .orderBy("balance", false)
                    .limit(10L)
                    .prepare());
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting user");
        }
    }

    public List<Transaction> getUserTransactionsById(long id) {
        try {
            Dao<Transaction, Long> transactionDao
                    = DaoManager.createDao(source, Transaction.class);

            return transactionDao.query(transactionDao.queryBuilder()
                    .orderBy("timestamp", false)
                    .where()
                    .eq("from_id", id)
                    .or()
                    .eq("to_id", id)
                    .and()
                    .lt("amount", ShiroEconomy.getApi().getPlugin()
                            .getConfig()
                            .getDouble("huge-transaction.minimum"))
                    .prepare());
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting user");
        }
    }

    public List<Transaction> getUserHugeTransactionsById(long id) {
        try {
            Dao<Transaction, Long> transactionDao
                    = DaoManager.createDao(source, Transaction.class);

            return transactionDao.query(transactionDao.queryBuilder()
                    .orderBy("timestamp", false)
                    .where()
                    .eq("from_id", id)
                    .or()
                    .eq("to_id", id)
                    .and()
                    .ge("amount", ShiroEconomy.getApi().getPlugin()
                            .getConfig()
                            .getDouble("huge-transaction.minimum"))
                    .prepare());
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting user");
        }
    }

    public boolean hasAccount(UUID uuid) {
        try {
            Dao<EconomyUser, Long> userDao
                    = DaoManager.createDao(source, EconomyUser.class);
            List<EconomyUser> users = userDao.queryForEq("uuid", uuid);

            return !users.isEmpty();
        } catch (SQLException e) {
            return false;
        }
    }
}
