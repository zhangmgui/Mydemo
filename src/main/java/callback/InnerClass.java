package callback;

import com.xytest.domain.CSADBase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/05/28.
 */
public class InnerClass {
    private static Integer myId = 10;

    public static void main(String[] args) throws ParseException {
        testRegex();
        t3((a, b) -> {
            System.out.println(a);
            System.out.println(myId--);
            System.out.println(myId);
            System.out.println(b);
        });
        ArrayList<CSADBase> list = new ArrayList<CSADBase>() {
            {
                add(new CSADBase(1, 2016, "hubei"));
                add(new CSADBase(2, 2017, "henan"));
                add(new CSADBase(3, 2013, "hebei"));
                add(new CSADBase(4, 2014, "jiangxi"));
                add(new CSADBase(5, 2015, "fujian"));
            }
        };
        Integer[] ints = {1, 2, 3, 4};
        String str = "hello";
        FanXin fanXin = new FanXin();
        //String[] see = fanXin.see(ints, str);
        Object[] objects = {str};

        for (Object object : objects) {
            System.out.println((String) object);
        }
        //  list.stream().collect(Collectors.averagingDouble())
        System.out.println(StringUtils.isNotEmpty("3"));
        String dateStr = "2017-05-29";

        list.sort(new Comparator<CSADBase>() {
            @Override
            public int compare(CSADBase o1, CSADBase o2) {
                return o1.getYear_month().compareTo(o2.getYear_month());
            }
        });
        list.forEach(p -> {
            System.out.println(p.getYear_month());
        });
        Date date = DateUtils.addMonths(DateUtils.parseDate(dateStr, "yyyy-MM-dd"), 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf.format(date));

      /* Object[] hubeis = list.stream().filter(p -> {
            if (p.getCarserial().equals("hubei")) {
               return true;
            }
            if (p.getID() >3){
                return true;
            }
            return false;
        }).collect()*/

     /*   boolean hubeis = list.stream().anyMatch(p -> {
            if (p.getCarserial().equals("hubei")) {
                return true;
            }
            return false;
        });*/
      /*  System.out.println(hubeis);*/
      /*  for (Object hubei : hubeis) {

            System.out.println(((CSADBase) hubei).getCarserial());
        }*/
    }

    public static aint t() {
        for (int i = 0; i < 10; i++) {
            return new aint() {
                @Override
                public void test1() {
                    int b = myId;
                    b--;
                    System.out.println(b);
                }
            };
        }
        return null;
    }

    public static void t2() {
        int c = 66;
        class Inner {
            public int age;

            private int getAge() {
                this.age = c;
                System.out.println(c);
                return this.age;
            }
        }
        new Inner().getAge();
    }

    public static void t3(myinterface aint) {
        System.out.println("ahead");
        int a = 10, d = 3;
        String b = "hello";
        aint.test1(a, b);
    }

    class cyInner {
        public int age = myId;

        public int getAge() {
            myId--;
            return this.age;
        }
    }

    interface aint {
        public void test1();
    }

    interface myinterface {
        public void test1(int a, String b);
    }

    static class FanXin {
        public <T, R> R[] see(T[] a, R b) {
            ArrayList<R> rs = new ArrayList<>();
            R[] rs1 = (R[]) new Object[]{b};

            for (T r : a) {
                System.out.println(r.toString());
            }
            return rs1;
        }
    }

    public static void testRegex() {
       String regex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        String pattern = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher("771515987@qq.comdgsgs");
        boolean b = matcher.find();
        String group = matcher.group("c");

        System.out.println(group);
        System.out.println(matcher.matches());


    }

}
