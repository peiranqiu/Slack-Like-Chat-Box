package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * a basic object for Group that is created by a moderator.
 */
@Entity
@Table(name = "Group")
public class Group {
  /**
   * the id of the group, which is unique
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int groupId;
  /**
   * The group name should be unique.
   */
  @Column(unique = true)
  private String name;
  /**
   * a private group should have password.
   */
  @Column(name = "password")
  private String password=null;
    /**
   * moderator list of this group. One group should have at least one moderator.
   */


  @OneToMany(targetEntity = Group.class)
  @JoinTable(name = "User_moderates_Group", joinColumns = {
          @JoinColumn(name = "User_User_id", referencedColumnName = "User_id"),
          @JoinColumn(name = "Group_Group_id", referencedColumnName = "Group_id")})
  @JsonIgnore
  private List<User> moderators = new ArrayList<>();
  /**
   * member list of this group.
   */
  @OneToMany(targetEntity = Group.class)
  @JoinTable(name = "User_has_Group", joinColumns = {
          @JoinColumn(name = "User_User_id", referencedColumnName = "User_id"),
          @JoinColumn(name = "Group_Group_id", referencedColumnName = "Group_id")})
  @JsonIgnore
  private List<User> members = new ArrayList<>();
  /**
   * a list of sub-groups inside this group.
   */
  @OneToMany(targetEntity = Group.class)
  @JoinTable(name = "Group_has_Group", joinColumns = {
          @JoinColumn(name = "super_Group_id", referencedColumnName = "Group_id"),
          @JoinColumn(name = "sub_Group_id", referencedColumnName = "Group_id")})
  @JsonIgnore
  private List<Group> groups = new ArrayList<>();
  /**
   * a list of users who follow this group.
   */
  @OneToMany(targetEntity = Group.class)
  @JoinTable(name = "User_follows_Group", joinColumns = {
          @JoinColumn(name = "User_User_id", referencedColumnName = "User_id"),
          @JoinColumn(name = "Group_Group_id", referencedColumnName = "Group_id")})
  @JsonIgnore
  private List<User> followers = new ArrayList<>();


  public Group(String name) {
    this.name = name;
  }

  public Group() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public List<User> getModerators() {
    return this.moderators;
  }

  public void addModerator(User moderator) {
    this.moderators.add(moderator);
  }

  public List<User> getMembers() {
    return members;
  }

  public void addMember(User member) {
    this.members.add(member);
  }

  public void removeMember(User member) {
    this.members.remove(member);
  }

  public List<Group> getGroups() {
    return groups;
  }

  public void addGroup(Group group) {
    this.groups.add(group);
  }

  public List<User> getFollowers() {
    return followers;
  }

  public void setFollower(User follower) {
    this.followers.add(follower);
  }

  /***
   * Returns the hashCode of this object.
   *
   * As name can be treated as a sort of identifier for
   * this instance, we can use the hashCode of "name"
   * for the complete object.
   *
   *
   * @return hashCode of "this"
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  /***
   * Makes comparison between two groups.
   *
   * Two group objects are equal if their name are equal ( names are case-sensitive )
   *
   * @param obj Object to compare
   * @return a predicate value for the comparison.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Group))
      return false;

    Group group = (Group) obj;
    return group.name.equals(this.name);
  }

}
