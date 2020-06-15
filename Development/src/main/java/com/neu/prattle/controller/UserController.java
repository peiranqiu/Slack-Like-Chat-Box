package com.neu.prattle.controller;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.model.User;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/***
 * A Resource class responsible for handling CRUD operations on User objects.
 *
 */

@Path(value = "/user")
public final class UserController {

  private UserService userService = UserServiceImpl.getInstance();
  private static final UserController userController = new UserController();

  /**
   * Singleton instance for user controller
   * @return a singleton instance
   */
  public static UserController getInstance(){
    return userController;
  }

  /***
   * Handles a HTTP POST request for user creation
   *
   * @param user -> The User object decoded from the payload of POST request.
   * @return -> A Response indicating the outcome of the requested operation.
   */
  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUserAccount(User user) {
    try {
      userService.addUser(user);
    } catch (UserAlreadyPresentException e) {
      return Response.status(409).build();
    }

    return Response.ok().build();
  }


  @GET
  @Path("/getAll")
  @Consumes(MediaType.APPLICATION_JSON)
  public String getAllUsers(){
    return "Test";
  }

}