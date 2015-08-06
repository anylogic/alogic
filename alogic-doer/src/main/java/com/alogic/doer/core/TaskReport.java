package com.alogic.doer.core;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.timer.core.Task;
import com.alogic.timer.core.Task.State;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Reportable;

/**
 * 任务报告
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 */
public interface TaskReport extends Reportable{
	
	/**
	 * 获取创建时间
	 * @return create time
	 */
	public long createTime();
	
	/**
	 * 获取状态改变时间
	 * @return 改变时间
	 */
	public long stateTime();
	
	/**
	 * 获取任务处理状态
	 * @return 处理状态
	 */
	public State state();
	
	/**
	 * 获取对应的任务
	 * @return 任务实例
	 */
	public Task getTask();
	
	/**
	 * 获取进度百分比(0至10000间的整数)
	 * @return 进度
	 */
	public int getPercent();
	
	/**
	 * 缺省实现
	 * 
	 * @author duanyy
	 * @since 1.6.3.4
	 */
	public static class Default implements TaskReport{
		
		/**
		 * 构造函数
		 * @param _task 任务
		 */
		public Default(Task _task){
			task = _task;
		}
		
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("id", task.id());
				
				xml.setAttribute("createTime", String.valueOf(createTime));
				xml.setAttribute("stateTime",String.valueOf(stateTime));
				xml.setAttribute("state",state.name());
				xml.setAttribute("percent", String.valueOf(percent));
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"id",task.id());
				
				JsonTools.setString(json, "createTime", String.valueOf(createTime));
				JsonTools.setString(json, "stateTime", String.valueOf(stateTime));
				JsonTools.setString(json,"state",state.name());
				JsonTools.setString(json,"percent",String.valueOf(percent));
			}
		}

		public long createTime() {
			return createTime;
		}

		public long stateTime() {
			return stateTime;
		}

		public State state() {
			return state;
		}

		public Task getTask() {
			return task;
		}

		public int getPercent() {
			return percent;
		}
		
		public void reportState(State _state,int _percent){
			state = _state;
			//当_percent小于0时不进行更新
			percent = _percent < 0 ? percent : _percent;
			stateTime = System.currentTimeMillis();
		}
		
		protected Task task = null;
		protected long createTime = System.currentTimeMillis();
		protected long stateTime = System.currentTimeMillis();
		protected int percent = 0;
		protected State state = State.New;
	}
}
