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
		// 보급고 다 지어졌는지 확인하고,  buildingSupply.remove()로 다 지어진 보급고는 삭제.
		
		
		// 보급고 짓기.
        // 주의! 게임 내의 인구 1은 게임 시스템 내에선 2 이다. -> 에디터상에서 2 -> 게임내의 인구 1. 아마 인구 0.5인 유닛때문에 그렇게 한듯.
        if((self.supplyTotal() + MyData.buildingSupply.size() * 16 - self.supplyUsed()) < 2 && self.minerals() >= 100)
        {
        	System.out.println("Building Supply!!");
        	for(Unit myUnit : self.getUnits())
        		if(myUnit.getType() == UnitType.Terran_SCV)
        		{
        			//지을 공간 검색.
        			TilePosition buildTilePos = getBuildTile(myUnit,UnitType.Terran_Supply_Depot, self.getStartLocation());
        			if(buildTilePos != null)
        			{
        				//짓기
        				myUnit.build(UnitType.Terran_Supply_Depot, buildTilePos);
        				
        				//지으러 가는 도중에 여러번 명령 내리는 것을 방지하기 위해, 미리 하나를 추가해놓는다.
        				Unit temp = myUnit;
        				MyData.buildingSupply.add(temp);
        				break;
        			}
        		}
        }
		
        
	}
	
	 // Returns a suitable TilePosition to build a given building type near 
	 // specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)
   //0~3 검사한뒤 0~5, 0~7 ... stopDist 까지 서서히 넓혀가면서 검색함.
	 public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
	 	TilePosition ret = null;
	 	int maxDist = 3;
	 	int stopDist = 40;
	 	
	 	// Refinery, Assimilator, Extractor  -> 가스
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
	 					// creep for Zerg  -> 저그 점막
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