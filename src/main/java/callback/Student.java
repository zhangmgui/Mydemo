package callback;

/**
 * Created by Administrator on 2017/05/21.
 */
public class Student {
    private String name = null;
    public Student(String name){
        this.name=name;
    }
    public void setName(String name){
        this.name=name;
    }
    public class doHomeWork implements doJob{
        @Override
        public void fillBlank(int a, int b, int result) {
            System.out.println(name + "求助小红" + result);
        }
    }
    public void callHelp(int a,int b){
        new SuperCalculator().add(a,b,new doHomeWork());
    }
}
