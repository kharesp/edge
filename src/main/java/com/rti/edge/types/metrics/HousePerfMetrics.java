

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package com.rti.edge.types.metrics;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class HousePerfMetrics   implements Copyable, Serializable{

    public int ts= 0;
    public float throughput_per_sec= 0;
    public float avg_latency_mili= 0;
    public float min_latency_mili= 0;
    public float max_latency_mili= 0;
    public int house_id= 0;

    public HousePerfMetrics() {

    }
    public HousePerfMetrics(int ts,
    		float throughput_per_sec,
    		float avg_latency_mili,
    		float min_latency_mili,
    		float max_latency_mili,
    		int house_id){
    	this.ts=ts;
    	this.throughput_per_sec= throughput_per_sec;
    	this.avg_latency_mili=avg_latency_mili;
    	this.min_latency_mili=min_latency_mili;
    	this.max_latency_mili=max_latency_mili;
    	this.house_id=house_id;
    }
    public HousePerfMetrics (HousePerfMetrics other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        HousePerfMetrics self;
        self = new  HousePerfMetrics();
        self.clear();
        return self;

    }

    public void clear() {

        ts= 0;
        throughput_per_sec= 0;
        avg_latency_mili= 0;
        min_latency_mili= 0;
        max_latency_mili= 0;
        house_id= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        HousePerfMetrics otherObj = (HousePerfMetrics)o;

        if(ts != otherObj.ts) {
            return false;
        }
        if(throughput_per_sec != otherObj.throughput_per_sec) {
            return false;
        }
        if(avg_latency_mili != otherObj.avg_latency_mili) {
            return false;
        }
        if(min_latency_mili != otherObj.min_latency_mili) {
            return false;
        }
        if(max_latency_mili != otherObj.max_latency_mili) {
            return false;
        }
        if(house_id != otherObj.house_id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)ts;
        __result += (int)throughput_per_sec;
        __result += (int)avg_latency_mili;
        __result += (int)min_latency_mili;
        __result += (int)max_latency_mili;
        __result += (int)house_id;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>HousePerfMetricsTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        HousePerfMetrics typedSrc = (HousePerfMetrics) src;
        HousePerfMetrics typedDst = this;

        typedDst.ts = typedSrc.ts;
        typedDst.throughput_per_sec = typedSrc.throughput_per_sec;
        typedDst.avg_latency_mili = typedSrc.avg_latency_mili;
        typedDst.min_latency_mili = typedSrc.min_latency_mili;
        typedDst.max_latency_mili = typedSrc.max_latency_mili;
        typedDst.house_id = typedSrc.house_id;

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("ts: ").append(ts).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("throughput_per_sec: ").append(throughput_per_sec).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("avg_latency_mili: ").append(avg_latency_mili).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("min_latency_mili: ").append(min_latency_mili).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("max_latency_mili: ").append(max_latency_mili).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("house_id: ").append(house_id).append("\n");  

        return strBuffer.toString();
    }

}
