package api;

import java.io.FileNotFoundException;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import model.Product;
import model.User;

@Path("/auth")
public class AuthController {
	
	AuthService service;

	public AuthController()
	{
		service=new AuthService();
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(User user) {
		
		if (service.add(user)) {
			return Response.status(200).entity(user).build();
		} else {
			return Response.status(500).entity("Greska pri dodavanju").build();
		}
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get()
	{
		JsonObject users = service.getUsers();
		
		if(users!=null)
		{
			return Response.status(200).entity(users).build();
		}else
		{
			return Response.status(404).build();
		}
				
	}
	
	@PUT
	@Path("/activate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activate(User user) throws FileNotFoundException
	{
		if(service.activate(user)) {
			return Response.status(200).entity(user).build();
		}
		else
		{
			return Response.status(500).entity("Greska pri aktivaciji").build();
		}
	}
	
	
	@PUT
	@Path("/block")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response block(User user) throws FileNotFoundException
	{
		if(service.block(user)) {
			return Response.status(200).entity(user).build();
		}
		else
		{
			return Response.status(500).entity("Greska pri aktivaciji").build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(User user) throws FileNotFoundException
	{
		if(service.delete(user)) {
			return Response.status(200).entity(user).build();
		}
		else
		{
			return Response.status(500).entity("Greska pri aktivaciji").build();
		}
	}
	
	
	
}
