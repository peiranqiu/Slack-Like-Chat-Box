package com.neu.prattle.service;

import com.neu.prattle.exceptions.AlreadyFollowException;
import com.neu.prattle.exceptions.FollowNotFoundException;
import com.neu.prattle.exceptions.NoPrivilegeException;
import com.neu.prattle.model.Group;
import com.neu.prattle.model.User;
import com.neu.prattle.service.api.APIFactory;

import java.util.List;

/**
 * The class made to delegate tasks to the JPA service and send results back to Service. Services
 * including user follow user and user follow group.
 */
public class FollowService {

  private static FollowService followService;

  static {
    followService = new FollowService();
  }

  private APIFactory api;

  /**
   * FollowService is a Singleton class.
   */
  private FollowService() {

    api = APIFactory.getInstance();
  }

  /**
   * Call this method to return an instance of this service.
   *
   * @return this
   */
  public static FollowService getInstance() {
    return followService;
  }

  /**
   * Set api to be used by this service.
   */
  public void setAPI(APIFactory apiFactory) {
    api = apiFactory;
  }


  /**
   * User1 follows user2. Return true if successfully followed.
   *
   * @param user1 the follower
   * @param user2 the followee
   * @return true if follow is successful
   */
  public boolean followUser(User user1, User user2) {
    List<User> list = followService.getFollowingUsers(user1);
    if (list.contains(user2)) {
      throw new AlreadyFollowException(String.format("User %s already followed user %s.", user1.getName(), user2.getName()));
    }
    return api.followUser(user1.getUserId(), user2.getUserId());
  }

  public boolean followUser(int user1Id, int user2Id) {
    return api.followUser(user1Id, user2Id);
  }

  /**
   * User1 unfollows user2. Return true if successfully unfollowed.
   *
   * @param user1 the follower
   * @param user2 the followee
   * @return true if unfollow is successful
   */
  public boolean unfollowUser(User user1, User user2) {
    List<User> list = followService.getFollowingUsers(user1);
    if (!list.contains(user2)) {
      throw new FollowNotFoundException(String.format("User %s has not followed user %s.", user1.getName(), user2.getName()));
    }
    return api.unfollowUser(user1.getUserId(), user2.getUserId());
  }

  public boolean unfollowUser(int user1Id, int user2Id) {
    return api.unfollowUser(user1Id, user2Id);
  }

  /**
   * Get a list of users that the specific user follows.
   *
   * @param user the specific user
   * @return the list of users the user follows
   */
  public List<User> getFollowingUsers(User user) {
    return api.getFollowedUsers(user.getUserId());
  }

  public List<User> getFollowingUsers(int userId) {
    return api.getFollowedUsers(userId);
  }

  /**
   * Get follower list for the given user.
   *
   * @param user the given user
   * @return the list of the user's followers
   */
  public List<User> userGetFollowers(User user) {
    return api.userGetFollowers(user.getUserId());
  }

  public List<User> userGetFollowers(int userId) {
    return api.userGetFollowers(userId);
  }

  /**
   * User follows group. Return true if successfully followed.
   *
   * @param user  the user
   * @param group the group
   * @return true if follow is successful
   */
  public boolean followGroup(User user, Group group) {
    if (group.getPassword() != null) {
      throw new NoPrivilegeException("The group is a private group and can not be followed.");
    }
    List<Group> list = getFollowingGroups(user);
    if (list.contains(group)) {
      throw new AlreadyFollowException(String.format("User %s already followed group %s.", user.getName(), group.getName()));
    }
    return api.followGroup(user.getUserId(), group.getGroupId());
  }

  public boolean followGroup(int userId, int groupId) {
    return api.followGroup(userId, groupId);
  }

  /**
   * User unfollows group. Return true if successfully unfollowed.
   *
   * @param user  the user
   * @param group the group
   * @return true if unfollow is successful
   */
  public boolean unfollowGroup(User user, Group group) {
    List<Group> list = followService.getFollowingGroups(user);
    if (!list.contains(group)) {
      throw new FollowNotFoundException(String.format("User %s has not followed group %s.", user.getName(), group.getName()));
    }
    return api.unfollowGroup(user.getUserId(), group.getGroupId());
  }

  public boolean unfollowGroup(int userId, int groupId) {
    return api.unfollowGroup(userId, groupId);
  }

  /**
   * Get a list of groups that the specific user follows.
   *
   * @param user the specific user
   * @return the list of groups the user follows
   */
  public List<Group> getFollowingGroups(User user) {

    return api.getFollowedGroups(user.getUserId());
  }

  public List<Group> getFollowingGroups(int userId) {

    return api.getFollowedGroups(userId);
  }

  /**
   * Get follower list for the given group.
   *
   * @param group the given group
   * @return the list of the group's followers
   */
  public List<User> groupGetFollowers(Group group) {

    return api.groupGetFollowers(group.getGroupId());
  }

  public List<User> groupGetFollowers(int groupId) {

    return api.groupGetFollowers(groupId);
  }
}
