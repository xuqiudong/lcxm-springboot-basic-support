package com.kjlink.cloud.mybatis.dataperm;

import org.springframework.lang.Nullable;

import com.kjlink.cloud.mybatis.meta.CurrentUserInfoUtil;

/**
 * 默认的sql占位符解析器
 *
 * @author Fulai
 * @since 2025-07-21
 */
public class SimpleDataPermissionSqlResolver implements DataPermissionSqlResolver {

    @Override
    public String resolve(String table, @Nullable String alias, String sqlFragment) {
        sqlFragment = resolveFragment(sqlFragment);
        sqlFragment = resolveUsername(sqlFragment);
        sqlFragment = replaceOrgId(sqlFragment);
        //替换别名
        sqlFragment = replaceAlias(alias, sqlFragment);
        return sqlFragment;
    }

    /**
     * t.xyz替换为实际表别名
     *
     * @param alias
     * @param sqlFragment
     * @return
     */
    protected String replaceAlias(String alias, String sqlFragment) {
        //已经是t，免替换，提高性能
        if ("t".equals(alias)) {
            return sqlFragment;
        }
        if (alias == null || alias.isEmpty()) {
            return sqlFragment.replaceAll("\\bt\\.(\\w+)\\b", "$1");
        }
        return sqlFragment.replaceAll("\\bt\\.(\\w+)\\b", alias + ".$1");
    }

    /**
     * 替换ORGID为当前用户的所属机构ID
     *
     * @param sqlFragment
     * @return
     */
    protected String replaceOrgId(String sqlFragment) {
        return sqlFragment.replace("{{ORGID}}", "'" + CurrentUserInfoUtil.getOrganizationId() + "'");
    }

    /**
     * 替换USERNAME为当前用户账号
     *
     * @param sqlFragment
     * @return
     */
    protected String resolveUsername(String sqlFragment) {
        return sqlFragment.replace("{{USERNAME}}", "'" + CurrentUserInfoUtil.getUsername() + "'");
    }

    /**
     * 自定义其他变量替换逻辑，子类可以重写
     * @param sqlFragment
     * @return
     */
    protected String resolveFragment(String sqlFragment) {
        return sqlFragment;
    }

}
