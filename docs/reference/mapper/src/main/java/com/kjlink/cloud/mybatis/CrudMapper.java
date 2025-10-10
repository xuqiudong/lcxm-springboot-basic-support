package com.kjlink.cloud.mybatis;

import com.kjlink.cloud.mybatis.query.Where;

/**
 * 通用实体Mapper，基于MyBatis-Plus的BaseMapper进行了增强，
 * 99%的常用单表操作方法都可以在这里找到。
 * </p>
 *
 * <h2>参数说明：</h2>
 * <ul>
 *     <li>
 *         &lt;T&gt; 实体类泛型
 *     </li>
 *      <li>
 *       Column&lt;T,A&gt; 以Lambda方式传递列名称，例如User::setName
 *      </li>
 *      <li>
 *          Where&lt;T&gt; 基于Wrapper封装了一个简单的where条件拼装工具，可以用在select/update/delete
 *      </li>
 *      <li>
 *          Orders&lt;T&gt; 以Lambda方式传递列名称组装order by语句
 *      </li>
 *      <li>
 *          PageQuery/QueryVo： 调用QueryUtil自动生成wrapper
 *      </li>
 * </ul>
 * <h2>方法命名规则</h2>
 * <ul>
 *     <li>查询操作：select开头. 一般命名规则：select 字段名 by 条件 order by 排序</li>
 *     <li>修改操作：update开头，一般命名规则：update 字段名 by 条件</li>
 *     <li>新增操作：insert开头</li>
 *     <li>删除操作: delete开头，一般命名规则：deleteBy 条件</li>
 *     <li>新增或修改: save开头</li>
 *     <li>特殊的: exists</li>
 *     <li>byColumn一般提供1、2、3列，且使用=号判断。超过3个可以使用{@linkplain Where}工具。
 *          由于内部实现不同，如果传入null值可能生成is null和 = null两种不同的行为
 *     </li>
 *
 * </ul>
 *
 * <h2>使用方法</h2>
 * <h3>定义Mapper</h3>
 * <code>
 *     <pre>
 *        &#64;Mapper
 *        public class UserMapper extends CrudMapper&lt;User&gt;{
 *            //这里可以继续加自己需要的sql
 *        }
 *     </pre>
 * </code>
 * <h3>使用</h3>
 * <code><pre>
 * &#64;Autowrired
 * private UserMapper mapper;
 * ...
 * User user = new User();
 * mapper.save(user);  //保存实体
 * mapper.updateByIdSelective(update); //修改实体
 * mapper.selectById("1"); //查询实体
 * mapper.selectByColumn(User::setName, "test");
 * mapper.deleteById("1"); //删除实体
 *
 * @author Fulai
 * @since 2025-07-01
 */
public interface CrudMapper<T> extends GenericCrudMapper<String, T> {

}
