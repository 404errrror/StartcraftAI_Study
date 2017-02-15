import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

import MyApi.*;
import MyApi.MyData.*;

public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();
    private Game game;
    private Player self;
    
    private MyApi myApi;

    public void run() {
  
    	
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit discovered " + unit.getType());
        
        //���ö��̰� �� �������� �����ִ� ���ö��̸� --;
        if(unit.getType() == UnitType.Terran_Supply_Depot){
        	System.out.println("sdfsd1");
        	for(int i = 0; MyData.buildingSupply.size() > i; i++){
        		System.out.println("sdfsd2");
        		if (MyData.buildingSupply.get(i).getType() != UnitType.Terran_Supply_Depot){
        			System.out.println("sdfsd3");
        			MyData.buildingSupply.remove(i);
        		}
        	}
        	MyData.buildingSupply.add(unit);
        	System.out.println("adfssd!!");
            System.out.println("buildingSupply : " + MyData.buildingSupply.size() * 16);
            System.out.println("My supplyTotal : " + self.supplyTotal());
            System.out.println("My usedTotal : " + self.supplyUsed());
        }
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        myApi = new MyApi(game);
        
      	//MyData  Init
    	new MyData();

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready");
        
        int i = 0;
        for(BaseLocation baseLocation : BWTA.getBaseLocations()){
        	System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
        	for(Position position : baseLocation.getRegion().getPolygon().getPoints()){
        		System.out.print(position + ", ");
        	}
        	System.out.println();
        }
        
        game.enableFlag(1); 		// ������ ���� �����ϵ��� ����.
        game.setLocalSpeed(30);		// ���� �ӵ� ����. 0~30. ��ʸ�Ʈ���� 20

    }
    

    @Override
    public void onFrame() {
        //game.setTextSize(10);
    //    game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());

        printInformation();
        
        StringBuilder units = new StringBuilder("My units:\n");

        //iterate through my units
        //��� ���� �˻�.
        for (Unit myUnit : self.getUnits()) {
            units.append(myUnit.getType()).append(" ").append(myUnit.getTilePosition()).append("\n");

            //if there's enough minerals, train an SCV
            if (myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50) {
            	if(!myUnit.isTraining())
            		myUnit.train(UnitType.Terran_SCV);
            }

            //if it's a worker and it's idle, send it to the closest mineral patch
            //�ϲ��� ��������� �̳׶� ĳ���ϱ�.
            if (myUnit.getType().isWorker() && myUnit.isIdle()) {
                Unit closestMineral = null;

                //find the closest mineral
                for (Unit neutralUnit : game.neutral().getUnits()) {
                    if (neutralUnit.getType().isMineralField()) {
                        if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
                            closestMineral = neutralUnit;
                        }
                    }
                }
                
                /////////////////////////���尡��� �̳׶����� �� ������ ������.

                //if a mineral patch was found, send the worker to gather it
                if (closestMineral != null) {
                    myUnit.gather(closestMineral, false);
                }
            }
            //��� ������ Ÿ���� ������ �� �߱�.
            game.drawLineMap(myUnit.getPosition(),myUnit.getOrderTargetPosition(), Color.Blue);
        }

        //draw my units on screen
        game.drawTextScreen(10, 10, units.toString());
        
        //��Ÿ�� ����Ʈ
        game.drawTextMap(self.getStartLocation().toPosition(), "StartingPoint!!");
        game.drawDotMap(self.getStartLocation().toPosition(), Color.Red);

        
        
        //���ް� �Ǽ�.
       myApi.BuildSupply();
        
    }
    public static void main(String[] args) {
        new TestBot1().run();
    }
    
    ///////////////////////////////////////////////
    void printInformation(){
    	
    	game.drawTextScreen(500,10, "My race : " + self.getRace());
    	game.drawTextScreen(500,20, "My minerals : " + self.minerals());
    	game.drawTextScreen(500,30, "My gas : " + self.gas());
    	game.drawTextScreen(500,40, "My supplyTotal : " + self.supplyTotal());
    	game.drawTextScreen(500,50, "My supplyUsed : " + self.supplyUsed());
    	game.drawTextScreen(500,60, "My supplyBuilding : "  + MyData.buildingSupply.size() * 16);
    }
    
	
}