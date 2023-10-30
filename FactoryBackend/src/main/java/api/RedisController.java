package api;

import java.util.ArrayList;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Product;

@Path("/products")
public class RedisController {
	RedisService service;
	
	public RedisController() {
		service = new RedisService();
	}
	
	@POST
	@Path("/generate")
	@Produces(MediaType.APPLICATION_JSON)
	public void generate()
	{
		
		service.generateProducts();
	
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get()
	{
		JsonObject products = service.getProducts();
		
		if(products!=null)
		{
			return Response.status(200).entity(products).build();
		}else
		{
			return Response.status(404).build();
		}
				
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id")int id)
	{
		JsonObject product = service.getProductById(id);
		
		if(product!=null)
		{
			return Response.status(200).entity(product).build();
		}else
		{
			return Response.status(404).build();
		}
					
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Product product) {
		if (service.add(product)) {
			return Response.status(200).entity(product).build();
		} else {
			return Response.status(500).entity("Greska pri dodavanju").build();
		}
	}
	
	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") int id) {
		if (service.remove(id)) {
			return Response.status(200).build();
		}
		return Response.status(404).entity("Ne postoji takav proizvod").build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response edit(Product product, @PathParam("id") int id) {
		if (service.update(product, id)) {
			return Response.status(200).entity(product).build();
		}
		return Response.status(404).build();
	}
	
	
	
}