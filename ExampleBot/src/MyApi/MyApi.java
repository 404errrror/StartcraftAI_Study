package MyApi;


import MyApi.MyData.MyData;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;



public class MyApi{
	private Game game;
	private Player self;
	public MyApi(Game game)
	{
		this.game = game;
		this.self = game.self();
	}

	
	
	
	
	public void BuildSupply()
	{
		// ���ް� �� ���������� Ȯ���ϰ�,  buildingSupply.remove()�� �� ������ ���ް�� ����.
		
		
		// ���ް� ����.
        // ����! ���� ���� �α� 1�� ���� �ý��� ������ 2 �̴�. -> �����ͻ󿡼� 2 -> ���ӳ��� �α� 1. �Ƹ� �α� 0.5�� ���ֶ����� �׷��� �ѵ�.
        if((self.supplyTotal() + MyData.buildingSupply.size() * 16 - self.supplyUsed()) < 2 && self.minerals() >= 100)
        {
        	System.out.println("Building Supply!!");
        	for(Unit myUnit : self.getUnits())
        		if(myUnit.getType() == UnitType.Terran_SCV)
        		{
        			//���� ���� �˻�.
        			TilePosition buildTilePos = getBuildTile(myUnit,UnitType.Terran_Supply_Depot, self.getStartLocation());
        			if(buildTilePos != null)
        			{
        				//����
        				myUnit.build(UnitType.Terran_Supply_Depot, buildTilePos);
        				
        				//������ ���� ���߿� ������ ��� ������ ���� �����ϱ� ����, �̸� �ϳ��� �߰��س��´�.
        				Unit temp = myUnit;
        				MyData.buildingSupply.add(temp);
        				break;
        			}
        		}
        }
		
        
	}
	
	 // Returns a suitable TilePosition to build a given building type near 
	 // specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)
   //0~3 �˻��ѵ� 0~5, 0~7 ... stopDist ���� ������ �������鼭 �˻���.
	 public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
	 	TilePosition ret = null;
	 	int maxDist = 3;
	 	int stopDist = 40;
	 	
	 	// Refinery, Assimilator, Extractor  -> ����
	 	if (buildingType.isRefinery()) {
	 		for (Unit n : game.neutral().getUnits()) {
	 			if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
	 					( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
	 					( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
	 					) return n.getTilePosition();
	 		}
	 	}
	 	
	 	while ((maxDist < stopDist) && (ret == null)) {
	 		for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
	 			for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
	 				if (game.canBuildHere(new TilePosition(i,j), buildingType, builder, false)) {
	 					// units that are blocking the tile
	 					boolean unitsInWay = false;
	 					for (Unit u : game.getAllUnits()) {
	 						if (u.getID() == builder.getID()) continue;
	 						if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
	 					}
	 					if (!unitsInWay) {
	 						return new TilePosition(i, j);
	 					}
	 					// creep for Zerg  -> ���� ����
	 					if (buildingType.requiresCreep()) {
	 						boolean creepMissing = false;
	 						for (int k=i; k<=i+buildingType.tileWidth(); k++) {
	 							for (int l=j; l<=j+buildingType.tileHeight(); l++) {
	 								if (!game.hasCreep(k, l)) creepMissing = true;
	 								break;
	 							}
	 						}
	 						if (creepMissing) continue; 
	 					}
	 				}
	 			}
	 		}
	 		maxDist += 2;
	 	}
	 	
	 	if (ret == null) game.printf("Unable to find suitable build position for "+buildingType.toString());
	 	return ret;
	 }
	 
	
}