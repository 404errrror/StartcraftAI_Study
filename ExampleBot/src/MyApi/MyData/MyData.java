package MyApi.MyData;

import java.util.ArrayList;
import java.util.List;

import bwapi.Unit;

public class MyData {


	//�����ִ� ����
	public static List<Unit> buildingSupply = new ArrayList<Unit>();
	//�Ʒ����� �α���.
	public static List<Unit> trainingUnit = new ArrayList<Unit>();
	//������
	public MyData()
	{
		buildingSupply.clear();
		trainingUnit.clear();
		System.out.println("MyData Init!!");
	}
	
	
}
