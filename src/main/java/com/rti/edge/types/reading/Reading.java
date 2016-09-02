package com.rti.edge.types.reading;

import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class Reading   implements Copyable, Serializable{
	private static final long serialVersionUID = 1L;
	public long id= 0;
    public int ts= 0;
    public float value= 0;
    public boolean property= false;
    public int plug_id= 0;
    public int household_id= 0;
    public int house_id= 0;

    public Reading() {

    }
    public Reading(String[] values){
    	id= Long.parseUnsignedLong(values[0]);
        ts= Integer.parseUnsignedInt(values[1]);
        value= Float.parseFloat(values[2]);
        property= Integer.parseInt(values[3])==1? true: false;
        plug_id= Integer.parseUnsignedInt(values[4]);
        household_id= Integer.parseUnsignedInt(values[5]);
        house_id= Integer.parseUnsignedInt(values[6]);
    }
    public Reading (Reading other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Reading self;
        self = new  Reading();
        self.clear();
        return self;

    }

    public void clear() {

        id= 0;
        ts= 0;
        value= 0;
        property= false;
        plug_id= 0;
        household_id= 0;
        house_id= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Reading otherObj = (Reading)o;

        if(id != otherObj.id) {
            return false;
        }
        if(ts != otherObj.ts) {
            return false;
        }
        if(value != otherObj.value) {
            return false;
        }
        if(property != otherObj.property) {
            return false;
        }
        if(plug_id != otherObj.plug_id) {
            return false;
        }
        if(household_id != otherObj.household_id) {
            return false;
        }
        if(house_id != otherObj.house_id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)id;
        __result += (int)ts;
        __result += (int)value;
        __result += (property == true)?1:0;
        __result += (int)plug_id;
        __result += (int)household_id;
        __result += (int)house_id;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ReadingTypeSupport</code>
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

        Reading typedSrc = (Reading) src;
        Reading typedDst = this;

        typedDst.id = typedSrc.id;
        typedDst.ts = typedSrc.ts;
        typedDst.value = typedSrc.value;
        typedDst.property = typedSrc.property;
        typedDst.plug_id = typedSrc.plug_id;
        typedDst.household_id = typedSrc.household_id;
        typedDst.house_id = typedSrc.house_id;

        return this;
    }

    public String toString(){
        return String.format("%s,%s,%.3f,%d,%s,%s,%s\n",Long.toUnsignedString(id),
				Integer.toUnsignedString(ts),
				value,
				(property?1:0),
				Integer.toUnsignedString(plug_id),
				Integer.toUnsignedString(household_id),
				Integer.toUnsignedString(house_id));
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("id: ").append(id).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("ts: ").append(ts).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("value: ").append(value).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("property: ").append(property).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("plug_id: ").append(plug_id).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("household_id: ").append(household_id).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("house_id: ").append(house_id).append("\n");  

        return strBuffer.toString();
    }

}
