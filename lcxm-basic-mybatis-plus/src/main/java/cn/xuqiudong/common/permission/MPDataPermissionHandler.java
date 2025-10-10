package cn.xuqiudong.common.permission;

/**
 * 描述:
 * mybatis-plus 权限处理器
 *
 * @author Vic.xu
 * @since 2025-09-23 14:43
 */

import cn.xuqiudong.common.permission.model.DataRowAuthColumn;
import cn.xuqiudong.common.permission.model.DataRowAuthModel;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.util.Set;

/**
 * 适配 MyBatis-Plus 官方插件的数据权限处理器
 * 复用原有国银/CP权限逻辑，替换 SQL 解析部分
 */
public class MPDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 1. 获取原有线程中的权限条件（复用原有逻辑）
        DataRowAuthModel authorityData = RowDataHelper.getLocalAuthorityData();
        if (authorityData == null) {
            return where;
        }
        Set<DataRowAuthColumn> columns = authorityData.getFilterColumns();
        if (columns == null || columns.isEmpty()) {
            return where;
        }

        // 2. 构建权限条件表达式（原有 OR 关系保持不变）
        Expression permissionExpr = null;
        for (DataRowAuthColumn column : columns) {
            // 调用原有枚举的 SQL 生成逻辑
            String conditionSql = column.getType().handlerSql(column.getColumn());

            // 用 MP 依赖的 JSqlParser 解析为 Expression
            Expression conditionExpr = parseSql(column, conditionSql);

            // 多个权限之间是 OR 关系
            permissionExpr = (permissionExpr == null)
                    ? conditionExpr
                    : new OrExpression(permissionExpr, conditionExpr);
        }
        // 3. 核心：用括号包裹所有权限条件（确保整体作为一个条件）
        if (permissionExpr != null) {
            permissionExpr = new ParenthesedExpressionList(permissionExpr);
        }

        // 4. 与原始WHERE条件合并（AND关系）
        return (where == null) ? permissionExpr : new AndExpression(where, permissionExpr);
    }

    /**
     * 解析 SQL 条件为 JSqlParser 表达式:
     * 有前置条件: 返回 (前置条件 AND 权限条件)
     * 无前置条件: 直接返回权限条件
     */
    private Expression parseSql(DataRowAuthColumn column, String permissionSql) {
        try {
            // 1. 先解析权限条件（如 "id = 1"）
            Expression permissionExpr = CCJSqlParserUtil.parseCondExpression(permissionSql);

            // 2. 若有前置条件（如 "a.xx_id is not null"），则组合为 "前置条件 AND 权限条件"
            if (column.hasPrecondition()) {
                Expression preconditionExpr = CCJSqlParserUtil.parseCondExpression(column.getPrecondition());
                // 前置条件在前，权限条件在后（符合 "满足A时才应用B" 的语义）
                Expression combinedExpr = new AndExpression(preconditionExpr, permissionExpr);
                //  用括号包裹 (前置条件 AND 权限条件)
                return new ParenthesedExpressionList(combinedExpr);
            }

            // 3. 无前置条件，直接返回权限条件
            return permissionExpr;
        } catch (Exception e) {
            // 异常信息包含完整上下文，方便调试
            throw new RuntimeException(
                    String.format("解析权限条件失败: 前置条件=[%s], 权限条件=[%s]",
                            column.getPrecondition(), permissionSql),
                    e
            );
        }
    }

}
