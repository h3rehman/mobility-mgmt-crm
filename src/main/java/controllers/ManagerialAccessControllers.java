package controllers;

import java.io.IOException;

import java.util.Base64;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.event.presenter.Presenter;

@RestController
@RequestMapping("/api")
public class ManagerialAccessControllers {
	
	private static String ONELOGIN_CLIENT_ID;
	private static String ONELOGIN_CLIENT_SECRET;
	private static String access_token;
	private String managerial_role_id;
	
	
	ManagerialAccessControllers(@Value("${onelogin.api.client_id}") String cid,  
								@Value("${onelogin.api.client_secret}") String csec,
								@Value("${onelogin.api.managerial_role_id}") String manRoleId){
		ONELOGIN_CLIENT_ID = cid;
		ONELOGIN_CLIENT_SECRET = csec;
		this.managerial_role_id = manRoleId;
	}
	
	static String getOLAccessToken() {
		System.out.println("########## Getting OL Acess token: " + ONELOGIN_CLIENT_ID);
		CloseableHttpClient client = HttpClientBuilder.create().build();

		HttpPost request = new HttpPost("https://api.us.onelogin.com/auth/oauth2/v2/token");

		String credentials = String.format("%s:%s", ONELOGIN_CLIENT_ID , ONELOGIN_CLIENT_SECRET);
		byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);

		request.setHeader("Authorization", authHeader);
		request.addHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity("{ \"grant_type\": \"client_credentials\" }", "UTF-8"));
		
		System.out.println("Retrieving OneLogin access token......!");

		try {
			CloseableHttpResponse reponse = client.execute(request);

			String content = EntityUtils.toString(reponse.getEntity());
			JSONObject json = new JSONObject(content);
			String accessToken = json.getString("access_token");
		
			return accessToken;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Token not processed";

	}
	
	
	@GetMapping("/checkUserManagerialAccess")
	public boolean checkUserManagerialAccess(@AuthenticationPrincipal Presenter user) throws ClientProtocolException, IOException {
		boolean check = false;
		if (user != null) {
			CloseableHttpClient client = HttpClientBuilder.create().build();	
			HttpGet request = new HttpGet("https://rtachicago.onelogin.com/api/2/roles/" + managerial_role_id + "/users");
			
			if (access_token == null) {
				access_token = getOLAccessToken();
			}
			
			request.setHeader("Authorization", "bearer " + access_token);
			HttpResponse response = client.execute(request);
			
			//get a fresh access token if the token expired. 
			if (response.getStatusLine().getStatusCode() != 200) {
				access_token = getOLAccessToken();
				request.setHeader("Authorization", "bearer " + access_token);
				response = client.execute(request);
			}
			
			String content = EntityUtils.toString(response.getEntity());
			JSONArray jsonResArray = new JSONArray(content);
			
			for (int i=0; i<jsonResArray.length(); i++) {
				JSONObject jsonObj = jsonResArray.getJSONObject(i);
				String roleUserName = jsonObj.getString("username");
				
				if (roleUserName.equalsIgnoreCase(user.getUsername())) {
					System.out.println("Role username: " + roleUserName);
					System.out.println("Active username: " + user.getUsername());
					check = true;
					break;
				}
			}
		}
		
		return check;	
	}
	
	
	@GetMapping("/managerialAccessRequest")
	public String managerialAccessRequest () {
	
		String accessToken = getOLAccessToken();
		return accessToken;
	}
	

}

