package org.ninetyone.resource;

import org.ninetyone.model.PersonEntity;
import org.ninetyone.model.TopScorersResponse;
import org.ninetyone.repository.PersonRepository;
import org.ninetyone.service.CSVProcessorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/topscorers")
@Produces(MediaType.APPLICATION_JSON)
public class TopScorersResource {
    
    private static final Logger LOG = Logger.getLogger(TopScorersResource.class);
    
    @Inject
    CSVProcessorService csvProcessorService;
    
    @Inject
    PersonRepository personRepository;
    
    @GET
    @Path("/sample")
    public TopScorersResponse getSampleResponse() {
        TopScorersResponse sample = new TopScorersResponse(78, 
            java.util.Arrays.asList("George Of The Jungle Sipho", "Lolo"), 5);
        return sample;
    }
    
    @GET
    @Path("/all")
    public Response getAllPersons() {
        try {
            List<PersonEntity> persons = csvProcessorService.getAllPersons();
            return Response.ok(persons).build();
        } catch (Exception e) {
            LOG.error("Error getting all persons", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("/database/top")
    public Response getTopScorersFromDatabase() {
        try {
            List<PersonEntity> topScorers = csvProcessorService.getTopScorersFromDatabase();
            if (topScorers.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No top scorers found in database\"}")
                        .build();
            }
            
            List<String> names = topScorers.stream()
                    .map(PersonEntity::getFullName)
                    .collect(Collectors.toList());
            
            int topScore = topScorers.isEmpty() ? 0 : topScorers.get(0).getScore();
            long totalCount = personRepository.count();
            int totalEntries = (int) totalCount;
            TopScorersResponse response = new TopScorersResponse(topScore, names, totalEntries);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            LOG.error("Error getting top scorers from database", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("/database/count")
    public Response getDatabaseCount() {
        try {
            long count = personRepository.count();
            return Response.ok("{\"count\": " + count + "}").build();
        } catch (Exception e) {
            LOG.error("Error getting count", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadCSV(
            @RestForm("fileData") InputStream fileData,
            @RestForm("fileName") String fileName) {
        try {
            LOG.info("Received file: " + fileName);
            
            if (fileData == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"No file data received\"}")
                        .build();
            }
            
            TopScorersResponse response = csvProcessorService.processCSV(fileData);
            
            if (response.getTopScorers().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"No valid data found in the CSV file.\"}")
                        .build();
            }
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.error("Error processing CSV file", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error processing file: " + errorMessage + "\"}")
                    .build();
        }
    }
    
    @POST
    @Path("/process-file")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response processFileFromPath(String filePath) {
        try {
            filePath = filePath.trim();
            if (filePath.startsWith("\"") && filePath.endsWith("\"")) {
                filePath = filePath.substring(1, filePath.length() - 1);
            }
            filePath = filePath.replace('\\', '/');
            
            LOG.info("Processing CSV from file path: " + filePath);
            
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (!path.toFile().exists()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"File not found: " + filePath + "\"}")
                        .build();
            }
            
            TopScorersResponse response = csvProcessorService.processCSVFromFile(filePath);
            
            if (response.getTopScorers().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"No valid data found in the CSV file.\"}")
                        .build();
            }
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOG.error("Error processing CSV file", e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error processing file: " + errorMessage + "\"}")
                    .build();
        }
    }
    
    @DELETE
    @Path("/database/clear")
    public Response clearDatabase() {
        try {
            personRepository.clearAll();
            return Response.ok("{\"message\": \"Database cleared successfully\"}").build();
        } catch (Exception e) {
            LOG.error("Error clearing database", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}