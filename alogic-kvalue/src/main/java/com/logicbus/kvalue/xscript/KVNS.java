package com.logicbus.kvalue.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;
import com.logicbus.kvalue.xscript.hash.KVHDel;
import com.logicbus.kvalue.xscript.hash.KVHExist;
import com.logicbus.kvalue.xscript.hash.KVHGet;
import com.logicbus.kvalue.xscript.hash.KVHGetAll;
import com.logicbus.kvalue.xscript.hash.KVHKeys;
import com.logicbus.kvalue.xscript.hash.KVHMGet;
import com.logicbus.kvalue.xscript.hash.KVHMSet;
import com.logicbus.kvalue.xscript.hash.KVHSet;
import com.logicbus.kvalue.xscript.hash.KVHValues;
import com.logicbus.kvalue.xscript.list.KVLGet;
import com.logicbus.kvalue.xscript.list.KVLInsert;
import com.logicbus.kvalue.xscript.list.KVLItems;
import com.logicbus.kvalue.xscript.list.KVLLPop;
import com.logicbus.kvalue.xscript.list.KVLLPush;
import com.logicbus.kvalue.xscript.list.KVLLength;
import com.logicbus.kvalue.xscript.list.KVLRPop;
import com.logicbus.kvalue.xscript.list.KVLRPush;
import com.logicbus.kvalue.xscript.list.KVLRem;
import com.logicbus.kvalue.xscript.list.KVLSet;
import com.logicbus.kvalue.xscript.list.KVLTrim;
import com.logicbus.kvalue.xscript.set.KVSAdd;
import com.logicbus.kvalue.xscript.set.KVSCard;
import com.logicbus.kvalue.xscript.set.KVSDiff;
import com.logicbus.kvalue.xscript.set.KVSDiffStore;
import com.logicbus.kvalue.xscript.set.KVSInter;
import com.logicbus.kvalue.xscript.set.KVSInterStore;
import com.logicbus.kvalue.xscript.set.KVSIsMember;
import com.logicbus.kvalue.xscript.set.KVSMembers;
import com.logicbus.kvalue.xscript.set.KVSPop;
import com.logicbus.kvalue.xscript.set.KVSRandMember;
import com.logicbus.kvalue.xscript.set.KVSRem;
import com.logicbus.kvalue.xscript.set.KVSUnion;
import com.logicbus.kvalue.xscript.set.KVSUnionStore;
import com.logicbus.kvalue.xscript.string.KVAppend;
import com.logicbus.kvalue.xscript.string.KVGet;
import com.logicbus.kvalue.xscript.string.KVSet;
import com.logicbus.kvalue.xscript.string.KVSetRange;
import com.logicbus.kvalue.xscript.string.KVStrlen;
import com.logicbus.kvalue.xscript.zset.KVZAdd;
import com.logicbus.kvalue.xscript.zset.KVZCard;
import com.logicbus.kvalue.xscript.zset.KVZCount;
import com.logicbus.kvalue.xscript.zset.KVZCountByLex;
import com.logicbus.kvalue.xscript.zset.KVZIncrby;
import com.logicbus.kvalue.xscript.zset.KVZRange;
import com.logicbus.kvalue.xscript.zset.KVZRangeByLex;
import com.logicbus.kvalue.xscript.zset.KVZRangeByScore;
import com.logicbus.kvalue.xscript.zset.KVZRank;
import com.logicbus.kvalue.xscript.zset.KVZRem;
import com.logicbus.kvalue.xscript.zset.KVZRemoveByLex;
import com.logicbus.kvalue.xscript.zset.KVZRemrangeByRank;
import com.logicbus.kvalue.xscript.zset.KVZRemrangeByScore;
import com.logicbus.kvalue.xscript.zset.KVZScore;

/**
 * KeyValue相关xscript插件的namespace
 * 
 * @author duanyy
 *
 */
public class KVNS extends Segment {

	public KVNS(String tag, Logiclet p) {
		super(tag, p);
		registerModule("kv-schema",KVSchema.class);
		registerModule("kv-table",KVTable.class);
		registerModule("kv-row", KVRow.class);
		registerModule("kv-del",KVDel.class);
		registerModule("kv-exist",KVExist.class);
		registerModule("kv-ttl",KVTTL.class);
		
		/**
		 * string
		 */
		registerModule("kv-set",KVSet.class);
		registerModule("kv-get",KVGet.class);
		registerModule("kv-append",KVAppend.class);
		registerModule("kv-strlen",KVStrlen.class);
		registerModule("kv-setrange",KVSetRange.class);
		
		/**
		 * hash
		 */
		registerModule("kv-hdel",KVHDel.class);
		registerModule("kv-hkeys",KVHKeys.class);
		registerModule("kv-hvals",KVHValues.class);
		registerModule("kv-hget",KVHGet.class);
		registerModule("kv-hgetall",KVHGetAll.class);
		registerModule("kv-hset",KVHSet.class);
		registerModule("kv-hmget",KVHMGet.class);
		registerModule("kv-hmset",KVHMSet.class);
		registerModule("kv-hexist",KVHExist.class);
		
		/**
		 * list
		 */
		registerModule("kv-llen",KVLLength.class);
		registerModule("kv-litems",KVLItems.class);
		registerModule("kv-llpush",KVLLPush.class);
		registerModule("kv-lrpush",KVLRPush.class);
		registerModule("kv-lget",KVLGet.class);
		registerModule("kv-lset",KVLSet.class);
		registerModule("kv-linsert",KVLInsert.class);
		registerModule("kv-llpop",KVLLPop.class);
		registerModule("kv-lrpop",KVLRPop.class);
		registerModule("kv-lrem",KVLRem.class);
		registerModule("kv-ltrim",KVLTrim.class);
		
		/**
		 * sortedSet
		 */
		registerModule("kv-zadd",KVZAdd.class);
		registerModule("kv-zcard",KVZCard.class);
		registerModule("kv-zcount",KVZCount.class);
		registerModule("kv-zincrby",KVZIncrby.class);
		registerModule("kv-zrange",KVZRange.class);
		registerModule("kv-zrangebyscore",KVZRangeByScore.class);
		registerModule("kv-zrank",KVZRank.class);
		registerModule("kv-zrem",KVZRem.class);
		registerModule("kv-zremrangebyrank",KVZRemrangeByRank.class);
		registerModule("kv-zremrangebyscore",KVZRemrangeByScore.class);
		registerModule("kv-zscore",KVZScore.class);
		registerModule("kv-zremrangebylex",KVZRemoveByLex.class);
		registerModule("kv-zrangebylex",KVZRangeByLex.class);
		registerModule("kv-zlexcount",KVZCountByLex.class);
		
		/**
		 * set
		 */
		registerModule("kv-sadd",KVSAdd.class);
		registerModule("kv-scard",KVSCard.class);
		registerModule("kv-sdiff",KVSDiff.class);
		registerModule("kv-sdiffstore",KVSDiffStore.class);
		registerModule("kv-sinter",KVSInter.class);
		registerModule("kv-sinterstore",KVSInterStore.class);
		registerModule("kv-sismember",KVSIsMember.class);
		registerModule("kv-smembers",KVSMembers.class);
		registerModule("kv-spop",KVSPop.class);
		registerModule("kv-srandmember",KVSRandMember.class);
		registerModule("kv-srem",KVSRem.class);		
		registerModule("kv-sunion",KVSUnion.class);
		registerModule("kv-sunionstore",KVSUnionStore.class);

	}

}
