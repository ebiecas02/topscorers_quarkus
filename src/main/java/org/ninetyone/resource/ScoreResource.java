package org.ninetyone.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ninetyone.model.ScoreRequest;
import org.ninetyone.model.ScoreResponse;
import org.ninetyone.model.TopScorersResponse;
import org.ninetyone.service.ScoreService;
import org.jboss.logging.Logger;

@Path("/api/scores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScoreResource {
    
    private static final Logger LOG = Logger.getLogger(ScoreResource.class);
    
    @Inject
    ScoreService scoreService;
    
    /**
     * POST endpoint to add new scores
     * Example: POST /api/scores
     * Body: {"firstName": "John", "lastName": "Doe", "score": 85}
     */
    @POST
    public Response addScore(@Valid ScoreRequest request) {
        try {
            LOG.info("Received request to add score");
            
            // Validate that at least firstName is provided
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"First name is required\"}")
                        .build();
            }
            
            // Validate score range
            if (request.getScore() == null || request.getScore() < 0 || request.getScore() > 100) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Score must be between 0 and 100\"}")
                        .build();
            }
            
            ScoreResponse response = scoreService.addScore(request);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
            
        } catch (Exception e) {
            LOG.error("Error adding score", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET endpoint to retrieve score of a specific person
     * Example: GET /api/scores/person?firstName=John&lastName=Doe
     * Or: GET /api/scores/person?fullName=John%20Doe
     */
    @GET
    @Path("/person")
    public Response getPersonScore(
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("fullName") String fullName) {
        try {
            LOG.info("Getting score for person");
            
            // Validate input
            if (fullName != null && !fullName.trim().isEmpty()) {
                // Search by full name
                ScoreResponse response = scoreService.getScoreByFullName(fullName);
                if (response.getMessage() != null && response.getMessage().equals("Person not found")) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(response)
                            .build();
                }
                return Response.ok(response).build();
                
            } else if (firstName != null && !firstName.trim().isEmpty()) {
                // Search by first name and optional last name
                String lastNameParam = (lastName != null) ? lastName : "";
                ScoreResponse response = scoreService.getScoreByFirstNameLastName(firstName, lastNameParam);
                if (response.getMessage() != null && response.getMessage().equals("Person not found")) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity(response)
                            .build();
                }
                return Response.ok(response).build();
                
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Please provide either 'fullName' or 'firstName' (and optional 'lastName')\"}")
                        .build();
            }
            
        } catch (Exception e) {
            LOG.error("Error getting person score", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET endpoint to retrieve top score(s)
     * Example: GET /api/scores/top
     */
    @GET
    @Path("/top")
    public Response getTopScorers() {
        try {
            LOG.info("Getting top scorers");
            
            TopScorersResponse response = scoreService.getTopScorers();
            
            if (response.getTopScorers().isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No scores found in database\"}")
                        .build();
            }
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.error("Error getting top scorers", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}