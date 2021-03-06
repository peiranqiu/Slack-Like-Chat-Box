package com.neu.prattle;

import com.neu.prattle.model.Group;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GroupService;
import com.neu.prattle.service.GroupServiceImpl;
import com.neu.prattle.service.api.APIFactory;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(MockitoJUnitRunner.class)
public class GroupMockitoTest {

  private GroupService groupService;

  @Mock
  private APIFactory api;

  private Group group2 = new Group("testGroup2");

  @Before
  public void setUp() {
    groupService = GroupServiceImpl.getInstance();
    api = APIFactory.getInstance();
    api = mock(APIFactory.class);
  }

  @Test
  public void testAddGroup() {
    when(api.create(any(Group.class))).thenReturn(true);
    groupService.setAPI(api);
    assertTrue(groupService.addGroup(group2));
  }


  @Test
  public void testFindGroup() throws SQLException {
    when(api.getGroup(anyString())).thenReturn(group2);
    groupService.setAPI(api);
    assertEquals(groupService.findGroupByName("testGroup2").get(), group2);

    when(api.getGroup(anyString())).thenReturn(null);
    groupService.setAPI(api);
    assertFalse(groupService.findGroupByName("testGroup3").isPresent());
  }

  @Test
  public void testgetSubGroupList() throws SQLException {
    List<Group> groupList = groupService.getSubGroupList(4);
    when(api.getAllSubgroups(anyInt())).thenReturn(groupList);
    groupService.setAPI(api);
    assertEquals(groupList, groupService.getSubGroupList(4));
  }

  @Test
  public void testAddSubgroupIntoGroup() throws SQLException {
    when(api.addSubgroup(anyInt(), anyInt())).thenReturn(true);
    groupService.setAPI(api);
    assertTrue(groupService.addSubgroupIntoGroup(1, 4));
  }

  @Test
  public void testFindGroupByName() throws SQLException {
    when(api.getGroup(anyString())).thenReturn(group2);
    groupService.setAPI(api);
    assertEquals(groupService.findGroupByName(group2.getName()).get().getGroupId(), group2.getGroupId());
  }


  @Test
  public void testFindGroupById() throws SQLException {
    when(api.getGroup(anyInt())).thenReturn(group2);
    groupService.setAPI(api);
    assertEquals(groupService.getGroupById(group2.getGroupId()).getName(), group2.getName());
  }

  @Test
  public void testSetPasswordForGroup() throws SQLException {
    when(api.setGroupPassword(anyInt(), anyString())).thenReturn(true);
    groupService.setAPI(api);
    assertTrue(groupService.setPasswordforGroup(group2.getGroupId(), "passWord1"));
  }

  @Test
  public void testSetPsw() throws SQLException {
    List<User> followers = new ArrayList<>();
    User f1 = new User("follow1");
    User f2 = new User("follow2");
    followers.add(f1);
    followers.add(f2);

    when(api.setGroupPassword(anyInt(), anyString())).thenReturn(true);
    when(api.groupGetFollowers(anyInt())).thenReturn(followers);
    when(api.unfollowGroup(anyInt(), anyInt())).thenReturn(true);
    groupService.setAPI(api);
    assertTrue(groupService.setPasswordforGroup(1, "ABCabc1234"));
  }

  @Test
  public void testGetAllGroups() throws SQLException{

    when(api.getAllGroups()).thenReturn(Arrays.asList(group2));
    groupService.setAPI(api);
    assertEquals(groupService.getAllGroups(), Arrays.asList(group2));
  }

  @Test
  public void testSQLException() throws SQLException {
    when(api.getGroup(anyString())).thenThrow(SQLException.class);
    when(api.getGroup(anyInt())).thenThrow(SQLException.class);
    when(api.setGroupPassword(anyInt(), anyString())).thenThrow(SQLException.class);
    when(api.addSubgroup(anyInt(), anyInt())).thenThrow(SQLException.class);
    when(api.getAllSubgroups(anyInt())).thenThrow(SQLException.class);
    when(api.getAllGroups()).thenThrow(SQLException.class);
    groupService.setAPI(api);
    groupService.findGroupByName("1");
    assertFalse(groupService.setPasswordforGroup(1, "password"));
    groupService.addSubgroupIntoGroup(1, 1);
    groupService.getSubGroupList(1);
    groupService.getGroupById(1);
    groupService.getAllGroups();
  }
}


