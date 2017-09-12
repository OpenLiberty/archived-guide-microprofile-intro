package io.openliberty.guides.microprofile;

import static org.junit.Assert.*;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.Test;

public class TestInventoryService {
	
	@Test
	public void testAllInOrder(){
		
		testEmptyInventory();
		testAddingASystem();
		testRetrievingSystemProperties();
		testAddingUnknownSystem();
	}
	   
	public void testEmptyInventory(){
	    String port = "9081";
	    String war = "LibertyProject";
	    String url = "http://localhost:" + port + "/" + war + "/";
	 
	    Client client = ClientBuilder.newClient();
	    client.register(JsrJsonpProvider.class);
	
	    Response response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();
	
	    assertEquals("Incorrect response code from " + url, 200, response.getStatus());
	
	    JsonObject obj = response.readEntity(JsonObject.class);
	    
	    assertEquals("The systems should be empty", obj.getInt("total"),0);
	    
	    response.close();
	}
    
	
	public void testAddingASystem(){
	    String port = "9081";
	    String war = "LibertyProject";
	    String url = "http://localhost:" + port + "/" + war + "/";
	 
	    Client client = ClientBuilder.newClient();
	    client.register(JsrJsonpProvider.class);

	    Response response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();

	    assertEquals("Incorrect response code from " + url, 200, response.getStatus());

	    JsonObject obj = response.readEntity(JsonObject.class);
		
	    InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            
            client.target(url + "Inventory/systems/" + hostname).request(MediaType.APPLICATION_JSON).get();
            
            response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();
            
    	    obj = response.readEntity(JsonObject.class);
    	    
    	    assertEquals("The property total should be equal 2 since the hostname (client) and your localhost visited the link",
    	            2,
    	            obj.getInt("total"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
 
	}
	
	
	public void testRetrievingSystemProperties(){
	    String port = "9081";
	    String war = "LibertyProject";
	    String url = "http://localhost:" + port + "/" + war + "/";
	 
	    Client client = ClientBuilder.newClient();
	    client.register(JsrJsonpProvider.class);

	    Response response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();

	    assertEquals("Incorrect response code from " + url, 200, response.getStatus());

	    JsonObject obj = response.readEntity(JsonObject.class);
	    
	    port = "9080";
	    url = "http://localhost:" + port + "/" + war + "/";
	    
	    client.target(url + "System/properties").request(MediaType.APPLICATION_JSON).get();
	    
	    port = "9081";
	    url = "http://localhost:" + port + "/" + war + "/";
	    
	    response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();
	    
	    obj = response.readEntity(JsonObject.class);
	    
	    assertEquals("The system property for the local and remote JVM should match",
	                 System.getProperty("os.name"),
	                 obj.getJsonObject("systems").getJsonObject("localhost").getString("os.name"));
	}
	
	public void testAddingUnknownSystem(){
	    String port = "9081";
	    String war = "LibertyProject";
	    String url = "http://localhost:" + port + "/" + war + "/";
	 
	    Client client = ClientBuilder.newClient();
	    client.register(JsrJsonpProvider.class);

	    Response response = client.target(url + "Inventory/systems").request(MediaType.APPLICATION_JSON).get();

	    assertEquals("Incorrect response code from " + url, 200, response.getStatus());
	    
	    response = client.target(url + "Inventory/systems/testingMicroporfile").request(MediaType.APPLICATION_JSON).get();
	    
	    JsonObject obj = response.readEntity(JsonObject.class);
	    
	    assertEquals("The hostname is not valid and the service should raise an error",
	            "Unknown hostname", obj.getString("ERROR"));
	}
}
