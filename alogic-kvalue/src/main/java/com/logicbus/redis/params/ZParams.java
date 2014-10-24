package com.logicbus.redis.params;

import com.logicbus.redis.client.Params;
import com.logicbus.redis.util.SafeEncoder;

public class ZParams extends Params {

	protected final byte[] KW_WEIGHTS = SafeEncoder.encode("WEIGHTS");

	protected final byte[] KW_AGGREGATE = SafeEncoder.encode("AGGREGATE");

	public enum Aggregate {
		SUM, MIN, MAX;

		public final byte[] raw;

		Aggregate() {
			raw = SafeEncoder.encode(name());
		}
	}

	public ZParams weights(final int... weights) {
		add(KW_WEIGHTS);
		for (final int weight : weights) {
			add(SafeEncoder.encode(weight));
		}

		return this;
	}

	public ZParams aggregate(final Aggregate aggregate) {
		add(KW_AGGREGATE);
		add(aggregate.raw);
		return this;
	}
}
