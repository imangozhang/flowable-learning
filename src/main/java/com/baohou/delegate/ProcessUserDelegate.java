package com.baohou.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ProcessUserDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // 获取当前遍历的用户
        String currentUser = (String) execution.getVariable("user");
        System.out.println("Processing user: " + currentUser);

        // 可以在此处添加业务逻辑（如调用外部服务、更新数据库等）

        // 设置流程变量（可选）
        execution.setVariable("processed_" + currentUser, true);
    }
}
