package com.anysoft.util;

import java.util.Comparator;
import java.util.Iterator;

/**
 * 超级Set
 * <p>Java中的{@link java.util.Set}不支持模糊的查找(颇为想念C++STL中的Set)。超级Set则支持模糊查找，见{@link #lowerBound(Object)}.<p>
 * <p>超级Set通过链表实现插入，通过数组实现检索，力图达到插入效率和查找效率的平衡。</p>
 * @author duanyy
 *
 * @param <object> 对象的类名
 */
public class SuperSet<object> {
	/**
	 * 链表结构的列表
	 */
	private SuperSetItem<object> root = new SuperSetItem<object>();
	/**
	 * 当前的对象个数
	 */
	private int currentCount = 0;
	/**
	 * 数据结构的列表
	 */
	private SuperSetItem<object> [] array = null;
	/**
	 * 数组结构和链表结构之间是否进行了同步
	 */
	private boolean isSync = false;	
	/**
	 * 对象的比较器
	 */
	private Comparator<object> comparator = null;
	
	/**
	 * 构造函数，指定对象的比较器
	 * <p>如果不指定对象的比较器，则要求对象实现了{@link Comparable}接口。</p>
	 * @param _comparator
	 */
	public SuperSet(Comparator<object> _comparator){
		comparator = _comparator;
	}
	/**
	 * 构造函数
	 */
	public SuperSet(){
		this(null);
	}	

	/**
	 * 比较两个对象的大小
	 * @param obj1 对象1
	 * @param obj2 对象2
	 * @return 如果相等，返回为0;如果obj1大于obj2，则返回为1;如果obj1小于obj2，则返回为-1
	 */
	@SuppressWarnings("unchecked")
	private int compare(object obj1,object obj2){
		if (comparator != null){
			return comparator.compare(obj1, obj2);
		}
		Comparable<object> c = (Comparable<object>)obj1;
		return c.compareTo(obj2);
	}
	/**
	 * 从链表结构同步到数组结构
	 */
	@SuppressWarnings("unchecked")
	private void sync(){
		if (currentCount <= 0) return ;
		if (array == null){
			array = new SuperSetItem[currentCount * 2];
		}else{
			if (array.length < currentCount){
				for (int i = 0 ; i < array.length ; i ++){
					array[i] = null;
				}
				array = new SuperSetItem[currentCount * 2];
			}
		}
		
		int index = 0;
		SuperSetItem<object> current = root;
		while (current.next != null){
			array[index ++] = current.next;
			current = current.next;
		}		
		for (;index < array.length; index++){
			array[index] = null;
		}
		isSync = true;
	}
	
	/**
	 * 回到第一个对象位置
	 * @return 迭代器
	 */
	public Iterator<object> begin(){
		return new SuperSetIterator<object>(root.next);
	}

	/**
	 * 当前Set中对象个数
	 * @return 对象个数
	 */
	public int size(){
		return currentCount;
	}
	/**
	 * 向Set中增加对象
	 * @param data 对象实例
	 */
	synchronized public void add(object data){
		if (data == null) return;		
		//先找到位置
		SuperSetItem<object> current = root;
		while (current.next != null){
			int ret = compare(data,current.next.content);
			if (ret == 0){
				//如果相等，替代他
				current.next.content = data;
				return ;
			}
			if (ret < 0){
				//找到了插入位置
				break;
			}
			current = current.next;
		}

		SuperSetItem<object> newItem = new SuperSetItem<object>();
		newItem.content = data;
		newItem.next = current.next;
		current.next = newItem;
		currentCount ++;
		isSync = false;
	}
	
	/**
	 * 从Set中删除对象
	 * <p>删除的不是data对象实例本身，而是Set中"等于"该对象的对象实例。</p>
	 * @param data 对象实例
	 */
	synchronized public void remove(object data){
		if (data == null) return ;
		//先找到位置
		SuperSetItem<object> current = root;
		while (current.next != null){
			int ret = compare(data,current.next.content);
			if (ret == 0){
				current.next.content = null;
				current.next = current.next.next;
				currentCount --;
				isSync = false;
				return ;
			}
			current = current.next;
		}	
	}
	
	/**
	 * 清空Set
	 */
	synchronized public void clear(){
		isSync = false;
		SuperSetItem<object> current = root;
		while (current.next != null){
			current.next.content = null;
			current = current.next;
		}	
		root.next = null;
		currentCount = 0;
	}
	/**
	 * 按索引查找对象
	 * @param index 对象位置
	 * @return 对象实例
	 */
	synchronized public object get(int index){
		if (!isSync){
			sync();
		}
		if (index < 0 || index >= currentCount)
			return null;
		
		return array[index].content;
	}
	/**
	 * 查找对象
	 * @param data 对象实例
	 * @return "等于"data的对象实例
	 */
	synchronized public object get(object data){
		if (!isSync){
			sync();
		}
		int iIndex = 0;
		int iStart = 0;
		int iEnd = array.length - 1;
		while (true){
			iIndex = (iStart + iEnd) / 2;
			int ret = compare(data,array[iIndex].content);
			if (ret == 0){
				return array[iIndex].content;
			}
			if (ret < 0){
				iEnd = iIndex - 1;
			}else{
				iStart = iIndex + 1;
			}
			if (iStart > iEnd){
				break;
			}
		}
		return null;
	}	
	/**
	 * 查找最接近对象的对象实例
	 * @param data 对象
	 * @return 对象实例
	 */
	synchronized public Iterator<object> lowerBound(object data){
		if (!isSync){
			sync();
		}
		int iIndex = 0;
		int iStart = 0;
		int iEnd = currentCount - 1;
		
		SuperSetIterator<object> iterator = new SuperSetIterator<object>(null);
		while (true){
			iIndex = (iStart + iEnd)/2;
			int ret = compare(data,array[iIndex].content);
			if (ret == 0){
				iterator.item = array[iIndex];
				return iterator;
			}
			if (ret < 0){
				iEnd = iIndex;
			}else{
				iStart = iIndex + 1;
			}
			if (iStart >= iEnd){
				break;
			}
		}
		iterator.item = array[iEnd];
		return iterator;
	}

	/**
	 * Set的Item
	 * @author duanyy
	 *
	 * @param <object> 对象的类名
	 */
	static class SuperSetItem <object>{
		public object content = null;
		public SuperSetItem<object> next = null;
	}
	
	/**
	 * Set的迭代器
	 * @author duanyy
	 *
	 * @param <object> 对象的类名
	 */
	static class SuperSetIterator<object> implements Iterator<object>{
		/**
		 * 迭代器对应的Item
		 */
		protected SuperSetItem<object> item = null;
		/**
		 * 构造函数
		 * @param _item 迭代器对应的Item
		 */
		protected SuperSetIterator(SuperSetItem<object> _item){
			item = _item;
		}
		/**
		 * 下一个对象是否存在
		 */
		public boolean hasNext() {
			return item != null;
		}
		/**
		 * 取下一个对象
		 */
		public object next() {
			if (item == null) return null;
			object content = item.content;
			item = item.next;
			return content;
		}
		/**
		 * 不支持迭代器的remove
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}		
	}
}
