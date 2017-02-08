package com.zrlh.llkc.organization;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class NavigationMethod {

	public static final String BASE_URL = "http://zhij.zrong.cn/api.php?";
//	public static final String BASE_RY = "http://prof.zrong.cn/interface/";
//	public static final String BASE_RY = "http://218.247.140.193:9990/profession/discuz/";
	public static final String BASE_RY = "http://192.168.0.19:9990/profession/discuz/";
	
	public static boolean isGzip = false;
	//网络链接失败
	public static final String NETWORKBUG = "network_bug";
	//手机imei号
	public static  String IMEI   ;
	//当前版本号
	public static String APP_Version ="1.0";
	//渠道类型
	public static String APP_Plat ="1"; //1 ADNROID 2 IOS
	//app
	public static String APP_Orgid ="dh"; 
	//当前经纬度
	public static  Double lat = 0.0;
	public static  Double lng = 0.0;
	public static  String citystring ;
	public static boolean IS_STOP_REQUEST = false;//请求线程是否停止

	public static ArrayList<Organization.ClassfiyHot> oneListRequest(){
		ArrayList<Organization.ClassfiyHot> oneArrayListbig = new ArrayList<Organization.ClassfiyHot>();
//		String resultr = "{\"List\":[{\"clsId\":\"1\",\"clsName\":\"销售·客服·采购\"}, {\"clsId\":\"2\",\"clsName\":\"IT·通信·电子\"}, {\"clsId\":\"3\",\"clsName\":\"房产·建筑建设·物业\"},{\"clsId\":\"4\",\"clsName\":\"财会·金融\"}, {\"clsId\":\"5\",\"clsName\":\"汽车·机械\"}, {\"clsId\":\"6\",\"clsName\":\"消费品·生产·加工·物流\"},{\"clsId\":\"7\",\"clsName\":\"市场·媒介·设计\"}, {\"clsId\":\"8\",\"clsName\":\"医药·化工·能源·环保\"}, {\"clsId\":\"9\",\"clsName\":\"管理·人力资源·行政\"},{\"clsId\":\"10\",\"clsName\":\"咨询·法律·教育·外语\"}, {\"clsId\":\"11\",\"clsName\":\"服务业\"}, {\"clsId\":\"12\",\"clsName\":\"农林·畜牧·科研\"}, {\"clsId\":\"13\",\"clsName\":\"其他\"},{\"clsId\":\"14\",\"clsName\":\"技工院校\"}]}";
		String resultr = "{\"List\":[{\"clsId\":\"1\",\"clsName\":\"销售·客服·采购\"}, {\"clsId\":\"2\",\"clsName\":\"IT·通信·电子\"}, {\"clsId\":\"3\",\"clsName\":\"房产·建筑建设·物业\"},{\"clsId\":\"4\",\"clsName\":\"财会·金融\"}, {\"clsId\":\"5\",\"clsName\":\"汽车·机械\"}, {\"clsId\":\"6\",\"clsName\":\"消费品·生产·加工·物流\"},{\"clsId\":\"7\",\"clsName\":\"市场·媒介·设计\"}, {\"clsId\":\"8\",\"clsName\":\"医药·化工·能源·环保\"}, {\"clsId\":\"9\",\"clsName\":\"管理·人力资源·行政\"},{\"clsId\":\"10\",\"clsName\":\"咨询·法律·教育·外语\"}, {\"clsId\":\"11\",\"clsName\":\"服务业\"}, {\"clsId\":\"12\",\"clsName\":\"农林·畜牧·科研\"}, {\"clsId\":\"13\",\"clsName\":\"其他\"}]}";

		JSONObject demoJson;
		try {
			demoJson = new JSONObject(resultr);
			JSONArray numberList = demoJson.getJSONArray("List");
			for(int i=0; i<numberList.length(); i++){
				Organization.ClassfiyHot twoClassfiyHot =  new Organization.ClassfiyHot();
				twoClassfiyHot.setClsId(numberList.getJSONObject(i).getString("clsId"));
				twoClassfiyHot.setClsName(numberList.getJSONObject(i).getString("clsName"));
				oneArrayListbig.add(twoClassfiyHot);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return oneArrayListbig;
	}

	public static ArrayList<ArrayList<Organization.ClassfiyHot>> twoListRequest(){
		ArrayList<ArrayList<Organization.ClassfiyHot>> twoArrayListBig = new ArrayList<ArrayList<Organization.ClassfiyHot>>();

		String resultr = "{\"List\":[[{\"clsId\":\"14\",\"clsName\":\"销售业务\"},{\"clsId\":\"15\",\"clsName\":\"销售管理\"},{\"clsId\":\"16\",\"clsName\":\"销售支持/商务\"},{\"clsId\":\"17\",\"clsName\":\"客户服务/售前/售后支持\"},{\"clsId\":\"18\",\"clsName\":\"采购/贸易\"}]," +
				"[{\"clsId\":\"19\",\"clsName\":\"计算机软件/系统集成\"},{\"clsId\":\"20\",\"clsName\":\"互联网/电子商务/网游\"},{\"clsId\":\"21\",\"clsName\":\"计算机硬件/设备\"},{\"clsId\":\"22\",\"clsName\":\"电信/通信技术开发及应用\"},{\"clsId\":\"23\",\"clsName\":\"电子/电气/半导体/仪器仪表\"},{\"clsId\":\"24\",\"clsName\":\"IT/技术支持及其他\"}]," +
				"[{\"clsId\":\"25\",\"clsName\":\"建筑装修/市政建设\"},{\"clsId\":\"26\",\"clsName\":\"房地产开发/经纪/中介\"},{\"clsId\":\"27\",\"clsName\":\"物业管理\"}]," +
				"[{\"clsId\":\"28\",\"clsName\":\"财务/审计/税务\"},{\"clsId\":\"29\",\"clsName\":\"银行\"},{\"clsId\":\"30\",\"clsName\":\"金融/证券/期货/投资\"},{\"clsId\":\"31\",\"clsName\":\"保险\"}]," +
				"[{\"clsId\":\"32\",\"clsName\":\"汽车/摩托车制造\"},{\"clsId\":\"33\",\"clsName\":\"汽车销售/服务\"},{\"clsId\":\"34\",\"clsName\":\"工程机械\"}]," +
				"[{\"clsId\":\"35\",\"clsName\":\"生产/加工/制造\"},{\"clsId\":\"36\",\"clsName\":\"交通运输服务\"},{\"clsId\":\"37\",\"clsName\":\"服装/纺织/食品/饮料\"},{\"clsId\":\"38\",\"clsName\":\"物流/仓储\"},{\"clsId\":\"39\",\"clsName\":\"技工/数控\"},{\"clsId\":\"40\",\"clsName\":\"质量管理/安全防护\"}]," +
				"[{\"clsId\":\"41\",\"clsName\":\"市场/营销\"},{\"clsId\":\"42\",\"clsName\":\"公关/媒介\"},{\"clsId\":\"43\",\"clsName\":\"美术/设计/创意\"},{\"clsId\":\"44\",\"clsName\":\"广告/会展\"},{\"clsId\":\"45\",\"clsName\":\"传媒/影视/报刊/出版/印刷\"}]," +
				"[{\"clsId\":\"46\",\"clsName\":\"生物/制药/医疗器械\"},{\"clsId\":\"47\",\"clsName\":\"化工\"},{\"clsId\":\"48\",\"clsName\":\"环境科学/环保\"},{\"clsId\":\"49\",\"clsName\":\"能源/矿产/地质勘查\"}]," +
				"[{\"clsId\":\"50\",\"clsName\":\"高级管理\"},{\"clsId\":\"51\",\"clsName\":\"人力资源\"},{\"clsId\":\"52\",\"clsName\":\"行政/后勤/文秘\"}]," +
				"[{\"clsId\":\"53\",\"clsName\":\"咨询/顾问\"},{\"clsId\":\"54\",\"clsName\":\"教育/培训/外语\"},{\"clsId\":\"55\",\"clsName\":\"律师/法务/合规\"}]," +
				"[{\"clsId\":\"56\",\"clsName\":\"零售/百货/连锁/超市\"},{\"clsId\":\"57\",\"clsName\":\"酒店/餐饮/旅游/娱乐\"},{\"clsId\":\"58\",\"clsName\":\"保健/美容/美发/健身\"},{\"clsId\":\"59\",\"clsName\":\"医院/医疗/护理\"},{\"clsId\":\"60\",\"clsName\":\"保安/家政/普通劳动力\"}]," +
				"[{\"clsId\":\"61\",\"clsName\":\"农/林/牧/渔\"},{\"clsId\":\"62\",\"clsName\":\"气象/航空航天\"},{\"clsId\":\"63\",\"clsName\":\"声光学技术/激光技术\"}]," +
				"[{\"clsId\":\"64\",\"clsName\":\"其他\"}]," +
				"[{\"clsId\":\"65\",\"clsName\":\"机械加工制造\"},{\"clsId\":\"66\",\"clsName\":\"电工电子\"},{\"clsId\":\"67\",\"clsName\":\"商贸服务\"},{\"clsId\":\"68\",\"clsName\":\"计算机信息\"},{\"clsId\":\"69\",\"clsName\":\"交通运输\"},{\"clsId\":\"70\",\"clsName\":\"农林类\"},{\"clsId\":\"71\",\"clsName\":\"其他\"}]]}";




		JSONObject demoJson;
		try {
			demoJson = new JSONObject(resultr);
			JSONArray numberList = demoJson.getJSONArray("List");
			for(int i=0; i<numberList.length(); i++){
				ArrayList<Organization.ClassfiyHot> twoArrayList = new ArrayList<Organization.ClassfiyHot>();

				JSONArray numberLis = numberList.getJSONArray(i);
				for(int j=0; j<numberLis.length(); j++){
					Organization.ClassfiyHot twoClassfiyHot =  new Organization.ClassfiyHot();
					twoClassfiyHot.setClsId(numberLis.getJSONObject(j).getString("clsId"));
					twoClassfiyHot.setClsName(numberLis.getJSONObject(j).getString("clsName"));
					twoArrayList.add(twoClassfiyHot);
				}
				twoArrayListBig.add(twoArrayList);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return twoArrayListBig;
	}


}
