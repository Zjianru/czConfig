package com.cz.config.server.lock;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 分布式锁管理类，用于实现分布式环境下的锁机制。
 * 通过利用数据库的行级锁来实现锁的获取和释放，确保分布式系统中的数据一致性。
 */
@Component
public class DistributedLocks {

    @Autowired
    DataSource dataSource; // 自动注入数据源，用于获取数据库连接

    Connection connection; // 数据库连接

    @Getter
    private final AtomicBoolean locked = new AtomicBoolean(false); // 标记锁的状态

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1); // 定时任务执行器，用于定时尝试获取锁

    /**
     * 初始化方法，在组件生命周期开始时执行。
     * 主要功能是获取数据库连接，并定时尝试获取锁。
     */
    @PostConstruct
    public void init() {
        try {
            connection = dataSource.getConnection(); // 获取数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 每隔5秒尝试获取锁
        executor.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试获取分布式锁。
     * 通过执行SQL语句，在数据库中对锁进行加锁操作。
     *
     * @return 总是返回true，表示锁操作已被尝试执行。
     * @throws SQLException 如果数据库操作失败。
     */
    public boolean lock() throws SQLException {
        // 设置锁等待超时时间
        String changeLockTimeOut = "set innodb_lock_wait_timeout =5";
        // 尝试获取锁的SQL语句
        String tryLockSql = "select app from locks where id=1 for update";
        connection.setAutoCommit(false); // 关闭自动提交，以便手动控制事务
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED); // 设置事务隔离级别
        connection.createStatement().execute(changeLockTimeOut); // 执行设置锁等待超时的语句
        connection.createStatement().execute(tryLockSql); // 执行尝试获取锁的SQL语句
        // 输出锁的状态
        if (locked.get()) {
            System.out.println("reenter this dist lock");
        } else {
            System.out.println("get this dist lock");
        }
        return true;
    }

    /**
     * 定时任务，尝试获取锁。
     * 如果获取锁成功，则将锁状态标记为true；如果失败，则标记为false。
     */
    private void tryLock() {
        try {
            lock(); // 尝试获取锁
            locked.set(true); // 锁获取成功，更新锁状态
        } catch (Exception e) {
            System.out.println("lock failed");
            locked.set(false); // 锁获取失败，更新锁状态
        }
    }

    /**
     * 关闭分布式锁机制，释放数据库连接。
     * 主要用于在应用停止时或发生异常时，清理资源。
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback(); // 回滚事务
                connection.close(); // 关闭数据库连接
            }
        } catch (SQLException e) {
            System.out.println("close connection failed...ignore this exception");
        }
    }
}
