package cn.xuqiudong.basic.core.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *  构建两个集合的差别:
 *  old集合中没有的数据:需要新增的
 *  old集合已经存在的数据: 需要删除的
 *  T 需要能做map的key 即:若是对象需重写hashCode() 和equals()
 * @author VIC
 *
 */
@SuppressWarnings("PMD")
public class CalcDiffCollection<T> {

	/**
	 * 需要新增的元素
	 */
	private List<T> onlyInNew = new ArrayList<>();


	/**
	 * 需要删除的id集合
	 */
	private List<T> onlyInOld = new ArrayList<>();
	
	/**
	 * 相同的元素
	 */
	private List<T> union = new ArrayList<>();
	 
	/**
	 * 
	 * @param olds 原来的数据库中的IDS
	 * @param news 现在的IDS
	 */
	public static <T> CalcDiffCollection<T> instance(Collection<T> olds, Collection<T> news){
		Map<T, T> oldMap = new HashMap<>();
		Map<T, T> nowMap = new HashMap<>();
		for(T old : olds) {
			oldMap.put(old, old);
		}
		for(T now : news) {
			nowMap.put(now, now);
		}
		List<T> onlyInNew = new ArrayList<>();
		List<T> onlyInOld = new ArrayList<>();
		List<T> union = new ArrayList<>();
		for(T key : oldMap.keySet()){
			if(nowMap.get(key) == null) {
				//当前的IDS中没有 则表示应该删除
				onlyInOld.add(key);
			}else {
				union.add(key);
			}
		}
		
		for(T key : nowMap.keySet()){
			if(oldMap.get(key) == null) {
				//原来的的IDS中没有 则表示应该新增
				onlyInNew.add(key);
			}
		}
		
		return new CalcDiffCollection<>(onlyInNew, onlyInOld, union);
	}
	
	
	private CalcDiffCollection(){}
	
	

	
	private CalcDiffCollection(List<T> onlyInNew, List<T> onlyInOld, List<T> union) {
		this.onlyInNew = onlyInNew;
		this.onlyInOld = onlyInOld;
		this.union = union;
	}


	public List<T> getOnlyInNew() {
		return onlyInNew;
	}


	public List<T> getOnlyInOld() {
		return onlyInOld;
	}


	public List<T> getUnion() {
		return union;
	}


	public void setOnlyInNew(List<T> onlyInNew) {
		this.onlyInNew = onlyInNew;
	}


	public void setOnlyInOld(List<T> onlyInOld) {
		this.onlyInOld = onlyInOld;
	}


	public void setUnion(List<T> union) {
		this.union = union;
	}


	@Override
	public String toString() {
		return "CalcDiffCollection [onlyInNew=" + onlyInNew + ", onlyInOld=" + onlyInOld + ", union=" + union + "]";
	}


	public static void main(String[] args) {
		Integer [] old = {1,2,3,4};
		Integer[] now = {3,4,5,6};
		CalcDiffCollection<Integer> a = CalcDiffCollection.instance(Arrays.asList(old), Arrays.asList(now));
		System.out.println(a);
	}


	
	
}
