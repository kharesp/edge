package com.rti.edge.analytics;

public class PerformanceMetrics {
	private static final long NS_TO_MILI=1000*1000L;
	private static final long NS_TO_SEC=1000*1000*1000L;
	public long count=0;
	private long start_ts=0;
	private long sample_input_ts=0;
	private long sample_output_ts=0;
	private long sum_latency=0;
	private long max_latency=Long.MIN_VALUE;
	private long min_latency=Long.MAX_VALUE;
	

	public void recordStartTime(){
		start_ts=System.nanoTime();
	}
	public void recordInputTime(){
		count++;
		sample_input_ts=System.nanoTime();
	}
	public void computeMetrics(){
		sample_output_ts=System.nanoTime();
		long latency=sample_output_ts-sample_input_ts;
		sum_latency+=latency;
		if(latency > max_latency)
			max_latency=latency;
		if(latency < min_latency)
			min_latency=latency;
	}
	public Metrics getUpdate(){
		long curr_ts=System.nanoTime();
		long elapsed_time=curr_ts-start_ts;
		float throughput_per_sec= (float)(count*NS_TO_SEC)/elapsed_time;
		float avg_latency_mili= (float)sum_latency/(count*NS_TO_MILI);
		float max_latency_mili=(float)max_latency/NS_TO_MILI;
		float min_latency_mili=(float)min_latency/NS_TO_MILI;

		return new Metrics(throughput_per_sec,
				avg_latency_mili,
				min_latency_mili,
				max_latency_mili);
				
		/*
		System.out.format("Throughput per sec:%f\n"
				+ "avg latency in milisec:%f\n"
				+ "max latency in milisec:%f\n"
				+ "min latency in milisec:%f\n\n",
				throughput_per_sec,avg_latency_mili,max_latency_mili,min_latency_mili);
		*/
	}


	public class Metrics{
		public float throughput_per_sec;
		public float avg_latency_mili;
		public float min_latency_mili;
		public float max_latency_mili;

		public Metrics(){}
		public Metrics(float throughput_per_sec,
				float avg_latency_mili,
				float min_latency_mili,
				float max_latency_mili){
			this.throughput_per_sec= throughput_per_sec;
			this.avg_latency_mili=avg_latency_mili;
			this.min_latency_mili=min_latency_mili;
			this.max_latency_mili=max_latency_mili;
		}
	}

}
