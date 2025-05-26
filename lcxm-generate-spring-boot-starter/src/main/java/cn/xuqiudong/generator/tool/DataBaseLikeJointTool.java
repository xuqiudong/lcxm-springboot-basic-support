package cn.xuqiudong.generator.tool;

import java.io.IOException;
import java.io.Writer;

import cn.xuqiudong.generator.autoconfigure.GeneratorProperties;
import cn.xuqiudong.generator.contant.DatabaseType;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * 自定义velocity函数：根据不同数据库类型 拼接不同的like sql语句
 * @author VIC.xu
 *
 */
public class DataBaseLikeJointTool extends Directive{

	/**
	 * 函数名  ：  #LikeJoint(attrName)
	 */
	@Override
	public String getName() {
		return "LikeJoint";
	}

	/**
	 * LINE： 不要#end结束符
	 * BLOCK：需要#end结束符
	 */
	@Override
	public int getType() {
		return LINE;
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node)
			throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		//第一个参数：查询字段对应 属性名
		String columnAttrname = node.jjtGetChild(0).value(context) + "";
		
		//根据数据库生成like 拼接字段 并输出
		DatabaseType databaseType = GeneratorProperties.getDatabaseType();
		String likeJoint = databaseType.getDialect().getLikeJoint().apply(columnAttrname);
		writer.write(likeJoint);

		return true;
	}

}
