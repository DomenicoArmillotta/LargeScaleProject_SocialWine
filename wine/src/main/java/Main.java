import databases.Advanced_graph;
import databases.Advanced_mongo;
import databases.Crud_graph;
import databases.Crud_mongo;
import menu.Menu;

public class Main {
    public static void main(String[] args) throws Exception {
        //Menu cd = new Menu();
        //cd.MainMenu();
        Crud_mongo mongo = new Crud_mongo();
        Advanced_mongo adv = new Advanced_mongo();
        // populate.populateSocial();
        Crud_graph graph = new Crud_graph("bolt://localhost:7687", "neo4j", "0000");
        Advanced_graph advgraph = new Advanced_graph("bolt://localhost:7687", "neo4j", "0000");
        //graph.addPost("test2","test2");
        //graph.createRelationLike("test2","Giuseppe");
        //graph.addUser("Roger Voss");
        //graph.createRelationFollow("Aldo","Giacomo");
        //graph.createRelationFollow("Aldo","Giovanni");
        //graph.createRelationFollow("Aldo","Giuseppe");
        //graph.allFollowedUserByTaster_name("Aldo");
    }
}