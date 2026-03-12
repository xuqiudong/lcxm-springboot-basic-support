package cn.xuqiudong.basic.third.transmission.log.service;

import cn.xuqiudong.basic.third.transmission.log.model.ThirdLogModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 第三方对接日志记录
 * @author VIC.xu
 *
 */
public class ThirdLogService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 新增记录日志，事务提交后
     * @param model 日志对象
     */
    public void insertWithTransaction(ThirdLogModel model) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    insert(model);
                }
            });

        } else {
            insert(model);
        }

    }

    /**
     * 新增记录日志
     * @param model 日志对象
     * @return 主键
     */
    public int insert(ThirdLogModel model) {
        String sql = "INSERT INTO t_stl_s_third_log (third, request, response, status,create_user_id, fid,  create_date) "
                + " VALUE(?, ?, ? , ? , ? , ?, now() )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            @SuppressFBWarnings("")
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, model.getThird());
                ps.setString(2, model.getRequest());
                ps.setString(3, model.getResponse());
                ps.setInt(4, model.getStatus());
                ps.setString(5, model.getCreateUserId());
                ps.setString(6, model.getFid());

                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

}
