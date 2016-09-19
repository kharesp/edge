package com.rti.edge.analytics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.apache.edgent.window.Window;
import org.apache.edgent.window.Windows;
import org.apache.edgent.window.Partition;
import org.apache.edgent.window.Policies;
import org.apache.edgent.function.BiConsumer;
import org.apache.edgent.function.Consumer;

import com.rti.edge.types.householdAvg.HouseholdAvg;
import com.rti.edge.types.reading.Reading;

@SuppressWarnings("serial")
public class HouseHoldReadingsProcessor implements Consumer<Consumer<HouseholdAvg>> {
	private Topology topology;
	private TStream<Reading> houseHold_stream;
	private int window_length_secs;
	private Consumer<HouseholdAvg> eventEmitter;
	private Window<Reading, Integer, ? extends List<Reading>> window;
	private Map<Integer,Float> plug_currSumLoad;
	private Map<Integer,Float> plug_expSumLoad;
	private Map<Integer,Float> plug_currAvgLoad;

	public HouseHoldReadingsProcessor(Topology topology,TStream<Reading> houseHold_stream,int window_length_secs){
		this.topology=topology;
		this.houseHold_stream=houseHold_stream;
		this.window_length_secs=window_length_secs;
		plug_currSumLoad=new HashMap<Integer,Float>();
		plug_expSumLoad=new HashMap<Integer,Float>();
		plug_currAvgLoad=new HashMap<Integer,Float>();
		
		window=Windows.window(Policies.alwaysInsert(), //insertion policy
				contentsPolicy(), //contents policy
				(partition)->{}, // eviction policy
				triggerPolicy(), // trigger policy
				tuple -> tuple.plug_id, //key function
				()-> new LinkedList<Reading>()); //list supplier
		window.registerPartitionProcessor(processWindowUpdate());
	}
	
	public TStream<HouseholdAvg> process(){
		houseHold_stream.sink(r -> window.insert(r));
		return topology.events(this);

	}
	
	private BiConsumer<Partition<Reading, Integer, LinkedList<Reading>>, Reading> contentsPolicy() {
		return (partition, tuple) -> {
			
			int plug_id= partition.getKey();

			if(!plug_currSumLoad.containsKey(plug_id)){
				plug_currSumLoad.put(plug_id, (float) 0);
				plug_expSumLoad.put(plug_id, (float)0);
				plug_currAvgLoad.put(plug_id, (float) 0);
			}

			LinkedList<Reading> contents = partition.getContents();

			int expired_count = 0;
			float expired_sum_loadValues=0;
			for (Reading r : contents) {
				if (tuple.ts - r.ts > window_length_secs) {
					expired_count++;
					expired_sum_loadValues+=r.value;
				} else {
					break;
				}
			}
			plug_expSumLoad.put(plug_id, expired_sum_loadValues);

			for (int i = 0; i < expired_count; i++)
				contents.removeFirst();

		};
	}
	
	private BiConsumer<Partition<Reading,Integer,LinkedList<Reading>>,Reading> triggerPolicy(){
		return (partition, tuple) ->{
			int plug_id=tuple.plug_id;
			LinkedList<Reading> contents=partition.getContents();
			float updated_sumLoad= plug_currSumLoad.get(plug_id)+tuple.value - plug_expSumLoad.get(plug_id);
			updated_sumLoad=(updated_sumLoad < 0)? updated_sumLoad*-1: updated_sumLoad;
			plug_currSumLoad.put(plug_id, updated_sumLoad);
			plug_currAvgLoad.put(plug_id, updated_sumLoad/contents.size());
			partition.process();
			
		};
	}

	private BiConsumer<List<Reading>, Integer> processWindowUpdate(){
		return (contents,key)->{
			Reading curr_update=contents.get(contents.size()-1);
			float avg_houseHoldLoad=0;
			for(float plug_load:plug_currAvgLoad.values()){
				avg_houseHoldLoad+=plug_load;
			}
			System.out.format("update ts:%d, house_hold:%d load:%f\n",
					curr_update.ts,curr_update.household_id, avg_houseHoldLoad);
			eventEmitter.accept(new HouseholdAvg(curr_update.ts,
					avg_houseHoldLoad,
					true,
					window_length_secs,
					curr_update.household_id,
					curr_update.house_id
					));
			
		};
	}

	@Override
	public void accept(Consumer<HouseholdAvg> eventEmitter) {
		this.eventEmitter=eventEmitter;
	}
}
