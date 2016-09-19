

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package com.rti.edge.types.houseAvg;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class HouseAvg   implements Copyable, Serializable{

    public int ts= 0;
    public float average= 0;
    public boolean property= false;
    public int window_size= 0;
    public int house_id= 0;

    public HouseAvg() {

    }
    public HouseAvg(int ts,float average,boolean property,int window_size,int house_id){
    	this.ts=ts;
    	this.average=average;
    	this.property=property;
    	this.window_size=window_size;
    	this.house_id=house_id;
    }
    public HouseAvg (HouseAvg other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        HouseAvg self;
        self = new  HouseAvg();
        self.clear();
        return self;

    }

    public void clear() {

        ts= 0;
        average= 0;
        property= false;
        window_size= 0;
        house_id= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        HouseAvg otherObj = (HouseAvg)o;

        if(ts != otherObj.ts) {
            return false;
        }
        if(average != otherObj.average) {
            return false;
        }
        if(property != otherObj.property) {
            return false;
        }
        if(window_size != otherObj.window_size) {
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
        __result += (int)average;
        __result += (property == true)?1:0;
        __result += (int)window_size;
        __result += (int)house_id;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>HouseAvgTypeSupport</code>
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

        HouseAvg typedSrc = (HouseAvg) src;
        HouseAvg typedDst = this;

        typedDst.ts = typedSrc.ts;
        typedDst.average = typedSrc.average;
        typedDst.property = typedSrc.property;
        typedDst.window_size = typedSrc.window_size;
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
        strBuffer.append("average: ").append(average).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("property: ").append(property).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("window_size: ").append(window_size).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("house_id: ").append(house_id).append("\n");  

        return strBuffer.toString();
    }

}
