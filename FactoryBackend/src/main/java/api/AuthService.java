package api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.json.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import model.User;

public class AuthService {
	
	public boolean add(User user)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		
		
		try {
			FileWriter jsonFile = new FileWriter("users.json", true);
			objectMapper.writeValue(jsonFile, user);
			
			
			jsonFile.close();
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	
	public JsonObject getUsers()
	{
		System.out.println(new File(".").getAbsolutePath().toString());
		
		ArrayList<User> users = new ArrayList<>();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();	
		try (BufferedReader reader = new BufferedReader(new FileReader("users.json"))) {
		    String line = reader.readLine();
		    
		    Pattern pattern = Pattern.compile("\\{.*?\\}");
	        Matcher matcher = pattern.matcher(line);
		    
	        while (matcher.find()) {
		        
	            String singleUser = matcher.group();
	            Gson gson = new Gson();
	            User u = gson.fromJson(singleUser, User.class);

	            users.add(u);
	            	
	        }      
	        reader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		JsonArrayBuilder usersArrayBuilder = Json.createArrayBuilder();

        for (User user : users) {
            JsonObjectBuilder userJsonBuilder = Json.createObjectBuilder()
                    .add("companyName", user.companyName)
                    .add("address", user.address)
                    .add("phoneNumber", user.phoneNumber)
                    .add("username", user.username)
                    .add("password", user.password)
                    .add("activated", user.activated)
                    .add("blocked", user.blocked);

            usersArrayBuilder.add(userJsonBuilder);
        }

        JsonArray usersArray = usersArrayBuilder.build();

        JsonObject data = Json.createObjectBuilder()
                .add("Users", usersArray)
                .build();
/*

		JSONArray usersArray = new JSONArray();

        for (User user : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("companyName", user.companyName);
            userJson.put("address", user.address);
            userJson.put("phoneNumber", user.phoneNumber);
            userJson.put("username", user.username);
            userJson.put("password", user.password);
            userJson.put("activated", user.activated);
            userJson.put("blocked", user.blocked);
            usersArray.put(userJson);
        }
        JSONObject data = new JSONObject();
        data.put("Users", usersArray); 
*/
		return data;
		
	}
	
	public boolean activate(User user) throws FileNotFoundException
	{
		ArrayList<User> users = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader("users.json"))) {
		    String line = reader.readLine();
		    String file="";
		    Pattern pattern = Pattern.compile("\\{.*?\\}");
	        Matcher matcher = pattern.matcher(line);
		    
	        while (matcher.find()) {
	        {
	            String singleUser = matcher.group();
	            Gson gson = new Gson();
	            User u = gson.fromJson(singleUser, User.class);

	            users.add(u);
	            	
	        }
	        reader.close();
	        }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		new PrintWriter("users.json").close();
		
		
		
		
		for(User u: users)
		{
			if(u.getUsername().equals(user.getUsername()))
			{
				u.setActivated(true);
			}
			
			add(u);
		}
		
		
		
		return true;
	}
 
	
	public boolean block(User user) throws FileNotFoundException
	{
		ArrayList<User> users = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader("users.json"))) {
		    String line = reader.readLine();
		    String file="";
		    Pattern pattern = Pattern.compile("\\{.*?\\}");
	        Matcher matcher = pattern.matcher(line);
		    
	        while (matcher.find()) {
	        {
	            String singleUser = matcher.group();
	            Gson gson = new Gson();
	            User u = gson.fromJson(singleUser, User.class);

	            users.add(u);
	            	
	        }
	        reader.close();
	        }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		new PrintWriter("users.json").close();
		
		
		
		
		for(User u: users)
		{
			if(u.getUsername().equals(user.getUsername()))
			{
				u.setBlocked(true);
			}
			
			add(u);
		}
		
		
		
		return true;
	}
	
	
	public boolean delete(User user) throws FileNotFoundException
	{
		ArrayList<User> users = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader("users.json"))) {
		    String line = reader.readLine();
		    String file="";
		    Pattern pattern = Pattern.compile("\\{.*?\\}");
	        Matcher matcher = pattern.matcher(line);
		    
	        while (matcher.find()) {
	        {
	            String singleUser = matcher.group();
	            Gson gson = new Gson();
	            User u = gson.fromJson(singleUser, User.class);

	            users.add(u);
	            	
	        }
	        reader.close();
	        }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		new PrintWriter("users.json").close();
		
		
		
		
		for(User u: users)
		{
			if(!u.getUsername().equals(user.getUsername()))
			{
				add(u);
			}
		}
		
		
		
		return true;
	}
	
	
	
}
