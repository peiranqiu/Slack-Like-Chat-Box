package com.neu.prattle.service;


import com.neu.prattle.exceptions.GroupNotFoundException;
import com.neu.prattle.model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GroupApI is a class that can connect this project with sql database for group entity.
 */
public class GroupAPI extends DBUtils {
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private PreparedStatement stmt = null;
  private ResultSet rs = null;

  public GroupAPI() {
    super();
  }

  /**
   * add group into database
   *
   * @param group adding group object.
   */
  public void addGroup(Group group) {
    try {
      super.insertTerm("mydb.Group", "name", group.getName());
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
  }

  /**
   * check if group exists in the database.
   *
   * @param name group name
   * @return the group
   */
  public Group getGroup(String name) throws SQLException {
    Group g = null;
    try {
      con = getConnection();
      String sql = "SELECT * FROM mydb.Group WHERE name =?";
      stmt = con.prepareStatement(sql);
      stmt.setString(1, name);
      rs = stmt.executeQuery();
      if (rs.next()) {
        g = constructGroup(rs);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    } finally {
      rs.close();
      stmt.close();
    }
    return g;
  }

  /**
   * check if group exists in the database.
   *
   * @param id group id
   * @return the group
   */
  public Group getGroupById(int id) throws SQLException {
    Group g = null;
    try {
      Connection con = getConnection();
      String sql = "SELECT * FROM mydb.Group WHERE Group_id =?";
      stmt = con.prepareStatement(sql);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();
      if (rs.next()) {
        g = constructGroup(rs);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    } finally {
      rs.close();
      stmt.close();
    }
    return g;
  }

  /**
   * A helper method to construct a group object with returned result set.
   * @param rs the result set
   * @return the group
   */
  public Group constructGroup(ResultSet rs) {
    Group group = new Group();
    try {
      group.setGroupId(rs.getInt("Group_id"));
      group.setName(rs.getString("name"));
      group.setPassword(rs.getString("password"));
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
    return group;
  }
}
