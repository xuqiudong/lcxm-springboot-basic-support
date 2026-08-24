package cn.xuqiudong.basic.secureid.model;

import java.util.IdentityHashMap;

/**
 * 单次响应 ID 加密的递归上下文。
 * <p>
 * 只表达一次响应处理过程中的状态，不跨请求复用。
 * @author Vic.xu
 */
public class SecureIdContext {

    /**
     * 当前递归路径上已经访问过的对象引用。
     * <p>
     * 使用 IdentityHashMap 是为了按对象引用判断循环，而不是按 equals 判断业务相等。
     */
    private final IdentityHashMap<Object, Boolean> visited;

    /**
     * 当前递归深度。
     */
    private final int depth;

    public SecureIdContext() {
        this(new IdentityHashMap<Object, Boolean>(), 0);
    }

    private SecureIdContext(IdentityHashMap<Object, Boolean> visited, int depth) {
        this.visited = visited;
        this.depth = depth;
    }

    public int depth() {
        return depth;
    }

    public SecureIdContext nextDepth() {
        return new SecureIdContext(visited, depth + 1);
    }

    public boolean isVisited(Object value) {
        return visited.containsKey(value);
    }

    public void visit(Object value) {
        visited.put(value, Boolean.TRUE);
    }

    /**
     * 从当前递归路径移除对象。
     * <p>
     * 这里的 visited 是“当前路径栈”，不是全局已处理集合。
     * 处理完当前分支后移除，能避免循环引用，同时允许同一个对象在另一个独立分支中被正常处理。
     */
    public void leave(Object value) {
        visited.remove(value);
    }
}
