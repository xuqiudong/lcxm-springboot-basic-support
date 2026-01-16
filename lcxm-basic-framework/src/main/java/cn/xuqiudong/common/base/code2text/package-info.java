/**
 * 描述:
 *   json 反序列化 把 编码值 转化为 展示文本 （追加）
 *   <p>
 *       扩展： 考虑反向转化， 比如导入的时候
 *   </p>
 * @author Vic.xu
 * @since 2026-01-09 11:38
 */
package cn.xuqiudong.common.base.code2text;

/**
 * TODO
 *  1. 转换的时候 考虑 语言环境？
 *  2，多code转换 比如 code1,code2 转换成 text1,text2
 *  3  防止多节点 重复预热
 *        code2text.preload.enabled: true
 *        code2text.preload.mode: REDIS_ONLY | LOCAL_ONLY | BOTH
 *  4. getOrLoad 的并发合并（防缓存击穿）   是否有必要
 */