package com.bobby.defaultsmsapp.utils;

import com.bobby.defaultsmsapp.config.JSONConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeChartSender {

	public WeChartSender() {
	}

	private static String accessTokenUrl = JSONConfig.instance().getWechartAccessTokenUrl();
	private static String sendUrl = JSONConfig.instance().getWechartMsgSendUrl();

	// 发送微信消息
	public static boolean sendWeChart(int appId,String msg) {
		String token = getAccessToken();
		try {
			JSONObject data = new JSONObject();
			data.put("touser", "@all");
			data.put("msgtype", "text");
			data.put("agentid", appId);

			JSONObject content = new JSONObject();
			content.put("content", msg);
			data.put("text", content);
			data.put("safe", 0);

			String str = post(sendUrl+token,data.toString());

			JSONObject ret = new JSONObject(str);
			String errorCode = ret.getString("errcode");
			if("0".equals(errorCode))
				return true;

		} catch (JSONException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public static String post(String httpsUrl, String dataStr) {
		HttpsURLConnection urlCon = null;
		try {
			urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
			urlCon.setDoInput(true);
			urlCon.setDoOutput(true);
			urlCon.setRequestMethod("POST");
			byte[] tmp = dataStr.getBytes("UTF-8");
			urlCon.setRequestProperty("Content-Length",String.valueOf(tmp.length));
			urlCon.setUseCaches(false);
			urlCon.getOutputStream().write(tmp);
			urlCon.getOutputStream().flush();
			urlCon.getOutputStream().close();

			int out = urlCon.getResponseCode();
			if (out < 200 || out > 299) {
				return "error httpstate:" + out;
			}

			int len = urlCon.getContentLength();

			StringBuilder sb = new StringBuilder(len);

			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return  sb.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error httpstate:";
	}

	private static long accessTokenTime = -1;
	private static String accessToken = "";

	/**
	 * 获取accessToken
	 * 
	 * @return
	 */
	public static String getAccessToken() {
		if (System.currentTimeMillis() - accessTokenTime < 3000) {
			return accessToken;
		}
		try {

			URL url = new URL(accessTokenUrl);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			try {
				urlConnection.connect();

				int out = urlConnection.getResponseCode();
				if (out < 200 || out > 299) {
					return "error httpstate:" + out;
				}

				int len = urlConnection.getContentLength();
				InputStream input = new BufferedInputStream(urlConnection.getInputStream());
				byte[] outBytes = new byte[len];
				int outlen = input.read(outBytes);
				while (outlen < len) {
					input.read(outBytes, outlen, len - outlen);
				}
				JSONObject obj = new JSONObject(new String(outBytes, "UTF-8"));
				accessToken = obj.getString("access_token");
				input.close();
			}finally {
				urlConnection.disconnect();
			}
			return accessToken;
		} catch (IOException e) {
			return "error IOException";
		} catch (JSONException e) {
			return "error JSONException";
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sendWeChart(2,"test");
	}

}
