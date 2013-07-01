package io.jedis;

//java client to access redis
import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;

/*
 * http://javaforu.blogspot.co.uk/2012/03/redis-and-jedis-delightfully-simple-and.html
 */
public class myJedis {
	public static void main(String[] args) throws InterruptedException {
		// See http://redis.io/commands#

		Jedis j = new Jedis("localhost", 6379);
		j.connect();
		System.out.println("Connected");

		try {
			behaveAsMap(j);

			behaveAsMapOfMaps(j);

			behaveAsMapOfLists(j);

			behaveAsMapOfSets(j);

			behaveAsMapOfSortedMaps(j);
		} finally {
			j.disconnect();
			System.out.println("\nDisconnected");
		}
	}

	public static void behaveAsMap(Jedis j) {
		System.out.println("\n[Map<String, String>]");

		// Store integer as string.
		j.set("burgers_sold", "0");

		for (int i = 0; i < 224; i++) {
			j.incrBy("burgers_sold", 1);
		}

		print("get(\"burgers_sold\")", j.get("burgers_sold"));
	}

	public static void behaveAsMapOfMaps(Jedis j) {
		System.out.println("\n[Map<String, Map<String, String>>]");

		j.hset("user_1", "name", "doh!");
		j.hset("user_1", "age", "39");

		j.pipelined(new PipelineBlock() {
			@Override
			public void execute() {
				hset("user_2", "name", "burns");
				hset("user_2", "age", "129");
				hset("user_2", "net_worth", "high");

				hset("user_3", "name", "marge");
				hset("user_3", "age", "38");
				hset("user_3", "kids", "2");
			}
		});

		print("hgetAll(\"user_1\")", j.hgetAll("user_1"));
		print("hgetAll(\"user_2\")", j.hgetAll("user_2"));
		print("hgetAll(\"user_3\")", j.hgetAll("user_3"));
	}

	public static void behaveAsMapOfLists(Jedis j) {
		System.out.println("\n[Map<String, List<String>>]");

		j.rpush("sequence_steps", "placed");
		j.rpush("sequence_steps", "fulfilled");
		j.rpush("sequence_steps", "shipped");
		j.rpush("sequence_steps", "confirmed");
		j.rpush("sequence_steps", "audited");

		long len = j.llen("sequence_steps");
		print("llen(\"sequence_steps\")", len);
		print("lrange(\"sequence_steps\", 0, len - 1)",
				j.lrange("sequence_steps", 0, len - 1));
		print("lrange(\"sequence_steps\", 0, Integer.MAX_VALUE)",
				j.lrange("sequence_steps", 0, Integer.MAX_VALUE));

		for (;;) {
			String value = j.lpop("sequence_steps");
			if (value == null) {
				break;
			}

			print("lpop(\"sequence_steps\")", value);
		}
	}

	public static void behaveAsMapOfSets(Jedis j) throws InterruptedException {
		System.out.println("\n[Map<String, Set<String>>]");

		j.sadd("bay area", "south bay");
		j.sadd("bay area", "east bay");
		j.sadd("bay area", "foster city");
		print("smembers(\"bay area\"): ", j.smembers("bay area"));

		j.sadd("city", "SF");
		j.sadd("city", "oakland");
		j.sadd("city", "daly city");
		j.sadd("city", "foster city");
		print("smembers(\"city\"): ", j.smembers("city"));

		print("sunion(\"bay area\", \"city\"): ", j.sunion("bay area", "city"));
		print("sinter(\"bay area\", \"city\"): ", j.sinter("bay area", "city"));

		System.out.println("Set expire(\"city\", 5)...sleeping...");
		j.expire("city", 5);
		Thread.sleep(6500);
		print("smembers(\"city\"): ", j.smembers("city"));
	}

	public static void behaveAsMapOfSortedMaps(Jedis j)
			throws InterruptedException {
		System.out
				.println("\n[Map<String, SortedMap<Double, LinkedHashSet<String>>>]");

		j.zadd("math_class", 99.8, "lisa");
		j.zadd("math_class", 100.0, "martin");
		j.zadd("math_class", 35.5, "milhouse");
		j.zadd("math_class", 35.5, "bart");
		j.zadd("math_class", 10, "nelson");
		j.zadd("math_class", 10, "ralph");
		j.zadd("math_class", 2.0, "homer");
		j.zadd("math_class", 18, "barney");

		print("zcard(\"math_class\")", j.zcard("math_class"));
		print("zcount(\"math_class\", 0, Double.MAX_VALUE)",
				j.zcount("math_class", 0, Double.MAX_VALUE));

		print("zrange(\"math_class\", 0, Integer.MAX_VALUE)",
				j.zrange("math_class", 0, Integer.MAX_VALUE));
		print("zrangeByScore(\"math_class\", 0, Double.MAX_VALUE)",
				j.zrangeByScore("math_class", 0, Double.MAX_VALUE));

		print("zrevrange(\"math_class\", 0, Integer.MAX_VALUE)",
				j.zrevrange("math_class", 0, Integer.MAX_VALUE));
		print("zrevrangeByScore(\"math_class\", Double.MAX_VALUE, 0)",
				j.zrevrangeByScore("math_class", Double.MAX_VALUE, 0));

		print("zscore(\"math_class\", \"bart\")",
				j.zscore("math_class", "bart"));
		print("zscore(\"math_class\", \"milhouse\")",
				j.zscore("math_class", "milhouse"));

		print("zrank(\"math_class\", \"bart\")", j.zrank("math_class", "bart"));
		print("zrank(\"math_class\", \"milhouse\")",
				j.zrank("math_class", "milhouse"));

		print("zrevrank(\"math_class\", \"bart\")",
				j.zrevrank("math_class", "bart"));
		print("zrevrank(\"math_class\", \"milhouse\")",
				j.zrevrank("math_class", "milhouse"));

		System.out.println("Set expire(\"math_class\", 4)...sleeping...");
		j.expire("math_class", 4);
		Thread.sleep(6500);
		print("zrange(\"math_class\", 0, Integer.MAX_VALUE)",
				j.zrange("math_class", 0, Integer.MAX_VALUE));
	}

	static void print(String command, Object result) {
		System.out.printf("%-55s: %s%n", command, result);
	}
}