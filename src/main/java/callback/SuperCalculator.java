package callback;

/**
 * Created by Administrator on 2017/05/21.
 */
public class SuperCalculator {
    public void add(int a,int b,doJob customer){
        int result = a + b;
        customer.fillBlank(a,b,result);
    }
}
