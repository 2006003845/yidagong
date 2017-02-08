package com.zzl.zl_app.util;



/**
 * 
 *<p>Titile:SystemAPI</p>
 *<p>Description:defined Method or constant or varouble when can be used whichever project</p>
 *<p>Copyright:Copyright(c)2010</p>
 *<p>Company:zrong</p>
 * @author yangzheng
 * @version 1.0
 * @date 2012-4-23
 */
public class SystemAPI
{
	public static void sleep(long interval)
	{
		try
		{
			Thread.sleep(interval);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
