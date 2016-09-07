package com.rti.edge.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import com.rti.edge.types.reading.Reading;
import rx.Observable;
import rx.observables.GroupedObservable;

public class ParseData {

	public static Observable<String> readFile(String file){
		return Observable.create(observer ->{
			try {
				long count=0;
				long startTime= System.nanoTime();
				BufferedReader br = new BufferedReader(new FileReader(file));
				for(String line=br.readLine();line!=null;line=br.readLine()){
					count+=1;
					observer.onNext(line);
					if(count%10000==0){
						System.out.format("Read %d lines\n",count);
					}
				}
				long endTime=System.nanoTime();
				System.out.println("Finished Reading File. Total lines read:"+count); 
				System.out.format("Took %f seconds or %f mins or %f hours to read the file\n",
						(endTime-startTime)/(1000000000.0),(endTime-startTime)/(1000000000*60.0),(endTime-startTime)/(1000000000*60.0*60.0));
				observer.onCompleted();
				br.close();

			} catch (IOException e) {
				observer.onError(e);
			}
		});
	}
	public static void splitHouseholdData(String filePath,String rootDir) {
		new File(rootDir).mkdirs();
		
		Observable<String> lines = readFile(filePath);
		Observable<GroupedObservable<Integer,Reading>> houseStreams= lines
				.map(l -> new Reading(l.split(",")))
				.groupBy(r -> r.house_id);

		houseStreams.subscribe(houseStream -> {
				Integer houseId= houseStream.getKey();
				new File(String.format(rootDir+"/%d",houseId)).mkdirs();
				houseStream.groupBy(r -> r.household_id).subscribe(houseHoldStream -> {
					Integer houseHoldId = houseHoldStream.getKey();
					try{
						FileOutputStream fos =new FileOutputStream(new File(String.
								format(rootDir+"/%d/%d.csv",houseId,houseHoldId)),true);
						houseHoldStream.subscribe(r -> {
							try {
								fos.write(r.toString().getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
						},e-> System.out.println(e),() ->{
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
					    });
					}catch(IOException e){
						System.out.println(e);
					}
				});
			});
	}
	public static void main(String args[]){
		splitHouseholdData("/vagrant/DEBS2014/sample10.csv","sample10");
	}

}

