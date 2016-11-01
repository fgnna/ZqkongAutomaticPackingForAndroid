package com.zuqiukong.automaticpacking;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.zuqiukong.automaticpacking.ProcessUtils.LineMsgHandle;

public class TestCode 
{
		public static void main(String[] args) 
		{
			/*
			InputStream in;
			OutputStream out;
			try
			{
				in = new FileInputStream(Constants.PROJECT_GRADLE_PATH);
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				
				StringBuilder contentString = new StringBuilder();
				String line = "";
				int lineCount = 0;
				while(( line = read.readLine() ) != null )
				{  
					contentString.append(line).append("\n");
				}
				read.close();
				in.close();
				String newBuild = contentString.toString().replaceAll(Constants.Gradle_Profiles_Regex,Constants.Gradle_Profiles_Text.replaceAll("<channelName>", "sadfasdfads"));
				System.out.println(newBuild);
			}catch (Exception e)
			{
				e.printStackTrace();
			}
			*/
			
			System.out.println(System.getProperty("os.name"));
			String[] cmdReleasePacking = {"/Users/mac/Desktop/develop/work/zuquikong_beta/gradlew","-p","/Users/mac/Desktop/develop/work/zuquikong_beta" ,"assembleDevelopRelease"};
			try {
				ProcessUtils.exec(cmdReleasePacking,"beta打包Release版",new LineMsgHandle() 
				{
					@Override
					public void handleLine(String line) 
					{
						if(line.indexOf("BUILD SUCCESSFUL")!= -1)
						{
							
						}
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
}
