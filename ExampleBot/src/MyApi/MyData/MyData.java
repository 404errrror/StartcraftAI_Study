package MyApi.MyData;

import java.util.ArrayList;
import java.util.List;

import bwapi.Unit;

public class MyData {


	//짓고있는 서플
	public static List<Unit> buildingSupply = new ArrayList<Unit>();
	//훈련중인 인구수.
	public static List<Unit> trainingUnit = new ArrayList<Unit>();
	//생성자
	public MyData()
	{
		buildingSupply.clear();
		trainingUnit.clear();
		System.out.println("MyData Init!!");
	}
	
	
}
