package callback;


/**
 * Created by Administrator on 2017/05/21.
 */
public class CallApp {
    public static void  main(String[] args){
        Seller 老奶奶 = new Seller("老奶奶");
        老奶奶.callHelp(3,8);
        Student 小明 = new Student("小明");
        小明.callHelp(7,9);
    }
}
