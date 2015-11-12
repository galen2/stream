import java.util.UUID;

import org.junit.Test;


public class base {
	@Test
	public void run(){
//        Long messageId = Long.parseLong(UUID.randomUUID().toString());
		System.out.println(UUID.randomUUID().toString());

	}
	
	@Test
	public void one(){
		  long retryInitialDelayMs = 1L;
	      long retryDelayMaxMs=500L;
		double retryDelayMultiplier = 0.3;
		int retryNum = 4;
		
		 double delayMultiplier = Math.pow(retryDelayMultiplier, retryNum - 1);
         double delay = retryInitialDelayMs * delayMultiplier;
         Long maxLong = Long.MAX_VALUE;
         long delayThisRetryMs = delay >= maxLong.doubleValue()
                                 ?  maxLong
                                 : (long) delay;
          long min = Math.min(delayThisRetryMs, retryDelayMaxMs);
          System.out.println(min);
          
	}

}
