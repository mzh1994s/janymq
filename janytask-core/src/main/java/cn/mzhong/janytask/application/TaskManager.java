package cn.mzhong.janytask.application;

import cn.mzhong.janytask.worker.TaskExecutor;

import java.util.Set;

/**
 * 任务管理器
 *
 * @since 2.0.0
 */
public interface TaskManager extends TaskComponent {

    /**
     * 收集当前任务管理器中的任务执行者，在任务管理器初始化之后调用
     *
     * @return 返回所有任务管理器初始化之后的任务执行者，没有将返回空的列表
     * @see #init()
     */
    Set<TaskExecutor> getTaskExecutors();
}
