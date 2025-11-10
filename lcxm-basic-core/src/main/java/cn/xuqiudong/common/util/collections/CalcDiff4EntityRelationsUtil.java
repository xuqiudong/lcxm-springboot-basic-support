package cn.xuqiudong.common.util.collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：构建两个集合的差别: 主要用于级联对象列表，在入库前判断哪些需要新增，哪些需要删除
 * 对象的唯一标示为其id
 *
 * @author Vic.xu
 * @see EntityIdentification
 * @see CalcDiff4EntityRelationsUtil#main(String[])
 * @since 2023-05-18
 */
public class CalcDiff4EntityRelationsUtil {

    /**
     * @param olds 从数据库查询出来的列表
     * @param news 从页面而来的列表数据 如果id是空的则表示是新增
     * @param <T>  extends EntityIdentification的包含id的泛型
     */
    public static <T extends EntityIdentification> RelationResult<T> calc(Collection<T> olds, Collection<T> news) {
        RelationResult<T> relationResult = new RelationResult<>();
        // 全部为空， 则不处理
        if (CollectionUtils.isEmpty(olds) && CollectionUtils.isEmpty(news)) {
            return relationResult;
        }

        // news 为null则表示全部要删除
        if (CollectionUtils.isEmpty(news)) {
            relationResult.setOnlyInOld(new ArrayList<>(olds));
            return relationResult;
        }

        // olds 为null则表示全部要新增
        if (CollectionUtils.isEmpty(olds)) {
            relationResult.setOnlyInNew(new ArrayList<>(news));
            return relationResult;
        }

        Map<Serializable, T> oldMap = new HashMap<>();
        Map<Serializable, T> nowMap = new HashMap<>();

        List<T> onlyInNew = new ArrayList<>();
        List<T> onlyInOld = new ArrayList<>();
        List<T> union = new ArrayList<>();

        for (T old : olds) {
            oldMap.put(old.getId(), old);
        }
        for (T now : news) {
            Serializable id = now.getId();
            if (ObjectUtils.isEmpty(id)) {
                onlyInNew.add(now);
            } else {
                nowMap.put(id, now);
            }

        }

        for (Map.Entry<Serializable, T> entry : oldMap.entrySet()) {
            //当前的IDS中没有 则表示应该删除
            if (nowMap.get(entry.getKey()) == null) {
                onlyInOld.add(entry.getValue());
            }
        }

        for (Map.Entry<Serializable, T> entry : nowMap.entrySet()) {
            //原来的的IDS中没有 则表示应该新增
            if (oldMap.get(entry.getKey()) == null) {
                onlyInNew.add(entry.getValue());
            } else {
                union.add(entry.getValue());
            }
        }
        return new RelationResult<>(onlyInNew, onlyInOld, union);
    }

    public static class RelationResult<T extends EntityIdentification> {
        /**
         * 需要新增的元素
         */
        private List<T> onlyInNew = new ArrayList<>();

        /**
         * 需要删除的集合
         */
        private List<T> onlyInOld = new ArrayList<>();

        /**
         * 相同的元素：union 中保存的为news中的对象（一般也即页面form传递来是数据，可以考虑用它更新数据库），
         */
        private List<T> union = new ArrayList<>();

        public RelationResult() {
            super();
        }

        public RelationResult(List<T> onlyInNew, List<T> onlyInOld, List<T> union) {
            this.onlyInNew = onlyInNew;
            this.onlyInOld = onlyInOld;
            this.union = union;
        }

        public List<T> getOnlyInNew() {
            return onlyInNew;
        }

        public void setOnlyInNew(List<T> onlyInNew) {
            this.onlyInNew = onlyInNew;
        }

        public List<T> getOnlyInOld() {
            return onlyInOld;
        }

        public void setOnlyInOld(List<T> onlyInOld) {
            this.onlyInOld = onlyInOld;
        }

        public List<T> getUnion() {
            return union;
        }

        public void setUnion(List<T> union) {
            this.union = union;
        }

    }

    public static void main(String[] args) {
        test();

    }

    private static void test() {

        Number id = null;
        String name = String.valueOf(id);

        System.out.println(name + " " + StringUtils.isBlank(name));
        List<Test> news = new ArrayList<>();
        news.add(new Test("", "新增00"));
        news.add(new Test("", "新增01"));
        news.add(new Test("1", "新增02"));
        news.add(new Test("2", "相同22"));
        news.add(new Test("3", "相同33"));
        List<Test> olds = new ArrayList<>();
        olds.add(new Test("2", "相同22"));
        olds.add(new Test("3", "相同33"));
        olds.add(new Test("5", "待删55"));
        olds.add(new Test("6", "待删66"));

        RelationResult<Test> calc = CalcDiff4EntityRelationsUtil.calc(olds, news);

        System.out.println("需要新增的：" + calc.onlyInNew);
        System.out.println("需要删除的：" + calc.onlyInOld);
        System.out.println("相同的：" + calc.union);

    }

    private static class Test implements EntityIdentification {

        private final String id;

        private final String name;

        public Test(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Test{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
        }
    }

}
