package com.bigdata.ligy;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author ToKill a MockingBird
 * @date 2020/01/03
 */
public class WordCount {
    public static void main(String[] args) {
        wordCount();
        System.out.println("运行结束");
    }

    public static void wordCount() {
        SparkConf conf = new SparkConf().setAppName("wordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        //读取静态文件
        JavaRDD<String> input = sc.textFile("input-2020/word.txt");
        //切分为单词
        JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) {
                List<String> strings = Arrays.asList(s.split(" "));
                return strings.iterator();
            }
        });

        //转换为键值对并计数
        JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer x, Integer y) throws Exception {
                return x + y;
            }
        });
        System.out.println(counts);
        counts.saveAsTextFile("output-2020/" + System.currentTimeMillis());
    }
}
