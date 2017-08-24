import java.sql.*;
import java.util.*;
import edu.brandeis.cs127b.pa2.graphviz.*;
public class Part2 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");

	static final String QUERY = "SELECT num1, num2, random() FROM generate_series(1, 5) as num1, generate_series(5,10) as num2";
        static final String REGION_QUERY = "SELECT r_regionkey, r_name FROM region;";
        static final String CUSTOMER_IN_REGION = "SELECT c_custkey FROM customer JOIN nation ON (c_nationkey = n_nationkey) "+
                                                 "WHERE n_regionkey = %d";
        static final String ORDER_IN_REGION = "SELECT o_orderkey FROM orders JOIN ("+
                                              CUSTOMER_IN_REGION+
                                              ") AS c1 ON (o_custkey = c_custkey)";
        static final String SUPPLIER_IN_REGION = "SELECT s_suppkey, n_regionkey FROM supplier JOIN nation ON (s_nationkey = n_nationkey)";
        static final String SALES_BY_REGION = "SELECT SUM(l_extendedprice),n_regionkey "+
                                              "FROM (lineitem JOIN ("+ORDER_IN_REGION+") AS l1 ON (l_orderkey = o_orderkey)) "+
                                              "JOIN ("+SUPPLIER_IN_REGION+") AS s1 ON (l_suppkey=s_suppkey) "+
                                              "GROUP BY n_regionkey;";
    
	public static void main(String[] args) throws SQLException{
		DirectedGraph g = new DirectedGraph();
		try {
			Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			Statement rgst = conn.createStatement();
                        // get the regions
     	  	        ResultSet regions = rgst.executeQuery(REGION_QUERY);
                        // map the region keys to region names
                        HashMap<Integer, Node> regionMap = new HashMap<>();
                        ArrayList<Integer> regionList = new ArrayList<>();
                        // Add each region as a node in the graph
                        while (regions.next()){
                            int rg = regions.getInt(1);
                            String rgName = regions.getString(2);
                            Node rgNode = new Node(rgName);
                            regionMap.put(rg, rgNode);
                            regionList.add(rg);
                        }
                        // For each region, find out the orders from customers
                        // in that region.
                        for (int cRegionKey: regionList){
                            Statement salesSt = conn.createStatement();
                            ResultSet salesByRegion = salesSt.executeQuery(String.format(SALES_BY_REGION, cRegionKey));
                            while (salesByRegion.next()){
                                double sales = salesByRegion.getDouble(1);
                                int sRegion = salesByRegion.getInt(2);
                                DirectedEdge e = new DirectedEdge(regionMap.get(cRegionKey), regionMap.get(sRegion));
                                e.addLabel(String.format("$%dM", (int) sales/1000000));
                                g.add(e);
                            }
                        }
			System.out.println(g);
		} catch (SQLException s) {
			throw s;
		}

		

	}

}
