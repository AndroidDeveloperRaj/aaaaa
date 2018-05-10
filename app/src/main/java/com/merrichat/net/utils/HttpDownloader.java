package com.merrichat.net.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 下载音乐
 */
public class HttpDownloader {
	public static final int FILE_DOWNLOAD_EXIST = 1;
	public static final int FILE_DOWNLOAD_SUCCESS = 0;
	public static final int FILE_DOWNLOAD_FAILED = -1;
	public static final String networkNotConnect = "networkNotConnect";

	/**
	 * 根据URL得到输入流
	 * 1.创建一个URL对象
	 * 2.通过URL对象，创建一个HttpURLConnection对象
	 * 3.得到InputStram
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromUrl(String urlString) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(urlString);
		HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
		inputStream = urlConn.getInputStream();
		return inputStream;
	}

	/**
	 * 根据URL下载文本文件，返回文件中的内容
	 * @param urlString
	 * @return
	 */
	public static String downloadTxtFile(String urlString){
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try{
			//从InputStream中读取数据
			inputStream = getInputStreamFromUrl(urlString);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while((line = bufferedReader.readLine()) != null){
				stringBuffer.append(line);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(null != bufferedReader){
				try{
					bufferedReader.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}

			if(null != inputStream){
				try{
					inputStream.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			//网络无法连接
			else{
				return networkNotConnect;
			}
		}

		return stringBuffer.toString();
	}

	/**
	 * 根据URL下载文件并保存在指定路径
	 * 返回FILE_DOWNLOAD_FAILED：文件下载出错；FILE_DOWNLOAD_SUCCESS：文件下载成功；FILE_EXIST：文件已经存在
	 * @param urlString
	 * @param dirName
	 * @param fileName
	 * @return
	 */
	public static int downloadFile(String urlString, String dirName, String fileName){
		InputStream inputStream = null;
		boolean isFileExist = true;

		try{
			isFileExist = isFileExist(dirName, fileName);
			if(isFileExist){
				return FILE_DOWNLOAD_EXIST;
			}
			else{
				//如果URL错误没有获得正确的链接会返回null，直接抛出异常，执行finally，抛出空指针异常
				inputStream = getInputStreamFromUrl(urlString);
				File file = writeInputStream2SDCard(dirName, fileName, inputStream);
				if(null == file){
					return FILE_DOWNLOAD_FAILED;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return FILE_DOWNLOAD_FAILED;
		}
		finally{
			if(null != inputStream){
				try {
					inputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return FILE_DOWNLOAD_SUCCESS;
	}

	/**
	 * 转换URL，支持中文
	 * @param urlString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String parseURL(String urlString) throws UnsupportedEncodingException{
		return URLEncoder.encode(urlString, "UTF-8")
				.replace("%3A", ":")
				.replace("%2F", "/");
	}

	/**
	 * 判断SD卡上的文件是否存在
	 * @param fileName
	 * @param dirName
	 * @return
	 */
	public static boolean isFileExist(String dirName, String fileName){
		File file = new File(dirName + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * @param dirName
	 * @param fileName
	 * @param inputStream
	 * @return
	 */
	public static File writeInputStream2SDCard(String dirName, String fileName, InputStream inputStream){
		File file = null;
		OutputStream outputStream = null;

		try{
			createDirInSDCard(dirName);
			file = createFileInSDCard(dirName, fileName);
			outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];//一次写4KB

			int count;
			while((count = inputStream.read(buffer)) != -1){
				outputStream.write(buffer, 0, count);
			}

			outputStream.flush();//清空缓存
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(null != outputStream){
				try{
					outputStream.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	public static File createDirInSDCard(String dirName) throws Exception{
		File dir = new File(dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 在SD卡上创建文件
	 * @param dirName
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static File createFileInSDCard(String dirName, String fileName) throws Exception{
		File file = new File(dirName + fileName);
		file.createNewFile();
		return file;
	}

}
