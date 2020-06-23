package com.neu.prattle.service.api;

import com.neu.prattle.model.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class are api for crud in message table.
 */
public class MessageAPI extends DBUtils {

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private PreparedStatement stmt = null;
  private ResultSet rs = null;

  public MessageAPI() {
    super();
  }

  /**
   * Get all users.
   */
  public List<Message> getAllPrivateMessages(String fromName, String toName) throws SQLException {
    List<Message> allMessages = new ArrayList<>();
    String sql = "SELECT * FROM Message WHERE from = fromName AND to = toName";
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        Message m = constructMessage(rs);
        allMessages.add(m);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    } finally {
      rs.close();
      stmt.close();
    }
    return allMessages;
  }

  /**
   * Fetch the Message with the given id from db
   *
   * @param fromName sender name
   * @param toName receiver name
   * @param timeStamp message timestamp
   *
   * @return if the Message is in the db
   *
   */
  public Message deleteMessage(String fromName, String toName, String timeStamp) throws SQLException {
    String sql = "DELETE FROM Message WHERE from = ? AND to = ? AND timeStamp = ?;";
    return prepareMessage(sql, fromName, toName, timeStamp);
  }


  public boolean addMessage(Message message) throws SQLException {
    boolean b = false;
    con = getConnection();
    String sqlInsert = "INSERT INTO Message (from, to, groupId, timeStamp, sendToGroup, Date) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement sttmt = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
      sttmt.setString(1, message.getFrom());
      sttmt.setString(2, message.getTo());
      sttmt.setString(3, message.getTimeStamp());
      sttmt.setBoolean(4, message.getSendToGroup());
      sttmt.setString(5, message.getDate());
      sttmt.executeUpdate();
      ResultSet result = sttmt.getGeneratedKeys();
      if (result.next()) {
        b = true;
      }
      result.close();
    } catch (SQLException e) {
      throw new IllegalStateException("Insert message failed.");
    }
    return b;
  }

  /**
   * A helper method to construct a Message with returned result set.
   *
   * @param rs the result set
   * @return the message
   */
  public Message constructMessage(ResultSet rs) {
    Message message = new Message();
    try {
      message.setFrom(rs.getString("from"));
      message.setTo(rs.getString("to"));
      message.setContent(rs.getString("message"));
      message.setDate(rs.getString("Date"));
      message.setTimeStamp(rs.getString("timeStamp"));
      message.setSendToGroup(rs.getBoolean("sendToGroup"));
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    }
    return message;
  }

  /**
   * Helper method to get Message with given from, to, and timeStamp
   *
   * @param sql  the sql query string
   * @param from   the sender name
   * @param to the receiver name
   * @param timeStamp the timeStamp
   *
   * @return the message if exist
   */
  public Message prepareMessage(String sql, String from, String to, String timeStamp) throws SQLException {
    Message m = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setString(1, from);
      stmt.setString(2, to);
      stmt.setString(3, timeStamp);
      rs = stmt.executeQuery();
      if (rs.next()) {
        m = constructMessage(rs);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.INFO, e.getMessage());
    } finally {
      rs.close();
      stmt.close();
    }
    return m;
  }
}
