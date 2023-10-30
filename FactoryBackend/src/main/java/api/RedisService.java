package api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import model.Product;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

//CRUD operacije sa proizvodima
public class RedisService {
	
	public void generateProducts()
	{
		List<Product> genericProducts = new ArrayList<Product>();
		genericProducts.add(new Product("Snickers", 1, 10, "A delicious chocolate bar."));
		genericProducts.add(new Product("Skittles", 2, 5, "Colorful fruit-flavored candies."));
		genericProducts.add(new Product("M&M's", 3, 8, "Colorful chocolate button-shaped candies."));
		genericProducts.add(new Product("Kit Kat", 4, 7, "Crunchy wafer bars coated in chocolate."));
		genericProducts.add(new Product("Twix", 5, 9, "Buttery cookie bars topped with caramel and coated in chocolate."));
		genericProducts.add(new Product("Reese's Peanut Butter Cups", 6, 6, "Chocolate cups filled with creamy peanut butter."));
		genericProducts.add(new Product("Milky Way", 7, 5, "Creamy nougat and caramel coated in milk chocolate."));
		genericProducts.add(new Product("Almond Joy", 8, 5, "Milk chocolate-covered coconut with almonds."));
		genericProducts.add(new Product("Hershey's", 9, 4, "Classic milk chocolate bar."));
		genericProducts.add(new Product("Nestle Crunch", 10, 6, "Crispy rice and chocolate."));
		
		//JedisPool pool = new JedisPool("localhost", 6379);
		JedisPool pool = new JedisPool("localhost", 6379);
		
		
		try(Jedis jedis = pool.getResource()){
			
			for(Product p : genericProducts)
			{
				jedis.lpush("myList", p.toString());
			}
			
		}
		
		pool.close();
		
	}
	
	public Set<String> getAllKeys() {	//get all keys of products in redis
		
		Jedis jedis = new Jedis("localhost", 6379);
	    Set<String> keys = new HashSet<>();
	    String cursor = "0";
	    ScanParams scanParams = new ScanParams().count(100);

	    do {
	        try {
	            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
	            cursor = scanResult.getCursor();
	            keys.addAll(scanResult.getResult());
	        } catch (JedisException e) {
	            // Handle any exceptions
	        }
	    } while (!cursor.equals("0"));
	    
	    jedis.close();

	    return keys;
	}
	
	
	public JsonObject getProducts()
	{
		JedisPool pool = new JedisPool("localhost", 6379);
		Set<String> keys = getAllKeys();
				
		ArrayList<String> products = new ArrayList<String>();
		
		try(Jedis jedis = pool.getResource())
		{					
			products=(ArrayList<String>) jedis.lrange("myList", 0, -1);			
		}
		
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		for(String product: products)
		{
			jsonArrayBuilder.add(product);
		}
		
		JsonObject jsonObject = Json.createObjectBuilder()
									.add("Products", jsonArrayBuilder)
									.build();
								
		pool.close();
		
		return jsonObject;
	}
	
	
	public JsonObject getProductById(int id)
	{
		JedisPool pool = new JedisPool("localhost", 6379);
				
		ArrayList<String> products = new ArrayList<String>();
		
		try(Jedis jedis = pool.getResource())
		{					
			products=(ArrayList<String>) jedis.lrange("myList", 0, -1);			
		}
		
		
		List<Product> productList = new ArrayList<>();
        for (String productString : products) {
            String[] parts = productString.split("\\|");
            if (parts.length == 4) {
                Product product = new Product(parts[0],Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),parts[3]);                
                productList.add(product);
            }
        }
		
        for (Product p : productList) {
			if (p.getId() == id) {
				JsonObject jsonObject;
				pool.close();
				return jsonObject = Json.createObjectBuilder()
		                .add("name", p.getName())
		                .add("id", p.getId())
		                .add("volume", p.getVolume())
		                .add("description", p.getDescription())
		                .build();
				
			}
		}
		
		
		
								
		pool.close();
		
		return null;
		
		
	}
	
	public boolean add(Product addedProduct)
	{
		JedisPool pool = new JedisPool("localhost", 6379);
		
		
		ArrayList<String> products = new ArrayList<String>();
		
		try(Jedis jedis = pool.getResource())
		{					
		
			
			products=(ArrayList<String>) jedis.lrange("myList", 0, -1);			
		
		
		
		List<Product> productList = new ArrayList<>();
        for (String productString : products) {
            String[] parts = productString.split("\\|");
            if (parts.length == 4) {
                Product product = new Product(parts[0],Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),parts[3]);                
                productList.add(product);
            }
        }
        
        for (Product p : productList) {
        	if(p.getName().equals(addedProduct.getName()))
        	{
        		remove(p.getId());
        		int volume = p.getVolume();
        		volume++;
        		p.setVolume(volume);
        		jedis.rpush("myList", p.toString());
        		
        		
        		pool.close();
        		return true;
        		
        	}
        }
        
        
        jedis.rpush("myList", addedProduct.toString());
        
        
        
        
		}
		
		pool.close();
        
		
		return true;
		
	}
	

	public boolean update(Product product, int id)
	{
		System.out.println(product.toString());
		
		if(remove(id))
		{
			
			JedisPool pool = new JedisPool("localhost", 6379);
						
			try(Jedis jedis = pool.getResource())
			{
				jedis.rpush("myList", product.toString());
				
			}
			
			pool.close();
			
			return true;
		}
		else
		{
			return false;
		}
	
	}
	
	
	public boolean remove(int id)
	{
		JedisPool pool = new JedisPool("localhost", 6379);
		ArrayList<String> products = new ArrayList<String>();
		String productToDelete = null;
		
		
		try(Jedis jedis = pool.getResource())
		{					
			products=(ArrayList<String>) jedis.lrange("myList", 0, -1);			
		
		
		
		List<Product> productList = new ArrayList<>();
        for (String productString : products) {
            String[] parts = productString.split("\\|");
            if (parts.length == 4) {
                Product product = new Product(parts[0],Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),parts[3]);                
                productList.add(product);
            }
        }
		
        for (Product p : productList) {
			if (p.getId() == id) {
				productToDelete = p.toString();
			}
		}
			
        	if(productToDelete!=null)
        	{
        		pool.close();
        		jedis.lrem("myList", 1, productToDelete);
        		return true;
        	}
        	else
        	{
        		pool.close();
        		return false;
        	}
        
        
		}
		
	}
	
}
