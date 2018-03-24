package com.redhat.iot;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class SoftwareSensorTaskScheduler extends ThreadPoolTaskScheduler implements AssetCallback {
	
	private final Map<Object, ScheduledFuture<?>> scheduledTasks =
	        new IdentityHashMap<>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SoftwareSensorTaskScheduler.class);

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
		
		ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);

		if(task instanceof AssetRunner) {
			AssetRunner assetRunner = (AssetRunner) task;
			scheduledTasks.put(assetRunner, future);
		}
		
		return future;
	}
	
	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
		
		ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);

		if(task instanceof AssetRunner) {
			AssetRunner assetRunner = (AssetRunner) task;
			scheduledTasks.put(assetRunner, future);
		}
		
		return future;
	}

	@Override
	public void assetTaskComplete(AssetRunner assetRunner) {
		ScheduledFuture<?> future = scheduledTasks.get(assetRunner);
		
		int assetCompletedIterations =  assetRunner.getCompletedIterations();
		int iterations = assetRunner.getAsset().getIterations();
		
		if(iterations > 0) {
			if(assetCompletedIterations >= iterations) {
				if(future != null) {
					LOGGER.info("Cancelling Task '{}' After {} Iterations", assetRunner.getAsset().getName(), assetCompletedIterations);
					future.cancel(false);
				}	
			}
		}
	}

}
