package com.xytest;

import com.xytest.Xls.ImportXls;
import com.xytest.utils.Color;
import com.xytest.utils.JDBCUtils;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Created by zhangmg on 2017/4/25.
 */
public class MyTest {
    @Test
    public void Atest() {
        double a = 1.6;
        double v = a * 100000000;
        System.out.println(v);

    }

    @Test
    public void BTest() throws Exception {
        String filepath = "C:\\Users\\zhangmg\\Desktop\\河南省01.xls";
        File fileDir = new File(filepath);
        Connection conn = JDBCUtils.getConnection();

        ImportXls.insertFinance(fileDir, conn);
    }

    @Test
    public void CTest() {
        Color red = Color.BLACK;
        System.out.println(red);
        int index = Color.RED.getIndex();
        System.out.println(index);
    }

    @Test
    public void DTest() {
        SpelExpressionParser parser = new SpelExpressionParser();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("map", map);
        Integer value = parser.parseExpression("#map['a'] = 3").getValue(context, Integer.class);
        Assert.isTrue(2 == value, "有问题");
    }
}
