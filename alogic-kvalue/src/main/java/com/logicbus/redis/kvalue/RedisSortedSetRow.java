package com.logicbus.redis.kvalue;

import java.util.List;
import java.util.Map;

import com.anysoft.util.Pair;
import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.core.SortedSetRow;
import com.logicbus.kvalue.core.Table.DataType;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.context.RedisContext;
import com.logicbus.redis.toolkit.SortedSetTool;

public class RedisSortedSetRow extends RedisBaseRow implements SortedSetRow {

	public RedisSortedSetRow(DataType _dataType, String _key,
			boolean _enableRWSplit, RedisContext _source, Partition _partition) {
		super(_dataType, _key, _enableRWSplit, _source, _partition);
	}

	
	public boolean add(String member, double score) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zadd(key(), member, score);
		}finally{
			client.poolClose();
		}
	}

	
	public boolean add(Pair<String, Double> element) {
		return add(element.key(),element.value());
	}

	
	public long add(Map<String, Double> elements) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zadd(key(), elements);
		}finally{
			client.poolClose();
		}
	}

	
	public long size() {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.size(key());
		}finally{
			client.poolClose();
		}
	}

	
	public double incr(String member, double score) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zincrby(key(), member, score);
		}finally{
			client.poolClose();
		}
	}

	
	public double score(String member) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zscore(key(), member);
		}finally{
			client.poolClose();
		}
	}

	
	public long rank(String member, boolean reverse) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrank(key(), member);
			}else{
				return tool.zrank(key(), member);
			}
		}finally{
			client.poolClose();
		}
	}

	
	public long count(double min, double max) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zcount(key(), min, max);
		}finally{
			client.poolClose();
		}
	}

	
	public long remove(String... members) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zrem(key(), members);
		}finally{
			client.poolClose();
		}
	}

	
	public long remove(long start, long stop) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zremrangebyrank(key(), start, stop);
		}finally{
			client.poolClose();
		}
	}

	
	public long remove(double min, double max) {
		Client client = getClient(false);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zremrangebyscore(key(), String.valueOf(min), String.valueOf(max));
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> rangeByScore(double min, double max, boolean reverse) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrangebyscore(key, String.valueOf(min),String.valueOf(max));
			}else{
				return tool.zrangebyscore(key, String.valueOf(min),String.valueOf(max));
			}
		}finally{
			client.poolClose();
		}
	}

	
	public List<Pair<String, Double>> rangeByScoreWithScores(double min,
			double max, boolean reverse) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrangebyscoreWithScores(key(), String.valueOf(min), String.valueOf(max));
			}else{
				return tool.zrangebyscoreWithScores(key(), String.valueOf(min), String.valueOf(max));
			}
		}finally{
			client.poolClose();
		}
	}

	
	public List<String> range(long start, long stop, boolean reverse) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrange(key(), start, stop);
			}else{
				return tool.zrange(key(), start, stop);
			}
		}finally{
			client.poolClose();
		}
	}

	
	public List<Pair<String, Double>> rangeWithScores(long start, long stop,
			boolean reverse) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrangeWithScores(key(), start, stop);
			}else{
				return tool.zrangeWithScores(key(), start, stop);
			}
		}finally{
			client.poolClose();
		}
	}


	@Override
	public List<String> rangeByScore(double min, double max, boolean reverse, long offset, long cnt) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrangebyscore(key, String.valueOf(min),String.valueOf(max),offset,cnt);
			}else{
				return tool.zrangebyscore(key, String.valueOf(min),String.valueOf(max),offset,cnt);
			}
		}finally{
			client.poolClose();
		}
	}


	@Override
	public List<Pair<String, Double>> rangeByScoreWithScores(double min, double max, boolean reverse, long offset,
			long cnt) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			if (reverse){
				return tool.zrevrangebyscoreWithScores(key(), String.valueOf(min), String.valueOf(max),offset,cnt);
			}else{
				return tool.zrangebyscoreWithScores(key(), String.valueOf(min), String.valueOf(max),offset,cnt);
			}
		}finally{
			client.poolClose();
		}
	}


	@Override
	public List<String> rangeByLex(String min, String max, long offset, long cnt) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zrangeByLex(key(), min, max, offset, cnt);
		}finally{
			client.poolClose();
		}
	}


	@Override
	public long removeByLex(String min, String max) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zremrangeByLex(key(), min, max);
		}finally{
			client.poolClose();
		}
	}


	@Override
	public long countByLex(String min, String max) {
		Client client = getClient(true);
		try {
			SortedSetTool tool = (SortedSetTool)client.getToolKit(SortedSetTool.class);
			return tool.zcountByLex(key(), min, max);
		}finally{
			client.poolClose();
		}
	}

}
