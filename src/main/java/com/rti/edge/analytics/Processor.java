package com.rti.edge.analytics;

import java.util.Arrays;

import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.edge.analytics.PerformanceMetrics.Metrics;
import com.rti.edge.edgent.DDSPublisherConnector;
import com.rti.edge.edgent.DDSSubscriber;
import com.rti.edge.edgent.DDSSubscriberConnector;
import com.rti.edge.types.houseAvg.HouseAvg;
import com.rti.edge.types.houseAvg.HouseAvgTypeSupport;
import com.rti.edge.types.metrics.HousePerfMetrics;
import com.rti.edge.types.metrics.HousePerfMetricsTypeSupport;
import com.rti.edge.types.reading.Reading;
import com.rti.edge.types.reading.ReadingTypeSupport;

public class Processor {
	private DirectProvider dp;
	private Topology topology;
	private int houseId;
	private PerformanceMetrics stats;
	private DDSPublisherConnector<HouseAvg> resultsPub = null;
	private	DDSPublisherConnector<HousePerfMetrics> metricsPub= null;

	public Processor(int houseId){
		this.houseId=houseId;
		stats=new PerformanceMetrics();
		dp=new DirectProvider();
		topology= dp.newTopology();
		try {
			resultsPub=new DDSPublisherConnector<HouseAvg>("HouseAvg", HouseAvgTypeSupport.get_instance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			metricsPub=new DDSPublisherConnector<HousePerfMetrics>("Metrics", HousePerfMetricsTypeSupport.get_instance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]){
		if(args.length<2){
			System.out.println("args: houseId window_length_secs");
			return;
		}
		int houseId=Integer.parseInt(args[0]);
		Integer window_length_secs= Integer.parseInt(args[1]);

		System.out.format("Started Processor for house id:%d.\nWill compute average load over %d seconds window size.\n",
				houseId,window_length_secs);

		new Processor(houseId).process(window_length_secs);
	}

	public void process(int window_length_secs)
	{
		TStream<Reading> houseStream= houseStream()
				.filter(r -> r.property)
				.peek(r -> {
					if(stats.count==0)
						stats.recordStartTime();
					stats.recordInputTime();
				});
		TStream<HouseAvg> result_stream=new HouseReadingsProcessor(topology,houseStream,window_length_secs).
				process();
		result_stream.sink(hAvg -> {
			stats.computeMetrics();
			System.out.println(hAvg);
			resultsPub.accept(hAvg);
			if(stats.count%10==0){
				Metrics update=stats.getUpdate();
				HousePerfMetrics perfStat= new HousePerfMetrics(hAvg.ts,
				update.throughput_per_sec,update.avg_latency_mili,
				update.min_latency_mili,update.max_latency_mili,hAvg.house_id);
				metricsPub.accept(perfStat);
			}
			
		});
		dp.submit(topology);
	}
	
	private TStream<Reading> houseStream(){
		DDSSubscriberConnector<Reading> readingSubscriber = null;
		try {
			readingSubscriber = new DDSSubscriberConnector<Reading>("Readings", ReadingTypeSupport.get_instance(),
					"house_id=%0",new StringSeq(Arrays.asList(Integer.toString(houseId))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		DDSSubscriber<Reading> consumer = new DDSSubscriber<Reading>(topology, readingSubscriber);
		return consumer.events();
	}


}
