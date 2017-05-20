package com.xytest.cons;

import com.sun.jmx.snmp.tasks.Task;
import com.xytest.domain.ArticleContent;
import com.xytest.service.CacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangmg on 2017/4/21.
 */

@RestController
@RequestMapping("/test")
public class TestController {
    /* @Autowired
     @Qualifier("cacheSbean")*/
    @Resource(name = "cacheSbean")
    private CacheService cacheService;
    @Resource(name = "cs")
    private CacheService cs;


    @GetMapping("exit/{id}/{name}")
    public void testExit(@PathVariable Integer id, @PathVariable String name) throws InterruptedException {
        Integer sum = 1000;
        ArrayList<Integer> integers = new ArrayList<>();
        Thread vice = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (integers) {
                    for (Integer i = 0; i < sum; i++) {
                        integers.add(i);
                        try {
                            integers.notify();
                            integers.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(i + "副线程");
                    }
                }

            }
        }, "副线程");
        vice.start();


        synchronized (integers) {
            for (Integer i = 0; i < sum; i++) {

                int size = integers.size();
                if (size < 20) {
                    integers.notifyAll();
                    integers.wait();
                }
                System.out.println("size:" + size + "主线程");
            }
            System.out.println("结束");
        }
    }


    @GetMapping("bTest")
    public void bTest() {
        List<ArticleContent> list = cacheService.getList();
        System.out.println(list);
        cs.say();
        SpelExpressionParser parser = new SpelExpressionParser();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map", map);
        Integer value = parser.parseExpression("#map['a'] = 3").getValue(context, Integer.class);
        Assert.isTrue(3 == value, "没问题");
    }

    @Bean("cacheSbean")
    public CacheService getCacheService() {
        return new CacheService();
    }

    @GetMapping("threadPoolTest")
    public void Ctest(){
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (Object.class){
                    for (int i = 0; i < 200; i++) {
                        System.out.println(i);
                    }
                }
            }
        });
      //  executorService.shutdown();
        executorService.submit(new Task() {
            @Override
            public void cancel() {
                System.out.println("结束");
            }

            @Override
            public void run() {
                System.out.println("run");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable");
            }
        });

    }
}


