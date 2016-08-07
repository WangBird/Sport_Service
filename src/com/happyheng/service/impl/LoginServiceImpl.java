package com.happyheng.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.happyheng.dao.UserDao;
import com.happyheng.entity.result.LoginResult;
import com.happyheng.service.LoginService;
import com.happyheng.utils.ConnectionFactory;


public class LoginServiceImpl implements LoginService{
	
	private UserDao userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	private static final int RESULT_NULL_USERNAME = 1,RESULT_WRONG_PASSWORD = 2;

	@Override
	public LoginResult login(String userName, String passWord) {
		
		Connection connection = null;
		LoginResult result = new LoginResult();
		
		connection = ConnectionFactory.getInstance().makeConnection();
		
		try {
			//1、先判断是否有相应的用户名
			int id = userDao.queryUserName(connection, userName);
			if (id == 0) {
				result.setCode(RESULT_NULL_USERNAME);
				return result;
			}
			
			//2、在判断密码是否正确
			int userId = userDao.queryPassWord(connection, id, passWord);
			if (userId == 0) {
				result.setCode(RESULT_WRONG_PASSWORD);
				return result;
			}
			
			//3、设置相应的token
			long currentTime  = System.currentTimeMillis();
			String token = userId+"_"+currentTime;
			
			userDao.updateToken(connection, userId, token);
			result.setCode(0);
			result.setToken(token);
			
			return result;
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			result.setCode(100);
			return result;
		}	
	}
	
}
