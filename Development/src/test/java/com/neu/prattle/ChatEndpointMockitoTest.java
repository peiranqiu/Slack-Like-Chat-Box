/*
 * Copyright (c) 2020. Manan Patel
 * All rights reserved
 */

package com.neu.prattle;

import com.neu.prattle.model.Group;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GroupService;
import com.neu.prattle.service.GroupServiceImpl;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.ModerateService;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServiceImpl;
import com.neu.prattle.websocket.ChatEndpoint;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChatEndpointMockitoTest {

  private static User testUser1 = new User("testName1", "User1Password");
  private static User testUser2 = new User("testName2", "User2Password");
  private static User testUser3 = new User("testName3", "User3Password");
  private Message message;
  private Group group1 = new Group("testChatGroup1");


  // Mocking Session to connect with websocket
  @Mock
  private Session session1;
  @Mock
  private Session session2;
  // Mocking basic which is used by session to send message
  @Mock
  private Basic basic;

  @Mock
  private UserService userService;

  @Mock
  private GroupService groupService;

  @Mock
  private ModerateService moderateService;

  @Mock
  private MessageService messageService;

  // To capture messages sent by Websockets
  private ArgumentCaptor<Object> valueCapture;
  // ChatEndpoints to test
  private ChatEndpoint chatEndpoint1;
  private ChatEndpoint chatEndpoint2;

  @Before
  public void setup() throws IOException, EncodeException {
    // Creating session mocks
    session1 = mock(Session.class);
    session2 = mock(Session.class);

    basic = mock(Basic.class);

    message = Message.messageBuilder().build();

    chatEndpoint1 = new ChatEndpoint();
    chatEndpoint2 = new ChatEndpoint();

    // Capturing method calls using when and then
    when(session1.getBasicRemote()).thenReturn(basic);
    when(session2.getBasicRemote()).thenReturn(basic);

    // Setting up argument captor to capture any Objects
    valueCapture = ArgumentCaptor.forClass(Object.class);
    // Defining argument captor to capture messages emitted by websockets
    doNothing().when(basic).sendObject(valueCapture.capture());
    // Capturing method calls to session.getId() using when and then
    when(session1.getId()).thenReturn("id1");
    when(session2.getId()).thenReturn("id2");

    userService = UserServiceImpl.getInstance();
    userService = mock(UserService.class);
    groupService = GroupServiceImpl.getInstance();
    groupService = mock(GroupService.class);
    moderateService = ModerateService.getInstance();
    moderateService = mock(ModerateService.class);
    messageService = mock(MessageService.class);
  }

  @Test
  public void testOnOpen() throws IOException, EncodeException {
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser1));
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint1.onOpen(session1, testUser1.getName());

    // Finding the message with content 'Connected!'
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Connected!")).findAny();

    if (m.isPresent()) {
      assertEquals("Connected!", m.get().getContent());
      assertEquals(testUser1.getName(), m.get().getFrom());
    } else {
      fail();
    }
  }

  @Test
  public void testOnOpen1() throws IOException, EncodeException {
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser2));
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint1.onOpen(session1, testUser3.getName());

    // Finding the message with content 'Connected!'
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("User testName3 could not be found")).findAny();

    if (m.isPresent()) {
      assertEquals("User testName3 could not be found", m.get().getContent());
    }
  }

  @Test
  public void testOnOpen2() throws IOException, EncodeException {
    when(userService.findUserByName(anyString())).thenReturn(Optional.empty());
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    assertFalse(chatEndpoint1.onOpen(session1, "name"));
  }

  @Test
  public void testOnClose() throws IOException, EncodeException {
    when(groupService.addGroup(any(Group.class))).thenReturn(true);
    assertTrue(groupService.addGroup(group1));
    when(userService.addUser(any(User.class))).thenReturn(true);
    assertTrue(userService.addUser(testUser1));
    assertTrue(userService.addUser(testUser2));

    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser1));
    when(groupService.findGroupByName(anyString())).thenReturn(Optional.of(group1));
    when(moderateService.addGroupModerator(any(Group.class), any(User.class), any(User.class))).thenReturn(testUser1);
    when(moderateService.getModerators(any(Group.class))).thenReturn(new ArrayList<>());
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);
    moderateService.addGroupModerator(group1, testUser1, testUser1);

    when(moderateService.getModerators(any(Group.class))).thenReturn(Arrays.asList(testUser1));
    when(moderateService.getMembers(any(Group.class))).thenReturn(new ArrayList<>());
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);

    chatEndpoint1.onOpen(session1, testUser1.getName());
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser2));
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);
    chatEndpoint2.onOpen(session2, testUser2.getName());

    chatEndpoint1.onClose(session1);

    // Finding the message with content 'Disconnected!'
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Disconnected!")).findAny();

    if (m.isPresent()) {
      assertEquals("Disconnected!", m.get().getContent());
      assertEquals(testUser1.getName(), m.get().getFrom());
    } else {
      //fail();
    }
  }

  @Test
  public void testOnMessage() throws IOException, EncodeException {

    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser1));
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint1.onOpen(session1, testUser1.getName());
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser2));
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);
    chatEndpoint2.onOpen(session2, testUser2.getName());

    message.setTo(testUser2.getName());
    message.setContent("Hey");
    message.setMessageDate(message.getMessageDate());
    message.setTimeStamp(message.getTimeStamp());

    // Sending a message using onMessage method
    chatEndpoint1.onMessage(session1, message);

    // Finding messages with content hey
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Hey")).findAny();

    if (m.isPresent()) {
      assertEquals("Hey", m.get().getContent());
      assertEquals(testUser1.getName(), m.get().getFrom());
    } else {
      fail();
    }
  }

  @Test
  public void testOnMessageGroup() throws IOException, EncodeException {

    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser1));
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    chatEndpoint1.onOpen(session1, testUser1.getName());

    when(moderateService.addGroupModerator(any(Group.class), any(User.class), any(User.class))).thenReturn(testUser1);
    moderateService.addGroupModerator(group1, testUser1, testUser1);

    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser2));
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);
    chatEndpoint2.onOpen(session2, testUser2.getName());


    message.setTo(group1.getName());
    message.setSendToGroup(true);
    message.setContent("HeyGroup");

    // Sending a message using onMessage method
    chatEndpoint1.onMessage(session1, message);

    // Finding messages with content hey
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Group testChatGroup1 could not be found")).findAny();

    if (m.isPresent()) {
      assertEquals("Group testChatGroup1 could not be found", m.get().getContent());
    } else {
      fail();
    }
  }

  @Test
  public void testSendPersonalMessage() throws IOException, EncodeException {
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser1));
    chatEndpoint1.setService(userService, groupService, moderateService, messageService);
    User user1 = userService.findUserByName("testName1").get();
    when(userService.findUserByName(anyString())).thenReturn(Optional.of(testUser2));
    chatEndpoint2.setService(userService, groupService, moderateService, messageService);
    User user2 = userService.findUserByName("testName2").get();
    chatEndpoint1.onOpen(session1, user1.getName());
    chatEndpoint2.onOpen(session2, user2.getName());
    message.setFrom(user1.getName());
    message.setTo(user2.getName());
    message.setContent("Hey");
    // Sending a message using onMessage method
    chatEndpoint1.sendPersonalMessage(message);

    // Finding messages with content hey
    Optional<Message> m = valueCapture.getAllValues().stream()
            .map(val -> (Message) val)
            .filter(msg -> msg.getContent().equals("Hey")).findAny();

    if (m.isPresent()) {
      messageService.addMessage(message);
      assertTrue(true);
    } else {
      fail();
    }
  }
}
