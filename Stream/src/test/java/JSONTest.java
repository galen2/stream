import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JSONTest {
	   private JSONArray jsonArray = null;
	    private JSONObject jsonObject = null;
	    
	    private Student bean = null;
	    
	    @Before
	    public void init() {
	        jsonArray = new JSONArray(); 
	        jsonObject = new JSONObject(); 
	        bean = new Student();
	        bean.setAddress("address");
	        bean.setEmail("email");
	        bean.setId(1);
	        bean.setName("haha");
	    }
	    
	    @After
	    public void destory() {
	        jsonArray = null;
	        jsonObject = null;
	        bean = null;
	        System.gc();
	    }
	    
	    public final void fail(String string) {
	        System.out.println(string);
	    }
	    
	    public final void failRed(String string) {
	        System.err.println(string);
	    }
	    
	    @Test
	    public void writeMap2JSON() {
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("A", "wefwef");
	        
	        Map<String, Object> map2 = new HashMap<String, Object>();
	        map2.put("22", "33333");
	        
	        map.put("23r23", map2);
	     /*   bean.setName("jack");
	        map.put("B", bean);
	        map.put("name", "json");
	        map.put("bool", Boolean.TRUE);
	        map.put("int", new Integer(1));
	        map.put("arr", new String[] { "a", "b" });
	        map.put("func", "function(i){ return this.arr[i]; }"); */
	        fail("==============Java Map >>> JSON Object==================");
	        int[] intArray = new int[]{1,4,5};
	        JSONArray jsonArray1 = JSONArray.fromObject(intArray);
	        System.out.println("int[] intArray");
	        System.out.println(jsonArray1);
	        
	        
	        fail(JSONObject.fromObject(map).toString());
	        String string = JSONObject.fromObject(map).toString();
	        fail("==============Java Map >>> JSON Array ==================");
//	        fail(JSONArray..fromObject(map).toString());
	        fail("==============Java Map >>> JSON Object==================");
//	        fail(JSONSerializer.toJSON(map).toString());
	    }
	    
	    private String json = "{\"address\":\"chian\",\"birthday\":{\"birthday\":\"2010-11-22\"},"+
	            "\"email\":\"email@123.com\",\"id\":22,\"name\":\"tom\"}";

	    @Test
	    public void toMap(){
	    	
	    	JSONObject fromObject = JSONObject.fromObject(json);
	    	 Map mapBean = (Map)JSONObject.toBean(fromObject, Map.class);
	    	 System.out.println(mapBean);
	    	 
	    }
	   class Student {
	    private int id;
	    private String name;
	    private String email;
	    private String address;
	 
	    //setter、getter
	    public String toString() {
	        return this.name + "#" + this.id + "#" + this.address + "#" + this.email;
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}
	    
	}
	
	
}
