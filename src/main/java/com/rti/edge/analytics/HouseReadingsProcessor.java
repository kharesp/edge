package com.rti.edge.analytics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.edgent.function.BiConsumer;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.apache.edgent.topology.plumbing.PlumbingStreams;
import org.apache.edgent.window.Partition;
import org.apache.edgent.window.Policies;
import org.apache.edgent.window.Window;
import org.apache.edgent.window.Windows;

import com.rti.edge.types.houseAvg.HouseAvg;
import com.rti.edge.types.householdAvg.HouseholdAvg;
import com.rti.edge.types.reading.Reading;

@SuppressWarnings("serial")
public class HouseReadingsProcessor implements Consumer<Consumer<HouseAvg>> {
	private Topology topology;
	private TStream<Reading> houseStream;
	private int window_length_secs;
	private Window<HouseholdAvg, Integer, ? extends List<HouseholdAvg>> window;
	private Consumer<HouseAvg> eventEmitter;
	private Map<Integer,Float> hh_currSumLoad;
	private Map<Integer,Float> hh_expSumLoad;
	private Map<Integer,Float> hh_currAvgLoad;


	public HouseReadingsProcessor(Topology topology, TStream<Reading> houseStream, int window_length_secs) {
		this.topology = topology;
		this.houseStream = houseStream;
		hh_currSumLoad=new HashMap<Integer,Float>();
		hh_expSumLoad=new HashMap<Integer,Float>();
		hh_currAvgLoad=new HashMap<Integer,Float>();
		this.window_length_secs = window_length_secs;
		this.window=Windows.window(Policies.alwaysInsert(), //insertion policy
				contentsPolicy(), //contents policy
				(partition)->{}, // eviction policy
				triggerPolicy(), // trigger policy
				h -> h.household_id, //key function
				()-> new LinkedList<HouseholdAvg>()); //list supplier
		window.registerPartitionProcessor(processWindowUpdate());
	}

	public TStream<HouseAvg> process() {
		List<TStream<Reading>> houseHold_streams = houseStream.
				split(20, r -> r.household_id); // assuming a max of 20 households	

		List<TStream<HouseholdAvg>> houseHold_avgLoad_streams = new LinkedList<TStream<HouseholdAvg>>();
		for (TStream<Reading> houseHold_stream : houseHold_streams) {
			TStream<HouseholdAvg> houseHold_avgLoad_stream = new HouseHoldReadingsProcessor(topology, houseHold_stream,
					window_length_secs).process();
			PlumbingStreams.isolate(houseHold_avgLoad_stream, 10);
			houseHold_avgLoad_streams.add(houseHold_avgLoad_stream);
		}
		TStream<HouseholdAvg> all_houseHold_avgs= houseHold_avgLoad_streams.get(0)
				.union(new HashSet<TStream<HouseholdAvg>>(houseHold_avgLoad_streams));
		all_houseHold_avgs.sink(h -> window.insert(h));
		return topology.events(this);

	}
	private BiConsumer<Partition<HouseholdAvg, Integer, LinkedList<HouseholdAvg>>, HouseholdAvg> contentsPolicy() {
		return (partition, tuple) -> {
			
			int household_id= partition.getKey();

			if(!hh_currSumLoad.containsKey(household_id)){
				hh_currSumLoad.put(household_id, (float) 0);
				hh_expSumLoad.put(household_id, (float)0);
				hh_currAvgLoad.put(household_id, (float) 0);
			}

			LinkedList<HouseholdAvg> contents = partition.getContents();

			int expired_count = 0;
			float expired_sum_loadValues=0;
			for (HouseholdAvg r : contents) {
				if (tuple.ts - r.ts > window_length_secs) {
					expired_count++;
					expired_sum_loadValues+=r.average;
				} else {
					break;
				}
			}
			hh_expSumLoad.put(household_id, expired_sum_loadValues);

			for (int i = 0; i < expired_count; i++)
				contents.removeFirst();

		};
	}
	
	private BiConsumer<Partition<HouseholdAvg,Integer,LinkedList<HouseholdAvg>>,HouseholdAvg> triggerPolicy(){
		return (partition, tuple) ->{
			int household_id=tuple.household_id;
			float updated_sumLoad= hh_currSumLoad.get(household_id)+tuple.average- hh_expSumLoad.get(household_id);
			updated_sumLoad=(updated_sumLoad < 0)? updated_sumLoad*-1: updated_sumLoad;
			hh_currSumLoad.put(household_id, updated_sumLoad);
			hh_currAvgLoad.put(household_id, updated_sumLoad/partition.getContents().size());
			partition.process();
		};
	}
	private BiConsumer<List<HouseholdAvg>, Integer> processWindowUpdate(){
		return (contents,key)->{
			HouseholdAvg curr_update=contents.get(contents.size()-1);
			float tot_houseLoad=0;
			for(float hh_load:hh_currAvgLoad.values()){
				tot_houseLoad+=hh_load;
			}
			HouseAvg houseAvg= new HouseAvg(curr_update.ts, //update ts
					tot_houseLoad, //total house load
					true, //load 
					window_length_secs, //window length
					curr_update.house_id); //house_id
			//System.out.print(houseAvg);
			eventEmitter.accept(houseAvg);
			
		};
	}

	@Override
	public void accept(Consumer<HouseAvg> eventEmitter) {
		this.eventEmitter=eventEmitter;
	}

}
