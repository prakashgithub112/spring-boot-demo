package com.example.demo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java.service.SearchCriteria;

@RestController
public class SearchController {

    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/search")
    public ResponseEntity getSearchResultViaAjax(
            @Valid @RequestBody SearchCriteria search, Errors errors) {

    	System.out.println("Inside getSearchResultViaAjax method");
        AjaxResponseBody result = new AjaxResponseBody();

        //If error, just return a 400 bad request, along with the error message
        /*if (true) {

            result.setMsg(errors.getAllErrors()
                        .stream().map(x -> x.getDefaultMessage())
                        .collect(Collectors.joining(",")));

            return Response.ok(result).build();//status(0).build();

        }*/
        
        if (errors.hasErrors()) {

            result.setMsg(errors.getAllErrors()
                        .stream().map(x -> x.getDefaultMessage())
                        .collect(Collectors.joining(",")));

            return ResponseEntity.badRequest().body(result);

        }


        List<User> users = userService.findByUserNameOrEmail(search.getUsername());
        //caching to store the results in cache to be accessed faster than before
        //Response.	builder	=	Response.ok(users,	"application/xml");
        Response.ResponseBuilder response = Response.ok(users).type(MediaType.APPLICATION_XML);
        /*Date	date	=	Calendar.getInstance(TimeZone.getTimeZone("GMT")).set(2010,	5,	15,	16,	0);	
        re.expires(date);						
        //return	builder.build();
*/        
        Date expirationDate = new Date(System.currentTimeMillis() + 3000);
        response.expires(expirationDate);
        /*
        return response.build();*/
       // List<User> users = userService.findByUserNameOrEmail(search.getUsername());
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        return ResponseEntity.ok(result);

    }

}