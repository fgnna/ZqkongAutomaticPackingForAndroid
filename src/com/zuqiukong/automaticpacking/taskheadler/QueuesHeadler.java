package com.zuqiukong.automaticpacking.taskheadler;

import java.util.LinkedList;
import java.util.List;

/**
 * 打包任务队列执行器
 * @author jie
 *
 */
public class QueuesHeadler  implements Runnable
{
	public static List<ChannelTask> queue = new LinkedList<ChannelTask>();
    /**
     * 假如 参数o 为任务
     * @param o
     */
    public static void addTask (ChannelTask t){
        synchronized (queue) {
        	queue.add(t); //添加任务
        	queue.notifyAll();//激活该队列对应的执行线程，全部Run起来
        }
    }
	
    @Override
    public void run() {
        while(true){
            synchronized (queue) {
                while(queue.isEmpty()){ //
                    try {
                    	queue.wait(); //队列为空时，使线程处于等待状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("wait...");
                }
                ChannelTask t= queue.remove(0); //得到第一个
                t.doWrok(); //执行该任务
                System.out.println("end");
            }
        }
    }


}
