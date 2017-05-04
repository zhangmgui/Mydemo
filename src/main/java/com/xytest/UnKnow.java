/*
package com.example;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

*/
/**
 * Created by zhangmg on 2017/4/21.
 *//*

public class UnKnow {
    ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

    FutureTask<List<Employee>> allEmployeeTask = null;
    if (dto.getDrawId() == 0 && dto.getExecutorId() == -1) {//未指定项目及执行人
        allEmployeeTask = new FutureTask<List<Employee>>(new Callable<List<Employee>>() {
            @Override
            public List<Employee> call() throws Exception {
//                    List<Employee> employees = null;
//                    if (null!=dto.getExecDeptId()) {
                return oaService.queryEmployeeByDeptIncludeChildren(dto.getExecDeptId() == -1 ? 11128 : dto.getExecDeptId());//默认查询IT中心人员
//                    } else {
//                        employees = oaService.queryEmployeeByDept(dto.getExecDeptId());
//                    }
            }
        });
        Constants.exec.submit(allEmployeeTask);//提交线程
}
*/
