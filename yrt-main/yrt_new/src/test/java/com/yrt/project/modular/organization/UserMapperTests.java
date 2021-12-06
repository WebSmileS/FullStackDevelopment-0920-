package com.yrt.project.modular.organization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.mapper.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserMapperTests {

		@Autowired
		private UserMapper mapper;
		
		@Test
		public void insertUserTest()throws Exception{
			User god =new User();
			god.setSystem_type(SystemType.VENDOR);
			god.setOrg_id(new Long(1));
			god.setPassword_cycle(0);
			god.setLongin_name("王安石");
			god.setPassword("wananshi");
			god.setPhone("123456789");
			assertEquals(1, mapper.insertUser(god));
		}
		
		@Test
		public void updateUserTest()throws Exception{
			User god = new User();
			god.setId(new Long(6));
			god.setLongin_name("王白石");
			god.setPassword("wanbaishi");
			god.setPhone("987654321");
			assertEquals(1, mapper.updateUser(god));
		}
		
		@Test
		public void deleteUserTest()throws Exception{
			assertEquals(1, mapper.deleteUser(new Long(6)));
		}
		
		@Test
		public void selectUserByIdTest()throws Exception{
			User god = mapper.selectUserById(new Long(6));
			assertEquals(new Integer(3), god.getSystem_type());
			assertEquals("yrt37",god.getLongin_name());
			assertEquals("e10adc3949ba59abbe56e057f20f883e", god.getPassword());
		}
}
