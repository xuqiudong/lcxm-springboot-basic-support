package cn.xuqiudong.common.permission.enums;

/**
 * 描述:
 *   用于测试的权限类型
 * @author Vic.xu
 * @since 2025-09-23 14:54
 */
public enum TestRowDataHandlerType implements RowDataHandlerType {

    /**
     * [id] = 1
     */
    ID_EQ_1 {
        @Override
        public String handlerSql(String column) {
            return column + " = 1";
        }
    },

    /**
     * [id] = 2
     */
    ID_EQ_2 {
        @Override
        public String handlerSql(String column) {
            return column + " = 2";
        }
    },
    /**
     * [id] > 2
     */
    ID_GT_2{
        @Override
        public String handlerSql(String column) {
            return column + " > 2";
        }
    },

    /**
     * [position] in (1,2,3)
     */
    POSITION {
        @Override
        public String handlerSql(String column) {
            return " and " + column + " in (1,2,3)";
        }
    },
    /**
     * [age] > 18
     */
    AGE_GT_18 {
        @Override
        public String handlerSql(String column) {
            return column + " > 18";
        }
    },
    /**
     * [age] < 35
     */
    AGE_LT_35 {
        @Override
        public String handlerSql(String column) {
            return column + " < 35";
        }
    },

}
